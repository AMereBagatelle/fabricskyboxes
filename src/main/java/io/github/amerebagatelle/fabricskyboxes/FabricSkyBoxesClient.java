package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceListener;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class FabricSkyBoxesClient implements ClientModInitializer {
    public static final String MODID = "fabricskyboxes";
    private static Logger LOGGER;
    private static KeyBinding toggleFabricSkyBoxes;

    @Override
    public void onInitializeClient() {
        toggleFabricSkyBoxes = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabricskyboxes.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_0, "category.fabricskyboxes"));
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SkyboxResourceListener());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleFabricSkyBoxes.wasPressed()) {
                SkyboxManager.getInstance().setEnabled(!SkyboxManager.getInstance().isEnabled());
                assert client.player != null;
                if (SkyboxManager.getInstance().isEnabled()) {
                    client.player.sendMessage(Text.translatable("fabricskyboxes.message.enabled"), false);
                } else {
                    client.player.sendMessage(Text.translatable("fabricskyboxes.message.disabled"), false);
                }
            }
        });
    }

    public static Logger getLogger() {
        if (LOGGER == null) {
            LOGGER = LogManager.getLogger("FabricSkyboxes");
        }
        return LOGGER;
    }
}
