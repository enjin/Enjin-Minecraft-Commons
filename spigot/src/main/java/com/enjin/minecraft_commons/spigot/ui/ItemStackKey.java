package com.enjin.minecraft_commons.spigot.ui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public class ItemStackKey {

    private final Object[] keys;

    private ItemStackKey(ItemStack stack) {
        if (!stack.hasItemMeta()) {
            this.keys = new Object[]{stack.getType(), stack.getDurability()};
        } else {
            ItemMeta meta = stack.getItemMeta();
            this.keys = new Object[]{stack.getType(), stack.getDurability(), meta.getDisplayName()};
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.keys);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemStackKey && Objects.deepEquals(this.keys, ((ItemStackKey) obj).keys);
    }

    @Override
    public String toString() {
        if (keys.length == 2) {
            return String.format("%s:%d", keys);
        } else {
            return String.format("%s:%d - %s", keys);
        }
    }

    public static ItemStackKey of(ItemStack stack) {
        return new ItemStackKey(stack);
    }
}
