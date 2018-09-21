package com.enjin.minecraft_commons.spigot.reflection.resolver;

import com.enjin.minecraft_commons.spigot.reflection.Minecraft;

public class OBCClassResolver extends ClassResolver {

    @Override
    public Class resolve(String... names) throws ClassNotFoundException {
        for (int i = 0; i < names.length; i++) {
            if (!names[i].startsWith("org.bukkit.craftbukkit")) {
                names[i] = "org.bukkit.craftbukkit." + Minecraft.getVersion() + names[i];
            }
        }

        return super.resolve(names);
    }
}
