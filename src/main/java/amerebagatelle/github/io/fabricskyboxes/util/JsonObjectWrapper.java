package amerebagatelle.github.io.fabricskyboxes.util;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
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

    public void setFocusedObject(JsonObject focusedObject) {
        this.focusedObject = focusedObject;
    }
}
