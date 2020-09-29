package amerebagatelle.github.io.fabricskyboxes.skyboxes.object;

import net.minecraft.util.Identifier;

public class Textures {
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
