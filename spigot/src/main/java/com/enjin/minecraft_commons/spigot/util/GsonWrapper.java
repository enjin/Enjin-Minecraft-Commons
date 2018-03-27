package com.enjin.minecraft_commons.spigot.util;

import com.google.gson.Gson;

public class GsonWrapper {

    private static final Gson gson = new Gson();

    public static String toJsonString(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T deserializeJson(String json, Class<T> type) {
        T obj = null;

        try {
            if (json != null) {
                obj = gson.fromJson(json, type);
            }
        } catch (Exception ex) {
            obj = null;
        }

        return obj;
    }

}
