package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.Utils;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.stream.Collectors;

public class AnimatedSquareTexturedSkybox extends TexturedSkybox {
    public static final Codec<AnimatedSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Fade.CODEC.fieldOf("fade").forGetter(AbstractSkybox::getFade),
            Utils.getClampedFloat(.0F, 1.0F).optionalFieldOf("maxAlpha", 1.0F).forGetter(AbstractSkybox::getMaxAlpha),
            Utils.getClampedFloat(.0F, 1.0F).optionalFieldOf("transitionSpeed", 1.0F).forGetter(AbstractSkybox::getTransitionSpeed),
            Codec.BOOL.optionalFieldOf("changeFog", false).forGetter(AbstractSkybox::isChangeFog),
            RGBA.CODEC.optionalFieldOf("fogColors", RGBA.ZERO).forGetter(AbstractSkybox::getFogColors),
            Codec.BOOL.optionalFieldOf("shouldRotate", false).forGetter(AbstractSkybox::isShouldRotate),
            Codec.BOOL.optionalFieldOf("decorations", false).forGetter(AbstractSkybox::isDecorations),
            Weather.CODEC.listOf().optionalFieldOf("weather", Lists.newArrayList(Weather.values())).forGetter((box) -> box.getWeather().stream().map(Weather::fromString).collect(Collectors.toList())),
            Identifier.CODEC.listOf().optionalFieldOf("biomes", ImmutableList.of()).forGetter(AbstractSkybox::getBiomes),
            Identifier.CODEC.listOf().optionalFieldOf("dimensions", ImmutableList.of()).forGetter(AbstractSkybox::getDimensions),
            HeightEntry.CODEC.listOf().optionalFieldOf("heightRanges", ImmutableList.of()).forGetter(AbstractSkybox::getHeightRanges),
            AnimationTextures.CODEC.fieldOf("animationTextures").forGetter(AnimatedSquareTexturedSkybox::getAnimationTextures),
            Codec.FLOAT.listOf().optionalFieldOf("axis", ImmutableList.of(.0F, .0F, .0F)).forGetter(TexturedSkybox::getAxis),
            Codec.BOOL.fieldOf("blend").forGetter(TexturedSkybox::isBlend),
            DecorationTextures.CODEC.optionalFieldOf("decorationTextures", DecorationTextures.DEFAULT).forGetter(AbstractSkybox::getDecorationTextures),
            Codec.FLOAT.fieldOf("framesPerSecond").forGetter(AnimatedSquareTexturedSkybox::getFramesPerSecond)
    ).apply(instance, AnimatedSquareTexturedSkybox::new));
    public AnimationTextures animationTextures;
    private float framesPerSecond;
    private long frameTimeMilliSeconds;
    private int count = 0;
    private long lastTime = 0L;

    public AnimatedSquareTexturedSkybox() {
    }

    public AnimatedSquareTexturedSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, boolean decorations, List<Weather> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, AnimationTextures animationTextures, List<Float> axis, boolean blend, DecorationTextures decorationTextures, float framesPerSecond) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, decorations, weather.stream().map(Weather::toString).collect(Collectors.toList()), biomes, dimensions, heightRanges, axis, blend, decorationTextures);
        this.animationTextures = animationTextures;
        this.framesPerSecond = framesPerSecond;
        this.axis = axis;

        if (framesPerSecond > 0 && framesPerSecond <= 360)
            this.frameTimeMilliSeconds = (long) (1000F / framesPerSecond);
        else
            this.frameTimeMilliSeconds = 16L;
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        if (this.lastTime == 0L) this.lastTime = System.currentTimeMillis();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        TextureManager textureManager = worldRendererAccess.getTextureManager();

        textureManager.bindTexture(this.animationTextures.getBottom().get(this.count));
        for (int i = 0; i < 6; ++i) {
            matrices.push();

            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west

            if (i == 1) {
                textureManager.bindTexture(this.animationTextures.getNorth().get(this.count));
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            }

            if (i == 2) {
                textureManager.bindTexture(this.animationTextures.getSouth().get(this.count));
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            }

            if (i == 3) {
                textureManager.bindTexture(this.animationTextures.getTop().get(this.count));
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            }

            if (i == 4) {
                textureManager.bindTexture(this.animationTextures.getEast().get(this.count));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            }

            if (i == 5) {
                textureManager.bindTexture(this.animationTextures.getWest().get(this.count));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 1.0F).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(1.0F, 1.0F).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(1.0F, 0.0F).color(1f, 1f, 1f, alpha).next();
            tessellator.draw();
            matrices.pop();
        }

        if (System.currentTimeMillis() >= (this.lastTime + this.frameTimeMilliSeconds)) {
            if (this.count < this.animationTextures.getSize()) {
                if (this.count + 1 == this.animationTextures.getSize()) {
                    this.count = 0;
                } else {
                    this.count++;
                }
            }
            this.lastTime = System.currentTimeMillis();
        }
    }

    @Override
    public Codec<? extends AbstractSkybox> getCodec(int schemaVersion) {
        if (schemaVersion == 2) {
            return CODEC;
        }
        return null;
    }

    @Override
    public String getType() {
        return "animated-square-textured";
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
    }

    public AnimationTextures getAnimationTextures() {
        return animationTextures;
    }

    private float getFramesPerSecond() {
        return framesPerSecond;
    }
}
