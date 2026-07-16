package me.flashyreese.mods.nuit.components;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.flashyreese.mods.nuit.util.CodecUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.Objects;
import java.util.Optional;

/**
 * Selects the time source used by a skybox.
 *
 * <p>Minecraft 1.21.11 does not expose the named world-clock registry added in 26.x. Configurations can therefore
 * use the default day-time clock, game time, or a fixed time. Named world clocks are rejected during decoding
 * instead of silently falling back to a different time source.</p>
 */
public record ClockSource(Type type, Optional<Identifier> clock, long fixedTime) {
    private static final String WORLD_CLOCK_UNAVAILABLE =
            "Named world clocks are unavailable on Minecraft 1.21.11";
    private static final Codec<ClockSource> STRING_CODEC = Codec.STRING.comapFlatMap(
            ClockSource::parseString,
            ClockSource::asString
    );
    private static final Codec<ClockSource> OBJECT_CODEC = Raw.CODEC.comapFlatMap(
            Raw::toClockSource,
            ClockSource::toRaw
    );

    public static final Codec<ClockSource> CODEC = Codec.either(OBJECT_CODEC, STRING_CODEC).xmap(
            either -> either.map(source -> source, source -> source),
            source -> source.requiresObjectEncoding() ? Either.left(source) : Either.right(source)
    );

    public ClockSource(Type type, Optional<Identifier> clock, long fixedTime) {
        this.type = Objects.requireNonNull(type, "Clock source type cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock source id cannot be null");
        this.fixedTime = fixedTime;

        if (type == Type.WORLD_CLOCK) {
            throw new UnsupportedOperationException(WORLD_CLOCK_UNAVAILABLE);
        }
        boolean registrySource = type == Type.WORLD_CLOCK;
        if (registrySource != clock.isPresent()) {
            throw new IllegalArgumentException(registrySource
                    ? "Clock source type '" + type.serializedName + "' requires an id"
                    : "Clock source type '" + type.serializedName + "' cannot have an id");
        }
        if (type == Type.FIXED && fixedTime < 0L) {
            throw new IllegalArgumentException("Fixed clock time cannot be negative");
        }
        if (type != Type.FIXED && fixedTime != 0L) {
            throw new IllegalArgumentException("Only fixed clock sources can have a fixed time");
        }
    }

    public static ClockSource defaultClock() {
        return new ClockSource(Type.DEFAULT, Optional.empty(), 0L);
    }

    public static ClockSource gameTime() {
        return new ClockSource(Type.GAME_TIME, Optional.empty(), 0L);
    }

    public static ClockSource fixed(long fixedTime) {
        return new ClockSource(Type.FIXED, Optional.empty(), fixedTime);
    }

    public static ClockSource worldClock(Identifier clock) {
        Objects.requireNonNull(clock, "Clock source id cannot be null");
        throw new UnsupportedOperationException(WORLD_CLOCK_UNAVAILABLE);
    }

    /**
     * Returns the current whole tick of the selected source.
     */
    public long getTicks(ClientLevel level) {
        ClockSourceRuntime.SampleView sample = ClockSourceRuntime.sample(level, this);
        return sample.currentTicks();
    }

    /**
     * Returns render-interpolated ticks while respecting world-clock pause and rate changes.
     */
    public double getRenderTicks(ClientLevel level, float tickDelta) {
        ClockSourceRuntime.SampleView sample = ClockSourceRuntime.sample(level, this);
        return sample.interpolate(tickDelta);
    }

    /**
     * Maps the selected source onto a caller-defined cycle.
     */
    public long getCycleTicks(ClientLevel level, long cycleDuration) {
        ClockSourceRuntime.SampleView sample = ClockSourceRuntime.sample(level, this);
        return (long) Math.floor(toCycleTicks(sample.currentTicks(), cycleDuration));
    }

