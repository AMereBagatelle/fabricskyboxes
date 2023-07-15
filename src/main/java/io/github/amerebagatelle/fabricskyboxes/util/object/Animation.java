package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class Animation {
    public static final Codec<Animation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Texture.CODEC.fieldOf("texture").forGetter(Animation::getTexture),
            UVRanges.CODEC.fieldOf("uvRanges").forGetter(Animation::getUvRanges),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).fieldOf("gridColumns").forGetter(Animation::getGridColumns),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).fieldOf("gridRows").forGetter(Animation::getGridRows),
            Utils.getClampedInteger(1, Integer.MAX_VALUE).fieldOf("duration").forGetter(Animation::getDuration),
            Codec.BOOL.optionalFieldOf("interpolate", true).forGetter(Animation::isInterpolate),
            Codec.unboundedMap(Codec.INT, Codec.INT).optionalFieldOf("frameDuration", new HashMap<>()).forGetter(Animation::getFrameDuration)
    ).apply(instance, Animation::new));

    private final Texture texture;
    private final UVRanges uvRanges;
    private final int gridRows;
    private final int gridColumns;
    private final int duration;
    private final boolean interpolate;
    private final Map<Integer, Integer> frameDuration;

    private UVRanges currentFrame;
    private long nextTime;
    private int index;

    public Animation(Texture texture, UVRanges uvRanges, int gridColumns, int gridRows, int duration, boolean interpolate, Map<Integer, Integer> frameDuration) {
        this.texture = texture;
        this.uvRanges = uvRanges;
        this.gridColumns = gridColumns;
        this.gridRows = gridRows;
        this.duration = duration;
        this.interpolate = interpolate;
        this.frameDuration = frameDuration;
    }

    public Texture getTexture() {
        return texture;
    }

    public UVRanges getUvRanges() {
        return uvRanges;
    }

    public int getGridColumns() {
        return gridColumns;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isInterpolate() {
        return interpolate;
    }

    public Map<Integer, Integer> getFrameDuration() {
        return frameDuration;
    }

    public void tick(long timeOfDay) {
        if (timeOfDay >= this.nextTime) {
            if (this.index + 1 == this.gridRows * this.gridColumns) {
                this.index = 0;
            } else {
                this.index++;
            }
            this.nextTime = timeOfDay + this.frameDuration.getOrDefault(this.index, this.duration);

            // Calculate the UV ranges for the current frame
            float frameWidth = 1.0F / this.gridColumns;
            float frameHeight = 1.0F / this.gridRows;
            float minU = (float) (this.index % this.gridColumns) * frameWidth;
            float maxU = minU + frameWidth;
            float minV = (float) (this.index / this.gridColumns) * frameHeight;
            float maxV = minV + frameHeight;
            this.currentFrame = new UVRanges(minU, minV, maxU, maxV);
        }
    }

    public UVRanges getCurrentFrame() {
        return currentFrame;
    }
}
