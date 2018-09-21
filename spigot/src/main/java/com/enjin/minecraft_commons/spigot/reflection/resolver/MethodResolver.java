package com.enjin.minecraft_commons.spigot.reflection.resolver;

import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.MethodWrapper;
import com.enjin.minecraft_commons.spigot.reflection.util.AccessUtil;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodResolver extends MemberResolver<Method> {

    public MethodResolver(Class<?> clazz) {
        super(clazz);
    }

    public MethodResolver(String className) throws ClassNotFoundException {
        super(className);
    }

    public Method resolveSignature(String... signatures) throws ReflectiveOperationException {
        for (Method method : clazz.getDeclaredMethods()) {
            String methodSignature = MethodWrapper.getMethodSignature(method);

            for (String signature : signatures) {
                if (signature.equals(methodSignature)) {
                    return AccessUtil.setAccessible(method);
                }
            }
        }

        return null;
    }

    public Method resolveSignatureSilent(String... signatures) {
        try {
            return resolveSignature(signatures);
        } catch (ReflectiveOperationException e) {}

        return null;
    }

    public MethodWrapper resolveSignatureWrapper(String... signatures) {
        return new MethodWrapper(resolveSignatureSilent(signatures));
    }

    @Override
    public Method resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredMethods()[index]);
    }

    @Override
    public Method resolveIndexSilent(int index) {
        try {
            return resolveIndex(index);
        } catch (IndexOutOfBoundsException | ReflectiveOperationException e) {}

        return null;
    }

    @Override
    public MethodWrapper resolveIndexWrapper(int index) {
        return new MethodWrapper<>(resolveIndexSilent(index));
    }

    public MethodWrapper resolveWrapper(String... names) {
        return new MethodWrapper(resolveSilent(names));
    }

    public MethodWrapper resolveWrapper(ResolverQuery... queries) {
        return new MethodWrapper(resolveSilent(queries));
    }

    public Method resolveSilent(String... names) {
        try {
            return resolve(names);
        } catch (Exception e) {}

        return null;
    }

    @Override
    public Method resolveSilent(ResolverQuery... queries) {
        return super.resolveSilent(queries);
    }

    public Method resolve(String... names) throws NoSuchMethodException {
        ResolverQuery.Builder builder = ResolverQuery.builder();

        for (String name : names) {
            builder.with(name);
        }

        return resolve(builder.build());
    }

    @Override
    public Method resolve(ResolverQuery... queries) throws NoSuchMethodException {
        try {
            return super.resolve(queries);
        } catch (ReflectiveOperationException e) {
            throw (NoSuchMethodException) e;
        }
    }

    @Override
    protected Method resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(query.getName()) && (query.getTypes().length == 0 || classArraysEqual(query.getTypes(), method.getParameterTypes()))) {
                return AccessUtil.setAccessible(method);
            }
        }

        throw new NoSuchMethodException();
    }

    @Override
    protected NoSuchMethodException notFoundException(String joinedNames) {
        return new NoSuchMethodException(String.format("Could not resolve method for %s in class %s", joinedNames, clazz));
    }

    static boolean classArraysEqual(Class<?>[] array1, Class<?>[] array2) {
        boolean equal = true;

        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (!Objects.equals(array1[i], array2[i])) {
                equal = false;
                break;
            }
        }

        return equal;
    }
}
