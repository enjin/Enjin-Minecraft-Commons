package com.enjin.minecraft_commons.spigot.nbt;

import com.enjin.minecraft_commons.spigot.util.MinecraftVersion;

import java.util.Set;

public class NBTCompound {

    private String name;
    private NBTCompound parent;

    protected NBTCompound(NBTCompound parent, String name) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public NBTCompound getParent() {
        return parent;
    }

    public Object getCompound() {
        return parent.getCompound();
    }

    public void setCompound(Object compound) {
        parent.setCompound(compound);
    }

    public void mergeCompound(NBTCompound compound) {
        NBTReflection.mergeCompound(this, compound);
    }

    public void set(String key, Object val) {
        try {
            NBTReflection.set(this, key, val);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public void setString(String key, String value) {
        NBTReflection.setString(this, key, value);
    }

    public String getString(String key) {
        return NBTReflection.getString(this, key);
    }

    public String getContent(String key) {
        return NBTReflection.getContent(this, key);
    }

    public void setByte(String key, Byte value) {
        NBTReflection.setByte(this, key, value);
    }

    public Byte getByte(String key) {
        return NBTReflection.getByte(this, key);
    }

    public void setShort(String key, Short value) {
        NBTReflection.setShort(this, key, value);
    }

    public Short getShort(String key) {
        return NBTReflection.getShort(this, key);
    }

    public void setInteger(String key, Integer value) {
        NBTReflection.setInteger(this, key, value);
    }

    public Integer getInteger(String key) {
        return NBTReflection.getInteger(this, key);
    }

    public void setLong(String key, Long value) {
        NBTReflection.setLong(this, key, value);
    }

    public Long getLong(String key) {
        return NBTReflection.getLong(this, key);
    }

    public void setFloat(String key, Float value) {
        NBTReflection.setFloat(this, key, value);
    }

    public Float getFloat(String key) {
        return NBTReflection.getFloat(this, key);
    }

    public void setDouble(String key, Double value) {
        NBTReflection.setDouble(this, key, value);
    }

    public Double getDouble(String key) {
        return NBTReflection.getDouble(this, key);
    }

    public void setBoolean(String key, Boolean value) {
        NBTReflection.setBoolean(this, key, value);
    }

    public Boolean getBoolean(String key) {
        return NBTReflection.getBoolean(this, key);
    }

    public void setByteArray(String key, Byte[] value) {
        NBTReflection.setByteArray(this, key, value);
    }

    public Byte[] getByteArray(String key) {
        return NBTReflection.getByteArray(this, key);
    }

    public void setIntegerArray(String key, Integer[] value) {
        NBTReflection.setIntegerArray(this, key, value);
    }

    public Integer[] getIntegerArray(String key) {
        return NBTReflection.getIntegerArray(this, key);
    }

    public void setObject(String key, Object value) {
        NBTReflection.setObject(this, key, value);
    }

    public <T> T getObject(String key, Class<T> type) {
        return NBTReflection.getObject(this, key, type);
    }

    public boolean hasKey(String key) {
        return NBTReflection.hasKey(this, key);
    }

    public void removeKey(String key) {
        NBTReflection.remove(this, key);
    }

    public Set<String> getKeys() {
        return NBTReflection.getKeys(this);
    }

    public NBTCompound addCompound(String name) {
        NBTReflection.addNBTTagCompound(this, name);
        return getCompound(name);
    }

    public NBTCompound getCompound(String name) {
        NBTCompound next = new NBTCompound(this, name);
        return NBTReflection.validCompound(next) ? next : null;
    }

    public NBTList getList(String name, NBTType type) {
        return NBTReflection.getList(this, name, type);
    }

    public NBTType getType(String name) {
        return MinecraftVersion.getVersion() == MinecraftVersion.MC1_7_R4
                ? NBTType.NBTTagEnd
                : NBTType.valueOf(NBTReflection.getType(this, name));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (String key : getKeys())
            builder.append(toString(key));

        return builder.toString();
    }

    public String toString(String key) {
        StringBuilder builder = new StringBuilder();
        NBTCompound compound = this;

        while (compound.getParent() != null) {
            builder.append("    ");
            compound = compound.getParent();
        }

        if (this.getType(key) == NBTType.NBTTagCompound)
            return this.getCompound(key).toString();
        else
            return builder.append('-')
                    .append(key)
                    .append(": ")
                    .append(getContent(key))
                    .append(System.lineSeparator())
                    .toString();
    }

    public String asNBTString() {
        return NBTReflection.toCompound(getCompound(), this).toString();
    }

}
