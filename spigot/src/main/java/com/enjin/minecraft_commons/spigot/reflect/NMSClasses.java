package com.enjin.minecraft_commons.spigot.reflect;

public class NMSClasses {

    private static Class _nbtBase;
    private static Class _nbtTagString;
    private static Class _nbtTagCompound;
    private static Class _nbtCompressedStreamTools;
    private static Class _mojangsonParser;
    private static Class _itemStack;
    private static Class _tileEntity;
    private static Class _blockPosition;

    public static Class getNBTBase() {
        if (_nbtBase == null) {
            _nbtBase = MinecraftReflection.getNMSClassSafely("NBTBase");
        }
        return _nbtBase;
    }

    public static Class getNBTTagString() {
        if (_nbtTagString == null) {
            _nbtTagString = MinecraftReflection.getNMSClassSafely("NBTTagString");
        }
        return _nbtTagString;
    }

    public static Class getNBTTagCompound() {
        if (_nbtTagCompound == null) {
            _nbtTagCompound = MinecraftReflection.getNMSClassSafely("NBTTagCompound");
        }
        return _nbtTagCompound;
    }

    public static Class getNBTCompressedStreamTools() {
        if (_nbtCompressedStreamTools == null) {
            _nbtCompressedStreamTools = MinecraftReflection.getNMSClassSafely("NBTCompressedStreamTools");
        }
        return _nbtCompressedStreamTools;
    }

    public static Class getMojangsonParser() {
        if (_mojangsonParser == null) {
            _mojangsonParser = MinecraftReflection.getNMSClassSafely("MojangsonParser");
        }
        return _mojangsonParser;
    }

    public static Class getItemStack() {
        if (_itemStack == null) {
            _itemStack = MinecraftReflection.getNMSClassSafely("ItemStack");
        }
        return _itemStack;
    }

    public static Class getTileEntity() {
        if (_tileEntity == null) {
            _tileEntity = MinecraftReflection.getNMSClassSafely("TileEntity");
        }
        return _tileEntity;
    }

    public static Class getBlockPosition() {
        if (_blockPosition == null) {
            _blockPosition = MinecraftReflection.getNMSClassSafely("BlockPosition");
        }
        return _blockPosition;
    }

}
