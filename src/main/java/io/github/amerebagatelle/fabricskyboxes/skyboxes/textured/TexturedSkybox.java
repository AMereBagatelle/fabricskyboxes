package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.DecorationTextures;
import io.github.amerebagatelle.fabricskyboxes.util.object.Fade;
import io.github.amerebagatelle.fabricskyboxes.util.object.HeightEntry;
import io.github.amerebagatelle.fabricskyboxes.util.object.RGBA;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

public abstract class TexturedSkybox extends AbstractSkybox {
    public List<Float> axis;
    public boolean blend;

    protected TexturedSkybox() {
    }

    protected TexturedSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, boolean decorations, List<String> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, List<Float> axis, boolean blend, DecorationTextures decorationTextures) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, decorations, weather, biomes, dimensions, heightRanges, decorationTextures);
        this.axis = axis;
        this.blend = blend;
    }

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
        this.setupBlendFunc();

        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;
        float timeRotation = !this.shouldRotate ? this.axis.get(1) : this.axis.get(1) + ((float) world.getTimeOfDay() / 24000) * 360;

        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotation));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(this.axis.get(0)));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.axis.get(2)));
        this.renderSkybox(worldRendererAccess, matrices, tickDelta);
        matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(this.axis.get(2)));
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(this.axis.get(0)));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(timeRotation));

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, this.alpha);

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
        if (this.blend)
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        else RenderSystem.defaultBlendFunc();
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
        this.axis = Lists.newArrayList(jsonObjectWrapper.getOptionalArrayFloat("axis", 0, 0), jsonObjectWrapper.getOptionalArrayFloat("axis", 1, 0), jsonObjectWrapper.getOptionalArrayFloat("axis", 2, 0));
        this.blend = jsonObjectWrapper.getOptionalBoolean("shouldBlend", false);
    }

    public boolean isBlend() {
        return this.blend;
    }

    public List<Float> getAxis() {
        return this.axis;
    }
}
