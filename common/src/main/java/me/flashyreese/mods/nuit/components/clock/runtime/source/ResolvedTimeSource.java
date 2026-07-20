package me.flashyreese.mods.nuit.components.clock.runtime.source;

import me.flashyreese.mods.nuit.components.clock.ClockSource;
import me.flashyreese.mods.nuit.components.clock.DefaultClockSource;

public record ResolvedTimeSource(boolean available, long ticks, Object identity) {
    public static ResolvedTimeSource of(long ticks, Object identity) {
        return new ResolvedTimeSource(true, ticks, identity);
    }

    public static ResolvedTimeSource missing(ClockSource source) {
        return new ResolvedTimeSource(false, 0L, source);
    }

    public static ResolvedTimeSource empty() {
        return of(0L, DefaultClockSource.CODEC);
    }
}
