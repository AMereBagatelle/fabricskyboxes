package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MinMaxEntry {
    public static final Codec<MinMaxEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(MinMaxEntry::getMin),
            Codec.FLOAT.fieldOf("max").forGetter(MinMaxEntry::getMax)
    ).apply(instance, MinMaxEntry::new));
    private final float min;
    private final float max;

    public MinMaxEntry(float min, float max) {
        if (min > max) {
            throw new IllegalStateException("Maximum value is lower than the minimum value:\n" + this);
        }
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
