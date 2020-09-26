package amerebagatelle.github.io.fabricskyboxes;

import amerebagatelle.github.io.fabricskyboxes.resource.SkyboxResourceLoader;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.MonoColorSkybox;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
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

        SkyboxManager.addSkyboxType(SquareTexturedSkybox.class);
        SkyboxManager.addSkyboxType(MonoColorSkybox.class);
    }

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger("fabricskyboxes");
        }
        return LOGGER;
    }
}
