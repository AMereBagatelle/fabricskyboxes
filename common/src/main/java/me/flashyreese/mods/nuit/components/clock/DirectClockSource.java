package me.flashyreese.mods.nuit.components.clock;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.flashyreese.mods.nuit.components.clock.runtime.LevelClockState;
import me.flashyreese.mods.nuit.components.clock.runtime.source.ResolvedTimeSource;
import me.flashyreese.mods.nuit.util.CodecUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record DirectClockSource(String type, Optional<ResourceLocation> id, long time) implements ClockSource {
    public static final Codec<DirectClockSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("type", WorldClockSource.TYPE).forGetter(DirectClockSource::type),
            ResourceLocation.CODEC.optionalFieldOf("id").forGetter(DirectClockSource::id),
            CodecUtils.getClampedLong(0L, Long.MAX_VALUE).optionalFieldOf("time", 0L).forGetter(DirectClockSource::time)
    ).apply(instance, DirectClockSource::new));

    @Override
    public ResolvedTimeSource resolve(LevelClockState levelClockState, ClientLevel level) {
        throw new UnsupportedOperationException("Cannot resolve direct clock source as it is not baked!");
    }

    public DataResult<ClockSource> bake() {
        ResourceLocation identifier = ResourceLocation.tryParse(this.type);
        if (identifier == null) {
            return DataResult.error(() -> "Invalid clock source type '" + this.type + "'");
        } else {
            return switch (identifier.toString()) {
                case "minecraft:default" -> this.noArguments(ClockSource.defaultClock());
                case "minecraft:game_time" -> this.noArguments(ClockSource.gameTime());
                case "minecraft:fixed" ->
                        this.id.isPresent() ? this.unexpectedArguments() : DataResult.success(ClockSource.fixed(this.time));
                case "minecraft:clock", "minecraft:world_clock" ->
                        DataResult.error(() -> ClockSource.WORLD_CLOCK_UNAVAILABLE);
                default -> DataResult.error(() -> "Unknown clock source type '" + this.type + "'");
            };
        }
    }

    private DataResult<ClockSource> noArguments(ClockSource source) {
        return this.id.isPresent() || this.time != 0L ? this.unexpectedArguments() : DataResult.success(source);
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
