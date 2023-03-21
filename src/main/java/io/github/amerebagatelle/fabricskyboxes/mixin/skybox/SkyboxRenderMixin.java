package io.github.amerebagatelle.fabricskyboxes.mixin.skybox;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.util.Constants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class SkyboxRenderMixin {

    /**
     * Contains the logic for when skyboxes should be rendered.
     */
    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void renderCustomSkyboxes(MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci) {
        SkyboxManager skyboxManager = SkyboxManager.getInstance();
        if (skyboxManager.isEnabled() && !skyboxManager.getActiveSkyboxes().isEmpty()) {
            runnable.run();
            this.renderSky((WorldRendererAccess) this, matrices, matrix4f, tickDelta);
            skyboxManager.renderSkyboxes((WorldRendererAccess) this, matrices, matrix4f, tickDelta, camera, bl);
            ci.cancel();
        }
    }

    // Vanilla copy of renderSky and renderEndSky with decorations stripped
    @Unique
    private void renderSky(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientWorld world = client.world;
        assert client.world != null;

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        if (client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.END) {
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
            RenderSystem.setShaderTexture(0, WorldRendererAccess.getEndSky());
            Tessellator tessellator = Tessellator.getInstance();

            for (int i = 0; i < 6; ++i) {
                matrices.push();
                if (i == 1) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                }

                if (i == 2) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                }

                if (i == 3) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
                }

                if (i == 4) {
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                }

                if (i == 5) {
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0F));
                }

                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(40, 40, 40, 255).next();
                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(40, 40, 40, 255).next();
                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(40, 40, 40, 255).next();
                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(40, 40, 40, 255).next();
                tessellator.draw();
                matrices.pop();
            }

            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        } else if (client.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.NORMAL) {
            Vec3d vec3d = world.getSkyColor(client.gameRenderer.getCamera().getPos(), tickDelta);
            float f = (float) vec3d.x;
            float g = (float) vec3d.y;
            float h = (float) vec3d.z;
            BackgroundRenderer.setFogBlack();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderColor(f, g, h, 1.0F);
            ShaderProgram shaderProgram = RenderSystem.getShader();
            worldRendererAccess.getLightSkyBuffer().bind();
            worldRendererAccess.getLightSkyBuffer().draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);
            VertexBuffer.unbind();
            RenderSystem.enableBlend();
            float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
            if (fs != null) {
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                matrices.push();
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
                float i = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
                float j = fs[0];
                float k = fs[1];
                float l = fs[2];
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]).next();

                for (int n = 0; n <= 16; ++n) {
                    float o = (float) n * (float) (Math.PI * 2) / 16.0F;
                    float p = MathHelper.sin(o);
                    float q = MathHelper.cos(o);
                    bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                }

                BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
                matrices.pop();
            }
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }
    }
}
