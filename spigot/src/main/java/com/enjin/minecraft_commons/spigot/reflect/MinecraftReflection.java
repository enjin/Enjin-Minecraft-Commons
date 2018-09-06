package com.enjin.minecraft_commons.spigot.reflect;

import org.bukkit.Bukkit;

public class MinecraftReflection {

    private static String _nmsPackage;
    private static String _bukkitPackage;

    static {
        String packageVersion = getPackageVersion();

        _nmsPackage = String.format("net.minecraft.server.%s", packageVersion);
        _bukkitPackage = String.format("org.bukkit.craftbukkit.%s", packageVersion);
    }

    public static String getPackageVersion() {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static Class getNMSClass(String target) throws ClassNotFoundException {
        return Class.forName(new StringBuilder(_nmsPackage).append('.')
                .append(target)
                .toString());
    }

    public static Class getCraftClass(String target) throws ClassNotFoundException {
        return Class.forName(new StringBuilder(_bukkitPackage).append('.')
                .append(target)
                .toString());
    }

    public static Class getNMSClassSafely(String target) {
        Class clazz;
        try {
            clazz = getNMSClass(target);
        } catch (ClassNotFoundException ex) {
            clazz = null;
            ex.printStackTrace();
        }
        return clazz;
    }

    public static Class getCraftClassSafely(String target) {
        Class clazz;
        try {
            clazz = getCraftClass(target);
        } catch (ClassNotFoundException ex) {
            clazz = null;
            ex.printStackTrace();
        }
        return clazz;
    }

}
