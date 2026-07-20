package me.flashyreese.mods.nuit.components.clock;

import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedTimeSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.Optional;

public record WorldClockSource(Optional<Identifier> clock) implements ClockSource {
    public static final String TYPE = "clock";

    public WorldClockSource {
        Objects.requireNonNull(clock, "Clock source id cannot be null");
        if (clock.isEmpty()) {
            throw new IllegalArgumentException("Clock source type '" + TYPE + "' requires an id");
        }
        throw new UnsupportedOperationException(ClockSource.WORLD_CLOCK_UNAVAILABLE);
    }

    @Override
    public ResolvedTimeSource resolve(LevelClockState levelClockState, ClientLevel level) {
        throw new IllegalStateException(ClockSource.WORLD_CLOCK_UNAVAILABLE);
    }

    @Override
    public DirectClockSource asDirectSource() {
        return new DirectClockSource(TYPE, this.clock, 0L);
    }

    @Override
    public String serializedName() {
        return TYPE;
    }

    @Override
    public String asString() {
        return this.clock().orElseThrow().toString();
    }
}
