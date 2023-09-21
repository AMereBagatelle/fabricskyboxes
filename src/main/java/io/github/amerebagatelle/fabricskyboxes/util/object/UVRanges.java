package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class UVRanges {
    public static final Codec<UVRanges> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UVRange.CODEC.fieldOf("north").forGetter(UVRanges::getNorth),
            UVRange.CODEC.fieldOf("south").forGetter(UVRanges::getSouth),
            UVRange.CODEC.fieldOf("east").forGetter(UVRanges::getEast),
            UVRange.CODEC.fieldOf("west").forGetter(UVRanges::getWest),
            UVRange.CODEC.fieldOf("top").forGetter(UVRanges::getTop),
            UVRange.CODEC.fieldOf("bottom").forGetter(UVRanges::getBottom)
    ).apply(instance, UVRanges::new));
    private final List<UVRange> textureList = Lists.newArrayList();
    private final UVRange north;
    private final UVRange south;
    private final UVRange east;
    private final UVRange west;
    private final UVRange top;
    private final UVRange bottom;

    public UVRanges(UVRange north, UVRange south, UVRange east, UVRange west, UVRange top, UVRange bottom) {
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

    public UVRange getNorth() {
        return this.north;
    }

    public UVRange getSouth() {
        return this.south;
    }

    public UVRange getEast() {
        return this.east;
    }

    public UVRange getWest() {
        return this.west;
    }

    public UVRange getTop() {
        return this.top;
    }

    public UVRange getBottom() {
        return this.bottom;
    }

    // 0 = bottom
    // 1 = north
    // 2 = south
    // 3 = top
    // 4 = east
    // 5 = west
    public UVRange byId(int i) {
        return this.textureList.get(i);
    }
}