package net.thenextlvl.binder;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

final class SimpleStaticBinder implements StaticBinder {
    private static final Map<ClassLoader, SimpleStaticBinder> INSTANCES = new ConcurrentHashMap<>();

    public static SimpleStaticBinder getInstance(ClassLoader loader) {
        return INSTANCES.computeIfAbsent(loader, SimpleStaticBinder::new);
    }

    private final Map<Class<?>, WeakReference<Object>> bindings = new ConcurrentHashMap<>();
    private final ClassLoader loader;

    private SimpleStaticBinder(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public <T> void bind(Class<T> clazz, T instance) throws IllegalArgumentException, SecurityException {
        if (bindings.containsKey(clazz)) {
            throw new IllegalArgumentException("Instance already bound for class " + clazz.getName());
        }
        if (!clazz.isInstance(instance)) {
            throw new IllegalArgumentException("Instance of " + instance.getClass().getName() + " is not assignable to " + clazz.getName());
        }
        if (loader != clazz.getClassLoader() && loader != ClassLoader.getSystemClassLoader()) {
            throw new SecurityException("Illegal binder access from class loader '" + loader.getName() + "'");
        }
        bindings.put(clazz, new WeakReference<>(instance));
    }

    @Override
    public <T> Optional<T> get(Class<T> clazz) throws SecurityException {
        if (loader != clazz.getClassLoader() && loader != ClassLoader.getSystemClassLoader()) {
            throw new SecurityException("Illegal binder access from class loader '" + loader.getName() + "'");
        }

        var reference = bindings.get(clazz);
        if (reference == null) return Optional.empty();

        var instance = reference.get();
        if (instance != null) return Optional.of(clazz.cast(instance));

        bindings.remove(clazz);
        return Optional.empty();
    }

    @Override
    public <T> T find(Class<T> clazz) throws IllegalStateException, SecurityException {
        return get(clazz).orElseThrow(() -> new IllegalStateException("No instance found for " + clazz.getName()));
    }
}
