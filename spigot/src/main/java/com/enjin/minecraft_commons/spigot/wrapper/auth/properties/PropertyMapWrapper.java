package com.enjin.minecraft_commons.spigot.wrapper.auth.properties;

import com.enjin.minecraft_commons.spigot.reflection.resolver.MethodResolver;
import com.enjin.minecraft_commons.spigot.reflection.resolver.ResolverQuery;
import com.enjin.minecraft_commons.spigot.wrapper.Wrapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PropertyMapWrapper extends Wrapper {

    static Class<?> Multimap           = CLASS_RESOLVER.resolveSilent("net.minecraft.util.com.google.common.collect.Multimap",
            "com.google.common.collect.Multimap");
    static Class<?> ForwardingMultimap = CLASS_RESOLVER.resolveSilent("net.minecraft.util.com.google.common.collect.ForwardingMultimap",
            "com.google.common.collect.ForwardingMultimap");

    static MethodResolver MultimapMethodResolver           = new MethodResolver(Multimap);
    static MethodResolver ForwardingMultimapMethodResolver = new MethodResolver(ForwardingMultimap);

    public PropertyMapWrapper() {
        super(Type.GENERAL, "net.minecraft.util.com.mojang.authlib.properties.PropertyMap",
                "com.mojang.authlib.properties.PropertyMap");
    }

    public PropertyMapWrapper(Object handle) {
        super(handle);
    }

    public PropertyMapWrapper(JsonArray jsonArray) {
        this();

        for (Iterator<JsonElement> iterator = jsonArray.iterator(); iterator.hasNext();) {
            JsonElement next = iterator.next();

            if (next instanceof JsonObject) {
                JsonObject jsonObject = next.getAsJsonObject();
                put(jsonObject.get("name").getAsString(), new PropertyWrapper(jsonObject.get("name").getAsString(),
                        jsonObject.get("value").getAsString(),
                        jsonObject.has("signature") ? jsonObject.get("signature").getAsString() : null));
            }
        }
    }

    public void putAll(PropertyMapWrapper wrapper) {
        ForwardingMultimapMethodResolver.resolveWrapper(new ResolverQuery("putAll", Multimap)).invoke(getHandle(), wrapper.getHandle());
    }

    public void put(String key, PropertyWrapper wrapper) {
        ForwardingMultimapMethodResolver.resolveWrapper("put").invoke(getHandle(), key, wrapper.getHandle());
    }

    public Collection valuesHandle() {
        return (Collection) MultimapMethodResolver.resolveWrapper("values").invoke(getHandle());
    }

    public Collection<PropertyWrapper> values() {
        List<PropertyWrapper> wrappers = new ArrayList<>();

        for (Object handle : valuesHandle()) {
            wrappers.add(new PropertyWrapper(handle));
        }

        return wrappers;
    }

    public void clear() {
        MultimapMethodResolver.resolveWrapper("clear").invoke(getHandle());
    }

    public JsonArray toJson() {
        JsonArray jsonArray = new JsonArray();

        for (PropertyWrapper wrapper : values()) {
            jsonArray.add(wrapper.toJson());
        }

        return jsonArray;
    }

}
