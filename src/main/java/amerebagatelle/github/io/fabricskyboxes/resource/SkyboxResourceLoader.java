package amerebagatelle.github.io.fabricskyboxes.resource;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import amerebagatelle.github.io.fabricskyboxes.SkyboxManager;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.MonoColorSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.TexturedSkybox;
import amerebagatelle.github.io.fabricskyboxes.util.JsonObjectWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

public class SkyboxResourceLoader {
    private static final Gson gson = new Gson();
    private static final JsonObjectWrapper objectWrapper = new JsonObjectWrapper();

    public static void setupResourceLoader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> resources = manager.findResources("sky", (string) -> string.endsWith(".json"));

                for (Identifier id : resources) {
                    try {
                        if (id.getNamespace().equals(FabricSkyBoxesClient.MODID)) {
                            JsonObject json = gson.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonObject.class);
                            SkyboxManager.getInstance().addSkybox(parseSkyboxJson(id, json));
                        }
                    } catch (IOException ignored) {
                    }
                }
            }

            @Override
            public Collection<Identifier> getFabricDependencies() {
                return Collections.emptyList();
            }

            @Override
            public Identifier getFabricId() {
                return new Identifier("fabricskyboxes", "skybox_json");
            }
        });
    }

    private static AbstractSkybox parseSkyboxJson(Identifier id, JsonObject json) {
        objectWrapper.setFocusedObject(json);
        AbstractSkybox skybox;
        try {
            if (json.get("type").getAsString().equals("color")) {
                skybox = new MonoColorSkybox(
                        json.get("red").getAsFloat(),
                        json.get("blue").getAsFloat(),
                        json.get("green").getAsFloat()
                );
            } else {
                skybox = new TexturedSkybox(
                        objectWrapper.getJsonStringAsId("texture_north"),
                        objectWrapper.getJsonStringAsId("texture_south"),
                        objectWrapper.getJsonStringAsId("texture_east"),
                        objectWrapper.getJsonStringAsId("texture_west"),
                        objectWrapper.getJsonStringAsId("texture_top"),
                        objectWrapper.getJsonStringAsId("texture_bottom"),
                        new int[]{json.get("axis").getAsJsonArray().get(0).getAsInt(), json.get("axis").getAsJsonArray().get(1).getAsInt()}
                );
            }
            skybox.startFadeIn = json.get("startFadeIn").getAsInt();
            skybox.endFadeIn = json.get("endFadeIn").getAsInt();
            skybox.startFadeOut = json.get("startFadeOut").getAsInt();
            skybox.endFadeOut = json.get("endFadeOut").getAsInt();
        } catch (NullPointerException e) {
            throw new NullPointerException("Could not get a required field.");
        }
        JsonElement element = objectWrapper.getOptionalValue("maxAlpha");
        skybox.maxAlpha = element != null && JsonHelper.isNumber(element) ? element.getAsFloat() : 1f;
        element = objectWrapper.getOptionalValue("transitionSpeed");
        skybox.transitionSpeed = element != null && JsonHelper.isNumber(element) ? element.getAsFloat() : 1f;
        element = objectWrapper.getOptionalValue("shouldRotate");
        skybox.shouldRotate = element != null && element.getAsJsonPrimitive().isBoolean() && element.getAsBoolean();
        element = objectWrapper.getOptionalValue("weather");
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    skybox.weather.add(jsonElement.getAsString());
                }
            } else if (JsonHelper.isString(element)) {
                skybox.weather.add(element.getAsString());
            }
        }
        element = objectWrapper.getOptionalValue("biomes");
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    skybox.biomes.add(new Identifier(jsonElement.getAsString()));
                }
            } else if (JsonHelper.isString(element)) {
                skybox.biomes.add(new Identifier(element.getAsString()));
            }
        }
        element = objectWrapper.getOptionalValue("dimensions");
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    skybox.dimensions.add(new Identifier(jsonElement.getAsString()));
                }
            } else if (JsonHelper.isString(element)) {
                skybox.dimensions.add(new Identifier(element.getAsString()));
            }
        }
        element = objectWrapper.getOptionalValue("heightRanges");
        if (element != null) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                JsonArray insideArray = jsonElement.getAsJsonArray();
                float low = insideArray.get(0).getAsFloat();
                float high = insideArray.get(1).getAsFloat();
                if (high > low) {
                    skybox.heightRanges.add(new Float[]{low, high});
                } else {
                    FabricSkyBoxesClient.getLogger().warn("Skybox " + id.toString() + " contains invalid height ranges.");
                }
            }
        }
        return skybox;
    }
}
