package amerebagatelle.github.io.fabricskyboxes.skyboxes.object;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;

public enum Weather {
    CLEAR("clear"),
    RAIN("rain"),
    SNOW("snow"),
    THUNDER("thunder");

    public static final Codec<Weather> CODEC = Codec.STRING.xmap(Weather::fromString, Weather::toString);
    private static final Map<String, Weather> VALUES;
    private final String name;

    Weather(String name) {
        this.name = name;
    }

    public static Weather fromString(String name) {
        return Objects.requireNonNull(VALUES.get(name));
    }

    static {
        ImmutableMap.Builder<String, Weather> builder = ImmutableMap.builder();
        for (Weather value : values()) {
            builder.put(value.name, value);
        }
        VALUES = builder.build();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
