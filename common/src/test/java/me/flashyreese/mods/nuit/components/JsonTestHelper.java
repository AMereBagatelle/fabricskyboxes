package me.flashyreese.mods.nuit.components;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonTestHelper {
    public static final Gson GSON = new GsonBuilder()
            .setLenient()
            .create();

    public static JsonObject readJson(String json) {
        return GSON.fromJson(json, JsonObject.class);
    }
}
