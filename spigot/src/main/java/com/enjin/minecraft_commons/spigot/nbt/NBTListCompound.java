package com.enjin.minecraft_commons.spigot.nbt;

import java.util.HashSet;
import java.util.Set;

public class NBTListCompound {

    private NBTList parent;
    private Object compound;

    protected NBTListCompound(NBTList parent, Object compound) {
        this.parent = parent;
        this.compound = compound;
    }

    public void setString(String key, String value) {
        if (value == null) {
            remove(key);
        } else {
            try {
                compound.getClass().getMethod("setString", String.class, String.class).invoke(compound, key, value);
                parent.save();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public String getString(String key) {
        String value;
        try {
            value = (String) compound.getClass().getMethod("getString", String.class).invoke(compound, key);
        } catch (Exception ex) {
            value = null;
        }
        return value;
    }

    public void setInteger(String key, Integer value) {
        try {
            compound.getClass().getMethod("setInt", String.class, int.class).invoke(compound, key, value);
            parent.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Integer getInteger(String key) {
        Integer value;
        try {
            value = (Integer) compound.getClass().getMethod("getInt", String.class).invoke(compound, key);
        } catch (Exception ex) {
            value = null;
        }
        return value;
    }

    public void setDouble(String key, Double value) {
        try {
            compound.getClass().getMethod("setDouble", String.class, double.class).invoke(compound, key, value);
            parent.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public double getDouble(String key) {
        Double value;
        try {
            value = (Double) compound.getClass().getMethod("getDouble", String.class).invoke(compound, key);
        } catch (Exception ex) {
            value = null;
        }
        return value;
    }

    public boolean hasKey(String key) {
        boolean result;
        try {
            result = (boolean) compound.getClass().getMethod("hasKey", String.class).invoke(compound, key);
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getKeys() {
        Set<String> set;
        try {
            set = (Set<String>) compound.getClass().getMethod("c").invoke(compound);
        } catch (Exception ex) {
            set = new HashSet<>();
        }
        return set;
    }

    public void remove(String key) {
        try {
            compound.getClass().getMethod("remove", String.class).invoke(compound, key);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
