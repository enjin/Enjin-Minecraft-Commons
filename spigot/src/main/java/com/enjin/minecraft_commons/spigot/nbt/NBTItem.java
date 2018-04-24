package com.enjin.minecraft_commons.spigot.nbt;

import org.bukkit.inventory.ItemStack;

public class NBTItem extends NBTCompound {

    private ItemStack stack;

    public NBTItem(ItemStack stack) {
        super(null, null);
        this.stack = stack.clone();
    }

    @Override
    public Object getCompound() {
        Object nmsItemStack = NBTReflection.createNMSItemStack(this.stack);
        return NBTReflection.getItemRootNBTTagCompound(nmsItemStack);
    }

    public void setCompound(Object compound) {
        this.stack = NBTReflection.getBukkitItemStack(
                NBTReflection.setEntityNBTTag(compound, NBTReflection.createNMSItemStack(this.stack)));
    }

    public ItemStack getItemStack() {
        return this.stack;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
    }

    public static NBTContainer convertItemToNBT(ItemStack stack) {
        return NBTReflection.convertNMSItemToNBTCompound(NBTReflection.createNMSItemStack(stack));
    }

    public static ItemStack convertNBTToItem(NBTCompound compound) {
        return NBTReflection.getBukkitItemStack(NBTReflection.convertNBTCompoundToNMSItem(compound));
    }

}
