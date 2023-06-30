package io.github.amerebagatelle.fabricskyboxes.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.Collection;

public class SkyboxResourceListener implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().setLenient().create();

    @Override
    public void reload(ResourceManager manager) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();

        // clear registered skyboxes on reload
        skyboxManager.clearSkyboxes();

        // load new skyboxes
        Collection<Identifier> resources = manager.findResources("sky", string -> string.endsWith(".json"));

        for (Identifier id : resources) {
            try {
                Resource resource = manager.getResource(id);
                JsonObject json = GSON.fromJson(new InputStreamReader(resource.getInputStream()), JsonObject.class);
                skyboxManager.addSkybox(id, json);
            } catch (Exception e) {
                FabricSkyBoxesClient.getLogger().error("Error reading skybox " + id.toString());
                e.printStackTrace();
            }
        }
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("fabricskyboxes", "skybox_json");
    }
}
