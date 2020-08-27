package amerebagatelle.github.io.fabricskyboxes.util;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

public class JsonObjectWrapper {
    private JsonObject focusedObject;

    public Identifier getJsonStringAsId(String key) {
        return new Identifier(focusedObject.get(key).getAsString());
    }

    public JsonElement getOptionalValue(String key) {
        JsonElement element = null;
        try {
            element = focusedObject.get(key);
        } catch (NullPointerException e) {
            FabricSkyBoxesClient.getLogger().debug("Optional value not set.");
        }
        return element;
    }

    public Float getOptionalFloat(String key) {
        if (getOptionalValue(key) != null) {
            return getOptionalValue(key).getAsFloat();
        }
        return null;
    }

    public void setFocusedObject(JsonObject focusedObject) {
        this.focusedObject = focusedObject;
    }
}
