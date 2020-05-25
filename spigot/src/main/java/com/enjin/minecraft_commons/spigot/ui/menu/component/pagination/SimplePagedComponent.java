package com.enjin.minecraft_commons.spigot.ui.menu.component.pagination;

import com.enjin.minecraft_commons.spigot.ui.Dimension;
import com.enjin.minecraft_commons.spigot.ui.Position;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SimplePagedComponent extends PagedComponent {

    private final List<ItemStack[][]> pages;

    public SimplePagedComponent(Dimension dimension) {
        super(dimension);
        this.pages = Lists.newArrayList();
    }

    public void clear() {
        this.pages.clear();
    }

    public void setItem(int page, int x, int y, ItemStack stack) {
        this.getPage(page)[y][x] = stack;
    }

    public void setItem(int page, Position position, ItemStack stack) {
        this.setItem(page, position.getX(), position.getY(), stack);
    }

    public ItemStack getItem(int page, int x, int y) {
        if (page >= this.getPageCount())
            return null;

        return this.pages.get(page)[y][x];
    }

    public ItemStack getItem(int page, Position position) {
        return this.getItem(page, position.getX(), position.getY());
    }

    @Override
    public ItemStack[][] getPage(Player player, int page) {
        return this.getPage(page);
    }

    public ItemStack[][] getPage(int page) {
        while (page >= this.getPageCount()) {
            this.pages.add(new ItemStack[getDimension().getHeight()][getDimension().getWidth()]);
        }
        return this.pages.get(page);
    }

    @Override
    public int getPageCount(Player player) {
        return this.getPageCount();
    }

    public int getPageCount() {
        return Math.max(this.pages.size(), 0);
    }
}
