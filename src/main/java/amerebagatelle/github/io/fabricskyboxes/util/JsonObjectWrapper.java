package amerebagatelle.github.io.fabricskyboxes.util;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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

    public Optional<Integer> getOptionalInt(String key) {
        return Optional.ofNullable(getOptionalValue(key).getAsInt());
    }

    public Optional<Float> getOptionalFloat(String key) {
        return Optional.ofNullable(getOptionalValue(key).getAsFloat());
    }

    public void setFocusedObject(JsonObject focusedObject) {
        this.focusedObject = focusedObject;
    }
}
