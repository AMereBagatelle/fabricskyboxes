package me.flashyreese.mods.nuit.components;

import net.minecraft.client.multiplayer.ClientLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/** Client-level runtime state for clock interpolation. */
final class ClockSourceRuntime {
    private static final Map<ClientLevel, LevelState> LEVEL_STATES = new WeakHashMap<>();

    private ClockSourceRuntime() {
    }

    static SampleView sample(ClientLevel level, ClockSource source) {
        Objects.requireNonNull(level, "Client level cannot be null");
        Objects.requireNonNull(source, "Clock source cannot be null");

        LevelState levelState = LEVEL_STATES.computeIfAbsent(level, ignored -> new LevelState());
        ResolvedSource resolvedSource = resolve(level, source);
        TickSample sample = levelState.samples.computeIfAbsent(source, ignored -> new TickSample());
        sample.update(level.getGameTime(), resolvedSource.ticks(), resolvedSource.identity());
        return new SampleView(sample.previousTicks, sample.currentTicks);
    }

    private static ResolvedSource resolve(ClientLevel level, ClockSource source) {
        return switch (source.type()) {
            case DEFAULT -> ResolvedSource.of(level.getDayTime(), ClockSource.Type.DEFAULT);
            case GAME_TIME -> ResolvedSource.of(level.getGameTime(), ClockSource.Type.GAME_TIME);
            case FIXED -> ResolvedSource.of(source.fixedTime(), source);
            case WORLD_CLOCK -> throw new IllegalStateException(
                    "Named world clocks are unavailable on Minecraft 1.21.1"
            );
        };
    }

    private static final class TickSample {
        private boolean initialized;
        private long tickId;
        private long previousTicks;
        private long currentTicks;
        private Object identity;

        private void update(long tickId, long ticks, Object identity) {
            if (!this.initialized || !Objects.equals(this.identity, identity)) {
                this.initialized = true;
                this.tickId = tickId;
                this.previousTicks = ticks;
                this.currentTicks = ticks;
                this.identity = identity;
                return;
            }

            if (this.tickId != tickId) {
                this.tickId = tickId;
                this.previousTicks = this.currentTicks;
            }
            this.currentTicks = ticks;
        }
    }

    record SampleView(long previousTicks, long currentTicks) {
        double interpolate(float tickDelta) {
            return (double) this.previousTicks
                    + ((double) this.currentTicks - this.previousTicks) * tickDelta;
        }
    }

    private static final class LevelState {
        private final Map<ClockSource, TickSample> samples = new HashMap<>();
    }

    private record ResolvedSource(long ticks, Object identity) {
        private static ResolvedSource of(long ticks, Object identity) {
            return new ResolvedSource(ticks, identity);
        }
    }
}
