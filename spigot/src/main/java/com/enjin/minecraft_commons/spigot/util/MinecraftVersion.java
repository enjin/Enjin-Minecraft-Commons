package com.enjin.minecraft_commons.spigot.util;

import org.bukkit.Bukkit;

public enum MinecraftVersion {
    UNKNOWN(0),
    MC1_7_R4(174),
    MC1_8_R3(183),
    MC1_9_R2(192),
    MC1_10_R1(1101),
    MC1_11_R1(1111),
    MC1_12_R1(1121),
    MC1_13_R1(1131),
    MC1_13_R2(1132);

    private static MinecraftVersion version;
    private static Boolean gsonEnabled;

    private final int identifier;
    private final String rawPackage;

    MinecraftVersion(int identifier) {
        this.identifier = identifier;
        this.rawPackage = this.name().replace("MC", "v");
    }

    public static MinecraftVersion findVersion(String identifier) {
        MinecraftVersion version = UNKNOWN;
        for (MinecraftVersion v : values()) {
            if (v.name().equalsIgnoreCase(identifier))
                version = v;
        }
        return version;
    }

    public static MinecraftVersion getVersion() {
        if (version == null) {
            final String ver = Bukkit.getServer().getClass().getPackage().getName()
                    .replace(".", ", ")
                    .replace("v", "MC")
                    .split(",")[3].trim();

            version = findVersion(ver);
        }

        return version;
    }

    public static boolean isGsonEnabled() {
        if (gsonEnabled != null) {
            try {
                gsonEnabled = Class.forName("com.google.gson.Gson") != null;
            } catch (ClassNotFoundException ex) {
                gsonEnabled = false;
            }
        }

        return gsonEnabled;
    }

    public int getIdentifier() {
        return identifier;
    }

    public String getRawPackage() {
        return rawPackage;
    }
}
