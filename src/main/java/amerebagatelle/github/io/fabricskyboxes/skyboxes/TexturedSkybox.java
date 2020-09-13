package amerebagatelle.github.io.fabricskyboxes.skyboxes;

import amerebagatelle.github.io.fabricskyboxes.mixin.WorldRendererAccess;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class TexturedSkybox extends AbstractSkybox {
    public final Identifier TEXTURE_NORTH;
    public final Identifier TEXTURE_SOUTH;
    public final Identifier TEXTURE_EAST;
    public final Identifier TEXTURE_WEST;
    public final Identifier TEXTURE_TOP;
    public final Identifier TEXTURE_BOTTOM;
    public final int[] axis;

    public TexturedSkybox(Identifier north, Identifier south, Identifier east, Identifier west, Identifier top, Identifier bottom, int[] axis) {
        TEXTURE_NORTH = north;
        TEXTURE_SOUTH = south;
        TEXTURE_EAST = east;
        TEXTURE_WEST = west;
        TEXTURE_TOP = top;
        TEXTURE_BOTTOM = bottom;
        this.axis = axis;
    }

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        TextureManager textureManager = worldRendererAccess.getTextureManager();

        assert MinecraftClient.getInstance().world != null;
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(axis[0]));
        float timeRotation = !shouldRotate ? axis[1] : axis[1] + ((float) MinecraftClient.getInstance().world.getTimeOfDay() / 24000) * 360;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotation));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(axis[2]));
        textureManager.bindTexture(TEXTURE_BOTTOM);
        for (int i = 0; i < 6; ++i) {
            matrices.push();

            // 0 = bottom
            // 1 = north
            // 2 = south
            // 3 = top
            // 4 = east
            // 5 = west

            if (i == 1) {
                textureManager.bindTexture(TEXTURE_NORTH);
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(90.0F));
            }

            if (i == 2) {
                textureManager.bindTexture(TEXTURE_SOUTH);
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
            }

            if (i == 3) {
                textureManager.bindTexture(TEXTURE_TOP);
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
            }

            if (i == 4) {
                textureManager.bindTexture(TEXTURE_EAST);
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
            }

            if (i == 5) {
                textureManager.bindTexture(TEXTURE_WEST);
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
        matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(axis[2]));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(timeRotation));
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(axis[0]));

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }
}
