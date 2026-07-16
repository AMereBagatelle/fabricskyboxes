package me.flashyreese.mods.nuit.components;

import me.flashyreese.mods.nuit.NuitClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.timeline.Timeline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Client-level runtime state for clock interpolation.
 */
final class ClockSourceRuntime {
    private static final Map<ClientLevel, LevelState> LEVEL_STATES = new WeakHashMap<>();

    private ClockSourceRuntime() {
    }

    static SampleView sample(ClientLevel level, ClockSource source) {
        Objects.requireNonNull(level, "Client level cannot be null");
        Objects.requireNonNull(source, "Clock source cannot be null");

        LevelState levelState = LEVEL_STATES.computeIfAbsent(level, ignored -> new LevelState());
        ResolvedSource resolvedSource = levelState.resolve(level, source);
        TickSample sample = levelState.samples.computeIfAbsent(source, ignored -> new TickSample());
        sample.update(
                level.getGameTime(),
                resolvedSource.ticks(),
                resolvedSource.available(),
                resolvedSource.identity()
        );
        return new SampleView(sample.previousTicks, sample.currentTicks);
    }

    static final class TickSample {
        private boolean initialized;
        private boolean available;
        private long tickId;
        private long previousTicks;
        private long currentTicks;
        private Object identity;

        void update(long tickId, long ticks, boolean available, Object identity) {
            if (!this.initialized || this.available != available || !Objects.equals(this.identity, identity)) {
                this.initialized = true;
                this.available = available;
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

        double interpolate(float tickDelta) {
            return (double) this.previousTicks
                    + ((double) this.currentTicks - this.previousTicks) * tickDelta;
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
        private final Set<MissingSource> warnedMissingSources = new HashSet<>();

        private ResolvedSource resolve(ClientLevel level, ClockSource source) {
            return switch (source.type()) {
                case DEFAULT -> level.dimensionType().defaultClock()
                        .map(holder -> this.resolvedClock(level, holder))
                        .orElseGet(ResolvedSource::empty);
                case GAME_TIME -> ResolvedSource.of(level.getGameTime(), source);
                case FIXED -> ResolvedSource.of(source.fixedTime(), source);
                case WORLD_CLOCK -> this.resolveWorldClock(level, source);
            };
        }

        private ResolvedSource resolveWorldClock(ClientLevel level, ClockSource source) {
            Identifier id = source.clock().orElseThrow();
            ResourceKey<WorldClock> key = ResourceKey.create(Registries.WORLD_CLOCK, id);
            Optional<Holder.Reference<WorldClock>> clock = level.registryAccess().get(key);
            if (clock.isEmpty()) {
                this.warnMissing(level, source.type(), id);
                return ResolvedSource.missing(source);
            }

            this.warnedMissingSources.remove(new MissingSource(source.type(), id));
            return this.resolvedClock(level, clock.get());
        }

        private ResolvedSource resolvedClock(ClientLevel level, Holder<WorldClock> clock) {
            return new ResolvedSource(
                    true,
                    level.clockManager().getTotalTicks(clock),
                    clock
            );
        }

        private void warnMissing(ClientLevel level, ClockSource.Type type, Identifier id) {
            MissingSource missingSource = new MissingSource(type, id);
            if (this.warnedMissingSources.add(missingSource)) {
                ResourceKey<Timeline> timelineKey = ResourceKey.create(Registries.TIMELINE, id);
                if (level.registryAccess().get(timelineKey).isPresent()) {
                    NuitClient.getLogger().warn(
                            "Skybox time source {} is a timeline, not a World Clock. "
                                    + "Assign timelines to dimensions through a datapack instead of Nuit's clock property.",
                            id
                    );
                    return;
                }

                NuitClient.getLogger().warn(
                        "Skybox time source references missing world clock {}. "
                                + "It will remain at tick 0 until the entry exists.",
                        id
                );
            }
        }
    }

    private record ResolvedSource(boolean available, long ticks, Object identity) {
        private static ResolvedSource of(long ticks, Object identity) {
            return new ResolvedSource(true, ticks, identity);
        }

        private static ResolvedSource empty() {
            return new ResolvedSource(true, 0L, ClockSource.Type.DEFAULT);
        }

        private static ResolvedSource missing(ClockSource source) {
            return new ResolvedSource(false, 0L, source);
        }
    }

    private record MissingSource(ClockSource.Type type, Identifier id) {
    }

}
