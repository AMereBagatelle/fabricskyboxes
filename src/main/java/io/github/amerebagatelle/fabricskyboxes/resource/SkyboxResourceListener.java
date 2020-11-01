package io.github.amerebagatelle.fabricskyboxes.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.internal.Metadata;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

public class SkyboxResourceListener implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().setLenient().create();
    private static final JsonObjectWrapper objectWrapper = new JsonObjectWrapper();

    @Override
    public void apply(ResourceManager manager) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();

        // clear registered skyboxes on reload
        skyboxManager.clearSkyboxes();

        // load new skyboxes
        // ! This will not work with schema versions at wrong version... remember the long debugging process you had that one time
        Collection<Identifier> resources = manager.findResources("sky", (string) -> string.endsWith(".json"));

        for (Identifier id : resources) {
            try {
                JsonObject json = GSON.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonObject.class);
                objectWrapper.setFocusedObject(json);
                AbstractSkybox skybox = this.parseSkyboxJson(id);
                if (skybox != null) {
                    skyboxManager.addSkybox(skybox);
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

    private AbstractSkybox parseSkyboxJson(Identifier id) {
        AbstractSkybox skybox;
        Metadata metadata;
        try {
            metadata = Metadata.CODEC.decode(JsonOps.INSTANCE, objectWrapper.getFocusedObject()).getOrThrow(false, System.err::println).getFirst();
        } catch (RuntimeException e) {
            FabricSkyBoxesClient.getLogger().warn("Skipping invalid skybox " + id.toString(), e);
            FabricSkyBoxesClient.getLogger().warn(objectWrapper.toString());
            return null;
        }
        SkyboxType<? extends AbstractSkybox> type = SkyboxType.REGISTRY.get(metadata.getId());
        Preconditions.checkNotNull(type, "Unknown skybox type: " + metadata.getId().getPath().replace('_', '-'));
        if (metadata.getSchemaVersion() == 1) {
            Preconditions.checkArgument(type.isLegacySupported(), "Unsupported schema version '1' for skybox type " + type.getName());
            FabricSkyBoxesClient.getLogger().debug("Using legacy deserializer for skybox " + id.toString());
            skybox = type.instantiate();
        } else {
            skybox = type.getCodec(metadata.getSchemaVersion()).decode(JsonOps.INSTANCE, objectWrapper.getFocusedObject()).getOrThrow(false, System.err::println).getFirst();
        }
        return skybox;
    }
}
