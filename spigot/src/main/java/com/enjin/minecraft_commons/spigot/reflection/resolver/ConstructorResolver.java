package com.enjin.minecraft_commons.spigot.reflection.resolver;

import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.ConstructorWrapper;
import com.enjin.minecraft_commons.spigot.reflection.util.AccessUtil;

import java.lang.reflect.Constructor;

public class ConstructorResolver extends MemberResolver<Constructor> {

    public ConstructorResolver(Class<?> clazz) {
        super(clazz);
    }

    public ConstructorResolver(String className) throws ClassNotFoundException {
        super(className);
    }

    @Override
    public Constructor resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
        return AccessUtil.setAccessible(clazz.getDeclaredConstructors()[index]);
    }

    @Override
    public Constructor resolveIndexSilent(int index) {
        try {
            return resolveIndex(index);
        } catch (IndexOutOfBoundsException | ReflectiveOperationException e) {}

        return null;
    }

    @Override
    public ConstructorWrapper resolveIndexWrapper(int index) {
        return new ConstructorWrapper<>(resolveIndexSilent(index));
    }

    public ConstructorWrapper resolveWrapper(Class<?>[]... types) {
        return new ConstructorWrapper(resolveSilent(types));
    }

    public Constructor resolveSilent(Class<?>[]... types) {
        try {
            return resolve(types);
        } catch (Exception e) {}

        return null;
    }

    public Constructor resolve(Class<?>[]... types) throws NoSuchMethodException {
        ResolverQuery.Builder builder = ResolverQuery.builder();

        for (Class<?>[] type : types) {
            builder.with(type);
        }

        try {
            return super.resolve(builder.build());
        } catch (ReflectiveOperationException e) {
            throw (NoSuchMethodException) e;
        }
    }

    @Override
    protected Constructor resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        return AccessUtil.setAccessible(clazz.getDeclaredConstructor(query.getTypes()));
    }

    public Constructor resolveFirstConstructor() throws ReflectiveOperationException {
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            return AccessUtil.setAccessible(constructor);
        }

        return null;
    }

    public Constructor resolveFirstConstructorSilent() {
        try {
            return resolveFirstConstructor();
        } catch (Exception e) {}

        return null;
    }

    public Constructor resolveLastConstructor() throws ReflectiveOperationException {
        Constructor result = null;

        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            result = constructor;
        }

        if (result != null) {
            return AccessUtil.setAccessible(result);
        }

        return null;
    }

    public Constructor resolveLastConstructorSilent() {
        try {
            return resolveLastConstructor();
        } catch (Exception e) {}

        return null;
    }

    @Override
    protected ReflectiveOperationException notFoundException(String joinedNames) {
        return new NoSuchMethodException(String.format("Could not resolve constructor for %s in class %s", joinedNames, clazz));
    }
}
