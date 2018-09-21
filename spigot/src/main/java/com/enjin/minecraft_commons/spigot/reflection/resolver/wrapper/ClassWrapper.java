package com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper;

import java.util.Objects;

public class ClassWrapper<R> extends WrapperAbstract {

    private final Class<R> clazz;

    public ClassWrapper(Class<R> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean exists() {
        return clazz != null;
    }

    public Class<R> getClazz() {
        return clazz;
    }

    public String getName() {
        return clazz.getName();
    }

    public R newInstance() {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public R newInstanceSilent() {
        try {
            return clazz.newInstance();
        } catch (Exception e) {}

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClassWrapper<?> that = (ClassWrapper<?>) o;

        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }
}
