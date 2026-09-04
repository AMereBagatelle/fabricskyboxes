package me.flashyreese.mods.nuit.api.skyboxes;

import com.mojang.blaze3d.vertex.PoseStack;
import me.flashyreese.mods.nuit.mixin.SkyRendererAccessor;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

/**
 * Base skybox lifecycle contract.
 */
public interface Skybox {
    /**
     * Render order for active skyboxes. Lower layers render first.
     */
    default int getLayer() {
        return 0;
    }

    /**
     * Renders this skybox for the current frame.
     *
     * @param skyRendererAccessor stable access to vanilla sky buffers.
     * @param poseStack sky model-view stack.
     * @param projectionMatrix frame projection matrix.
     * @param tickDelta partial tick.
     * @param camera active camera.
     * @param thickFog whether thick fog is active.
     * @param fogCallback restores vanilla fog state.
     */
    void render(SkyRendererAccessor skyRendererAccessor, PoseStack poseStack, Matrix4f projectionMatrix,
                float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback);

    /**
     * Updates this skybox once per client world tick.
     */
    void tick(ClientLevel clientLevel);

    /**
     * @return whether this skybox should currently participate in rendering or skybox-dependent effects.
     */
    boolean isActive();
}
