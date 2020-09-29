package io.github.amerebagatelle.fabricskyboxes.skyboxes.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Identifier;

public class DecorationTextures {
    public static final DecorationTextures DEFAULT = new DecorationTextures(new Identifier("textures/environment/sun.png"), new Identifier("textures/environment/moon_phases.png"));
    public static final Codec<DecorationTextures> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("sun").forGetter(DecorationTextures::getSun),
            Identifier.CODEC.fieldOf("moon").forGetter(DecorationTextures::getMoon)
    ).apply(instance, DecorationTextures::new));
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
}
