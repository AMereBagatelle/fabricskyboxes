package me.flashyreese.mods.nuit.components.clock;

import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedSource;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.Optional;

public class GameTimeClockSource implements ClockSource {
    public static final String TYPE = "game_time";

    @Override
    public ResolvedSource resolve(LevelClockState levelClockState, ClientLevel level) {
        return ResolvedSource.of(level.getGameTime(), this);
    }

    @Override
    public RawClockSource asRawSource() {
        return new RawClockSource(TYPE, Optional.empty(), 0L);
    }

    @Override
    public String serializedName() {
        return TYPE;
    }
}
