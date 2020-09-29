package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Fade {
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("startFadeIn").forGetter(Fade::getStartFadeIn),
            Codec.INT.fieldOf("endFadeIn").forGetter(Fade::getEndFadeIn),
            Codec.INT.fieldOf("startFadeOut").forGetter(Fade::getStartFadeOut),
            Codec.INT.fieldOf("endFadeOut").forGetter(Fade::getEndFadeOut)
    ).apply(instance, Fade::new));
    private final int startFadeIn;
    private final int endFadeIn;
    private final int startFadeOut;
    private final int endFadeOut;

    public Fade(int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut) {
        this.startFadeIn = startFadeIn;
        this.endFadeIn = endFadeIn;
        this.startFadeOut = startFadeOut;
        this.endFadeOut = endFadeOut;
    }

    public int getStartFadeIn() {
        return this.startFadeIn;
    }

    public int getEndFadeIn() {
        return this.endFadeIn;
    }

    public int getStartFadeOut() {
        return this.startFadeOut;
    }

    public int getEndFadeOut() {
        return this.endFadeOut;
    }
}