package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceListener;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.AnimatedSquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FabricSkyBoxesClient implements ClientModInitializer {
    public static final String MODID = "fabricskyboxes";
    private static Logger LOGGER;

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SkyboxResourceListener());

        SkyboxManager.addSkyboxType(AnimatedSquareTexturedSkybox::new);
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
