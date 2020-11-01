package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.AnimatedSquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

public final class SkyboxType<T extends AbstractSkybox> {
    public static final Registry<SkyboxType<? extends AbstractSkybox>> REGISTRY;
    public static final SkyboxType<MonoColorSkybox> MONO_COLOR_SKYBOX;
    public static final SkyboxType<SquareTexturedSkybox> SQUARE_TEXTURED_SKYBOX;
    public static final SkyboxType<AnimatedSquareTexturedSkybox> ANIMATED_SQUARE_TEXTURED_SKYBOX;
    public static final Codec<Identifier> SKYBOX_ID_CODEC;

    private final BiMap<Integer, Codec<T>> codecBiMap;
    private final boolean legacySupported;
    private final String name;
    @Nullable
    private final Supplier<T> factory;

    private SkyboxType(BiMap<Integer, Codec<T>> codecBiMap, boolean legacySupported, String name, @Nullable Supplier<T> factory) {
        this.codecBiMap = codecBiMap;
        this.legacySupported = legacySupported;
        this.name = name;
        this.factory = factory;
    }

    public String getName() {
        return this.name;
    }

    public boolean isLegacySupported() {
        return this.legacySupported;
    }

    @Nullable
    public T instantiate() {
        return Objects.requireNonNull(this.factory, "Can't instantiate from a null factory").get();
    }

    public Codec<T> getCodec(int schemaVersion) {
        return Objects.requireNonNull(this.codecBiMap.get(schemaVersion), String.format("Skybox type '%s' does not support schema version '%s'", this.name, schemaVersion));
    }

    private static <T extends AbstractSkybox> SkyboxType<T> register(SkyboxType<T> type) {
        return Registry.register(SkyboxType.REGISTRY, new Identifier(FabricSkyBoxesClient.MODID, type.getName()), type);
    }

    static {
        REGISTRY = FabricRegistryBuilder.<SkyboxType<? extends AbstractSkybox>, SimpleRegistry<SkyboxType<? extends AbstractSkybox>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(FabricSkyBoxesClient.MODID, "skybox_type")), Lifecycle.stable())).buildAndRegister();
        MONO_COLOR_SKYBOX = SkyboxType.Builder.create(MonoColorSkybox.class, "monocolor").legacySupported().factory(MonoColorSkybox::new).add(2, MonoColorSkybox.CODEC).build();
        SQUARE_TEXTURED_SKYBOX = SkyboxType.Builder.create(SquareTexturedSkybox.class, "square-textured").legacySupported().factory(SquareTexturedSkybox::new).add(2, SquareTexturedSkybox.CODEC).build();
        ANIMATED_SQUARE_TEXTURED_SKYBOX = SkyboxType.Builder.create(AnimatedSquareTexturedSkybox.class, "animated-square-textured").add(2, AnimatedSquareTexturedSkybox.CODEC).build();
        SKYBOX_ID_CODEC = Codec.STRING.xmap((s) -> {
            if (!s.contains(":")) {
                return new Identifier(FabricSkyBoxesClient.MODID, s.replace('-', '_'));
            }
            return new Identifier(s.replace('-', '_'));
        }, (id) -> {
            if (id.getNamespace().equals(FabricSkyBoxesClient.MODID)) {
                return id.getPath().replace('_', '-');
            }
            return id.toString().replace('_', '-');
        });
    }

    public static class Builder<T extends AbstractSkybox> {
        private String name;
        private final ImmutableBiMap.Builder<Integer, Codec<T>> builder = ImmutableBiMap.builder();
        private boolean legacySupported = false;
        private Supplier<T> factory;

        private Builder() {
        }

        public static <S extends AbstractSkybox> Builder<S> create(Class<S> clazz, String name) {
            Builder<S> builder = new Builder<>();
            builder.name = name;
            return builder;
        }

        public static <S extends AbstractSkybox> Builder<S> create(String name) {
            Builder<S> builder = new Builder<>();
            builder.name = name;
            return builder;
        }

        protected Builder<T> legacySupported() {
            this.legacySupported = true;
            return this;
        }

        protected Builder<T> factory(Supplier<T> factory) {
            this.factory = factory;
            return this;
        }

        public Builder<T> add(int schemaVersion, Codec<T> codec) {
            Preconditions.checkArgument(schemaVersion >= 2, "schema version was lesser than 2");
            Preconditions.checkNotNull(codec, "codec was null");
            this.builder.put(schemaVersion, codec);
            return this;
        }

        public SkyboxType<T> build() {
            if (this.legacySupported) {
                Preconditions.checkNotNull(this.factory, "factory was null");
            }
            return new SkyboxType<>(this.builder.build(), this.legacySupported, this.name, this.factory);
        }
    }
}
