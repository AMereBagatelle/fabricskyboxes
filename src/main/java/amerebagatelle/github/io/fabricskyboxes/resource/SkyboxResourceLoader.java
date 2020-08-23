package amerebagatelle.github.io.fabricskyboxes.resource;

import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.MonoColorSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.SquareTextureSkybox;
import amerebagatelle.github.io.fabricskyboxes.util.SkyboxManager;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SkyboxResourceLoader {
    public static void setupResourceLoader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> resources = manager.findResources("sky", (string) -> string.endsWith(".json"));

                for(Identifier id : resources) {
                    try {
                        JsonParser parser = new JsonParser();
                        JsonObject json = (JsonObject)parser.parse(new InputStreamReader(manager.getResource(id).getInputStream()));

                        SkyboxManager.getInstance().addSkybox(parseJson(json));
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

    public static AbstractSkybox parseJson(JsonObject json) {
        AbstractSkybox skybox;
        if(json.get("texture").getAsString().equals("color")) {
            skybox = new MonoColorSkybox(json.get("red").getAsFloat(), json.get("blue").getAsFloat(), json.get("green").getAsFloat());
        } else {
            skybox = new SquareTextureSkybox(new Identifier(json.get("texture").getAsString()));
        }
        skybox.startFadeIn = json.get("startFadeIn").getAsInt();
        skybox.endFadeIn = json.get("endFadeIn").getAsInt();
        skybox.endFadeOut = json.get("endFadeOut").getAsInt();
        skybox.blend = json.get("blend").getAsString();
        skybox.rotate = json.get("rotate").getAsBoolean();
        skybox.speed = json.get("speed").getAsFloat();
        JsonArray axis = json.get("axis").getAsJsonArray();
        skybox.axis = new Vec3d(axis.get(0).getAsDouble(), axis.get(1).getAsDouble(), axis.get(2).getAsDouble());
        skybox.weather = json.get("weather").getAsString();
        JsonArray biomes = json.getAsJsonArray("biomes");
        ArrayList<Identifier> biomeIds = Lists.newArrayList();
        for (int i = 0; i < biomes.size(); i++) {
            biomeIds.add(new Identifier(biomes.get(i).getAsString()));
        }
        skybox.biomes = biomeIds;
        JsonArray dimensions = json.getAsJsonArray("biomes");
        ArrayList<Identifier> dimensionIds = Lists.newArrayList();
        for (int i = 0; i < biomes.size(); i++) {
            dimensionIds.add(new Identifier(dimensions.get(i).getAsString()));
        }
        skybox.dimensions = dimensionIds;
        JsonArray heights = json.getAsJsonArray("heights");
        int[] heightInts = new int[heights.size()];
        for (int i = 0; i < heights.size(); i++) {
            heightInts[i] = heights.get(i).getAsInt();
        }
        skybox.heights = heightInts;
        skybox.transition = json.get("transition").getAsInt();
        return skybox;
    }
}
