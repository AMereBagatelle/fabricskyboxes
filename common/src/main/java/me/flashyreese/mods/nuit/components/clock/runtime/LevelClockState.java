package me.flashyreese.mods.nuit.components.clock.runtime;

import me.flashyreese.mods.nuit.components.clock.ClockSource;

import java.util.HashMap;
import java.util.Map;

public class LevelClockState {
    protected final Map<ClockSource, TickSample> samples = new HashMap<>();

    public TickSample getTickSample(ClockSource clockSource) {
        return this.samples.computeIfAbsent(clockSource, ignored -> new TickSample());
    }
}
