package com.enjin.minecraft_commons.spigot.ui.event;

import com.enjin.minecraft_commons.spigot.ui.AbstractMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class MenuEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private final AbstractMenu menu;
    private final Player player;

    protected MenuEvent(AbstractMenu menu, Player player) {
        this.menu = menu;
        this.player = player;
    }

    public final AbstractMenu getMenu() {
        return menu;
    }

    public final Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