    /**
     * Maps the selected source onto a caller-defined cycle with render interpolation.
     */
    public double getRenderCycleTicks(ClientLevel level, float tickDelta, long cycleDuration) {
        ClockSourceRuntime.SampleView sample = ClockSourceRuntime.sample(level, this);
        return toCycleTicks(sample.interpolate(tickDelta), cycleDuration);
    }

    static double toCycleTicks(double ticks, long cycleDuration) {
        if (cycleDuration <= 0L) {
            throw new IllegalArgumentException("Cycle duration must be positive");
        }
        return Mth.positiveModulo(ticks, (double) cycleDuration);
    }

    private boolean requiresObjectEncoding() {
        return this.type == Type.FIXED;
    }

    private static DataResult<ClockSource> parseString(String value) {
        return switch (value) {
            case "default", "minecraft:default" -> DataResult.success(defaultClock());
            case "game_time", "minecraft:game_time" -> DataResult.success(gameTime());
            case "fixed", "minecraft:fixed" -> DataResult.error(
                    () -> "Fixed clock sources require an object with a time value"
            );
            case "clock", "world_clock", "minecraft:clock", "minecraft:world_clock" -> DataResult.error(
                    () -> WORLD_CLOCK_UNAVAILABLE
            );
            default -> {
                Identifier clock = Identifier.tryParse(value);
                if (clock == null) {
                    yield DataResult.error(() -> "Invalid clock source '" + value + "'");
                }
                yield DataResult.error(() -> WORLD_CLOCK_UNAVAILABLE + ": " + clock);
            }
        };
    }

    private static String asString(ClockSource source) {
        return switch (source.type()) {
            case DEFAULT -> "default";
            case GAME_TIME -> "game_time";
            case WORLD_CLOCK -> source.clock().orElseThrow().toString();
            case FIXED -> throw new IllegalStateException(
                    "Clock source type '" + source.type().serializedName + "' requires object encoding"
            );
        };
    }

    private Raw toRaw() {
        return switch (this.type) {
            case DEFAULT -> new Raw("default", Optional.empty(), 0L);
            case GAME_TIME -> new Raw("game_time", Optional.empty(), 0L);
            case FIXED -> new Raw("fixed", Optional.empty(), this.fixedTime);
            case WORLD_CLOCK -> new Raw("clock", this.clock, 0L);
        };
    }

    public enum Type {
        DEFAULT("default"),
        GAME_TIME("game_time"),
        FIXED("fixed"),
        WORLD_CLOCK("clock");

        private final String serializedName;

        Type(String serializedName) {
            this.serializedName = serializedName;
        }
    }

    private record Raw(String type, Optional<Identifier> id, long time) {
        private static final Codec<Raw> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("type", "clock").forGetter(Raw::type),
                Identifier.CODEC.optionalFieldOf("id").forGetter(Raw::id),
                CodecUtils.getClampedLong(0L, Long.MAX_VALUE).optionalFieldOf("time", 0L).forGetter(Raw::time)
        ).apply(instance, Raw::new));

        private DataResult<ClockSource> toClockSource() {
            return switch (this.type) {
                case "default", "minecraft:default" -> this.noArguments(defaultClock());
                case "game_time", "minecraft:game_time" -> this.noArguments(gameTime());
                case "fixed", "minecraft:fixed" -> this.id.isPresent()
                        ? this.unexpectedArguments()
                        : DataResult.success(fixed(this.time));
                case "clock", "world_clock", "minecraft:clock", "minecraft:world_clock" ->
                        this.registrySource();
                default -> DataResult.error(() -> "Unknown clock source type '" + this.type + "'");
            };
        }

        private DataResult<ClockSource> noArguments(ClockSource source) {
            return this.id.isPresent() || this.time != 0L
                    ? this.unexpectedArguments()
                    : DataResult.success(source);
        }

        private DataResult<ClockSource> registrySource() {
            return DataResult.error(() -> WORLD_CLOCK_UNAVAILABLE);
        }

        private DataResult<ClockSource> unexpectedArguments() {
            return DataResult.error(() -> "Clock source type '" + this.type + "' has unexpected fields");
        }
    }
}
