package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class TexturedSkybox extends AbstractSkybox {
    public Rotation rotation;
    public boolean blend;

    protected TexturedSkybox() {
    }

    protected TexturedSkybox(Fade fade, float maxAlpha, float transitionSpeed, boolean changeFog, RGBA fogColors, boolean shouldRotate, List<String> weather, List<Identifier> biomes, List<Identifier> dimensions, List<HeightEntry> heightRanges, Rotation rotation, boolean blend, Decorations decorations) {
        super(fade, maxAlpha, transitionSpeed, changeFog, fogColors, shouldRotate, weather, biomes, dimensions, heightRanges, decorations);
        this.rotation = rotation;
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

        List<Float> rotationStatic = rotation.getRotationStatic();

        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;
        float timeRotation = !this.shouldRotate ? 0 : ((float) world.getTimeOfDay() / 24000) * 360;

        matrices.push();
        applyTimeRotation(matrices, timeRotation);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.get(0)));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.get(1)));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.get(2)));
        this.renderSkybox(worldRendererAccess, matrices, tickDelta);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.get(2)));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.get(1)));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.get(0)));

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, this.alpha);
        matrices.pop();

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

    private void applyTimeRotation(MatrixStack matrices, float timeRotation) {
        // Very ugly, find a better way to do this
        List<Float> timeRotationAxis = rotation.getRotationAxis();
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(timeRotationAxis.get(0)));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotationAxis.get(1)));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(timeRotationAxis.get(2)));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotation));
        matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(timeRotationAxis.get(2)));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(timeRotationAxis.get(1)));
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(timeRotationAxis.get(0)));
    }

    @Override
    public void parseJson(JsonObjectWrapper jsonObjectWrapper) {
        super.parseJson(jsonObjectWrapper);
        this.rotation = new Rotation(Lists.newArrayList(0f, 0f, 0f), Lists.newArrayList(jsonObjectWrapper.getOptionalArrayFloat("axis", 0, 0), jsonObjectWrapper.getOptionalArrayFloat("axis", 1, 0), jsonObjectWrapper.getOptionalArrayFloat("axis", 2, 0)));
        this.blend = jsonObjectWrapper.getOptionalBoolean("shouldBlend", false);
    }

    public boolean isBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return rotation;
    }
}
