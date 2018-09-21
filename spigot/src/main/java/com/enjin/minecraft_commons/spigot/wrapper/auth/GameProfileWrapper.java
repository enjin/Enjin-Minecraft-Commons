package com.enjin.minecraft_commons.spigot.wrapper.auth;

import com.enjin.minecraft_commons.spigot.wrapper.ConstructorPopulator;
import com.enjin.minecraft_commons.spigot.wrapper.Wrapper;
import com.enjin.minecraft_commons.spigot.wrapper.auth.properties.PropertyMapWrapper;
import com.google.gson.JsonObject;

import java.util.UUID;

public class GameProfileWrapper extends Wrapper {

    public GameProfileWrapper(final UUID uuid, final String name) {
        super(CLASS_RESOLVER.resolveWrapper("net.minecraft.util.com.mojang.authlib.GameProfile",
                "com.mojang.authlib.GameProfile"), new ConstructorPopulator() {
            @Override
            public Class<?>[] types() {
                return new Class[]{
                        UUID.class,
                        String.class
                };
            }

            @Override
            public Object[] values() {
                return new Object[]{
                        uuid,
                        name
                };
            }
        });
    }

    public GameProfileWrapper(Object handle) {
        super(handle);
    }

    public GameProfileWrapper(JsonObject jsonObject) {
        this(parseUUID(jsonObject.get("id").getAsString()), jsonObject.get("name").getAsString());

        getProperties().clear();

        if (jsonObject.has("properties")) {
            getProperties().putAll(new PropertyMapWrapper(jsonObject.get("properties").getAsJsonArray()));
        }
    }

    public UUID getId() {
        return getFieldValue("id");
    }

    public String getName() {
        return getFieldValue("name");
    }

    public void setName(String name) {
        setFieldValue(name, "name");
    }

    public Object getPropertiesHandle() {
        return getFieldValue("properties");
    }

    public PropertyMapWrapper getProperties() {
        return new PropertyMapWrapper(getPropertiesHandle());
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", getId() != null ? getId().toString() : "");
        jsonObject.addProperty("name", getName());

        PropertyMapWrapper properties = getProperties();
        if (properties != null) {
            jsonObject.add("properties", getProperties().toJson());
        }

        return jsonObject;
    }

    static UUID parseUUID(String string) {
        if (string == null || string.isEmpty()) {
            return null;
        }

        return UUID.fromString(string);
    }

}
