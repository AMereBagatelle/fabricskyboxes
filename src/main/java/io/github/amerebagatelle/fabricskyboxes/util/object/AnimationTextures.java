package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

import java.util.List;

public class AnimationTextures {
    public static final Codec<AnimationTextures> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().fieldOf("north").forGetter(AnimationTextures::getNorth),
            Identifier.CODEC.listOf().fieldOf("south").forGetter(AnimationTextures::getSouth),
            Identifier.CODEC.listOf().fieldOf("east").forGetter(AnimationTextures::getEast),
            Identifier.CODEC.listOf().fieldOf("west").forGetter(AnimationTextures::getWest),
            Identifier.CODEC.listOf().fieldOf("top").forGetter(AnimationTextures::getTop),
            Identifier.CODEC.listOf().fieldOf("bottom").forGetter(AnimationTextures::getBottom)
    ).apply(instance, AnimationTextures::new));
    private final List<Identifier> north;
    private final List<Identifier> south;
    private final List<Identifier> east;
    private final List<Identifier> west;
    private final List<Identifier> top;
    private final List<Identifier> bottom;

    public AnimationTextures(List<Identifier> north, List<Identifier> south, List<Identifier> east, List<Identifier> west, List<Identifier> top, List<Identifier> bottom) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.top = top;
        this.bottom = bottom;
    }

    public List<Identifier> getNorth() {
        return this.north;
    }

    public List<Identifier> getSouth() {
        return this.south;
    }

    public List<Identifier> getEast() {
        return this.east;
    }

    public List<Identifier> getWest() {
        return this.west;
    }

    public List<Identifier> getTop() {
        return this.top;
    }

    public List<Identifier> getBottom() {
        return this.bottom;
    }
}