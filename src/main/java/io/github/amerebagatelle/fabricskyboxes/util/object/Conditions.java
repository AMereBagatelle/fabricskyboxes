package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.FabricSkyBoxesClient;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Conditions {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(Conditions::getBiomes),
            Identifier.CODEC.listOf().optionalFieldOf("worlds", ImmutableList.of()).forGetter(Conditions::getWorlds),
            Weather.CODEC.listOf().optionalFieldOf("weather", ImmutableList.of()).forGetter(Conditions::getWeathers),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("xRanges", ImmutableList.of()).forGetter(Conditions::getXRanges),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("yRanges", ImmutableList.of()).forGetter(Conditions::getYRanges),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("heights", ImmutableList.of()).forGetter(Conditions::getHeights), // TODO for next version, remove this
            MinMaxEntry.CODEC.listOf().optionalFieldOf("zRanges", ImmutableList.of()).forGetter(Conditions::getZRanges)
    ).apply(instance, Conditions::new));
    public static final Conditions NO_CONDITIONS = new Builder().build();
    private final List<Identifier> biomes;
    private final List<Identifier> worlds;
    private final List<Weather> weathers;
    private final List<MinMaxEntry> yRanges;
    private final List<MinMaxEntry> zRanges;
    private final List<MinMaxEntry> xRanges;

    // For compatibility with older skyboxes
    private final List<MinMaxEntry> heights;

    public Conditions(List<Identifier> biomes, List<Identifier> worlds, List<Weather> weathers, List<MinMaxEntry> xRanges, List<MinMaxEntry> yRanges, List<MinMaxEntry> zRanges){
        this.biomes = biomes;
        this.worlds = worlds;
        this.weathers = weathers;
        this.xRanges = xRanges;
        this.yRanges = yRanges;
        // because it won't pass tests otherwise
        this.heights = ImmutableList.of();
        this.zRanges = zRanges;
    }

    public Conditions(List<Identifier> biomes, List<Identifier> worlds, List<Weather> weathers, List<MinMaxEntry> xRanges, List<MinMaxEntry> yRanges, List<MinMaxEntry> heights, List<MinMaxEntry> zRanges){
        this.biomes = biomes;
        this.worlds = worlds;
        this.weathers = weathers;
        this.xRanges = xRanges;
        this.heights = heights;
        if(!heights.isEmpty()) {
            FabricSkyBoxesClient.getLogger().error("A currently loaded skybox has the deprecated heights condition, please rename to yRanges condition");
            if(!yRanges.isEmpty()) throw new RuntimeException("Cannot have both height and yRanges in conditions block");
            this.yRanges = heights;
        } else {
            this.yRanges = yRanges;
        }
        this.zRanges = zRanges;
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

    public List<MinMaxEntry> getYRanges() {
        return this.yRanges;
    }

    public List<MinMaxEntry> getHeights() {
        return heights;
    }

    public List<MinMaxEntry> getXRanges() {
        return this.xRanges;
    }

    public List<MinMaxEntry> getZRanges() {
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
                .xRanges(skybox.getXRanges())
                .yRanges(skybox.getYRanges())
                .zRanges(skybox.getZRanges())
                .build();
    }

    public static class Builder {
        private final List<Identifier> biomes = Lists.newArrayList();
        private final List<Identifier> worlds = Lists.newArrayList();
        private final List<Weather> weathers = Lists.newArrayList();
        private final List<MinMaxEntry> yRanges = Lists.newArrayList();
        private final List<MinMaxEntry> zRanges = Lists.newArrayList();
        private final List<MinMaxEntry> xRanges = Lists.newArrayList();

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

        public Builder yRanges(Collection<MinMaxEntry> heights) {
            this.yRanges.addAll(heights);
            return this;
        }

        public Builder zRanges(Collection<MinMaxEntry> zRanges) {
            this.zRanges.addAll(zRanges);
            return this;
        }

        public Builder xRanges(Collection<MinMaxEntry> xRanges) {
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

        public Builder xRanges(MinMaxEntry... xRanges) {
            return this.xRanges(Lists.newArrayList(xRanges));
        }

        public Builder yRanges(MinMaxEntry... yRanges) {
            return this.yRanges(Lists.newArrayList(yRanges));
        }

        public Builder zRanges(MinMaxEntry... zRanges) {
            return this.zRanges(Lists.newArrayList(zRanges));
        }

        public Conditions build() {
            return new Conditions(this.biomes, this.worlds, this.weathers, this.xRanges, this.yRanges, this.zRanges);
        }
    }
}
