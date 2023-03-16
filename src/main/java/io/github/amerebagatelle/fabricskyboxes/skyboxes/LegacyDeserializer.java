package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.BiConsumer;

public class LegacyDeserializer<T extends AbstractSkybox> {
    public static final Registry<LegacyDeserializer<? extends AbstractSkybox>> REGISTRY = FabricRegistryBuilder.<LegacyDeserializer<? extends AbstractSkybox>, SimpleRegistry<LegacyDeserializer<? extends AbstractSkybox>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(FabricSkyBoxesClient.MODID, "legacy_skybox_deserializer")), Lifecycle.stable())).buildAndRegister();
    public static final LegacyDeserializer<MonoColorSkybox> MONO_COLOR_SKYBOX_DESERIALIZER = register(new LegacyDeserializer<>(LegacyDeserializer::decodeMonoColor, MonoColorSkybox.class), "mono_color_skybox_legacy_deserializer");
    public static final LegacyDeserializer<SquareTexturedSkybox> SQUARE_TEXTURED_SKYBOX_DESERIALIZER = register(new LegacyDeserializer<>(LegacyDeserializer::decodeSquareTextured, SquareTexturedSkybox.class), "square_textured_skybox_legacy_deserializer");
    private final BiConsumer<JsonObjectWrapper, AbstractSkybox> deserializer;

    private LegacyDeserializer(BiConsumer<JsonObjectWrapper, AbstractSkybox> deserializer, Class<T> clazz) {
        this.deserializer = deserializer;
    }

    private static void decodeSquareTextured(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        decodeSharedData(wrapper, skybox);
        ((SquareTexturedSkybox) skybox).rotation = new Rotation(new Vector3f(0f, 0f, 0f), new Vector3f(wrapper.getOptionalArrayFloat("axis", 0, 0), wrapper.getOptionalArrayFloat("axis", 1, 0), wrapper.getOptionalArrayFloat("axis", 2, 0)), 0, 1, 0);
        ((SquareTexturedSkybox) skybox).blend = new Blend(wrapper.getOptionalBoolean("shouldBlend", false) ? "add" : "", 0, 0, 0, false, false, false, true);
        ((SquareTexturedSkybox) skybox).textures = new Textures(
                new Texture(wrapper.getJsonStringAsId("texture_north")),
                new Texture(wrapper.getJsonStringAsId("texture_south")),
                new Texture(wrapper.getJsonStringAsId("texture_east")),
                new Texture(wrapper.getJsonStringAsId("texture_west")),
                new Texture(wrapper.getJsonStringAsId("texture_top")),
                new Texture(wrapper.getJsonStringAsId("texture_bottom"))
        );
    }

    private static void decodeMonoColor(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        decodeSharedData(wrapper, skybox);
        ((MonoColorSkybox) skybox).color = new RGBA(wrapper.get("red").getAsFloat(), wrapper.get("blue").getAsFloat(), wrapper.get("green").getAsFloat());
    }

    private static void decodeSharedData(JsonObjectWrapper wrapper, AbstractSkybox skybox) {
        float maxAlpha = wrapper.getOptionalFloat("maxAlpha", 1f);
        skybox.properties = new Properties.Builder()
                .fade(new Fade(
                        wrapper.get("startFadeIn").getAsInt(),
                        wrapper.get("endFadeIn").getAsInt(),
                        wrapper.get("startFadeOut").getAsInt(),
                        wrapper.get("endFadeOut").getAsInt(),
                        false
                ))
                .maxAlpha(maxAlpha)
                .transitionInDuration((int) (maxAlpha / wrapper.getOptionalFloat("transitionSpeed", 0.05f)))
                .transitionOutDuration((int) (maxAlpha / wrapper.getOptionalFloat("transitionSpeed", 0.05f)))
                .shouldRotate(wrapper.getOptionalBoolean("shouldRotate", false))
                .changeFog(wrapper.getOptionalBoolean("changeFog", false))
                .fogColors(new RGBA(
                        wrapper.getOptionalFloat("fogRed", 0f),
                        wrapper.getOptionalFloat("fogGreen", 0f),
                        wrapper.getOptionalFloat("fogBlue", 0f)
                ))
                .build();
        // decorations
        skybox.decorations = Decorations.DEFAULT;
        // environment specifications
        JsonElement element;
        element = wrapper.getOptionalValue("weather").orElse(null);
        if (element != null) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    skybox.conditions.getWeathers().add(Weather.fromString(jsonElement.getAsString()));
                }
            } else if (JsonHelper.isString(element)) {
                skybox.conditions.getWeathers().add(Weather.fromString(element.getAsString()));
            }
        }
        element = wrapper.getOptionalValue("biomes").orElse(null);
        processIds(element, skybox.conditions.getBiomes());
        element = wrapper.getOptionalValue("dimensions").orElse(null);
        processIds(element, skybox.conditions.getWorlds());
        element = wrapper.getOptionalValue("heightRanges").orElse(null);
        if (element != null) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement jsonElement : array) {
                JsonArray insideArray = jsonElement.getAsJsonArray();
                float low = insideArray.get(0).getAsFloat();
                float high = insideArray.get(1).getAsFloat();
                skybox.conditions.getYRanges().add(new MinMaxEntry(low, high));
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

    private static <T extends AbstractSkybox> LegacyDeserializer<T> register(LegacyDeserializer<T> deserializer, String name) {
        return Registry.register(LegacyDeserializer.REGISTRY, new Identifier(FabricSkyBoxesClient.MODID, name), deserializer);
    }

    public BiConsumer<JsonObjectWrapper, AbstractSkybox> getDeserializer() {
        return this.deserializer;
    }
}
