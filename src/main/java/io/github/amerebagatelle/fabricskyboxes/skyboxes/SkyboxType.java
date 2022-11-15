package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.AnimatedSquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SingleSpriteAnimatedSquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SingleSpriteSquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public class SkyboxType {
    private static Map<String, Class<? extends AbstractSkybox>> types = Map.of(
            "monocolor", MonoColorSkybox.class,
            "square-textured-skybox", SquareTexturedSkybox.class,
            "animated-square-textured", AnimatedSquareTexturedSkybox.class,
            "single-sprite-square-textured", SingleSpriteSquareTexturedSkybox.class,
            "single-sprite-animated-square-textured", SingleSpriteAnimatedSquareTexturedSkybox.class
    );

    public static void addType(String type, Class<? extends AbstractSkybox> supplier) {
        types.put(type, supplier);
    }

    public static Class<? extends AbstractSkybox> getType(String key) {
        return types.get(key);
    }
}
