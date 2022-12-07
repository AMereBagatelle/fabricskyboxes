package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.util.List;

public class Textures {
    public static final Codec<Textures> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Texture.CODEC.fieldOf("north").forGetter(Textures::getNorth),
            Texture.CODEC.fieldOf("south").forGetter(Textures::getSouth),
            Texture.CODEC.fieldOf("east").forGetter(Textures::getEast),
            Texture.CODEC.fieldOf("west").forGetter(Textures::getWest),
            Texture.CODEC.fieldOf("top").forGetter(Textures::getTop),
            Texture.CODEC.fieldOf("bottom").forGetter(Textures::getBottom)
    ).apply(instance, Textures::new));
    private final List<Texture> textureList = Lists.newArrayList();
    private final Texture north;
    private final Texture south;
    private final Texture east;
    private final Texture west;
    private final Texture top;
    private final Texture bottom;

    public Textures(Texture north, Texture south, Texture east, Texture west, Texture top, Texture bottom) {
        this.north = north;
        this.south = south;
        this.east = east;
        this.west = west;
        this.top = top;
        this.bottom = bottom;
        this.textureList.add(bottom);
        this.textureList.add(north);
        this.textureList.add(south);
        this.textureList.add(top);
        this.textureList.add(east);
        this.textureList.add(west);
    }

    public Textures(Identifier north, Identifier south, Identifier east, Identifier west, Identifier top, Identifier bottom) {
        this(new Texture(north), new Texture(south), new Texture(east), new Texture(west), new Texture(top), new Texture(bottom));
    }

    public Texture getNorth() {
        return this.north;
    }

    public Texture getSouth() {
        return this.south;
    }

    public Texture getEast() {
        return this.east;
    }

    public Texture getWest() {
        return this.west;
    }

    public Texture getTop() {
        return this.top;
    }

    public Texture getBottom() {
        return this.bottom;
    }

    // 0 = bottom
    // 1 = north
    // 2 = south
    // 3 = top
    // 4 = east
    // 5 = west
    public Texture byId(int i) {
        return this.textureList.get(i);
    }
}
