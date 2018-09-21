package com.enjin.minecraft_commons.spigot.reflection.resolver;

import com.enjin.minecraft_commons.spigot.reflection.resolver.wrapper.WrapperAbstract;

import java.lang.reflect.Member;

public abstract class MemberResolver<T extends Member> extends ResolverAbstract<T> {

    protected Class<?> clazz;

    public MemberResolver(Class<?> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("class cannot be null");
        }

        this.clazz = clazz;
    }

    public MemberResolver(String className) throws ClassNotFoundException {
        this(new ClassResolver().resolve(className));
    }

    public abstract T resolveIndex(int index) throws IndexOutOfBoundsException, ReflectiveOperationException;

    public abstract T resolveIndexSilent(int index);

    public abstract WrapperAbstract resolveIndexWrapper(int index);

}
