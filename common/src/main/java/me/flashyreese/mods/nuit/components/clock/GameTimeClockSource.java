package me.flashyreese.mods.nuit.components.clock;

import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedTimeSource;
import net.minecraft.client.multiplayer.ClientLevel;

import java.util.Optional;

public class GameTimeClockSource implements ClockSource {
    public static final String TYPE = "game_time";

    @Override
    public ResolvedTimeSource resolve(LevelClockState levelClockState, ClientLevel level) {
        return ResolvedTimeSource.of(level.getGameTime(), this);
    }

    @Override
    public DirectClockSource asDirectSource() {
        return new DirectClockSource(TYPE, Optional.empty(), 0L);
    }

    @Override
    public String serializedName() {
        return TYPE;
    }
}
