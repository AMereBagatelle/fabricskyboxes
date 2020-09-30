package io.github.amerebagatelle.fabricskyboxes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import io.github.amerebagatelle.fabricskyboxes.resource.SkyboxResourceLoader;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import io.github.amerebagatelle.fabricskyboxes.util.object.Fade;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import io.github.amerebagatelle.fabricskyboxes.util.object.Textures;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;

import net.minecraft.screen.PlayerScreenHandler;

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
