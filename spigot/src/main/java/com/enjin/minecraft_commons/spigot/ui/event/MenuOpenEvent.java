package com.enjin.minecraft_commons.spigot.ui.event;

import com.enjin.minecraft_commons.spigot.ui.AbstractMenu;
import org.bukkit.entity.Player;

public class MenuOpenEvent extends MenuEvent {
    public MenuOpenEvent(AbstractMenu menu, Player player) {
        super(menu, player);
    }
}
