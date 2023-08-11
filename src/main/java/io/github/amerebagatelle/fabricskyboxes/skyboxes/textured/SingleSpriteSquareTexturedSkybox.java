package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class SingleSpriteSquareTexturedSkybox extends TexturedSkybox {
    public static Codec<SingleSpriteSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
            Texture.CODEC.fieldOf("texture").forGetter(SingleSpriteSquareTexturedSkybox::getTexture)
    ).apply(instance, SingleSpriteSquareTexturedSkybox::new));
    protected Texture texture;

    private final UVRanges uvRanges;

    public SingleSpriteSquareTexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, Texture texture) {
        super(properties, conditions, decorations, blend);
        this.texture = texture;
        this.uvRanges = new UVRanges(
                texture.withUV(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1),
                texture.withUV(2.0F / 3.0F, 0, 1, 1.0F / 2.0F),
                texture.withUV(2.0F / 3.0F, 1.0F / 2.0F, 1, 1),
                texture.withUV(0, 1.0F / 2.0F, 1.0F / 3.0F, 1),
                texture.withUV(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F),
                texture.withUV(0, 0, 1.0F / 3.0F, 1.0F / 2.0F)
        );
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.SINGLE_SPRITE_SQUARE_TEXTURED_SKYBOX;
    }

    public Texture getTexture() {
        return this.texture;
    }

    @Override
    public void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, Camera camera, boolean thickFog) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        RenderSystem.setShaderTexture(0, texture.getTextureId());
        for (int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west
            UVRange tex = this.uvRanges.byId(i);
            matrices.push();

            if (i == 1) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            } else if (i == 2) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            } else if (i == 3) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
            } else if (i == 4) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
            } else if (i == 5) {
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
            }

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(tex.getMinU(), tex.getMinV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(tex.getMinU(), tex.getMaxV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(tex.getMaxU(), tex.getMaxV()).color(1f, 1f, 1f, alpha).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(tex.getMaxU(), tex.getMinV()).color(1f, 1f, 1f, alpha).next();
            matrices.pop();
        }
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
}
