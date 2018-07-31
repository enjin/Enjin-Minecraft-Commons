package com.enjin.minecraft_commons.spigot.ui.event;

import com.enjin.minecraft_commons.spigot.ui.AbstractMenu;
import org.bukkit.entity.Player;

public class MenuDrawEvent extends MenuEvent {
    public MenuDrawEvent(AbstractMenu menu, Player player) {
        super(menu, player);
    }
}
