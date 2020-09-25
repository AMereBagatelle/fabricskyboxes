package amerebagatelle.github.io.fabricskyboxes.skyboxes.textured;

import amerebagatelle.github.io.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;

public abstract class TexturedSkybox extends AbstractSkybox {
    public float[] axis;
    public boolean blend;

    @Override
    public final void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        setupBlendFunc();

        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;
        float timeRotation = !shouldRotate ? axis[1] : axis[1] + ((float) world.getTimeOfDay() / 24000) * 360;

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotation));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(axis[0]));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(axis[2]));
        renderSkybox(worldRendererAccess, matrices, tickDelta);
        matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(axis[2]));
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(axis[0]));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(timeRotation));

        super.render(worldRendererAccess, matrices, tickDelta);

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    public abstract void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    public void setupBlendFunc() {
        RenderSystem.enableBlend();
        if (blend)
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        else RenderSystem.defaultBlendFunc();
    }
}
