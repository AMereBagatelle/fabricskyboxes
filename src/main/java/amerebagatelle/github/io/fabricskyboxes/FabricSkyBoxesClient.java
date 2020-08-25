package amerebagatelle.github.io.fabricskyboxes;

import amerebagatelle.github.io.fabricskyboxes.resource.SkyboxResourceLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FabricSkyBoxesClient implements ClientModInitializer {
    public static final String MODID = "fabricskyboxes";

    @Override
    public void onInitializeClient() {
        SkyboxResourceLoader.setupResourceLoader();
    }
}
