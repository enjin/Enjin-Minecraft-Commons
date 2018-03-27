package com.enjin.minecraft_commons.spigot.nbt;

import com.enjin.minecraft_commons.spigot.util.MethodNames;

import java.lang.reflect.Method;

public class NBTList {

    private String name;
    private NBTCompound parent;
    private NBTType type;
    private Object list;

    protected NBTList(NBTCompound parent, String name, NBTType type, Object list) {
        this.name = name;
        this.parent = parent;
        this.type = type;
        this.list = list;
        if (!(type == NBTType.NBTTagString || type == NBTType.NBTTagCompound))
            throw new UnsupportedOperationException(String.format("%s not supported by NBTList", type.name()));
    }

    protected void save() {
        parent.set(name, list);
    }

    public NBTListCompound addCompound() {
        if (type != NBTType.NBTTagCompound)
            throw new IllegalStateException("This list does not support adding compounds");

        NBTListCompound obj;

        try {
            Method method = list.getClass().getMethod("add", NBTReflection.getNBTBase());
            Object compound = NBTReflection.getNBTTagCompound().newInstance();
            method.invoke(list, compound);
            obj = new NBTListCompound(this, compound);
        } catch (Exception ex) {
            obj = null;
        }

        return obj;
    }

    public NBTListCompound getCompound(int index) {
        if (type != NBTType.NBTTagCompound)
            throw new IllegalStateException("This list does not support getting compounds");

        NBTListCompound obj;

        try {
            Method method = list.getClass().getMethod("get", int.class);
            Object compound = method.invoke(list, index);
            obj = new NBTListCompound(this, compound);
        } catch (Exception ex) {
            obj = null;
        }

        return obj;
    }

    public void addString(String s) {
        if (type != NBTType.NBTTagCompound)
            throw new IllegalStateException("This list does not support adding strings");

        try {
            Method method = list.getClass().getMethod("add", NBTReflection.getNBTBase());
            method.invoke(list, NBTReflection.getNBTTagString().getConstructor(String.class).newInstance(s));
            save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setString(int index, String s) {
        if (type != NBTType.NBTTagCompound)
            throw new IllegalStateException("This list does not support setting strings");

        try {
            Method method = list.getClass().getMethod("a", int.class, NBTReflection.getNBTBase());
            method.invoke(list, index, NBTReflection.getNBTTagString().getConstructor(String.class).newInstance(s));
            save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getString(int index) {
        if (type != NBTType.NBTTagCompound)
            throw new IllegalStateException("This list does not support getting strings");

        String obj;

        try {
            Method method = list.getClass().getMethod("getString", int.class);
            obj = (String) method.invoke(list, index);
        } catch (Exception ex) {
            obj = null;
        }

        return obj;
    }

    public void remove(int index) {
        try {
            Method method = list.getClass().getMethod(MethodNames.getRemoveMethodName(), int.class);
            method.invoke(list, index);
            save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int size() {
        int size;
        try {
            Method method = list.getClass().getMethod("size");
            size = (int) method.invoke(list);
        } catch (Exception ex) {
            size = -1;
        }
        return size;
    }

    public NBTType getType() {
        return type;
    }
}
