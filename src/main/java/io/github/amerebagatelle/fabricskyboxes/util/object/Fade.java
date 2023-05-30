package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Fade {
    public static final Fade DEFAULT = new Fade(-1, -1, -1, -1, false);
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("startFadeIn", -1).forGetter(Fade::getStartFadeIn),
            Codec.INT.optionalFieldOf("endFadeIn", -1).forGetter(Fade::getEndFadeIn),
            Codec.INT.optionalFieldOf("startFadeOut", -1).forGetter(Fade::getStartFadeOut),
            Codec.INT.optionalFieldOf("endFadeOut", -1).forGetter(Fade::getEndFadeOut),
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::isAlwaysOn)
    ).apply(instance, Fade::new));
    private final int startFadeIn;
    private final int endFadeIn;
    private final int startFadeOut;
    private final int endFadeOut;
    private final boolean alwaysOn;

    public Fade(int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut, boolean alwaysOn) {
        this.startFadeIn = startFadeIn;
        this.endFadeIn = endFadeIn;
        this.startFadeOut = startFadeOut;
        this.endFadeOut = endFadeOut;
        this.alwaysOn = alwaysOn;
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

    public boolean isAlwaysOn() {
        return alwaysOn;
    }
}