package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class Textures {
    public static final Codec<Textures> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("north").forGetter(Textures::getNorth),
            Identifier.CODEC.fieldOf("south").forGetter(Textures::getSouth),
            Identifier.CODEC.fieldOf("east").forGetter(Textures::getEast),
            Identifier.CODEC.fieldOf("west").forGetter(Textures::getWest),
            Identifier.CODEC.fieldOf("top").forGetter(Textures::getTop),
            Identifier.CODEC.fieldOf("bottom").forGetter(Textures::getBottom)
    ).apply(instance, Textures::new));
    private final Identifier north;
    private final Identifier south;
    private final Identifier east;
    private final Identifier west;
    private final Identifier top;
    private final Identifier bottom;

    public Textures(Identifier north, Identifier south, Identifier east, Identifier west, Identifier top, Identifier bottom) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.top = top;
        this.bottom = bottom;
    }

    public Identifier getNorth() {
        return this.north;
    }

    public Identifier getSouth() {
        return this.south;
    }

    public Identifier getEast() {
        return this.east;
    }

    public Identifier getWest() {
        return this.west;
    }

    public Identifier getTop() {
        return this.top;
    }

    public Identifier getBottom() {
        return this.bottom;
    }
}