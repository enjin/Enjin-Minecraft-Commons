package com.enjin.minecraft_commons.core.command;

public interface CommandCallerType<T> {

    Class<? extends T> getRawType();

    boolean compare(T caller);

}
