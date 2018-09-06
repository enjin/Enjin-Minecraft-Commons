package com.enjin.minecraft_commons.spigot.nbt;

import com.enjin.minecraft_commons.spigot.reflect.BukkitClasses;
import com.enjin.minecraft_commons.spigot.reflect.NMSClasses;
import com.enjin.minecraft_commons.spigot.util.GsonWrapper;
import com.enjin.minecraft_commons.spigot.util.MethodNames;
import com.enjin.minecraft_commons.spigot.util.MinecraftVersion;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.Stack;

public class NBTReflection {

    public static Object createNBTTagCompound() {
        Class clazz = NMSClasses.getNBTTagCompound();
        Object obj;
        try {
            obj = clazz.newInstance();
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object createBlockPosition(int x, int y, int z) {
        Class clazz = NMSClasses.getBlockPosition();
        Object obj;
        try {
            obj = clazz.getConstructor(int.class, int.class, int.class).newInstance(x, y, z);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object createNMSItemStack(ItemStack item) {
        Class clazz = BukkitClasses.getCraftItemStack();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("asNMSCopy", ItemStack.class);
            obj = method.invoke(clazz, item);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object createNMSEntity(Entity entity) {
        Class clazz = BukkitClasses.getCraftEntity();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("getHandle");
            obj = method.invoke(clazz.cast(entity));
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object parseNBT(String json) {
        Class clazz = NMSClasses.getMojangsonParser();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("parse", String.class);
            obj = method.invoke(null, json);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object readNBTFile(FileInputStream stream) {
        Class clazz = NMSClasses.getNBTCompressedStreamTools();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("a", InputStream.class);
            obj = method.invoke(clazz, stream);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object saveNBTFile(Object nbt, FileOutputStream stream) {
        Class clazz = NMSClasses.getNBTCompressedStreamTools();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("a", NMSClasses.getNBTTagCompound(), OutputStream.class);
            obj = method.invoke(clazz, nbt, stream);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static ItemStack getBukkitItemStack(Object item) {
        Class clazz = BukkitClasses.getCraftItemStack();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("asCraftMirror", item.getClass());
            obj = method.invoke(clazz, item);
        } catch (Exception ex) {
            obj = null;
        }
        return ItemStack.class.cast(obj);
    }

    public static Object getItemRootNBTTagCompound(Object item) {
        Class clazz = item.getClass();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("getTag");
            obj = method.invoke(item);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object convertNBTCompoundToNMSItem(NBTCompound compound) {
        Class clazz = NMSClasses.getItemStack();
        Object obj;
        try {
            obj = clazz.getConstructor(NMSClasses.getNBTTagCompound()).newInstance(toCompound(compound.getCompound(), compound));
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static NBTContainer convertNMSItemToNBTCompound(Object item) {
        Class clazz = item.getClass();
        Method method;
        NBTContainer container;
        try {
            method = clazz.getMethod("save", NMSClasses.getNBTTagCompound());
            container = new NBTContainer(method.invoke(item, createNBTTagCompound()));
        } catch (Exception ex) {
            container = null;
        }
        return container;
    }

    public static Object getEntityNBTTagCompound(Object item) {
        Class clazz = item.getClass();
        Method method;
        Object tag;
        Object obj;
        try {
            method = clazz.getMethod(MethodNames.getEntityNbtGetterMethodName(), NMSClasses.getNBTTagCompound());
            tag = createNBTTagCompound();
            obj = method.invoke(item, tag);
            if (obj == null)
                obj = tag;
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object setEntityNBTTag(Object tag, Object entity) {
        Method method;
        Object obj;
        try {
            method = entity.getClass().getMethod(MethodNames.getEntityNbtSetterMethodName(), NMSClasses.getNBTTagCompound());
            method.invoke(entity, tag);
            obj = tag;
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object setNBTTag(Object tag, Object item) {
        Method method;
        Object obj;
        try {
            method = item.getClass().getMethod("setTag", tag.getClass());
            method.invoke(item, tag);
            obj = item;
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static Object getTileEntityNBTTagCompound(BlockState tile) {
        Method method;
        Object pos;
        Object craftWorld;
        Object nmsWorld;
        Object entity;
        Object tag;
        Object obj;
        try {
            pos = createBlockPosition(tile.getX(), tile.getY(), tile.getZ());
            craftWorld = BukkitClasses.getCraftWorld().cast(tile.getWorld());
            nmsWorld = craftWorld.getClass().getMethod("getHandle").invoke(craftWorld);
            entity = nmsWorld.getClass().getMethod("getTileEntity", pos.getClass()).invoke(nmsWorld, pos);
            method = NMSClasses.getTileEntity().getMethod(MethodNames.getTileDataMethodName(), NMSClasses.getNBTTagCompound());
            tag = createNBTTagCompound();
            obj = method.invoke(entity, tag);
            if (obj == null)
                obj = tag;
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static void setTileEntityNBTTagCompound(BlockState tile, Object compound) {
        Method method;
        Object pos;
        Object craftWorld;
        Object nmsWorld;
        Object entity;
        try {
            pos = createBlockPosition(tile.getX(), tile.getY(), tile.getZ());
            craftWorld = BukkitClasses.getCraftWorld().cast(tile.getWorld());
            nmsWorld = craftWorld.getClass().getMethod("getHandle").invoke(craftWorld);
            entity = nmsWorld.getClass().getMethod("getTileEntity", pos.getClass()).invoke(nmsWorld, pos);
            method = NMSClasses.getTileEntity().getMethod("a", NMSClasses.getNBTTagCompound());
            method.invoke(entity, compound);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static Object getChildNBTTagCompound(Object compound, String name) {
        Class clazz = compound.getClass();
        Method method;
        Object obj;
        try {
            method = clazz.getMethod("getCompound", String.class);
            obj = method.invoke(compound, name);
        } catch (Exception ex) {
            obj = null;
        }
        return obj;
    }

    public static void addNBTTagCompound(NBTCompound compound, String name) {
        if (name == null) {
            remove(compound, name);
        } else {
            Object tag = compound.getCompound();
            if (tag == null)
                tag = createNBTTagCompound();
            if (validCompound(compound)) {
                Object comp = toCompound(tag, compound);
                Method method;
                try {
                    method = tag.getClass().getMethod("set", String.class, NMSClasses.getNBTBase());
                    method.invoke(comp, name, createNBTTagCompound());
                    compound.setCompound(tag);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static boolean validCompound(NBTCompound compound) {
        Object obj = compound.getCompound();
        if (obj == null)
            createNBTTagCompound();
        return toCompound(obj, compound) != null;
    }

    public static Object toCompound(Object tag, NBTCompound compound) {
        Stack<String> stack = new Stack<>();
        while (compound.getParent() != null) {
            stack.add(compound.getName());
            compound = compound.getParent();
        }

        while (!stack.isEmpty()) {
            tag = getChildNBTTagCompound(tag, stack.pop());
            if (tag == null)
                break;
        }

        return tag;
    }

    public static void mergeCompound(NBTCompound first, NBTCompound second) {
        Object tag = second.getCompound();
        if (tag == null)
            tag = createNBTTagCompound();
        if (validCompound(first)) {
            Object comp = toCompound(tag, first);
            Method method;
            try {
                method = comp.getClass().getMethod("a", NMSClasses.getNBTTagCompound());
                method.invoke(comp, second.getCompound());
                first.setCompound(tag);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void setValue(NBTCompound compound, String key, Object value, String methodName, Class type) {
        if (value == null) {
            remove(compound, key);
        } else {
            Object tag = compound.getCompound();
            if (tag == null)
                tag = createNBTTagCompound();
            if (validCompound(compound)) {
                Object comp = toCompound(tag, compound);
                Method method;
                try {
                    method = comp.getClass().getMethod(methodName, String.class, type);
                    method.invoke(comp, key, value);
                    compound.setCompound(tag);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static <T> T getValue(NBTCompound compound, String key, String methodName, Class<T> type) {
        T result = null;
        Object tag = compound.getCompound();
        if (tag == null)
            tag = createNBTTagCompound();
        if (validCompound(compound)) {
            Object comp = toCompound(tag, compound);
            Method method;
            try {
                method = comp.getClass().getMethod(methodName, String.class);
                result = type.cast(method.invoke(comp, key));
            } catch (Exception ex) {
                result = null;
            }
        }
        return result;
    }

    public static void set(NBTCompound compound, String key, Object value) throws Throwable {
        if (validCompound(compound))
            setValue(compound, key, value, "set", NMSClasses.getNBTBase());
        else
            throw new Throwable("The provided compound is not valid.");
    }

    public static void setString(NBTCompound compound, String key, String value) {
        setValue(compound, key, value, "setString", String.class);
    }

    public static String getString(NBTCompound compound, String key) {
        return getValue(compound, key, "getString", String.class);
    }

    public static String getContent(NBTCompound compound, String key) {
        return getValue(compound, key, "get", String.class);
    }

    public static void setByte(NBTCompound compound, String key, Byte value) {
        setValue(compound, key, value, "setByte", byte.class);
    }

    public static Byte getByte(NBTCompound compound, String key) {
        return getValue(compound, key, "getByte", byte.class);
    }

    public static void setShort(NBTCompound compound, String key, Short value) {
        setValue(compound, key, value, "setShort", int.class);
    }

    public static Short getShort(NBTCompound compound, String key) {
        return getValue(compound, key, "getShort", short.class);
    }

    public static void setInteger(NBTCompound compound, String key, Integer value) {
        setValue(compound, key, value, "setInt", int.class);
    }

    public static Integer getInteger(NBTCompound compound, String key) {
        return getValue(compound, key, "getInt", int.class);
    }

    public static void setLong(NBTCompound compound, String key, Long value) {
        setValue(compound, key, value, "setLong", long.class);
    }

    public static Long getLong(NBTCompound compound, String key) {
        return getValue(compound, key, "getLong", long.class);
    }

    public static void setFloat(NBTCompound compound, String key, Float value) {
        setValue(compound, key, value, "setFloat", float.class);
    }

    public static Float getFloat(NBTCompound compound, String key) {
        return getValue(compound, key, "getFloat", float.class);
    }

    public static void setDouble(NBTCompound compound, String key, Double value) {
        setValue(compound, key, value, "setDouble", double.class);
    }

    public static Double getDouble(NBTCompound compound, String key) {
        return getValue(compound, key, "getDouble", double.class);
    }

    public static void setBoolean(NBTCompound compound, String key, Boolean value) {
        setValue(compound, key, value, "setBoolean", boolean.class);
    }

    public static Boolean getBoolean(NBTCompound compound, String key) {
        return getValue(compound, key, "getBoolean", boolean.class);
    }

    public static void setByteArray(NBTCompound compound, String key, Byte[] value) {
        setValue(compound, key, value, "setByteArray", byte[].class);
    }

    public static Byte[] getByteArray(NBTCompound compound, String key) {
        return ArrayUtils.toObject(getValue(compound, key, "getByteArray", byte[].class));
    }

    public static void setIntegerArray(NBTCompound compound, String key, Integer[] value) {
        setValue(compound, key, value, "setIntArray", int[].class);
    }

    public static Integer[] getIntegerArray(NBTCompound compound, String key) {
        return ArrayUtils.toObject(getValue(compound, key, "getIntArray", int[].class));
    }

    public static void setObject(NBTCompound compound, String key, Object value) {
        if (MinecraftVersion.isGsonEnabled()) {
            try {
                setString(compound, key, GsonWrapper.toJsonString(value));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static <T> T getObject(NBTCompound compound, String key, Class<T> type) {
        T result = null;
        if (MinecraftVersion.isGsonEnabled()) {
            String json = getString(compound, key);
            if (json != null)
                result = GsonWrapper.deserializeJson(json, type);
        }
        return result;
    }

    public static NBTList getList(NBTCompound compound, String key, NBTType type) {
        NBTList result = null;
        Object tag = compound.getCompound();
        if (tag == null)
            tag = createNBTTagCompound();
        if (validCompound(compound)) {
            Object comp = toCompound(tag, compound);
            Method method;
            try {
                method = comp.getClass().getMethod("getList", String.class, int.class);
                result = new NBTList(compound, key, type, method.invoke(comp, key, type.getId()));
            } catch (Exception ex) {
                result = null;
            }
        }
        return result;
    }

    public static boolean hasKey(NBTCompound compound, String key) {
        boolean result = false;
        Object tag = compound.getCompound();
        if (tag == null)
            tag = createNBTTagCompound();
        if (validCompound(compound)) {
            Object comp = toCompound(tag, compound);
            Method method;
            try {
                method = comp.getClass().getMethod("hasKey", String.class);
                result = (boolean) method.invoke(comp, key);
            } catch (Exception ex) {
                result = false;
            }
        }
        return result;
    }

    public static void remove(NBTCompound compound, String key) {
        Object tag = compound.getCompound();
        if (tag == null)
            tag = createNBTTagCompound();
        if (validCompound(compound)) {
            Object comp = toCompound(tag, compound);
            Method method;
            try {
                method = comp.getClass().getMethod("remove", String.class);
                method.invoke(comp, key);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Set<String> getKeys(NBTCompound compound) {
        Set<String> result = null;
        Object tag = compound.getCompound();
        if (tag == null)
            tag = createNBTTagCompound();
        if (validCompound(compound)) {
            Object comp = toCompound(tag, compound);
            Method method;
            try {
                method = comp.getClass().getMethod("c");
                result = (Set<String>) method.invoke(comp);
            } catch (Exception ex) {
                result = null;
            }
        }
        return result;
    }

    public static byte getType(NBTCompound compound, String key) {
        return getValue(compound, key, MethodNames.getTypeMethodName(), byte.class);
    }
}
