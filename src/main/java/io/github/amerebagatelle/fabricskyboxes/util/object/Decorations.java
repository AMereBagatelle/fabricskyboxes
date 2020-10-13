package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import net.minecraft.util.Identifier;

/**
 * The moon texture must be a 4 wide, 2 high, stacked texture.
 * This is due to the fact that the moon is rendered with a
 * different u/v value depending on the moon phase.
 */
public class Decorations {
    public static final Codec<Decorations> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("sun").forGetter(Decorations::getSunTexture),
            Identifier.CODEC.fieldOf("moon").forGetter(Decorations::getMoonTexture),
            Codec.BOOL.optionalFieldOf("showSun", true).forGetter(Decorations::isSunEnabled),
            Codec.BOOL.optionalFieldOf("showMoon", true).forGetter(Decorations::isMoonEnabled),
            Codec.BOOL.optionalFieldOf("showStars", true).forGetter(Decorations::isStarsEnabled)
    ).apply(instance, Decorations::new));
    public static final Decorations DEFAULT = new Decorations(WorldRendererAccess.getSUN(), WorldRendererAccess.getMOON_PHASES(), false, false, false);
    private final Identifier sunTexture;
    private final Identifier moonTexture;
    private final boolean sunEnabled;
    private final boolean moonEnabled;
    private final boolean starsEnabled;

    public Decorations(Identifier sun, Identifier moon, boolean sunEnabled, boolean moonEnabled, boolean starsEnabled) {
        this.sunTexture = sun;
        this.moonTexture = moon;
        this.sunEnabled = sunEnabled;
        this.moonEnabled = moonEnabled;
        this.starsEnabled = starsEnabled;
    }

    public Identifier getSunTexture() {
        return this.sunTexture;
    }

    public Identifier getMoonTexture() {
        return this.moonTexture;
    }

    public boolean isSunEnabled() {
        return this.sunEnabled;
    }

    public boolean isMoonEnabled() {
        return this.moonEnabled;
    }

    public boolean isStarsEnabled() {
        return this.starsEnabled;
    }

    public Decorations withSun(Identifier sun) {
        return new Decorations(sun, this.moonTexture, this.isSunEnabled(), this.isMoonEnabled(), this.isStarsEnabled());
    }

    public Decorations withMoon(Identifier moon) {
        return new Decorations(this.sunTexture, moon, this.isSunEnabled(), this.isMoonEnabled(), this.isStarsEnabled());
    }
}
