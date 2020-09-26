package amerebagatelle.github.io.fabricskyboxes.util;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class JsonObjectWrapper {
    private JsonObject focusedObject;

    public JsonElement get(String memberName) {
        return focusedObject.get(memberName);
    }

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

    public float getOptionalArrayFloat(String key, int index, float normal) {
        JsonElement element = getOptionalValue(key);
        return element != null && element.isJsonArray() ? element.getAsJsonArray().get(index).getAsFloat() : normal;
    }

    public void setFocusedObject(JsonObject focusedObject) {
        this.focusedObject = focusedObject;
    }
}
