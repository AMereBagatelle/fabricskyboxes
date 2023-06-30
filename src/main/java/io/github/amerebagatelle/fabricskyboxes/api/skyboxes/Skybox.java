package io.github.amerebagatelle.fabricskyboxes.api.skyboxes;

import io.github.amerebagatelle.fabricskyboxes.mixin.skybox.WorldRendererAccess;
import net.minecraft.client.util.math.MatrixStack;

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
    void render(WorldRendererAccess worldRendererAccess, MatrixStack matrices, float tickDelta);

    /**
     * Gets the state of the skybox.
     *
     * @return State of the skybox.
     */
    boolean isActive();

    /**
     * Whether the skybox will be active in the next frame.
     *
     * @return State of skybox of the next frame.
     */
    boolean isActiveLater();
}
