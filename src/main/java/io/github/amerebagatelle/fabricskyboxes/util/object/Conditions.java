package io.github.amerebagatelle.fabricskyboxes.util.object;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class Conditions {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(Conditions::getBiomes),
            Identifier.CODEC.listOf().optionalFieldOf("worlds", ImmutableList.of()).forGetter(Conditions::getWorlds),
            Weather.CODEC.listOf().optionalFieldOf("weather", ImmutableList.of()).forGetter(Conditions::getWeathers),
            HeightEntry.CODEC.listOf().optionalFieldOf("heights", ImmutableList.of()).forGetter(Conditions::getHeights),
            RangeEntry.CODEC.listOf().optionalFieldOf("xRanges", ImmutableList.of()).forGetter(Conditions::getXRanges),
            RangeEntry.CODEC.listOf().optionalFieldOf("zRanges", ImmutableList.of()).forGetter(Conditions::getZRanges)
    ).apply(instance, Conditions::new));
    public static final Conditions NO_CONDITIONS = new Builder().build();
    private final List<Identifier> biomes;
    private final List<Identifier> worlds;
    private final List<Weather> weathers;
    private final List<HeightEntry> heights;
    private final List<RangeEntry> zRanges;
    private final List<RangeEntry> xRanges;

    public Conditions(List<Identifier> biomes, List<Identifier> worlds, List<Weather> weathers, List<HeightEntry> heights, List<RangeEntry> zRanges, List<RangeEntry> xRanges){
        this.biomes = biomes;
        this.worlds = worlds;
        this.weathers = weathers;
        this.heights = heights;
        this.zRanges = zRanges;
        this.xRanges = xRanges;
    }

    public List<Identifier> getBiomes() {
        return this.biomes;
    }

    public List<Identifier> getWorlds() {
        return this.worlds;
    }

    public List<Weather> getWeathers() {
        return this.weathers;
    }

    public List<HeightEntry> getHeights() {
        return this.heights;
    }

    public List<RangeEntry> getXRanges() {
        return this.xRanges;
    }

    public List<RangeEntry> getZRanges() {
        return this.zRanges;
    }

    public static Conditions ofSkybox(AbstractSkybox skybox) {
        return new Builder()
                .biomes(skybox.getBiomes())
                .worlds(skybox.getWorlds())
                .weather(skybox.getWeather()
                        .stream()
                        .map(Weather::fromString)
                        .collect(Collectors.toSet()))
                .heights(skybox.getHeightRanges())
                .zRanges(skybox.getConditions().getZRanges())
                .xRanges(skybox.getConditions().getZRanges())
                .build();
    }

    public static class Builder {
        private final List<Identifier> biomes = Lists.newArrayList();
        private final List<Identifier> worlds = Lists.newArrayList();
        private final List<Weather> weathers = Lists.newArrayList();
        private final List<HeightEntry> heights = Lists.newArrayList();
        private final List<RangeEntry> zRanges = Lists.newArrayList();
        private final List<RangeEntry> xRanges = Lists.newArrayList();

        public Builder biomes(Collection<Identifier> biomeIds) {
            this.biomes.addAll(biomeIds);
            return this;
        }

        public Builder worlds(Collection<Identifier> worldIds) {
            this.worlds.addAll(worldIds);
            return this;
        }

        public Builder weather(Collection<Weather> weathers) {
            this.weathers.addAll(weathers);
            return this;
        }

        public Builder heights(Collection<HeightEntry> heights) {
            this.heights.addAll(heights);
            return this;
        }

        public Builder zRanges(Collection<RangeEntry> zRanges) {
            this.zRanges.addAll(zRanges);
            return this;
        }

        public Builder xRanges(Collection<RangeEntry> xRanges) {
            this.xRanges.addAll(xRanges);
            return this;
        }

        public Builder biomes(Identifier... biomeIds) {
            return this.biomes(Lists.newArrayList(biomeIds));
        }

        public Builder worlds(Identifier... worldIds) {
            return this.worlds(Lists.newArrayList(worldIds));
        }

        public Builder weather(Weather... weathers) {
            return this.weather(Lists.newArrayList(weathers));
        }

        public Builder heights(HeightEntry... heights) {
            return this.heights(Lists.newArrayList(heights));
        }

        public Builder zRanges(RangeEntry... zRanges) {
            return this.zRanges(Lists.newArrayList(zRanges));
        }

        public Builder xRanges(RangeEntry... xRanges) {
            return this.xRanges(Lists.newArrayList(xRanges));
        }

        public Conditions build() {
            return new Conditions(this.biomes, this.worlds, this.weathers, this.heights, this.zRanges, this.xRanges);
        }
    }
}
