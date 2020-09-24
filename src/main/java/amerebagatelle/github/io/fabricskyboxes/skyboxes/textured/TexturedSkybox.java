package amerebagatelle.github.io.fabricskyboxes.skyboxes.textured;

import amerebagatelle.github.io.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;

public abstract class TexturedSkybox extends AbstractSkybox {
    public float[] axis;
    public boolean blend;

    @Override
    public void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        RenderSystem.enableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));
        this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, alpha);
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(world.getSkyAngle(tickDelta) * 360.0F));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(-90.0F));
    }
}
