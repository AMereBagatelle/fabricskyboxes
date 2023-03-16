package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public abstract class TexturedSkybox extends AbstractSkybox implements RotatableSkybox {
    public Rotation rotation;
    public Blend blend;

    protected TexturedSkybox() {
    }

    protected TexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend) {
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
    public final void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        blend.applyBlendFunc(this.alpha);

        ClientWorld world = Objects.requireNonNull(MinecraftClient.getInstance().world);

        Vector3f rotationStatic = this.rotation.getStatic();

        matrices.push();
        float timeRotationX = this.getProperties().isShouldRotate() && this.rotation.getRotationSpeedX() != 0F ? 360F * MathHelper.floorMod(world.getTimeOfDay() / (24000 / this.rotation.getRotationSpeedX()), 1) : 0;
        float timeRotationY = this.getProperties().isShouldRotate() && this.rotation.getRotationSpeedY() != 0F ? 360F * MathHelper.floorMod(world.getTimeOfDay() / (24000 / this.rotation.getRotationSpeedY()), 1) : 0;
        float timeRotationZ = this.getProperties().isShouldRotate() && this.rotation.getRotationSpeedZ() != 0F ? 360F * MathHelper.floorMod(world.getTimeOfDay() / (24000 / this.rotation.getRotationSpeedZ()), 1) : 0;
        this.applyTimeRotation(matrices, timeRotationX, timeRotationY, timeRotationZ);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationStatic.x()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationStatic.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationStatic.z()));
        this.renderSkybox(worldRendererAccess, matrices, tickDelta, camera, thickFog);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotationStatic.z()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationStatic.y()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationStatic.x()));
        matrices.pop();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        this.renderDecorations(worldRendererAccess, matrices, matrix4f, tickDelta, bufferBuilder, this.alpha);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        // fixme:
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Override this method instead of render if you are extending this skybox.
     */
    public abstract void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, Camera camera, boolean thickFog);

    private void applyTimeRotation(MatrixStack matrices, float timeRotationX, float timeRotationY, float timeRotationZ) {
        // Very ugly, find a better way to do this
        Vector3f timeRotationAxis = this.rotation.getAxis();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(timeRotationAxis.x()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(timeRotationAxis.y()));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(timeRotationAxis.z()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(timeRotationX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(timeRotationY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(timeRotationZ));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(timeRotationAxis.z()));
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(timeRotationAxis.y()));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(timeRotationAxis.x()));
    }

    public Blend getBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
