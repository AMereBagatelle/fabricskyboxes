package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;

public class Conditions {
    public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(Conditions::getBiomes),
            Identifier.CODEC.listOf().optionalFieldOf("worlds", ImmutableList.of()).forGetter(Conditions::getWorlds),
            Identifier.CODEC.listOf().optionalFieldOf("dimensions", ImmutableList.of()).forGetter(Conditions::getDimensions),
            Identifier.CODEC.listOf().optionalFieldOf("effects", ImmutableList.of()).forGetter(Conditions::getEffects),
            Weather.CODEC.listOf().optionalFieldOf("weather", ImmutableList.of()).forGetter(Conditions::getWeathers),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("xRanges", ImmutableList.of()).forGetter(Conditions::getXRanges),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("yRanges", ImmutableList.of()).forGetter(Conditions::getYRanges),
            MinMaxEntry.CODEC.listOf().optionalFieldOf("zRanges", ImmutableList.of()).forGetter(Conditions::getZRanges),
            Loop.CODEC.optionalFieldOf("loop", Loop.DEFAULT).forGetter(Conditions::getLoop)
    ).apply(instance, Conditions::new));
    public static final Conditions DEFAULT = new Builder().build();
    private final List<Identifier> biomes;
    private final List<Identifier> worlds;
    private final List<Identifier> dimensions;
    private final List<Identifier> effects;
    private final List<Weather> weathers;
    private final List<MinMaxEntry> yRanges;
    private final List<MinMaxEntry> zRanges;
    private final List<MinMaxEntry> xRanges;
    private final Loop loop;

    public Conditions(List<Identifier> biomes, List<Identifier> worlds, List<Identifier> dimensions, List<Identifier> effects, List<Weather> weathers, List<MinMaxEntry> xRanges, List<MinMaxEntry> yRanges, List<MinMaxEntry> zRanges, Loop loop) {
        this.biomes = biomes;
        this.worlds = worlds;
        this.dimensions = dimensions;
        this.effects = effects;
        this.weathers = weathers;
        this.xRanges = xRanges;
        this.yRanges = yRanges;
        this.zRanges = zRanges;
        this.loop = loop;
    }

    public static Conditions ofSkybox(AbstractSkybox skybox) {
        return new Builder()
                .biomes(skybox.getConditions().getBiomes())
                .worlds(skybox.getConditions().getWorlds())
                .dimensions(skybox.getConditions().getDimensions())
                .effects(skybox.getConditions().getEffects())
                .weather(skybox.getConditions().getWeathers())
                .xRanges(skybox.getConditions().getXRanges())
                .yRanges(skybox.getConditions().getYRanges())
                .zRanges(skybox.getConditions().getZRanges())
                .loop(skybox.getConditions().getLoop())
                .build();
    }

    public List<Identifier> getBiomes() {
        return this.biomes;
    }

    public List<Identifier> getWorlds() {
        return this.worlds;
    }

    public List<Identifier> getDimensions() {
        return dimensions;
    }

    public List<Identifier> getEffects() {
        return effects;
    }

    public List<Weather> getWeathers() {
        return this.weathers;
    }

    public List<MinMaxEntry> getYRanges() {
        return this.yRanges;
    }

    public List<MinMaxEntry> getXRanges() {
        return this.xRanges;
    }

    public List<MinMaxEntry> getZRanges() {
        return this.zRanges;
    }

    public Loop getLoop() {
        return this.loop;
    }

    public static class Builder {
        private final List<Identifier> biomes = Lists.newArrayList();
        private final List<Identifier> worlds = Lists.newArrayList();
        private final List<Identifier> dimensions = Lists.newArrayList();
        private final List<Identifier> effects = Lists.newArrayList();
        private final List<Weather> weathers = Lists.newArrayList();
        private final List<MinMaxEntry> yRanges = Lists.newArrayList();
        private final List<MinMaxEntry> zRanges = Lists.newArrayList();
        private final List<MinMaxEntry> xRanges = Lists.newArrayList();
        private Loop loop = Loop.DEFAULT;

        public Builder biomes(Collection<Identifier> biomeIds) {
            this.biomes.addAll(biomeIds);
            return this;
        }

        public Builder worlds(Collection<Identifier> worldIds) {
            this.worlds.addAll(worldIds);
            return this;
        }

        public Builder dimensions(Collection<Identifier> dimensionIds) {
            this.dimensions.addAll(dimensionIds);
            return this;
        }

        public Builder effects(Collection<Identifier> effectIds) {
            this.effects.addAll(effectIds);
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

        public Builder loop(Loop loop) {
            this.loop = loop;
            return this;
        }

        public Conditions build() {
            return new Conditions(this.biomes, this.worlds, this.dimensions, this.effects, this.weathers, this.xRanges, this.yRanges, this.zRanges, this.loop);
        }
    }
}
