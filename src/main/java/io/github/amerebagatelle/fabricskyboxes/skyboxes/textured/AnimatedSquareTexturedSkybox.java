package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import java.util.List;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.DefaultProperties;
import io.github.amerebagatelle.fabricskyboxes.util.object.Textures;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.util.math.MatrixStack;

public class AnimatedSquareTexturedSkybox extends SquareTexturedSkybox {
    public static Codec<AnimatedSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultProperties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getDefaultProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.NO_CONDITIONS).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Codec.BOOL.fieldOf("blend").forGetter(TexturedSkybox::isBlend),
            Textures.CODEC.listOf().fieldOf("animationTextures").forGetter(AnimatedSquareTexturedSkybox::getAnimationTextures),
            Codec.FLOAT.fieldOf("fps").forGetter(AnimatedSquareTexturedSkybox::getFps)
    ).apply(instance, AnimatedSquareTexturedSkybox::new));
    public List<Textures> animationTextures;
    public float fps;
    public long frameTimeMillis;
    public int count = 0;
    public long lastTime = 0L;

    public AnimatedSquareTexturedSkybox() {
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.ANIMATED_SQUARE_TEXTURED_SKYBOX;
    }

    public AnimatedSquareTexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, boolean blend, List<Textures> animationTextures, float fps) {
        super(properties, conditions, decorations, blend, null);
        this.animationTextures = animationTextures;
        this.fps = fps;
        if (fps > 0 && fps <= 360) {
            this.frameTimeMillis = (long) (1000F / fps);
        } else {
            this.frameTimeMillis = 16L;
        }
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        if (this.lastTime == 0L) this.lastTime = System.currentTimeMillis();
        this.textures = this.getAnimationTextures().get(this.count);

        super.renderSkybox(worldRendererAccess, matrices, tickDelta);

        if (System.currentTimeMillis() >= (this.lastTime + this.frameTimeMillis)) {
            if (this.count < this.getAnimationTextures().size()) {
                if (this.count + 1 == this.getAnimationTextures().size()) {
                    this.count = 0;
                } else {
                    this.count++;
                }
            }
            this.lastTime = System.currentTimeMillis();
        }
    }

    public List<Textures> getAnimationTextures() {
        return this.animationTextures;
    }

    public float getFps() {
        return this.fps;
    }
}
