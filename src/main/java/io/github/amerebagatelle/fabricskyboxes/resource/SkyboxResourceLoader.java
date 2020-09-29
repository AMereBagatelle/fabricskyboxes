package io.github.amerebagatelle.fabricskyboxes.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Supplier;

public class SkyboxResourceLoader {
    private static final Gson gson = new Gson();
    private static final JsonObjectWrapper objectWrapper = new JsonObjectWrapper();

    public static void setupResourceLoader() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public void apply(ResourceManager manager) {
                SkyboxManager skyboxManager = SkyboxManager.getInstance();

                // clear registered skyboxes on reload
                skyboxManager.clearSkyboxes();

                // load new skyboxes
                Collection<Identifier> resources = manager.findResources("sky", (string) -> string.endsWith(".json"));

                for (Identifier id : resources) {
                    try {
                        if (id.getNamespace().equals(FabricSkyBoxesClient.MODID)) {
                            JsonObject json = gson.fromJson(new InputStreamReader(manager.getResource(id).getInputStream()), JsonObject.class);
                            objectWrapper.setFocusedObject(json);
                            skyboxManager.addSkybox(parseSkyboxJson());
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

    private static AbstractSkybox parseSkyboxJson() {
        AbstractSkybox skybox = null;

        try {
            int schemaVersion = 1;
            if (objectWrapper.contains("schemaVersion")) {
                schemaVersion = objectWrapper.get("schemaVersion").getAsInt();
            }
            String jsonSkyboxType = objectWrapper.get("type").getAsString();
            for (Supplier<? extends AbstractSkybox> skyboxType : SkyboxManager.getSkyboxTypes()) {
                if (jsonSkyboxType.equals(skyboxType.get().getType())) {
                    skybox = skyboxType.get();
                    break;
                }
            }

            if (skybox == null) {
                throw new IllegalStateException();
            }
            if (schemaVersion > 1) {
                Codec<? extends AbstractSkybox> codec = Objects.requireNonNull(skybox.getCodec(schemaVersion), String.format("Schema version %s is not supported by type %s of class %s", schemaVersion, skybox.getType(), skybox.getClass().getName()));
                skybox = codec.decode(JsonOps.INSTANCE, objectWrapper.getFocusedObject()).getOrThrow(false, System.err::println).getFirst();
                return skybox;
            }
            skybox.parseJson(objectWrapper);
        } catch (RuntimeException e) {
            RuntimeException exception = new NullPointerException("Could not get a required field.");
            exception.addSuppressed(e);
            throw exception;
        }

        return Objects.requireNonNull(skybox);
    }
}
