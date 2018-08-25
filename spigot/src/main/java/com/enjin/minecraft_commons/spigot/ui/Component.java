package com.enjin.minecraft_commons.spigot.ui;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface Component {

    Dimension getDimension();

    Container getParent();

    void setParent(Container container);

    void draw(Player player);

    default void setItem(Player target, Position position, ItemStack stack) {
        Preconditions.checkArgument(position.getX() >= 0 && position.getX() < getDimension().getWidth(),
                "X has to be [0 - width)");
        Preconditions.checkArgument(position.getY() >= 0 && position.getY() < getDimension().getHeight(),
                "Y has to be [0 - height)");
        getParent().setItem(target, this, position, stack);
    }

    default void onOpen(Player player) {
    }

    default void onClose(Player player) {
    }

    default void onClick(Player player, ClickType clickType, Position position) {
    }

    default void onPlace(Player player, ItemStack current, ItemStack cursor, Position position) {
    }

    default void onDrag(Player player, ItemStack current, ItemStack cursor, Position position) {
    }

    default void onPickup(Player player, ItemStack current, ItemStack cursor, Position position) {
    }

    boolean isAllowPlace();

    boolean isAllowDrag();

    boolean isAllowPickup();

    void setAllowPlace(boolean allowPlace);

    void setAllowDrag(boolean allowDrag);

    void setAllowPickup(boolean allowPickup);

    Optional<SlotUpdateHandler> getSlotUpdateHandler();

    Optional<ClickHandler> getClickHandler();

}
