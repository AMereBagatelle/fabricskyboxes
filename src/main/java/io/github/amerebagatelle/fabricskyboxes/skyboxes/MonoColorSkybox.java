package io.github.amerebagatelle.fabricskyboxes.skyboxes;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.Properties;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.json.JsonClassDiscriminator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

@Serializable(with = )
@JsonClassDiscriminator(discriminator = "monocolor")
public class MonoColorSkybox extends AbstractSkybox {
    public RGBA color;

    public MonoColorSkybox(Properties properties, Conditions conditions, Decorations decorations, RGBA color) {
        super(properties, conditions, decorations);
        this.color = color;
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        if (this.alpha > 0) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = Objects.requireNonNull(client.world);
            RenderSystem.disableTexture();
            BackgroundRenderer.setFogBlack();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), 1.0F);
            worldRendererAccess.getLightSkyBuffer().bind();
            worldRendererAccess.getLightSkyBuffer().drawElements();
            VertexBuffer.unbind();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float[] skyColor = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
            float skySide;
            float skyColorGreen;
            float o;
            float p;
            float q;
            if (skyColor != null) {
                RenderSystem.disableTexture();
                matrices.push();
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                skySide = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(skySide));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                float skyColorRed = skyColor[0];
                skyColorGreen = skyColor[1];
                float skyColorBlue = skyColor[2];
                Matrix4f matrix4f2 = matrices.peek().getPositionMatrix();
                bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f2, 0.0F, 100.0F, 0.0F).color(skyColorRed, skyColorGreen, skyColorBlue, skyColor[3]).next();

                for (int n = 0; n <= 16; ++n) {
                    o = (float) n * 6.2831855F / 16.0F;
                    p = MathHelper.sin(o);
                    q = MathHelper.cos(o);
                    bufferBuilder.vertex(matrix4f2, p * 120.0F, q * 120.0F, -q * 40.0F * skyColor[3]).color(skyColor[0], skyColor[1], skyColor[2], 0.0F).next();
                }

                BufferRenderer.drawWithShader(bufferBuilder.end());
                matrices.pop();
            }

            this.renderDecorations(worldRendererAccess, matrices, matrix4f, tickDelta, bufferBuilder, this.alpha);

            RenderSystem.disableTexture();
            RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
            //noinspection ConstantConditions
            double d = client.player.getCameraPosVec(tickDelta).y - world.getLevelProperties().getSkyDarknessHeight(world);
            if (d < 0.0D) {
                matrices.push();
                matrices.translate(0.0D, 12.0D, 0.0D);
                matrices.pop();
            }

            if (world.getDimensionEffects().isAlternateSkyColor()) {
                RenderSystem.setShaderColor(this.color.getRed() * 0.2F + 0.04F, this.color.getBlue() * 0.2F + 0.04F, this.color.getGreen() * 0.6F + 0.1F, 1.0F);
            } else {
                RenderSystem.setShaderColor(this.color.getRed(), this.color.getBlue(), this.color.getGreen(), 1.0F);
            }

            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
        }
    }

    public RGBA getColor() {
        return this.color;
    }
}
