package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;

public class OverworldSkybox extends AbstractSkybox {

    @Override
    public void render(MatrixStack matrixStack, float delta, VertexBuffer lightSkyBuffer, VertexBuffer darkSkyBuffer, VertexBuffer starsBuffer, VertexFormat skyVertexFormat, TextureManager textureManager, float alpha) {
        ClientWorld world = client.world;
        RenderSystem.disableTexture();
        Vec3d vec3d = world.method_23777(this.client.gameRenderer.getCamera().getBlockPos(), delta);
        float f = (float)vec3d.x;
        float g = (float)vec3d.y;
        float h = (float)vec3d.z;
        BackgroundRenderer.setFogBlack();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.depthMask(false);
        RenderSystem.enableFog();
        RenderSystem.color3f(f, g, h);
        lightSkyBuffer.bind();
        skyVertexFormat.startDrawing(0L);
        lightSkyBuffer.draw(matrixStack.peek().getModel(), 7);
        VertexBuffer.unbind();
        skyVertexFormat.endDrawing();
        RenderSystem.disableFog();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] fs = world.getSkyProperties().getSkyColor(world.getSkyAngle(delta), delta);
        float r;
        float s;
        float o;
        float p;
        float q;
        if (fs != null) {
            RenderSystem.disableTexture();
            RenderSystem.shadeModel(7425);
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            r = MathHelper.sin(world.getSkyAngleRadians(delta)) < 0.0F ? 180.0F : 0.0F;
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(r));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
            float j = fs[0];
            s = fs[1];
            float l = fs[2];
            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, s, l, alpha).next();
            int m = 1;

            for(int n = 0; n <= 16; ++n) {
                o = (float)n * 6.2831855F / 16.0F;
                p = MathHelper.sin(o);
                q = MathHelper.cos(o);
                bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
            }

            bufferBuilder.end();
            BufferRenderer.draw(bufferBuilder);
            matrixStack.pop();
            RenderSystem.shadeModel(7424);
        }

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrixStack.push();
        r = 1.0F - world.getRainGradient(delta);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, r);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(delta) * 360.0F));
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        s = 30.0F;
        renderSun(textureManager, bufferBuilder, matrix4f2, s);
        s = 20.0F;
        int t = world.getMoonPhase();
        int u = t % 4;
        int v = t / 4 % 2;
        float w = (float)(u + 0) / 4.0F;
        o = (float)(v + 0) / 2.0F;
        p = (float)(u + 1) / 4.0F;
        q = (float)(v + 1) / 2.0F;
        renderMoon(textureManager, bufferBuilder, matrix4f2, s, p, q, w, o);
        RenderSystem.disableTexture();
        renderStars(world, delta, r, starsBuffer, skyVertexFormat, matrixStack);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableFog();
        matrixStack.pop();
        RenderSystem.disableTexture();
        RenderSystem.color3f(0.0F, 0.0F, 0.0F);
        double d = this.client.player.getCameraPosVec(delta).y - world.getLevelProperties().getSkyDarknessHeight();
        if (d < 0.0D) {
            matrixStack.push();
            matrixStack.translate(0.0D, 12.0D, 0.0D);
            darkSkyBuffer.bind();
            skyVertexFormat.startDrawing(0L);
            darkSkyBuffer.draw(matrixStack.peek().getModel(), 7);
            VertexBuffer.unbind();
            skyVertexFormat.endDrawing();
            matrixStack.pop();
        }

        if (world.getSkyProperties().isAlternateSkyColor()) {
            RenderSystem.color3f(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F);
        } else {
            RenderSystem.color3f(f, g, h);
        }

        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.disableFog();
    }
}
