package com.enjin.minecraft_commons.spigot.ui.menu.component.pagination;

import com.enjin.minecraft_commons.spigot.ui.Dimension;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DynamicPagedComponent extends PagedComponent {

    private final PageProvider provider;

    public DynamicPagedComponent(Dimension dimension, PageProvider provider) {
        super(dimension);
        this.provider = provider;
    }

    @Override
    public ItemStack[][] getPage(Player player, int page) {
        return this.provider.getPage(player, page);
    }

    @Override
    public int getPageCount(Player player) {
        return Math.max(this.provider.getPageCount(player), 1);
    }
}
