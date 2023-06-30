package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.RotatableSkybox;
import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

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
    public final void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta) {
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        this.blend.applyBlendFunc(this.alpha);

        ClientWorld world = Objects.requireNonNull(MinecraftClient.getInstance().world);

        Vec3f rotationStatic = this.rotation.getStatic();

        matrices.push();

        // axis + time rotation
        double timeRotationX = this.rotation.getRotationSpeedX() != 0F ? this.rotation.getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.rotation.getRotationSpeedX()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.rotation.getRotationSpeedX()), 1))) : 0D;
        double timeRotationY = this.rotation.getRotationSpeedY() != 0F ? this.rotation.getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.rotation.getRotationSpeedY()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.rotation.getRotationSpeedY()), 1))) : 0D;
        double timeRotationZ = this.rotation.getRotationSpeedZ() != 0F ? this.rotation.getSkyboxRotation() ? 360D * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.rotation.getRotationSpeedZ()), 1) : 360D * world.getDimension().getSkyAngle((long) (24000 * MathHelper.floorMod(world.getTimeOfDay() / (24000.0D / this.rotation.getRotationSpeedZ()), 1))) : 0D;
        this.applyTimeRotation(matrices, (float) timeRotationX, (float) timeRotationY, (float) timeRotationZ);
        // static
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotationStatic.getX()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotationStatic.getY()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotationStatic.getZ()));
        RenderSystem.enableTexture();
        this.renderSkybox(worldRendererAccess, matrices, tickDelta);
        RenderSystem.disableTexture();
        matrices.pop();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();

        this.renderDecorations(worldRendererAccess, matrices, tickDelta, bufferBuilder, this.alpha);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    /**
     * Override this method instead of render if you are extending this skybox.
     */
    public abstract void renderSkybox(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    private void applyTimeRotation(MatrixStack matrices, float timeRotationX, float timeRotationY, float timeRotationZ) {
        // Very ugly, find a better way to do this
        Vec3f timeRotationAxis = this.rotation.getAxis();
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(timeRotationAxis.getX()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(timeRotationAxis.getY()));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(timeRotationAxis.getZ()));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(timeRotationX));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(timeRotationY));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(timeRotationZ));
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
