package me.flashyreese.mods.nuit.components.clock;

import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedSource;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.Optional;

public record FixedClockSource(long time) implements ClockSource {
    public static final String TYPE = "fixed";

    public FixedClockSource {
        if (time < 0L) {
            throw new IllegalArgumentException("Fixed clock time cannot be negative");
        }
    }

    @Override
    public ResolvedSource resolve(LevelClockState levelClockState, ClientLevel level) {
        return ResolvedSource.of(this.time, this);
    }

    @Override
    public RawClockSource asRawSource() {
        return new RawClockSource(TYPE, Optional.empty(), 0L);
    }

    @Override
    public String serializedName() {
        return TYPE;
    }

    @Override
    public String asString() {
        throw new IllegalStateException("Clock source type '" + TYPE + "' requires object encoding");
    }

    @Override
    public boolean requiresObjectEncoding() {
        return true;
    }
}
