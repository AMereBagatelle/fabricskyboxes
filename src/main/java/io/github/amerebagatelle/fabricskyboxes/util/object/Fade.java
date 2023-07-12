package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;

public class Fade {
    public static final Fade DEFAULT = new Fade(0, 0, 0, 0, false);
    public static final Codec<Fade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("startFadeIn", 0).forGetter(Fade::getStartFadeIn),
            Codec.INT.optionalFieldOf("endFadeIn", 0).forGetter(Fade::getEndFadeIn),
            Codec.INT.optionalFieldOf("startFadeOut", 0).forGetter(Fade::getStartFadeOut),
            Codec.INT.optionalFieldOf("endFadeOut", 0).forGetter(Fade::getEndFadeOut),
            Codec.BOOL.optionalFieldOf("alwaysOn", false).forGetter(Fade::isAlwaysOn)
    ).apply(instance, Fade::new));
    private final int startFadeIn;
    private final int endFadeIn;
    private final int startFadeOut;
    private final int endFadeOut;
    private final boolean alwaysOn;

    public Fade(int startFadeIn, int endFadeIn, int startFadeOut, int endFadeOut, boolean alwaysOn) {
        this.startFadeIn = normalizeAndWarnIfDifferent(startFadeIn, alwaysOn);
        this.endFadeIn = normalizeAndWarnIfDifferent(endFadeIn, alwaysOn);
        this.startFadeOut = normalizeAndWarnIfDifferent(startFadeOut, alwaysOn);
        this.endFadeOut = normalizeAndWarnIfDifferent(endFadeOut, alwaysOn);
        this.alwaysOn = alwaysOn;
    }

    private static int normalizeAndWarnIfDifferent(int time, boolean ignore) {
        if (ignore) {
            return time;
        }
        int normalized = Utils.normalizeTickTime(time);
        return Utils.warnIfDifferent(time, normalized, String.format("Provided time of %s has been normalized to %s", time, normalized));
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