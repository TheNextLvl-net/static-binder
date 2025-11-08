package net.thenextlvl.binder;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

/**
 * A static binder for binding and retrieving instances of classes.
 *
 * @since 0.1.0
 */
public sealed interface StaticBinder permits SimpleStaticBinder {
    /**
     * Gets the instance of the static binder for the current thread's context class loader.
     *
     * @return the static binder instance
     */
    @CheckReturnValue
    static StaticBinder getInstance() {
        return getInstance(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Gets the instance of the static binder for the specified class loader.
     *
     * @param loader the class loader
     * @return the static binder instance
     */
    @CheckReturnValue
    static StaticBinder getInstance(ClassLoader loader) {
        return SimpleStaticBinder.getInstance(loader);
    }

    /**
     * Binds an instance of the given class to the binder.
     *
     * @param clazz    the class to bind
     * @param instance the instance to bind
     * @param <T>      the type of the class
     * @throws IllegalArgumentException if the class already has a binding
     * @throws IllegalArgumentException if the instance is not assignable to the class
     * @throws SecurityException        if the binder access is not allowed
     */
    @Contract(mutates = "this")
    <T> void bind(Class<T> clazz, T instance) throws IllegalArgumentException, SecurityException;

    /**
     * Retrieves an instance of the given class from the binder.
     *
     * @param clazz the class to retrieve
     * @param <T>   the type of the class
     * @return an optional containing the instance if found, empty otherwise
     * @throws SecurityException if the binder access is not allowed
     * @see #find(Class)
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    <T> Optional<T> get(Class<T> clazz) throws SecurityException;

    /**
     * Retrieves an instance of the given class from the binder.
     *
     * @param clazz the class to retrieve
     * @param <T>   the type of the class
     * @return the instance if found, throws an exception otherwise
     * @throws IllegalStateException if no instance is found
     * @throws SecurityException     if the binder access is not allowed
     * @see #get(Class)
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    <T> T find(Class<T> clazz) throws IllegalStateException, SecurityException;
}
