package com.enjin.minecraft_commons.spigot.reflect;

public class BukkitClasses {

    private static Class _craftItemStack;
    private static Class _craftEntity;
    private static Class _craftWorld;

    public static Class getCraftItemStack() {
        if (_craftItemStack == null) {
            _craftItemStack = MinecraftReflection.getCraftClassSafely("inventory.CraftItemStack");
        }
        return _craftItemStack;
    }

    public static Class getCraftEntity() {
        if (_craftEntity == null) {
            _craftEntity = MinecraftReflection.getCraftClassSafely("entity.CraftEntity");
        }
        return _craftEntity;
    }

    public static Class getCraftWorld() {
        if (_craftWorld == null) {
            _craftWorld = MinecraftReflection.getCraftClassSafely("CraftWorld");
        }
        return _craftWorld;
    }

}
