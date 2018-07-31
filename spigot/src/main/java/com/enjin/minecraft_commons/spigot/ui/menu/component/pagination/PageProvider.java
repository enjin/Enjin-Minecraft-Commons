package com.enjin.minecraft_commons.spigot.ui.menu.component.pagination;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PageProvider {

    ItemStack[][] getPage(Player player, int page);

    int getPageCount(Player player);

}
