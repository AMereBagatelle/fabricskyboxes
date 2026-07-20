package me.flashyreese.mods.nuit.components.clock.runtime;

import net.minecraft.util.Mth;

public class InterpolatedSample {
    protected long previousTicks;
    protected long currentTicks;

    public InterpolatedSample(long previousTicks, long currentTicks) {
        this.previousTicks = previousTicks;
        this.currentTicks = currentTicks;
    }

    public InterpolatedSample() {
        this(0L, 0L);
    }

    public double interpolate(float tickDelta) {
        return Mth.lerp(tickDelta, this.previousTicks, this.currentTicks);
    }

    public long previousTicks() {
        return this.previousTicks;
    }

    public long currentTicks() {
        return this.currentTicks;
    }
}
