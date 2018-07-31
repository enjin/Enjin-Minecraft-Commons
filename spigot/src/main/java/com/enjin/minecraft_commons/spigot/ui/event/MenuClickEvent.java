package com.enjin.minecraft_commons.spigot.ui.event;

import com.enjin.minecraft_commons.spigot.ui.AbstractMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;

public class MenuClickEvent extends MenuEvent implements Cancellable {
    private boolean cancelled;

    private final int x;
    private final int y;
    private final ClickType clickType;

    public MenuClickEvent(AbstractMenu menu, Player player, int x, int y, ClickType clickType) {
        super(menu, player);
        this.x = x;
        this.y = y;
        this.clickType = clickType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
}
