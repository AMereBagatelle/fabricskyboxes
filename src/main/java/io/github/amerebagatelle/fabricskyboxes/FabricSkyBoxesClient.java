package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceLoader;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Environment(EnvType.CLIENT)
public class FabricSkyBoxesClient implements ClientModInitializer {
    public static final String MODID = "fabricskyboxes";
    private static Logger LOGGER;

    @Override
    public void onInitializeClient() {
        SkyboxResourceLoader.setupResourceLoader();

        SkyboxManager.addSkyboxType(SquareTexturedSkybox::new);
        SkyboxManager.addSkyboxType(MonoColorSkybox::new);
    }

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger("fabricskyboxes");
        }
        return LOGGER;
    }
}
