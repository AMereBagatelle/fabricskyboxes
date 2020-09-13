package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.SkyboxManager;
import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

public class MonoColorSkybox extends AbstractSkybox {
    private final float red;
    private final float blue;
    private final float green;

    public MonoColorSkybox(float red, float blue, float green) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        if (alpha > 0) {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = client.world;
            assert world != null;
            RenderSystem.disableTexture();
            BackgroundRenderer.setFogBlack();
            BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
            RenderSystem.depthMask(false);
            RenderSystem.enableFog();
            RenderSystem.color3f(red, blue, green);
            worldRendererAccess.getLightSkyBuffer().bind();
            worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
            worldRendererAccess.getLightSkyBuffer().draw(matrices.peek().getModel(), 7);
            VertexBuffer.unbind();
            worldRendererAccess.getSkyVertexFormat().endDrawing();
            RenderSystem.disableFog();
            RenderSystem.disableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float[] fs = world.getSkyProperties().getSkyColor(world.method_30274(tickDelta), tickDelta);
            float r;
            float s;
            float o;
            float p;
            float q;
            if (fs != null) {
                RenderSystem.disableTexture();
                RenderSystem.shadeModel(7425);
                matrices.push();
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                r = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(r));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                float j = fs[0];
                s = fs[1];
                float l = fs[2];
                Matrix4f matrix4f = matrices.peek().getModel();
                bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, s, l, fs[3]).next();

                for (int n = 0; n <= 16; ++n) {
                    o = (float) n * 6.2831855F / 16.0F;
                    p = MathHelper.sin(o);
                    q = MathHelper.cos(o);
                    bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                }

                bufferBuilder.end();
                BufferRenderer.draw(bufferBuilder);
                matrices.pop();
                RenderSystem.shadeModel(7424);
            }

            RenderSystem.enableTexture();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.method_30274(tickDelta) * 360.0F));

            this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, alpha);

            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableFog();
            matrices.pop();
            RenderSystem.disableTexture();
            RenderSystem.color3f(0.0F, 0.0F, 0.0F);
            assert client.player != null;
            double d = client.player.getCameraPosVec(tickDelta).y - world.getLevelProperties().getSkyDarknessHeight();
            if (d < 0.0D) {
                matrices.push();
                matrices.translate(0.0D, 12.0D, 0.0D);
                worldRendererAccess.getDarkSkyBuffer().bind();
                worldRendererAccess.getSkyVertexFormat().startDrawing(0L);
                worldRendererAccess.getDarkSkyBuffer().draw(matrices.peek().getModel(), 7);
                VertexBuffer.unbind();
                worldRendererAccess.getSkyVertexFormat().endDrawing();
                matrices.pop();
            }

            if (world.getSkyProperties().isAlternateSkyColor()) {
                RenderSystem.color3f(red * 0.2F + 0.04F, blue * 0.2F + 0.04F, green * 0.6F + 0.1F);
            } else {
                RenderSystem.color3f(red, blue, green);
            }

            RenderSystem.enableTexture();
            RenderSystem.depthMask(true);
            RenderSystem.disableFog();
        }
    }

    @Override
    public float getAlpha() {
        float alpha = super.getAlpha();
        if (alpha > 0.1) {
            SkyboxManager.shouldChangeFog = true;
            SkyboxManager.fogRed = red;
            SkyboxManager.fogBlue = blue;
            SkyboxManager.fogGreen = green;
        }
        return alpha;
    }
}
