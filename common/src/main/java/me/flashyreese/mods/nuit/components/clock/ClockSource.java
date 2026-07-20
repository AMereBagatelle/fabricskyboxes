package me.flashyreese.mods.nuit.components.clock;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import me.flashyreese.mods.nuit.components.clock.runtime.ClockSourceRuntime;
import me.flashyreese.mods.nuit.components.clock.runtime.InterpolatedSample;
import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedSource;
import me.flashyreese.mods.nuit.util.Utils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.Optional;

/**
 * Selects the time source used by a skybox.
 *
 * <p>World-clock sources expose their raw synchronized tick counter. Mojang timelines remain part of the level's
 * environment-attribute system and are consumed through the camera attribute probe.</p>
 */
public interface ClockSource {
    Codec<ClockSource> STRING_CODEC = Codec.STRING.comapFlatMap(
            ClockSource::parseString,
            ClockSource::asString
    );

    Codec<ClockSource> OBJECT_CODEC = RawClockSource.CODEC.comapFlatMap(
            RawClockSource::asBakedClockSource,
            ClockSource::asRawSource
    );

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
        return new WorldClockSource(Optional.of(Objects.requireNonNull(clock)));
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

    private static DataResult<ClockSource> parseString(String value) {
        return switch (value) {
            case "default", "minecraft:default" -> DataResult.success(defaultClock());
            case "game_time", "minecraft:game_time" -> DataResult.success(gameTime());
            case "fixed", "minecraft:fixed" ->
                    DataResult.error(() -> "Fixed clock sources require an object with a time value");
            case "clock", "world_clock", "minecraft:clock", "minecraft:world_clock" ->
                    DataResult.error(() -> "World clock sources require an id");
            default -> {
                Identifier clock = Identifier.tryParse(value);
                if (clock == null) {
                    yield DataResult.error(() -> "Invalid clock source '" + value + "'");
                } else {
                    yield DataResult.success(worldClock(clock));
                }
            }
        };
    }

    ResolvedSource resolve(LevelClockState clockState, ClientLevel level);

    RawClockSource asRawSource();

    String serializedName();

    default String asString() {
        return this.serializedName();
    }

    default boolean requiresObjectEncoding() {
        return false;
    }
}
