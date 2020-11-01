package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import java.util.List;
import java.util.function.BiConsumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.Fade;
import io.github.amerebagatelle.fabricskyboxes.util.object.HeightEntry;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import io.github.amerebagatelle.fabricskyboxes.util.object.Rotation;
import io.github.amerebagatelle.fabricskyboxes.util.object.Textures;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public class LegacyDeserializer<T extends AbstractSkybox> {
    public static final Registry<LegacyDeserializer<? extends AbstractSkybox>> REGISTRY;
    public static final LegacyDeserializer<MonoColorSkybox> MONO_COLOR_SKYBOX_DESERIALIZER = register(new LegacyDeserializer<>(SkyboxType.MONO_COLOR_SKYBOX, LegacyDeserializer::decodeMonoColor));
    public static final LegacyDeserializer<SquareTexturedSkybox> SQUARE_TEXTURED_SKYBOX_DESERIALIZER = register(new LegacyDeserializer<>(SkyboxType.SQUARE_TEXTURED_SKYBOX, LegacyDeserializer::decodeSquareTextured));
    private final SkyboxType<T> skyboxType;
    private final BiConsumer<JsonObjectWrapper, T> deserializer;

    private LegacyDeserializer(SkyboxType<T> skyboxType, BiConsumer<JsonObjectWrapper, T> deserializer) {
        this.skyboxType = skyboxType;
        this.deserializer = deserializer;
    }

    public SkyboxType<T> getSkyboxType() {
        return this.skyboxType;
    }

    public BiConsumer<JsonObjectWrapper, T> getDeserializer() {
        return this.deserializer;
    }

    private static void decodeSquareTextured(JsonObjectWrapper wrapper, SquareTexturedSkybox skybox) {
        decodeSharedData(wrapper, skybox);
        skybox.rotation = new Rotation(new Vector3f(0f, 0f, 0f), new Vector3f(wrapper.getOptionalArrayFloat("axis", 0, 0), wrapper.getOptionalArrayFloat("axis", 1, 0), wrapper.getOptionalArrayFloat("axis", 2, 0)));
        skybox.blend = wrapper.getOptionalBoolean("shouldBlend", false);
        skybox.textures = new Textures(
                wrapper.getJsonStringAsId("texture_north"),
                wrapper.getJsonStringAsId("texture_south"),
                wrapper.getJsonStringAsId("texture_east"),
                wrapper.getJsonStringAsId("texture_west"),
                wrapper.getJsonStringAsId("texture_top"),
                wrapper.getJsonStringAsId("texture_bottom")
        );
    }

    private static void decodeMonoColor(JsonObjectWrapper wrapper, MonoColorSkybox skybox) {
        decodeSharedData(wrapper, skybox);
        skybox.color = new RGBA(wrapper.get("red").getAsFloat(), wrapper.get("blue").getAsFloat(), wrapper.get("green").getAsFloat());
    }

    private static void decodeSharedData(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        skybox.fade = new Fade(
                wrapper.get("startFadeIn").getAsInt(),
                wrapper.get("endFadeIn").getAsInt(),
                wrapper.get("startFadeOut").getAsInt(),
                wrapper.get("endFadeOut").getAsInt(),
                false
        );
        // alpha changing
        skybox.maxAlpha = wrapper.getOptionalFloat("maxAlpha", 1f);
        skybox.transitionSpeed = wrapper.getOptionalFloat("transitionSpeed", 1f);
        // rotation
        skybox.shouldRotate = wrapper.getOptionalBoolean("shouldRotate", false);
        // decorations
        skybox.decorations = Decorations.DEFAULT;
        // fog
        skybox.changeFog = wrapper.getOptionalBoolean("changeFog", false);
        skybox.fogColors = new RGBA(
                wrapper.getOptionalFloat("fogRed", 0f),
                wrapper.getOptionalFloat("fogGreen", 0f),
                wrapper.getOptionalFloat("fogBlue", 0f)
        );
        // environment specifications
        JsonElement element;
        element = wrapper.getOptionalValue("weather").orElse(null);
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    skybox.weather.add(jsonElement.getAsString());
                }
            } else if (JsonHelper.isString(element)) {
                skybox.weather.add(element.getAsString());
            }
        }
        element = wrapper.getOptionalValue("biomes").orElse(null);
        processIds(element, skybox.biomes);
        element = wrapper.getOptionalValue("dimensions").orElse(null);
        processIds(element, skybox.worlds);
        element = wrapper.getOptionalValue("heightRanges").orElse(null);
        if (element != null) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                JsonArray insideArray = jsonElement.getAsJsonArray();
                float low = insideArray.get(0).getAsFloat();
                float high = insideArray.get(1).getAsFloat();
                skybox.heightRanges.add(new HeightEntry(low, high));
            }
        }
    }

    private static void processIds(JsonElement element, List<Identifier> list) {
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    list.add(new Identifier(jsonElement.getAsString()));
                }
            } else if (JsonHelper.isString(element)) {
                list.add(new Identifier(element.getAsString()));
            }
        }
    }

    private static <T extends AbstractSkybox> LegacyDeserializer<T> register(LegacyDeserializer<T> deserializer) {
        return Registry.register(LegacyDeserializer.REGISTRY, new Identifier(FabricSkyBoxesClient.MODID, deserializer.getSkyboxType().getName()), deserializer);
    }

    static {
        REGISTRY = FabricRegistryBuilder.<LegacyDeserializer<? extends AbstractSkybox>, SimpleRegistry<LegacyDeserializer<? extends AbstractSkybox>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(FabricSkyBoxesClient.MODID, "legacy_skybox_deserializer")), Lifecycle.stable())).buildAndRegister();
    }
}
