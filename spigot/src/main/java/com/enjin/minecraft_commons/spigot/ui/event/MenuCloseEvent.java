package com.enjin.minecraft_commons.spigot.ui.event;

import com.enjin.minecraft_commons.spigot.ui.AbstractMenu;
import org.bukkit.entity.Player;

public class MenuCloseEvent extends MenuEvent {
    public MenuCloseEvent(AbstractMenu menu, Player player) {
        super(menu, player);
    }
}
