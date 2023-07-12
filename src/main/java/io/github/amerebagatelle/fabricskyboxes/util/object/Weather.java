package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;

import java.util.Map;
import java.util.Objects;

public enum Weather {
    CLEAR("clear"),
    RAIN("rain"),
    BIOME_RAIN("rain_biome"),
    SNOW("snow"),
    THUNDER("thunder");

    private static final Map<String, Weather> VALUES;
    public static final Codec<Weather> CODEC = Codec.STRING.xmap(Weather::fromString, Weather::toString);

    static {
        ImmutableMap.Builder<String, Weather> builder = ImmutableMap.builder();
        for (Weather value : values()) {
            builder.put(value.name, value);
        }
        VALUES = builder.build();
    }

    private final String name;

    Weather(String name) {
        this.name = name;
    }

    public static Weather fromString(String name) {
        return Objects.requireNonNull(VALUES.get(name));
    }

    @Override
    public String toString() {
        return this.name;
    }
}
