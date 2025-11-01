# Static Binder

A lightweight Java library for static dependency binding and retrieval.

## Overview

Static Binder provides a simple way to bind and retrieve instances of classes throughout your application using a thread-safe, class-loader-scoped approach.
It uses weak references to prevent memory leaks and includes security controls to ensure proper access boundaries.

## Features

- **Thread-safe**: Concurrent access support
- **Class-loader scoped**: Each class loader maintains its own binder instance
- **Memory efficient**: Uses weak references to avoid memory leaks
- **Security conscious**: Enforces class loader access boundaries
- **Simple API**: Minimal interface with just three core methods

## Installation

### Gradle

```kts
repositories {
    maven("https://repo.thenextlvl.net/snapshots")
}

dependencies {
    implementation("net.thenextlvl:static-binder:0.1.1")
}
```

## Quick Start

### Basic Usage

```java
import net.thenextlvl.binder.StaticBinder;

// Get the binder instance for the current thread
StaticBinder binder = StaticBinder.getInstance();

// Bind an instance
MyService service = new MyServiceImpl();
binder.bind(MyService.class, service);

// Retrieve the instance
Optional<MyService> optionalService = binder.get(MyService.class);
MyService retrievedService = optionalService.orElse(null);

// Or retrieve with exception if not found
MyService requiredService = binder.find(MyService.class);
```

### Class Loader Specific Usage

```java
// Get binder for a specific class loader
ClassLoader customLoader = MyClass.class.getClassLoader();
StaticBinder binder = StaticBinder.getInstance(customLoader);

// Bind and retrieve as usual
binder.bind(MyService.class, new MyServiceImpl());
MyService service = binder.find(MyService.class);
```

## Usage Patterns

### Service Locator Pattern

```java
public class ServiceLocator {
    private static final StaticBinder binder = StaticBinder.getInstance();

    public static <T> T getService(Class<T> serviceClass) {
        return binder.find(serviceClass);
    }

    public static <T> void registerService(Class<T> serviceClass, T implementation) {
        binder.bind(serviceClass, implementation);
    }
}
```

### Configuration Setup

```java
public class ApplicationConfig {
    public static void initialize() {
        StaticBinder binder = StaticBinder.getInstance();

        // Bind configuration
        binder.bind(DatabaseConfig.class, new DatabaseConfig());
        binder.bind(CacheService.class, new RedisCacheService());
        binder.bind(UserRepository.class, new JdbcUserRepository());
    }
}
```

## Security Considerations

The binder enforces class loader boundaries to prevent unauthorized access:

- Only classes from the same class loader or system class loader can access bindings
- Attempts to access bindings from different class loaders will throw `SecurityException`
- This ensures proper isolation in multi-module or plugin-based applications

## Memory Management

- Bindings use `WeakReference` to prevent memory leaks
- When an instance is garbage collected, the binding is automatically cleaned up
- The binder itself is cached per class loader for efficiency

## Requirements

- Java 21 or higher
- No external dependencies (only optional compile-time annotations)