package com.enjin.minecraft_commons.spigot.ui.menu.component.pagination;

import org.bukkit.entity.Player;

public interface PageHandler {

    boolean handle(Player player, int page);

    default PageHandler andThen(PageHandler handler) {
        return (player, page) -> {
            boolean first = this.handle(player, page);
            boolean second = handler.handle(player, page);
            return first && second;
        };
    }

}
