package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface ClickHandler {
    boolean handle(Player player, ClickType type, Position position);
}
