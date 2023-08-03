package io.github.amerebagatelle.fabricskyboxes.util.object;

public class FogRGBA extends RGBA {
    private final float density;

    public FogRGBA(float red, float green, float blue, float alpha, float density) {
        super(red, green, blue, alpha);
        this.density = density;
    }

    public FogRGBA(RGBA rgba) {
        super(rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
        this.density = 1F;
    }

    public float getDensity() {
        return density;
    }
}
