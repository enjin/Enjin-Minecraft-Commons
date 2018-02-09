package com.enjin.minecraft_commons.spigot.command;

import com.enjin.java_commons.ObjectUtils;
import com.enjin.minecraft_commons.core.command.CommandCallerType;
import org.bukkit.command.CommandSender;

public class SpigotCommandCallerType implements CommandCallerType<CommandSender> {

    private Class<? extends CommandSender> rawType;

    public SpigotCommandCallerType(Class<? extends CommandSender> rawType) {
        if (rawType == null)
            throw new NullPointerException("The raw type of a CommandCallerType is null.");
        this.rawType = rawType;
    }

    @Override
    public Class<? extends CommandSender> getRawType() {
        return this.rawType;
    }

    @Override
    public boolean compare(CommandSender caller) {
        return !ObjectUtils.isNull(caller) && this.rawType.isAssignableFrom(caller.getClass());
    }

}
