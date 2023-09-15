package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.config.FabricSkyBoxesConfig;
import io.github.amerebagatelle.fabricskyboxes.config.SkyBoxDebugScreen;
import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceListener;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class FabricSkyBoxesClient implements ClientModInitializer {
    public static final String MODID = "fabricskyboxes";
    private static Logger LOGGER;
    private static FabricSkyBoxesConfig CONFIG;

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger("FabricSkyboxes");
        }
        return LOGGER;
    }

    public static FabricSkyBoxesConfig config() {
        if (CONFIG == null) {
            CONFIG = loadConfig();
        }

        return CONFIG;
    }

    private static FabricSkyBoxesConfig loadConfig() {
        return FabricSkyBoxesConfig.load(FabricLoader.getInstance().getConfigDir().resolve("fabricskyboxes-config.json").toFile());
    }

    @Override
    public void onInitializeClient() {
        SkyboxType.initRegistry();
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SkyboxResourceListener());
        SkyboxManager.getInstance().setEnabled(config().generalSettings.enable);
        ClientTickEvents.END_WORLD_TICK.register(SkyboxManager.getInstance());
        ClientTickEvents.END_CLIENT_TICK.register(config().getKeyBinding());

        //
        //KeyBinding keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabricskyboxes.toggle.debug_screen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.fabricskyboxes"));
        SkyBoxDebugScreen screen = new SkyBoxDebugScreen(Text.of("Skybox Debug Screen"));
        /*ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                client.setScreen(screen);
            }
        });*/
        HudRenderCallback.EVENT.register(screen);
    }
}