package io.github.amerebagatelle.fabricskyboxes.api.skyboxes;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public interface Skybox {

    /**
     * For purposes on which order the skyboxes will be rendered.
     * The default priority will be set to 0.
     *
     * @return The priority of the skybox.
     */
    default int getPriority() {
        return 0;
    }

    /**
     * The main render method for a skybox.
     * Override this if you are creating a skybox from this one.
     *
     * @param worldRendererAccess Access to the worldRenderer as skyboxes often require it.
     * @param matrices            The current MatrixStack.
     * @param tickDelta           The current tick delta.
     * @param camera              The player camera.
     * @param thickFog            Is using thick fog.
     */
    void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, Matrix4f matrix4f, float tickDelta, Camera camera, boolean thickFog);

    /**
     * Gets the state of the skybox.
     *
     * @return State of the skybox.
     */
    boolean isActive();
}
