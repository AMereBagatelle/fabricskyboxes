package amerebagatelle.github.io.fabricskyboxes.resource;

import amerebagatelle.github.io.fabricskyboxes.SkyboxManager;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.TexturedSkybox;
import amerebagatelle.github.io.fabricskyboxes.util.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

public class SkyboxResourceLoader {
    private static final Gson gson = new Gson();

    public static void setupResourceLoader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                Collection<Identifier> resources = manager.findResources("sky", (string) -> string.endsWith(".json"));

                for (Identifier id : resources) {
                    try {
                        JsonObject json = gson.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonObject.class);
                        SkyboxManager.getInstance().addSkybox(parseSkyboxJson(json));
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

    private static AbstractSkybox parseSkyboxJson(JsonObject json) {
        TexturedSkybox skybox = new TexturedSkybox(
                Utils.getJsonStringAsId("texture_north", json),
                Utils.getJsonStringAsId("texture_south", json),
                Utils.getJsonStringAsId("texture_east", json),
                Utils.getJsonStringAsId("texture_west", json),
                Utils.getJsonStringAsId("texture_top", json),
                Utils.getJsonStringAsId("texture_bottom", json)
        );
        skybox.startFadeIn = json.get("startFadeIn").getAsInt();
        skybox.endFadeIn = json.get("endFadeIn").getAsInt();
        skybox.startFadeOut = json.get("startFadeOut").getAsInt();
        skybox.endFadeOut = json.get("endFadeOut").getAsInt();
        return skybox;
    }
}
