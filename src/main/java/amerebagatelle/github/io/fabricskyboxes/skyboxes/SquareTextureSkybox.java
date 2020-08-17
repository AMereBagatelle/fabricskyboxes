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

public class SquareTextureSkybox extends AbstractSkybox {
    private final Identifier texture;

    public SquareTextureSkybox(Identifier texture) {
        this.texture = texture;
    }

    @Override
    public void render(MatrixStack matrixStack, float delta, VertexBuffer lightSkyBuffer, VertexBuffer darkSkyBuffer, VertexBuffer starsBuffer, VertexFormat skyVertexFormat, TextureManager textureManager, float alpha) {
        ClientWorld world = client.world;
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        textureManager.bindTexture(texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        float u = 0.0f;
        float v = 0.0f;
        float width = 1020.0f;
        float height = 1020.0f;
        for(int i = 0; i < 6; ++i) {
            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west

            matrixStack.push();
            if (i == 1) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                u = 1028.0f;
                v = 1028.0f;
            }

            if (i == 2) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
                u = 2048.0f;
                v = 0.0f;
            }

            if (i == 3) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                u = 1028.0f;
                v = 0.0f;
            }

            if (i == 4) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
                u = 2048.0f;
                v = 1028.0f;
            }

            if (i == 5) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
                u = 0.0f;
                v = 1028.0f;
            }

            Matrix4f matrix4f = matrixStack.peek().getModel();
            bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(u / 3072, v / 2048).color(255, 255, 255, (int)(alpha*255)).next();
            bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(u / 3072, (v+width) / 2048).color(255, 255, 255, (int)(alpha*255)).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture((u+height) / 3072, (v+width) / 2048).color(255, 255, 255, (int)(alpha*255)).next();
            bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture((u+height) / 3072, v / 2048).color(255, 255, 255, (int)(alpha*255)).next();
            tessellator.draw();
            matrixStack.pop();
        }

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrixStack.push();
        assert world != null;
        float r = 1.0F - world.getRainGradient(delta);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, r);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(delta) * 360.0F));
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        float s = 30.0F;
        renderSun(textureManager, bufferBuilder, matrix4f2, s);
        s = 20.0F;
        int t = world.getMoonPhase();
        int u2 = t % 4;
        int v2 = t / 4 % 2;
        float w = (float)(u2) / 4.0F;
        float o = (float)(v2) / 2.0F;
        float p = (float)(u2 + 1) / 4.0F;
        float q = (float)(v2 + 1) / 2.0F;
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

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}
