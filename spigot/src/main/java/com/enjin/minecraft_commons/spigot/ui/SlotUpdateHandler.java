package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface SlotUpdateHandler {

    void handle(Player player, int rawSlot, ItemStack oldItem, ItemStack newItem);

}
