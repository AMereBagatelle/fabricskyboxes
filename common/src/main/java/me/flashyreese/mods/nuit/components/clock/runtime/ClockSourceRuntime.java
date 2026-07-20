package me.flashyreese.mods.nuit.components.clock.runtime;

import me.flashyreese.mods.nuit.components.clock.ClockSource;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedSource;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Client-level runtime state for clock interpolation.
 */
public final class ClockSourceRuntime {
    private static final Map<ClientLevel, LevelClockState> LEVEL_STATES = new WeakHashMap<>();

    private ClockSourceRuntime() {
    }

    public static InterpolatedSample sample(ClientLevel level, ClockSource source) {
        Objects.requireNonNull(level, "Client level cannot be null");
        Objects.requireNonNull(source, "Clock source cannot be null");

        LevelClockState levelState = LEVEL_STATES.computeIfAbsent(level, ignored -> new LevelClockState());
        ResolvedSource resolvedSource = source.resolve(levelState, level);

        TickSample sample = levelState.getTickSample(source);
        sample.update(
                level.getGameTime(),
                resolvedSource.ticks(),
                resolvedSource.available(),
                resolvedSource.identity()
        );

        return new InterpolatedSample(sample.previousTicks, sample.currentTicks);
    }
}
