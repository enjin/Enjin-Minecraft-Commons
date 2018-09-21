package com.enjin.minecraft_commons.spigot.wrapper;

public interface ConstructorPopulator {

    Class<?>[] types();

    Object[] values();

}
