package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class MonoColorSkybox extends AbstractSkybox {
    public static Codec<MonoColorSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Properties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getProperties),
            Conditions.CODEC.optionalFieldOf("conditions", Conditions.DEFAULT).forGetter(AbstractSkybox::getConditions),
            Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
            RGBA.CODEC.optionalFieldOf("color", RGBA.DEFAULT).forGetter(MonoColorSkybox::getColor),
            Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(MonoColorSkybox::getBlend)
    ).apply(instance, MonoColorSkybox::new));
    public RGBA color;
    public Blend blend;

    public MonoColorSkybox() {
    }

    public MonoColorSkybox(Properties properties, Conditions conditions, Decorations decorations, RGBA color, Blend blend) {
        super(properties, conditions, decorations);
        this.color = color;
        this.blend = blend;
    }

    @Override
    public SkyboxType<? extends AbstractSkybox> getType() {
        return SkyboxType.MONO_COLOR_SKYBOX;
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog) {
        if (this.alpha > 0) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            this.blend.applyBlendFunc(this.alpha);
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

            for (int i = 0; i < 6; ++i) {
                matrices.push();
                if (i == 1) {
                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                } else if (i == 2) {
                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                } else if (i == 3) {
                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                } else if (i == 4) {
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
                } else if (i == 5) {
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                    matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
                }

                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.alpha).next();
                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.alpha).next();
                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.alpha).next();
                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.alpha).next();
                BufferRenderer.drawWithShader(bufferBuilder.end());
                matrices.pop();
            }

            this.renderDecorations(worldRendererAccess, matrices, projectionMatrix, tickDelta, bufferBuilder, this.alpha);

            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
        }
    }

    public RGBA getColor() {
        return this.color;
    }

    public Blend getBlend() {
        return blend;
    }
}
