package amerebagatelle.github.io.fabricskyboxes.skyboxes.object;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class HeightEntry {
    public static final Codec<HeightEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(HeightEntry::getMin),
            Codec.FLOAT.fieldOf("max").forGetter(HeightEntry::getMax)
    ).apply(instance, HeightEntry::new));
    private final float min;
    private final float max;

    public HeightEntry(float min, float max) {
        if (min > max) {
            throw new IllegalStateException("Maximum value is lower than the minimum value:\n" + this.toString());
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

    public float[] toFloatArray() {
        return new float[]{this.min, this.max};
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
