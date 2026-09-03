package me.flashyreese.mods.nuit.components;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClockSourceTest {
    @Test
    public void decodesSupportedClockSources() {
        assertEquals(
                ClockSource.defaultClock(),
                ClockSource.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"default\"")).getOrThrow()
        );
        assertEquals(
                ClockSource.gameTime(),
                ClockSource.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"game_time\"")).getOrThrow()
        );
        assertEquals(
                ClockSource.fixed(6000L),
                ClockSource.CODEC.parse(
                        JsonOps.INSTANCE,
                        JsonParser.parseString("{\"type\":\"fixed\",\"time\":6000}")
                ).getOrThrow()
        );
    }

    @Test
    public void rejectsNamedWorldClocks() {
        assertTrue(ClockSource.CODEC.parse(
                JsonOps.INSTANCE,
                JsonParser.parseString("\"minecraft:overworld\"")
        ).result().isEmpty());
        assertTrue(ClockSource.CODEC.parse(
                JsonOps.INSTANCE,
                JsonParser.parseString("{\"type\":\"clock\",\"id\":\"example:clock\"}")
        ).result().isEmpty());
    }

    @Test
    public void wrapsNegativeAndOverflowingTicks() {
        assertEquals(23999.5D, ClockSource.toCycleTicks(-0.5D, 24000L));
        assertEquals(1.25D, ClockSource.toCycleTicks(24001.25D, 24000L));
    }
}
