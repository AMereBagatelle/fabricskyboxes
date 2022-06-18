package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

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
    public final void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        blend.applyBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha); // Todo: Calculate our brightness based off position of the player and biomes/height ranges/weather and move this into Blend.

        ClientWorld world = Objects.requireNonNull(MinecraftClient.getInstance().world);

        Vec3f rotationStatic = this.rotation.getStatic();

        matrices.push();
        float timeRotation = this.shouldRotate ? (float) (world.getSkyAngleRadians(tickDelta) * (180 / Math.PI)) : 0;
        this.applyTimeRotation(matrices, timeRotation);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
        this.renderSkybox(worldRendererAccess, matrices, tickDelta, camera, thickFog);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
        matrices.pop();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        this.renderDecorations(worldRendererAccess, matrices, matrix4f, tickDelta, bufferBuilder, this.alpha);

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    /**
     * Override this method instead of render if you are extending this skybox.
     */
    public abstract void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta, Camera camera, boolean thickFog);

    private void applyTimeRotation(MatrixStack matrices, float timeRotation) {
        // Very ugly, find a better way to do this
        Vec3f timeRotationAxis = this.rotation.getAxis();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(timeRotationAxis.getX()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(timeRotationAxis.getY()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(timeRotationAxis.getZ()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(timeRotation * rotation.getRotationSpeed()));
        matrices.multiply(Vec3f.NEGATIVE_Z.getDegreesQuaternion(timeRotationAxis.getZ()));
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(timeRotationAxis.getY()));
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(timeRotationAxis.getX()));
    }

    public Blend getBlend() {
        return this.blend;
    }

    public Rotation getRotation() {
        return this.rotation;
    }
}
