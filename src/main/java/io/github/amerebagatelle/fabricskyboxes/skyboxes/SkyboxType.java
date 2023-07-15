package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.*;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class SkyboxType<T extends AbstractSkybox> {
    public static final Registry<SkyboxType<? extends AbstractSkybox>> REGISTRY;
    public static final SkyboxType<MonoColorSkybox> MONO_COLOR_SKYBOX;
    public static final SkyboxType<OverworldSkybox> OVERWORLD_SKYBOX;
    public static final SkyboxType<EndSkybox> END_SKYBOX;
    public static final SkyboxType<SquareTexturedSkybox> SQUARE_TEXTURED_SKYBOX;
    public static final SkyboxType<SingleSpriteSquareTexturedSkybox> SINGLE_SPRITE_SQUARE_TEXTURED_SKYBOX;
    public static final SkyboxType<AnimatedSquareTexturedSkybox> ANIMATED_SQUARE_TEXTURED_SKYBOX;
    public static final SkyboxType<SingleSpriteAnimatedSquareTexturedSkybox> SINGLE_SPRITE_ANIMATED_SQUARE_TEXTURED_SKYBOX;
    public static final SkyboxType<SomeNewTypeSkybox> SOME_NEW_TYPE_SKYBOX;
    public static final Codec<Identifier> SKYBOX_ID_CODEC;

    static {
        REGISTRY = FabricRegistryBuilder.<SkyboxType<? extends AbstractSkybox>, SimpleRegistry<SkyboxType<? extends AbstractSkybox>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(FabricSkyBoxesClient.MODID, "skybox_type")), Lifecycle.stable())).buildAndRegister();
        MONO_COLOR_SKYBOX = register(SkyboxType.Builder.create(MonoColorSkybox.class, "monocolor").legacySupported().deserializer(LegacyDeserializer.MONO_COLOR_SKYBOX_DESERIALIZER).factory(MonoColorSkybox::new).add(2, MonoColorSkybox.CODEC).build());
        OVERWORLD_SKYBOX = register(SkyboxType.Builder.create(OverworldSkybox.class, "overworld").add(2, OverworldSkybox.CODEC).build());
        END_SKYBOX = register(SkyboxType.Builder.create(EndSkybox.class, "end").add(2, EndSkybox.CODEC).build());
        SQUARE_TEXTURED_SKYBOX = register(SkyboxType.Builder.create(SquareTexturedSkybox.class, "square-textured").deserializer(LegacyDeserializer.SQUARE_TEXTURED_SKYBOX_DESERIALIZER).legacySupported().factory(SquareTexturedSkybox::new).add(2, SquareTexturedSkybox.CODEC).build());
        SINGLE_SPRITE_SQUARE_TEXTURED_SKYBOX = register(SkyboxType.Builder.create(SingleSpriteSquareTexturedSkybox.class, "single-sprite-square-textured").add(2, SingleSpriteSquareTexturedSkybox.CODEC).build());
        ANIMATED_SQUARE_TEXTURED_SKYBOX = register(SkyboxType.Builder.create(AnimatedSquareTexturedSkybox.class, "animated-square-textured").add(2, AnimatedSquareTexturedSkybox.CODEC).build());
        SINGLE_SPRITE_ANIMATED_SQUARE_TEXTURED_SKYBOX = register(SkyboxType.Builder.create(SingleSpriteAnimatedSquareTexturedSkybox.class, "single-sprite-animated-square-textured").add(2, SingleSpriteAnimatedSquareTexturedSkybox.CODEC).build());
        SOME_NEW_TYPE_SKYBOX = register(SkyboxType.Builder.create(SomeNewTypeSkybox.class, "some-new-type").add(2, SomeNewTypeSkybox.CODEC).build());
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

    private final BiMap<Integer, Codec<T>> codecBiMap;
    private final boolean legacySupported;
    private final String name;
    @Nullable
    private final Supplier<T> factory;
    @Nullable
    private final LegacyDeserializer<T> deserializer;
    private SkyboxType(BiMap<Integer, Codec<T>> codecBiMap, boolean legacySupported, String name, @Nullable Supplier<T> factory, @Nullable LegacyDeserializer<T> deserializer) {
        this.codecBiMap = codecBiMap;
        this.legacySupported = legacySupported;
        this.name = name;
        this.factory = factory;
        this.deserializer = deserializer;
    }

    public static void initRegistry() {
        if (REGISTRY == null) {
            System.err.println("[FabricSkyboxes] Registry not loaded?");
        }
    }

    private static <T extends AbstractSkybox> SkyboxType<T> register(SkyboxType<T> type) {
        return Registry.register(SkyboxType.REGISTRY, type.createId(FabricSkyBoxesClient.MODID), type);
    }

    public String getName() {
        return this.name;
    }

    public boolean isLegacySupported() {
        return this.legacySupported;
    }

    @NotNull
    public T instantiate() {
        return Objects.requireNonNull(Objects.requireNonNull(this.factory, "Can't instantiate from a null factory").get());
    }

    @Nullable
    public LegacyDeserializer<T> getDeserializer() {
        return this.deserializer;
    }

    public Identifier createId(String namespace) {
        return this.createIdFactory().apply(namespace);
    }

    public Function<String, Identifier> createIdFactory() {
        return (ns) -> new Identifier(ns, this.getName().replace('-', '_'));
    }

    public Codec<T> getCodec(int schemaVersion) {
        return Objects.requireNonNull(this.codecBiMap.get(schemaVersion), String.format("Unsupported schema version '%d' for skybox type %s", schemaVersion, this.name));
    }

    public static class Builder<T extends AbstractSkybox> {
        private final ImmutableBiMap.Builder<Integer, Codec<T>> builder = ImmutableBiMap.builder();
        private String name;
        private boolean legacySupported = false;
        private Supplier<T> factory;
        private LegacyDeserializer<T> deserializer;

        private Builder() {
        }

        public static <S extends AbstractSkybox> Builder<S> create(@SuppressWarnings("unused") Class<S> clazz, String name) {
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

        protected Builder<T> deserializer(LegacyDeserializer<T> deserializer) {
            this.deserializer = deserializer;
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
                Preconditions.checkNotNull(this.deserializer, "deserializer was null");
            }
            return new SkyboxType<>(this.builder.build(), this.legacySupported, this.name, this.factory, this.deserializer);
        }

        public SkyboxType<T> buildAndRegister(String namespace) {
            return Registry.register(SkyboxType.REGISTRY, new Identifier(namespace, this.name.replace('-', '_')), this.build());
        }
    }
}
