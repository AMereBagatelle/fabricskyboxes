package me.flashyreese.mods.nuit.components.clock;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.flashyreese.mods.nuit.components.clock.runtime.ClockSourceRuntime;
import me.flashyreese.mods.nuit.components.clock.runtime.InterpolatedSample;
import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedTimeSource;
import me.flashyreese.mods.nuit.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import java.util.Objects;

/**
 * Selects the time source used by a skybox.
 *
 * <p>Minecraft 1.21.11 does not expose the named world-clock registry added in 26.x. Configurations can therefore
 * use the default day-time clock, game time, or a fixed time. Named world clocks are rejected during decoding
 * instead of silently falling back to a different time source.</p>
 */
public interface ClockSource {
    String WORLD_CLOCK_UNAVAILABLE = "Named world clocks are unavailable on Minecraft 1.21.11";

    Codec<ClockSource> STRING_CODEC = Codec.STRING.comapFlatMap(ClockSource::parseSourceType, ClockSource::asString);

    Codec<ClockSource> OBJECT_CODEC = DirectClockSource.CODEC.comapFlatMap(DirectClockSource::bake, ClockSource::asDirectSource);

    Codec<ClockSource> CODEC = Codec.either(OBJECT_CODEC, STRING_CODEC).xmap(
            either -> either.map(source -> source, source -> source),
            source -> source.requiresObjectEncoding() ? Either.left(source) : Either.right(source)
    );

    static DefaultClockSource defaultClock() {
        return new DefaultClockSource();
    }

    static GameTimeClockSource gameTime() {
        return new GameTimeClockSource();
    }

    static FixedClockSource fixed(long fixedTime) {
        return new FixedClockSource(fixedTime);
    }

    static WorldClockSource worldClock(Identifier clock) {
        Objects.requireNonNull(clock, "Clock source id cannot be null");
        throw new UnsupportedOperationException(WORLD_CLOCK_UNAVAILABLE);
    }

    /**
     * Returns the current whole tick of the selected source.
     */
    default long getTicks(ClientLevel level) {
        InterpolatedSample sample = ClockSourceRuntime.sample(level, this);
        return sample.currentTicks();
    }

    /**
     * Returns render-interpolated ticks while respecting world-clock pause and rate changes.
     */
    default double getRenderTicks(ClientLevel level, float tickDelta) {
        InterpolatedSample sample = ClockSourceRuntime.sample(level, this);
        return sample.interpolate(tickDelta);
    }

    /**
     * Maps the selected source onto a caller-defined cycle.
     */
    default long getCycleTicks(ClientLevel level, long cycleDuration) {
        InterpolatedSample sample = ClockSourceRuntime.sample(level, this);
        return (long) Math.floor(Utils.toCycleTicks(sample.currentTicks(), cycleDuration));
    }

    /**
     * Maps the selected source onto a caller-defined cycle with render interpolation.
     */
    default double getRenderCycleTicks(ClientLevel level, float tickDelta, long cycleDuration) {
        InterpolatedSample sample = ClockSourceRuntime.sample(level, this);
        return Utils.toCycleTicks(sample.interpolate(tickDelta), cycleDuration);
    }

    private static DataResult<ClockSource> parseSourceType(String value) {
        Identifier identifier = Identifier.tryParse(value);
        if (identifier == null) {
            return DataResult.error(() -> "Invalid clock source '" + value + "'");
        } else {
            return switch (identifier.toString()) {
                case "minecraft:default" -> DataResult.success(defaultClock());
                case "minecraft:game_time" -> DataResult.success(gameTime());
                case "minecraft:fixed" ->
                        DataResult.error(() -> "Fixed clock sources require an object with a time value");
                case "minecraft:clock", "minecraft:world_clock" -> DataResult.error(() -> WORLD_CLOCK_UNAVAILABLE);
                default -> DataResult.error(() -> WORLD_CLOCK_UNAVAILABLE + ": " + identifier);
            };
        }
    }

    ResolvedTimeSource resolve(LevelClockState clockState, ClientLevel level);

    DirectClockSource asDirectSource();

    String serializedName();

    default String asString() {
        return this.serializedName();
    }

    default boolean requiresObjectEncoding() {
        return false;
    }
}
