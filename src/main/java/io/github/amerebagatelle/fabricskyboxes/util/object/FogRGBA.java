package io.github.amerebagatelle.fabricskyboxes.util.object;

public class FogRGBA extends RGBA {
    private final boolean modifyDensity;
    private final float density;

    public FogRGBA(float red, float green, float blue, float alpha, boolean modifyDensity, float density) {
        super(red, green, blue, alpha);
        this.modifyDensity = modifyDensity;
        this.density = density;
    }

    public FogRGBA(RGBA rgba) {
        super(rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha());
        this.modifyDensity = false;
        this.density = 1F;
    }

    public boolean isModifyDensity() {
        return modifyDensity;
    }

    public float getDensity() {
        return density;
    }
}
