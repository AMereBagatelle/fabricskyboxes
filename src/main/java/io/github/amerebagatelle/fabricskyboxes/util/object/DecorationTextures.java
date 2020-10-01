package io.github.amerebagatelle.fabricskyboxes.util.object;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

/**
 * The moon texture must be a 4 wide, 2 high, stacked texture.
 * This is due to the fact that the moon is rendering with a
 * different u/v value depending on the moon phase.
 */
public class DecorationTextures {
    public static final Codec<DecorationTextures> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("sun").forGetter(DecorationTextures::getSun),
            Identifier.CODEC.fieldOf("moon").forGetter(DecorationTextures::getMoon)
    ).apply(instance, DecorationTextures::new));
    public static final DecorationTextures DEFAULT = new DecorationTextures(WorldRendererAccess.getSUN(), WorldRendererAccess.getMOON_PHASES());
    private final Identifier sun;
    private final Identifier moon;

    public DecorationTextures(Identifier sun, Identifier moon) {
        this.sun = sun;
        this.moon = moon;
    }

    public Identifier getSun() {
        return this.sun;
    }

    public Identifier getMoon() {
        return this.moon;
    }

    public DecorationTextures setSun(Identifier sun) {
        return new DecorationTextures(sun, this.moon);
    }

    public DecorationTextures setMoon(Identifier moon) {
        return new DecorationTextures(this.sun, moon);
    }
}
