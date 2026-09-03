package me.flashyreese.mods.nuit.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class Conditions {
    private static final Map<ResourceLocation, ResourceLocation> LEGACY_WORLD_TO_SKYBOX = Map.of(
            ResourceLocation.withDefaultNamespace("overworld"), ResourceLocation.withDefaultNamespace("overworld"),
            ResourceLocation.withDefaultNamespace("the_nether"), ResourceLocation.withDefaultNamespace("none"),
            ResourceLocation.withDefaultNamespace("nether"), ResourceLocation.withDefaultNamespace("none"),
            ResourceLocation.withDefaultNamespace("the_end"), ResourceLocation.withDefaultNamespace("end")
    );
    private static final Codec<Condition<ResourceLocation>> SKYBOX_CONDITION_CODEC = Condition.create(ResourceLocation.CODEC);
    private static final Codec<Condition<ResourceLocation>> LEGACY_WORLD_CONDITION_CODEC = SKYBOX_CONDITION_CODEC.xmap(Conditions::normalizeLegacyWorldCondition, condition -> Condition.of());

    public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Condition.create(ResourceLocation.CODEC).optionalFieldOf("biomes", Condition.of()).forGetter(Conditions::getBiomes),
            SKYBOX_CONDITION_CODEC.optionalFieldOf("skyboxes", Condition.of()).forGetter(Conditions::getSkyboxes),
            LEGACY_WORLD_CONDITION_CODEC.optionalFieldOf("worlds", Condition.of()).forGetter(conditions -> Condition.of()),
            Condition.create(ResourceLocation.CODEC).optionalFieldOf("dimensions", Condition.of()).forGetter(Conditions::getDimensions),
            Condition.create(ResourceLocation.CODEC).optionalFieldOf("effects", Condition.of()).forGetter(Conditions::getEffects),
            Condition.create(Weather.CODEC).optionalFieldOf("weather", Condition.of()).forGetter(Conditions::getWeathers),
            Condition.create(RangeEntry.CODEC).optionalFieldOf("xRanges", Condition.of()).forGetter(Conditions::getXRanges),
            Condition.create(RangeEntry.CODEC).optionalFieldOf("yRanges", Condition.of()).forGetter(Conditions::getYRanges),
            Condition.create(RangeEntry.CODEC).optionalFieldOf("zRanges", Condition.of()).forGetter(Conditions::getZRanges)
    ).apply(instance, Conditions::create));

    private final Condition<ResourceLocation> biomes;
    private final Condition<ResourceLocation> skyboxes;
    private final Condition<ResourceLocation> dimensions;
    private final Condition<ResourceLocation> effects;
    private final Condition<Weather> weathers;
    private final Condition<RangeEntry> xRanges;
    private final Condition<RangeEntry> yRanges;
    private final Condition<RangeEntry> zRanges;

    public Conditions(Condition<ResourceLocation> biomes, Condition<ResourceLocation> skyboxes, Condition<ResourceLocation> dimensions, Condition<ResourceLocation> effects, Condition<Weather> weathers, Condition<RangeEntry> xRanges, Condition<RangeEntry> yRanges, Condition<RangeEntry> zRanges) {
        this.biomes = biomes;
        this.skyboxes = skyboxes;
        this.dimensions = dimensions;
        this.effects = effects;
        this.weathers = weathers;
        this.xRanges = xRanges;
        this.yRanges = yRanges;
        this.zRanges = zRanges;
    }

    public static Conditions of() {
        return new Conditions(Condition.of(), Condition.of(), Condition.of(), Condition.of(), Condition.of(), Condition.of(), Condition.of(), Condition.of());
    }

    public Condition<ResourceLocation> getBiomes() {
        return this.biomes;
    }

    public Condition<ResourceLocation> getSkyboxes() {
        return this.skyboxes;
    }

    /** @deprecated Use {@link #getSkyboxes()}. */
    @Deprecated(forRemoval = true)
    public Condition<ResourceLocation> getWorlds() {
        return this.getSkyboxes();
    }

    public Condition<ResourceLocation> getDimensions() {
        return this.dimensions;
    }

    public Condition<ResourceLocation> getEffects() {
        return this.effects;
    }

    public Condition<Weather> getWeathers() {
        return this.weathers;
    }

    public Condition<RangeEntry> getXRanges() {
        return this.xRanges;
    }

    public Condition<RangeEntry> getYRanges() {
        return this.yRanges;
    }

    public Condition<RangeEntry> getZRanges() {
        return this.zRanges;
    }

    private static Conditions create(
            Condition<ResourceLocation> biomes,
            Condition<ResourceLocation> skyboxes,
            Condition<ResourceLocation> legacyWorlds,
            Condition<ResourceLocation> dimensions,
            Condition<ResourceLocation> effects,
            Condition<Weather> weathers,
            Condition<RangeEntry> xRanges,
            Condition<RangeEntry> yRanges,
            Condition<RangeEntry> zRanges
    ) {
        Condition<ResourceLocation> resolvedSkyboxes = skyboxes.entries().isEmpty() ? legacyWorlds : skyboxes;
        return new Conditions(
                biomes,
                resolvedSkyboxes,
                dimensions,
                effects,
                weathers,
                xRanges,
                yRanges,
                zRanges
        );
    }

    private static Condition<ResourceLocation> normalizeLegacyWorldCondition(Condition<ResourceLocation> condition) {
        List<ResourceLocation> entries = new ObjectArrayList<>(condition.entries().size());
        for (ResourceLocation entry : condition.entries()) {
            entries.add(LEGACY_WORLD_TO_SKYBOX.getOrDefault(entry, entry));
        }
        return new Condition<>(condition.excludes(), entries);
    }
}
