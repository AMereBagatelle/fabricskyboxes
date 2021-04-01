package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;

public abstract class TexturedSkybox extends AbstractSkybox implements RotatableSkybox {
    public Rotation rotation;
    public Blend blend;

    protected TexturedSkybox() {
    }

    protected TexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, Blend blend) {
        super(properties, conditions, decorations);
        this.blend = blend;
        this.rotation = properties.getRotation();
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
        RenderSystem.enableBlend();

        blend.applyBlendFunc();

        Vector3f rotationStatic = this.rotation.getStatic();

        ClientWorld world = MinecraftClient.getInstance().world;
        assert world != null;
        float timeRotation = !this.shouldRotate ? 0 : ((float) world.getTimeOfDay() / 24000) * 360;

        matrices.push();
        this.applyTimeRotation(matrices, timeRotation);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
        this.renderSkybox(worldRendererAccess, matrices, tickDelta);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
        matrices.pop();

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

    private void applyTimeRotation(MatrixStack matrices, float timeRotation) {
        // Very ugly, find a better way to do this
        Vector3f timeRotationAxis = this.rotation.getAxis();
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(timeRotationAxis.getX()));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotationAxis.getY()));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(timeRotationAxis.getZ()));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(timeRotation * rotation.getRotationSpeed()));
        matrices.multiply(Vector3f.NEGATIVE_Z.getDegreesQuaternion(timeRotationAxis.getZ()));
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(timeRotationAxis.getY()));
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion(timeRotationAxis.getX()));
    }

    public Blend getBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
