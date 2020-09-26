package amerebagatelle.github.io.fabricskyboxes.skyboxes.textured;

import amerebagatelle.github.io.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import amerebagatelle.github.io.fabricskyboxes.skyboxes.AbstractSkybox;
import amerebagatelle.github.io.fabricskyboxes.util.JsonObjectWrapper;
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

    /**
     * Overrides and makes final here as there are options that should always be respected in a textured skybox.
     *
     * @param worldRendererAccess Access to the worldRenderer as skyboxes often require it.
     * @param matrices            The current MatrixStack.
     * @param tickDelta           The current tick delta.
     */
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

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, alpha);

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    /**
     * Override this method instead of render if you are extending this skybox.
     */
    public abstract void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    /**
     * Sets up the blend for a textured skybox.
     */
    public void setupBlendFunc() {
        RenderSystem.enableBlend();
        if (blend)
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        else RenderSystem.defaultBlendFunc();
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
        axis = new float[]{jsonObjectWrapper.getOptionalArrayFloat("axis", 0, 0), jsonObjectWrapper.getOptionalArrayFloat("axis", 1, 0), jsonObjectWrapper.getOptionalArrayFloat("axis", 2, 0)};
        blend = jsonObjectWrapper.getOptionalBoolean("shouldBlend", true);
    }
}
