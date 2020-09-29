package amerebagatelle.github.io.fabricskyboxes.skyboxes.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RGBA {
    public static final RGBA ZERO = new RGBA(.0F, .0F, .0F, .0F);
    public static final Codec<RGBA> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("red").forGetter(RGBA::getRed),
            Codec.FLOAT.fieldOf("blue").forGetter(RGBA::getBlue),
            Codec.FLOAT.fieldOf("green").forGetter(RGBA::getGreen),
            Codec.FLOAT.optionalFieldOf("alpha", 1.0F).forGetter(RGBA::getAlpha)
            ).apply(instance, RGBA::new));
    private final float red;
    private final float blue;
    private final float green;
    private final float alpha;

    public RGBA(float red, float blue, float green, float alpha) {
        this.red = red;
        this.blue = blue;
        this.green = green;
        this.alpha = alpha;
    }

    public float getRed() {
        return this.red;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getGreen() {
        return this.green;
    }

    public float getAlpha() {
        return this.alpha;
    }
}
