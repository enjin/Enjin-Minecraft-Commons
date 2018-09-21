package com.enjin.minecraft_commons.spigot.reflection.resolver;

import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.FieldWrapper;
import com.enjin.minecraft_commons.spigot.reflection.util.AccessUtil;

import java.lang.reflect.Field;

public class FieldResolver extends MemberResolver<Field> {

    public FieldResolver(Class<?> clazz) {
        super(clazz);
    }

    public FieldResolver(String className) throws ClassNotFoundException {
        super(className);
    }

    @Override
    public Field resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException {
        return AccessUtil.setAccessible(this.clazz.getDeclaredFields()[index]);
    }

    @Override
    public Field resolveIndexSilent(int index) {
        try {
            return resolveIndex(index);
        } catch (IndexOutOfBoundsException | ReflectiveOperationException e) {}

        return null;
    }

    @Override
    public FieldWrapper resolveIndexWrapper(int index) {
        return new FieldWrapper<>(resolveIndexSilent(index));
    }

    public FieldWrapper resolveWrapper(String... names) {
        return new FieldWrapper(resolveSilent(names));
    }

    public Field resolveSilent(String... names) {
        try {
            return resolve(names);
        } catch (Exception e) {}

        return null;
    }

    public Field resolve(String... names) throws NoSuchFieldException {
        ResolverQuery.Builder builder = ResolverQuery.builder();

        for (String name : names) {
            builder.with(name);
        }

        try {
            return super.resolve(builder.build());
        } catch (ReflectiveOperationException e) {
            throw (NoSuchFieldException) e;
        }
    }

    public Field resolveSilent(ResolverQuery... queries) {
        try {
            return resolve(queries);
        } catch (Exception e) {}

        return null;
    }

    public Field resolve(ResolverQuery... queries) throws NoSuchFieldException {
        try {
            return super.resolve(queries);
        } catch (ReflectiveOperationException e) {
            throw (NoSuchFieldException) e;
        }
    }

    @Override
    protected Field resolveObject(ResolverQuery query) throws ReflectiveOperationException {
        if (query.getTypes() == null || query.getTypes().length == 0) {
            return AccessUtil.setAccessible(clazz.getDeclaredField(query.getName()));
        } else {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(query.getName())) {
                    for (Class type : query.getTypes()) {
                        if (field.getType().equals(type)) {
                            return field;
                        }
                    }
                }
            }
        }

        return null;
    }

    public Field resolveByFirstType(Class<?> type) throws ReflectiveOperationException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(type)) {
                return AccessUtil.setAccessible(field);
            }
        }

        throw new NoSuchFieldException(String.format("Could not resolve field of type '%s' in class %s", type.toString(), clazz));
    }

    public Field resolveByFirstTypeSilent(Class<?> type) {
        try {
            return resolveByFirstType(type);
        } catch (Exception e) {}

        return null;
    }

    public Field resolveByLastType(Class<?> type) throws ReflectiveOperationException {
        Field result = null;

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().equals(type)) {
                result = field;
            }
        }

        if (result == null) {
            throw new NoSuchFieldException(String.format("Could not resolve field of type '%s' in class %s", type.toString(), clazz));
        }

        return AccessUtil.setAccessible(result);
    }

    public Field resolveByLastTypeSilent(Class<?> type) {
        try {
            return resolveByLastType(type);
        } catch (Exception e) {}

        return null;
    }

    @Override
    protected ReflectiveOperationException notFoundException(String joinedNames) {
        return new NoSuchFieldException(String.format("Could not resolve field for %s in class %s", joinedNames, clazz));
    }
}
