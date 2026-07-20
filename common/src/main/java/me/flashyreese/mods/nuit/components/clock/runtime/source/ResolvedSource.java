package me.flashyreese.mods.nuit.components.clock.runtime.source;

import me.flashyreese.mods.nuit.components.clock.ClockSource;
import me.flashyreese.mods.nuit.components.clock.DefaultClockSource;

public record ResolvedSource(boolean available, long ticks, Object identity) {
    public static ResolvedSource of(long ticks, Object identity) {
        return new ResolvedSource(true, ticks, identity);
    }

    public static ResolvedSource missing(ClockSource source) {
        return new ResolvedSource(false, 0L, source);
    }

    public static ResolvedSource empty() {
        return of(0L, DefaultClockSource.CODEC);
    }
}
