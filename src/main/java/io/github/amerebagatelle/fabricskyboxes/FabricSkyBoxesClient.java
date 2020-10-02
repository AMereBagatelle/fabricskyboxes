package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceLoader;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FabricSkyBoxesClient implements ClientModInitializer {
    public static final String MODID = "fabricskyboxes";

    @Override
    public void onInitializeClient() {
        SkyboxResourceLoader.setupResourceLoader();

        SkyboxManager.addSkyboxType(SquareTexturedSkybox::new);
        SkyboxManager.addSkyboxType(MonoColorSkybox::new);
    }
}
