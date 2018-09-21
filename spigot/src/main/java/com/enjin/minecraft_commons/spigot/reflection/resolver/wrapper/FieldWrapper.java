package com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper;


import java.lang.reflect.Field;
import java.util.Objects;

public class FieldWrapper<R> extends WrapperAbstract {

    private final Field field;

    public FieldWrapper(Field field) {
        this.field = field;
    }

    @Override
    public boolean exists() {
        return field != null;
    }

    public String getName() {
        return field.getName();
    }

    public R get(Object object) {
        try {
            return (R) field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public R getSilent(Object object) {
        try {
            return (R) field.get(object);
        } catch (Exception e) {}

        return null;
    }

    public void set(Object object, R value) {
        try {
            field.set(object, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSilent(Object object, R value) {
        try {
            field.set(object, value);
        } catch (Exception e) {}
    }

    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldWrapper<?> that = (FieldWrapper<?>) o;

        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
