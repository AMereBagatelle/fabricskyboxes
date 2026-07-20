package me.flashyreese.mods.nuit.components.clock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedTimeSource;
import me.flashyreese.mods.nuit.util.CodecUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record DirectClockSource(String type, Optional<Identifier> id, long time) implements ClockSource {
    public static final Codec<DirectClockSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", "clock").forGetter(DirectClockSource::type),
            Identifier.CODEC.optionalFieldOf("id").forGetter(DirectClockSource::id),
            CodecUtils.getClampedLong(0L, Long.MAX_VALUE).optionalFieldOf("time", 0L).forGetter(DirectClockSource::time)
    ).apply(instance, DirectClockSource::new));

    @Override
    public ResolvedTimeSource resolve(LevelClockState levelClockState, ClientLevel level) {
        throw new UnsupportedOperationException("Cannot resolve direct clock source as it is not baked!");
    }

    // TODO: Abstract more (i.e Clock Source Registry system (FUTURE PR))
    public DataResult<ClockSource> bake() {
        return switch (this.type) {
            case "default", "minecraft:default" -> this.noArguments(ClockSource.defaultClock());
            case "game_time", "minecraft:game_time" -> this.noArguments(ClockSource.gameTime());
            case "fixed", "minecraft:fixed" ->
                    this.id.isPresent() ? this.unexpectedArguments() : DataResult.success(ClockSource.fixed(this.time));
            case "clock", "world_clock", "minecraft:clock", "minecraft:world_clock" -> this.registrySource();
            default -> DataResult.error(() -> "Unknown clock source type '" + this.type + "'");
        };
    }

    private DataResult<ClockSource> noArguments(ClockSource source) {
        return this.id.isPresent() || this.time != 0L ? this.unexpectedArguments() : DataResult.success(source);
    }

    private DataResult<ClockSource> registrySource() {
        return DataResult.error(() -> ClockSource.WORLD_CLOCK_UNAVAILABLE);
    }

    private DataResult<ClockSource> unexpectedArguments() {
        return DataResult.error(() -> "Clock source type '" + this.type + "' has unexpected fields");
    }

    @Override
    public DirectClockSource asDirectSource() {
        return this;
    }

    @Override
    public String serializedName() {
        return this.type;
    }
}
