package com.enjin.minecraft_commons.spigot.ui.menu;

import com.enjin.minecraft_commons.spigot.ui.Dimension;
import com.enjin.minecraft_commons.spigot.ui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ChestMenu extends Menu {

    public ChestMenu(String name, int rows) {
        super(name, new Dimension(9, rows));
    }

    @Override
    protected Inventory createInventory(Player player) {
        return Bukkit.createInventory(null, getSize(), getName(player));
    }

}
