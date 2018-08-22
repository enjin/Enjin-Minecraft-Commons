package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public interface Container {

    Dimension getDimension();

    String getName(Player player);

    boolean addComponent(Position position, Component component);

    boolean removeComponent(Component component);

    void setItem(Player player, Component component, Position position, ItemStack stack);

    Optional<ItemStack> getItem(Player player, Component component, Position position);

    void update(Player player);

    void updateAll();

    void destroy();
}
