package amerebagatelle.github.io.fabricskyboxes.resource;

import amerebagatelle.github.io.fabricskyboxes.FabricSkyBoxesClient;
import amerebagatelle.github.io.fabricskyboxes.SkyboxStateManager;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.MonoColorSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.TexturedSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.Fade;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.RGBA;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.object.Textures;
import amerebagatelle.github.io.fabricskyboxes.util.JsonObjectWrapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

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
import java.util.Objects;

public class SkyboxResourceLoader {
    private static final Gson gson = new Gson();
    private static final JsonObjectWrapper objectWrapper = new JsonObjectWrapper();

    public static void setupResourceLoader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> resources = manager.findResources("sky", (string) -> string.endsWith(".json"));
                SkyboxStateManager.getInstance().clearSkyboxes();

                for (Identifier id : resources) {
                    try {
                        if (id.getNamespace().equals(FabricSkyBoxesClient.MODID)) {
                            JsonObject json = gson.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonObject.class);
                            SkyboxStateManager.getInstance().addSkybox(parseSkyboxJson(id, json));
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

    private static AbstractSkybox parseV2(JsonObject json) {
        AbstractSkybox skyBox = null;
        if (json.get("type").getAsString().equals("color")) {
            skyBox = MonoColorSkybox.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.err::println).getFirst();
        } else if (json.get("type").getAsString().equals("textured")) {
            skyBox = TexturedSkybox.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.err::println).getFirst();
        }
        return skyBox;
    }

    private static AbstractSkybox parseSkyboxJson(Identifier id, JsonObject json) {
        objectWrapper.setFocusedObject(json);
        AbstractSkybox skybox;
        if (json.has("schemaVersion")) {
            if (json.get("schemaVersion").getAsInt() == 2) {
                return Objects.requireNonNull(parseV2(json));
            }
        }
        try {
            if (json.get("type").getAsString().equals("color")) {
                skybox = new MonoColorSkybox(
                        json.get("red").getAsFloat(),
                        json.get("blue").getAsFloat(),
                        json.get("green").getAsFloat()
                );
            } else {
                skybox = new TexturedSkybox(
                        new Textures(
                                objectWrapper.getJsonStringAsId("texture_north"),
                                objectWrapper.getJsonStringAsId("texture_south"),
                                objectWrapper.getJsonStringAsId("texture_east"),
                                objectWrapper.getJsonStringAsId("texture_west"),
                                objectWrapper.getJsonStringAsId("texture_top"),
                                objectWrapper.getJsonStringAsId("texture_bottom")
                        ),
                        Lists.newArrayList(json.get("axis").getAsJsonArray().get(0).getAsFloat(), json.get("axis").getAsJsonArray().get(1).getAsFloat(), json.get("axis").getAsJsonArray().get(2).getAsFloat())
                );
            }
            skybox.fade = new Fade(json.get("startFadeIn").getAsInt(), json.get("endFadeIn").getAsInt(), json.get("startFadeOut").getAsInt(), json.get("endFadeOut").getAsInt());
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new NullPointerException("Could not get a required field.");
        }
        // alpha changing
        skybox.maxAlpha = objectWrapper.getOptionalFloat("maxAlpha", 1f);
        skybox.transitionSpeed = objectWrapper.getOptionalFloat("transitionSpeed", 1f);
        // rotation
        skybox.shouldRotate = objectWrapper.getOptionalBoolean("shouldRotate", false);
        // decorations
        skybox.decorations = objectWrapper.getOptionalBoolean("decorations", false);
        // fog
        skybox.changeFog = objectWrapper.getOptionalBoolean("changeFog", false);
        skybox.fogColors = new RGBA(objectWrapper.getOptionalFloat("fogRed", 0f), objectWrapper.getOptionalFloat("fogGreen", 0f), objectWrapper.getOptionalFloat("fogBlue", 0f));
        // environment specifications
        JsonElement element;
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
                    skybox.heightRangesF.add(new float[]{low, high});
                } else {
                    FabricSkyBoxesClient.getLogger().warn("Skybox " + id.toString() + " contains invalid height ranges.");
                }
            }
        }
        return skybox;
    }
}
