package io.github.amerebagatelle.fabricskyboxes.util;

import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import org.jetbrains.annotations.Nullable;

public class JsonObjectWrapper {
    private JsonObject focusedObject;

    public Identifier getJsonStringAsId(String key) {
        return new Identifier(focusedObject.get(key).getAsString());
    }

    @Nullable
    public JsonElement getOptionalValue(String key) {
        JsonElement element = null;
        try {
            element = focusedObject.get(key);
        } catch (NullPointerException e) {
            FabricSkyBoxesClient.getLogger().debug(String.format("Optional value %s not set.", key));
        }
        return element;
    }

    public float getOptionalFloat(String key, float normal) {
        JsonElement element = getOptionalValue(key);
        return element != null && JsonHelper.isNumber(element) ? element.getAsFloat() : normal;
    }

    public boolean getOptionalBoolean(String key, boolean normal) {
        JsonElement element = getOptionalValue(key);
        return element != null && element.getAsJsonPrimitive().isBoolean() ? element.getAsBoolean() : normal;
    }

    public void setFocusedObject(JsonObject focusedObject) {
        this.focusedObject = focusedObject;
    }
}
