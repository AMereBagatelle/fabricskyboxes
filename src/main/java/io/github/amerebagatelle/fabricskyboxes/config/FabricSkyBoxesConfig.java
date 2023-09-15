package io.github.amerebagatelle.fabricskyboxes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class FabricSkyBoxesConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.PRIVATE)
            .create();
    public final GeneralSettings generalSettings = new GeneralSettings();
    private final KeyBindingImpl keyBinding = new KeyBindingImpl();
    private File file;

    public static FabricSkyBoxesConfig load(File file) {
        FabricSkyBoxesConfig config;
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                config = GSON.fromJson(reader, FabricSkyBoxesConfig.class);
            } catch (Exception e) {
                FabricSkyBoxesClient.getLogger().error("Could not parse config, falling back to defaults!", e);
                config = new FabricSkyBoxesConfig();
            }
        } else {
            config = new FabricSkyBoxesConfig();
        }
        config.file = file;
        config.save();

        return config;
    }

    public KeyBindingImpl getKeyBinding() {
        return this.keyBinding;
    }

    public void save() {
        File dir = this.file.getParentFile();

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Could not create parent directories");
            }
        } else if (!dir.isDirectory()) {
            throw new RuntimeException("The parent file is not a directory");
        }

        try (FileWriter writer = new FileWriter(this.file)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Could not save configuration file", e);
        }
    }

    public static class GeneralSettings {
        public boolean enable = true;
        public int unexpectedTransitionDuration = 20;
        public boolean keepVanillaBehaviour = true;

        public boolean debugMode = false;
        public boolean debugHud = false;
    }


    public static class KeyBindingImpl implements ClientTickEvents.EndTick {

        public final KeyBinding toggleFabricSkyBoxes;
        public final KeyBinding toggleSkyboxDebugHud;

        public KeyBindingImpl() {
            this.toggleFabricSkyBoxes = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabricskyboxes.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.fabricskyboxes"));
            this.toggleSkyboxDebugHud = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabricskyboxes.toggle.debug_hud", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F12, "category.fabricskyboxes"));
        }

        @Override
        public void onEndTick(MinecraftClient client) {
            while (this.toggleFabricSkyBoxes.wasPressed()) {
                FabricSkyBoxesClient.config().generalSettings.enable = !FabricSkyBoxesClient.config().generalSettings.enable;
                FabricSkyBoxesClient.config().save();
                SkyboxManager.getInstance().setEnabled(FabricSkyBoxesClient.config().generalSettings.enable);

                assert client.player != null;
                if (SkyboxManager.getInstance().isEnabled()) {
                    client.player.sendMessage(Text.translatable("fabricskyboxes.message.enabled"), false);
                } else {
                    client.player.sendMessage(Text.translatable("fabricskyboxes.message.disabled"), false);
                }
            }
            while (this.toggleSkyboxDebugHud.wasPressed()) {
                FabricSkyBoxesClient.config().generalSettings.debugHud = !FabricSkyBoxesClient.config().generalSettings.debugHud;
                FabricSkyBoxesClient.config().save();
            }
        }
    }
}
