package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;

public class Properties {
    public static final Codec<Properties> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.optionalFieldOf("priority", 0).forGetter(Properties::getPriority),
            Fade.CODEC.fieldOf("fade").forGetter(Properties::getFade),
            Utils.getClampedFloat(.0F, 1.0F).optionalFieldOf("maxAlpha", 1.0F).forGetter(Properties::getMaxAlpha),
            Utils.getClampedFloat(.0F, 1.0F).optionalFieldOf("transitionSpeed", 1.0F).forGetter(Properties::getTransitionSpeed),
            Codec.BOOL.optionalFieldOf("changeFog", false).forGetter(Properties::isChangeFog),
            RGBA.CODEC.optionalFieldOf("fogColors", RGBA.ZERO).forGetter(Properties::getFogColors),
            Codec.BOOL.optionalFieldOf("sunSkyTint", true).forGetter(Properties::isRenderSunSkyTint),
            Codec.BOOL.optionalFieldOf("shouldRotate", false).forGetter(Properties::isShouldRotate),
            Rotation.CODEC.optionalFieldOf("rotation", Rotation.DEFAULT).forGetter(Properties::getRotation)
    ).apply(instance, Properties::new));
    private final int priority;
    private final Fade fade;
    private final float maxAlpha;
    private final float transitionSpeed;
    private final boolean changeFog;
    private final RGBA fogColors;
    private final boolean renderSunSkyTint;
    private final boolean shouldRotate;
    private final Rotation rotation;

    public Properties(int priority, Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean renderSunSkyTint, boolean shouldRotate, Rotation rotation) {
        this.priority = priority;
        this.fade = fade;
        this.maxAlpha = maxAlpha;
        this.transitionSpeed = transitionSpeed;
        this.changeFog = changeFog;
        this.fogColors = fogColors;
        this.renderSunSkyTint = renderSunSkyTint;
        this.shouldRotate = shouldRotate;
        this.rotation = rotation;
    }

    public int getPriority() {
        return priority;
    }

    public Fade getFade() {
        return this.fade;
    }

    public float getMaxAlpha() {
        return this.maxAlpha;
    }

    public float getTransitionSpeed() {
        return this.transitionSpeed;
    }

    public boolean isChangeFog() {
        return this.changeFog;
    }

    public RGBA getFogColors() {
        return this.fogColors;
    }

    public boolean isRenderSunSkyTint() {
        return renderSunSkyTint;
    }

    public boolean isShouldRotate() {
        return this.shouldRotate;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public static Properties ofSkybox(AbstractSkybox skybox) {
        Rotation rot = Rotation.DEFAULT;
        if (skybox instanceof RotatableSkybox) {
            rot = ((RotatableSkybox) skybox).getRotation();
        }
        return new Builder()
                .changeFog(skybox.getProperties().isChangeFog())
                .renderSunSkyTint(skybox.getProperties().isRenderSunSkyTint())
                .shouldRotate(skybox.getProperties().isShouldRotate())
                .fogColors(skybox.getProperties().getFogColors())
                .transitionSpeed(skybox.getProperties().getTransitionSpeed())
                .fade(skybox.getProperties().getFade())
                .maxAlpha(skybox.getProperties().getMaxAlpha())
                .rotation(rot)
                .build();
    }

    public static class Builder {
        private int priority = 0;
        private Fade fade = Fade.ZERO;
        private float maxAlpha = 1.0F;
        private float transitionSpeed = 1.0F;
        private boolean changeFog = false;
        private RGBA fogColors = RGBA.ZERO;
        private boolean renderSunSkyTint = true;
        private boolean shouldRotate = false;
        private Rotation rotation = Rotation.DEFAULT;

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder fade(Fade fade) {
            this.fade = fade;
            return this;
        }

        public Builder maxAlpha(float maxAlpha) {
            this.maxAlpha = maxAlpha;
            return this;
        }

        public Builder transitionSpeed(float transitionSpeed) {
            this.transitionSpeed = transitionSpeed;
            return this;
        }

        public Builder changesFog() {
            this.changeFog = true;
            return this;
        }

        public Builder rendersSunSkyTint() {
            this.renderSunSkyTint = true;
            return this;
        }

        public Builder fogColors(RGBA fogColors) {
            this.fogColors = fogColors;
            return this;
        }

        public Builder rotation(Rotation rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder rotates() {
            this.shouldRotate = true;
            return this;
        }

        public Builder changeFog(boolean changeFog) {
            if (changeFog) {
                return this.changesFog();
            } else {
                return this;
            }
        }

        public Builder renderSunSkyTint(boolean renderSunSkyTint) {
            if (renderSunSkyTint) {
                return this.rendersSunSkyTint();
            } else {
                return this;
            }
        }

        public Builder shouldRotate(boolean shouldRotate) {
            if (shouldRotate) {
                return this.rotates();
            } else {
                return this;
            }
        }

        public Properties build() {
            return new Properties(this.priority, this.fade, this.maxAlpha, this.transitionSpeed, this.changeFog, this.fogColors, this.renderSunSkyTint, this.shouldRotate, this.rotation);
        }
    }
}
