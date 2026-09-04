package me.flashyreese.mods.nuit.skybox.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.flashyreese.mods.nuit.components.*;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import me.flashyreese.mods.nuit.render.NuitShaders;
import me.flashyreese.mods.nuit.skybox.AbstractSkybox;
import me.flashyreese.mods.nuit.util.Utils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiTexturedSkybox extends TexturedSkybox {
    public static Codec<MultiTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.optionalFieldOf("properties", Properties.of()).forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.of()).forGetter(AbstractSkybox::getConditions),
            Blend.CODEC.optionalFieldOf("blend", Blend.normal()).forGetter(TexturedSkybox::getBlend),
            AnimatableTexture.CODEC.listOf().optionalFieldOf("animatableTextures", new ArrayList<>()).forGetter(MultiTexturedSkybox::getAnimations)
    ).apply(instance, MultiTexturedSkybox::new));
    protected final List<AnimatableTexture> animatableTextures;

    private final float quadSize = 100F;
    private final UVRange quad = new UVRange(-this.quadSize, -this.quadSize, this.quadSize, this.quadSize);

    public MultiTexturedSkybox(Properties properties, Conditions conditions, Blend blend, List<AnimatableTexture> animatableTextures) {
        super(properties, conditions, blend);
        this.animatableTextures = animatableTextures;
    }

    @Override
    public void renderSkybox(SkyRendererAccessor skyRendererAccess, PoseStack poseStack, Matrix4f projectionMatrix,
                             float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback) {
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
        for (AnimatableTexture animatableTexture : this.animatableTextures) {
            animatableTexture.update(level.getGameTime(), tickDelta);
        }

        for (AnimatableTexture animatableTexture : this.animatableTextures) {
            if (animatableTexture.getCurrentFrame() == null) {
                continue;
            }

            boolean interpolate = shouldInterpolate(animatableTexture)
                    && NuitShaders.getFrameBlendedSkybox() != null;
            VertexFormat vertexFormat = interpolate
                    ? NuitShaders.FRAME_BLENDED_SKYBOX_FORMAT
                    : DefaultVertexFormat.POSITION_TEX;
            BufferBuilder bufferBuilder = null;

            for (int face = 0; face < 6; ++face) {
                UVRange faceUVRange = Utils.TEXTURE_FACES[face];
                UVRange intersect = Utils.findUVIntersection(faceUVRange, animatableTexture.getUvRange()); // todo: cache this intersections so we don't waste gpu cycles
                if (intersect == null) {
                    continue;
                }

                if (bufferBuilder == null) {
                    bufferBuilder = Tesselator.getInstance().begin(
                            VertexFormat.Mode.QUADS,
                            vertexFormat
                    );
                }

                poseStack.pushPose();
                Utils.rotateSkyBoxByFace(poseStack, face);
                Matrix4f matrix4f = poseStack.last().pose();
                UVRange position = Utils.mapUVRanges(faceUVRange, this.quad, intersect);
                UVRange currentFrame = Utils.mapUVRanges(
                        animatableTexture.getUvRange(),
                        animatableTexture.getCurrentFrame(),
                        intersect
                );
                if (interpolate) {
                    UVRange nextFrame = Utils.mapUVRanges(
                            animatableTexture.getUvRange(),
                            animatableTexture.getNextFrame(),
                            intersect
                    );
                    addFrameBlendedVertices(
                            bufferBuilder,
                            matrix4f,
                            position,
                            currentFrame,
                            nextFrame,
                            animatableTexture.getFrameBlend()
                    );
                } else {
                    addTexturedVertices(bufferBuilder, matrix4f, position, currentFrame);
                }
                poseStack.popPose();
            }

            if (bufferBuilder != null) {
                RenderSystem.setShader(interpolate
                        ? NuitShaders::getFrameBlendedSkybox
                        : GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, animatableTexture.getTexture().getTextureId());
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            }
        }
    }

    private static boolean shouldInterpolate(AnimatableTexture animatableTexture) {
        return animatableTexture.isInterpolate()
                && animatableTexture.hasMultipleFrames()
                && animatableTexture.getNextFrame() != null
                && animatableTexture.getFrameBlend() > 0.0F;
    }

    private void addTexturedVertices(
            BufferBuilder builder,
            Matrix4f matrix4f,
            UVRange position,
            UVRange currentFrame
    ) {
        builder.addVertex(matrix4f, position.minU(), -this.quadSize, position.minV()).setUv(currentFrame.minU(), currentFrame.minV());
        builder.addVertex(matrix4f, position.minU(), -this.quadSize, position.maxV()).setUv(currentFrame.minU(), currentFrame.maxV());
        builder.addVertex(matrix4f, position.maxU(), -this.quadSize, position.maxV()).setUv(currentFrame.maxU(), currentFrame.maxV());
        builder.addVertex(matrix4f, position.maxU(), -this.quadSize, position.minV()).setUv(currentFrame.maxU(), currentFrame.minV());
    }

    private void addFrameBlendedVertices(
            BufferBuilder builder,
            Matrix4f matrix4f,
            UVRange position,
            UVRange currentFrame,
            UVRange nextFrame,
            float frameBlend
    ) {
        addFrameBlendedVertex(builder, matrix4f, position.minU(), position.minV(), currentFrame.minU(), currentFrame.minV(), nextFrame.minU(), nextFrame.minV(), frameBlend);
        addFrameBlendedVertex(builder, matrix4f, position.minU(), position.maxV(), currentFrame.minU(), currentFrame.maxV(), nextFrame.minU(), nextFrame.maxV(), frameBlend);
        addFrameBlendedVertex(builder, matrix4f, position.maxU(), position.maxV(), currentFrame.maxU(), currentFrame.maxV(), nextFrame.maxU(), nextFrame.maxV(), frameBlend);
        addFrameBlendedVertex(builder, matrix4f, position.maxU(), position.minV(), currentFrame.maxU(), currentFrame.minV(), nextFrame.maxU(), nextFrame.minV(), frameBlend);
    }

    private static void addFrameBlendedVertex(
            BufferBuilder builder,
            Matrix4f matrix4f,
            float x,
            float z,
            float currentU,
            float currentV,
            float nextU,
            float nextV,
            float frameBlend
    ) {
        int blend = Math.round(Mth.clamp(frameBlend, 0.0F, 1.0F) * 255.0F);
        builder.addVertex(matrix4f, x, -100.0F, z)
                .setUv(currentU, currentV)
                .setUv1(packUv(nextU), packUv(nextV))
                .setColor(blend, 255, 255, 255);
    }

    private static int packUv(float uv) {
        return Math.round(Mth.clamp(uv, 0.0F, 1.0F) * Short.MAX_VALUE);
    }

    public List<AnimatableTexture> getAnimations() {
        return this.animatableTextures;
    }

    @Override
    public List<ResourceLocation> getTexturesToRegister() {
        return this.animatableTextures.stream().map(texture -> texture.getTexture().getTextureId()).toList();
    }
}
