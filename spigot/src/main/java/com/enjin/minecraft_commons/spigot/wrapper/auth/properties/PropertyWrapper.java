package com.enjin.minecraft_commons.spigot.wrapper.auth.properties;

import com.enjin.minecraft_commons.spigot.wrapper.ConstructorPopulator;
import com.enjin.minecraft_commons.spigot.wrapper.Wrapper;
import com.google.gson.JsonObject;

public class PropertyWrapper extends Wrapper {

    public PropertyWrapper(Object handle) {
        super(handle);
    }

    public PropertyWrapper(final String name, final String value, final String signature) {
        super(CLASS_RESOLVER.resolveWrapper("net.minecraft.util.com.mojang.authlib.properties.Property",
                "com.mojang.authlib.properties.Property"), new ConstructorPopulator() {
            @Override
            public Class<?>[] types() {
                return new Class[] {
                        String.class,
                        String.class,
                        String.class
                };
            }

            @Override
            public Object[] values() {
                return new Object[] {
                        name,
                        value,
                        signature
                };
            }
        });
    }

    public PropertyWrapper(String name, String value) {
        this(name, value, null);
    }

    public PropertyWrapper(JsonObject jsonObject) {
        this(jsonObject.get("name").getAsString(), jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());
    }

    public String getName() {
        return getFieldValue("name");
    }

    public String getValue() {
        return getFieldValue("value");
    }

    public String getSignature() {
        return getFieldValue("signature");
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", getName());
        jsonObject.addProperty("value", getValue());
        jsonObject.addProperty("signature", getSignature());
        return jsonObject;
    }

}
