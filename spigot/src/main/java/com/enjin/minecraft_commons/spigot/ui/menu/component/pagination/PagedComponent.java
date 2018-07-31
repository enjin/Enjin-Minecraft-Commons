package com.enjin.minecraft_commons.spigot.ui.menu.component.pagination;

import com.enjin.minecraft_commons.spigot.ui.Dimension;
import com.enjin.minecraft_commons.spigot.ui.Position;
import com.enjin.minecraft_commons.spigot.ui.menu.component.MenuComponent;
import com.enjin.minecraft_commons.spigot.ui.menu.component.pagination.PageHandler;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class PagedComponent extends MenuComponent {

    private final Map<Player, Integer> pageByPlayer;

    private PageHandler pageHandler;

    public PagedComponent(Dimension dimension) {
        super(dimension);
        this.pageByPlayer = createPlayerMap(Maps::<Player, Integer>newHashMap);
    }

    public boolean nextPage(Player player) {
        int next = (this.pageByPlayer.getOrDefault(player, 0) + 1) % this.getPageCount(player);
        if (next == 0) {
            return false;
        }
        showPage(player, next);
        return next < this.getPageCount(player) - 1;
    }

    public boolean previousPage(Player player) {
        int prev = (this.pageByPlayer.getOrDefault(player, 1) - 1) % this.getPageCount(player);
        if (prev < 0) {
            return false;
        }
        showPage(player, prev);
        return prev > 0;
    }

    public int getPageNumber(Player player) {
        return this.pageByPlayer.getOrDefault(player, 0);
    }

    public abstract ItemStack[][] getPage(Player player, int page);

    public abstract int getPageCount(Player player);

    public void showPage(Player player, int page) {
        page = Math.min(page, getPageCount(player));
        this.pageByPlayer.put(player, page);
        if (!this.pageHandler.handle(player, page)) {
            return;
        }
        draw(player);
        player.updateInventory();
    }

    public void onPage(PageHandler handler) {
        if (this.pageHandler == null) {
            this.pageHandler = handler;
        } else {
            this.pageHandler = this.pageHandler.andThen(handler);
        }
    }

    @Override
    public void draw(Player player) {
        ItemStack[][] pageData = this.getPage(player, this.pageByPlayer.getOrDefault(player, 0));
        for (int y = 0; y < pageData.length; y++) {
            for (int x = 0; x < pageData[y].length; x++) {
                Position pos = Position.of(x, y);
                setItem(player, pos, pageData[y][x]);
            }
        }
    }
}
