package io.github.amerebagatelle.fabricskyboxes.util.object;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class Conditions {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().fieldOf("biomes").forGetter(Conditions::getBiomes),
            Identifier.CODEC.listOf().fieldOf("worlds").forGetter(Conditions::getWorlds),
            Weather.CODEC.listOf().fieldOf("weather").forGetter(Conditions::getWeathers),
            HeightEntry.CODEC.listOf().fieldOf("heights").forGetter(Conditions::getHeights)
    ).apply(instance, Conditions::new));
    private final List<Identifier> biomes;
    private final List<Identifier> worlds;
    private final List<Weather> weathers;
    private final List<HeightEntry> heights;

    public Conditions(List<Identifier> biomes, List<Identifier> worlds, List<Weather> weathers, List<HeightEntry> heights) {
        this.biomes = biomes;
        this.worlds = worlds;
        this.weathers = weathers;
        this.heights = heights;
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

    public static Conditions ofSkybox(AbstractSkybox skybox) {
        return new Builder()
                .biomes(skybox.getBiomes())
                .worlds(skybox.getWorlds())
                .weather(skybox.getWeather()
                        .stream()
                        .map(Weather::fromString)
                        .collect(Collectors.toSet()))
                .heights(skybox.getHeightRanges())
                .build();
    }

    public static class Builder {
        private final List<Identifier> biomes = Lists.newArrayList();
        private final List<Identifier> worlds = Lists.newArrayList();
        private final List<Weather> weathers = Lists.newArrayList();
        private final List<HeightEntry> heights = Lists.newArrayList();

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

        public Conditions build() {
            return new Conditions(this.biomes, this.worlds, this.weathers, this.heights);
        }
    }
}
