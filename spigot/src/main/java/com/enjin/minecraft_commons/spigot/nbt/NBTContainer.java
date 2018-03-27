package com.enjin.minecraft_commons.spigot.nbt;

public class NBTContainer extends NBTCompound {

    private Object nbt;

    public NBTContainer() {
        this(NBTReflection.createNBTTagCompound());
    }

    protected NBTContainer(Object nbt) {
        super(null, null);
        this.nbt = nbt;
    }

    public NBTContainer(String json) throws IllegalArgumentException {
        super(null, null);
        try {
            this.nbt = NBTReflection.parseNBT(json);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Malformed Json: " + ex.getMessage());
        }
    }

    @Override
    public Object getCompound() {
        return nbt;
    }

    @Override
    public void setCompound(Object nbt) {
        this.nbt = nbt;
    }

}
