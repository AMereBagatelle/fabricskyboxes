package me.flashyreese.mods.nuit.components.clock;

import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.MissingSource;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.clock.WorldClock;

import java.util.Objects;
import java.util.Optional;

public record WorldClockSource(Optional<Identifier> clock) implements ClockSource {
    public static final String TYPE = "clock";

    public WorldClockSource {
        Objects.requireNonNull(clock, "Clock source id cannot be null");
        if (clock.isEmpty()) {
            throw new IllegalArgumentException("Clock source type '" + TYPE + "' requires an id");
        }
    }

    @Override
    public ResolvedSource resolve(LevelClockState levelClockState, ClientLevel level) {
        Identifier id = this.clock().orElseThrow();
        ResourceKey<WorldClock> key = ResourceKey.create(Registries.WORLD_CLOCK, id);
        Optional<Holder.Reference<WorldClock>> clock = level.registryAccess().get(key);
        if (clock.isEmpty()) {
            levelClockState.warnMissing(level, id);
            return ResolvedSource.missing(this);
        }

        levelClockState.warnedMissingSources().remove(new MissingSource(id));
        return levelClockState.resolvedClock(level, clock.get());
    }

    @Override
    public DirectClockSource asDirectSource() {
        return new DirectClockSource(TYPE, null, 0L);
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
