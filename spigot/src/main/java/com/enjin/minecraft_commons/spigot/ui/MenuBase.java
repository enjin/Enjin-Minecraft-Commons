package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.entity.Player;

public abstract class MenuBase {

    private final int maxItems;
    protected MenuItem[] items;
    protected boolean exitOnClickOutside = true;
    protected MenuCloseBehavior menuCloseBehavior;

    protected MenuBase(int maxItems) {
        this.maxItems = maxItems;
        this.items = new MenuItem[maxItems];
    }

    public int getMaxItems() {
        return maxItems;
    }

    public boolean isExitOnClickOutside() {
        return exitOnClickOutside;
    }

    public void setExitOnClickOutside(boolean exitOnClickOutside) {
        this.exitOnClickOutside = exitOnClickOutside;
    }

    public MenuCloseBehavior getMenuCloseBehavior() {
        return menuCloseBehavior;
    }

    public void setMenuCloseBehavior(MenuCloseBehavior menuCloseBehaviour) {
        this.menuCloseBehavior = menuCloseBehaviour;
    }

    public abstract void openMenu(Player player);

    public abstract void closeMenu(Player player);

    public void switchMenu(MenuAPI api, Player player, MenuBase menu) {
        api.switchMenu(player, this, menu);
    }
}
