package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class AnimatedSquareTexturedSkybox extends SquareTexturedSkybox {
    public static final Codec<AnimatedSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DefaultProperties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getDefaultProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.NO_CONDITIONS).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            Textures.CODEC.listOf().fieldOf("animationTextures").forGetter(AnimatedSquareTexturedSkybox::getAnimationTextures),
            Codec.FLOAT.fieldOf("fps").forGetter(AnimatedSquareTexturedSkybox::getFps)
    ).apply(instance, AnimatedSquareTexturedSkybox::new));
    private final List<Textures> animationTextures;
    private final float fps;
    private final long frameTimeMillis;
    private int count = 0;
    private long lastTime = 0L;

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.ANIMATED_SQUARE_TEXTURED_SKYBOX;
    }

    public AnimatedSquareTexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, Blend blend, List<Textures> animationTextures, float fps) {
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
