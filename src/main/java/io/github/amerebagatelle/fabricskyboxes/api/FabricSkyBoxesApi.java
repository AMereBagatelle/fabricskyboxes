package io.github.amerebagatelle.fabricskyboxes.api;

import com.google.gson.JsonObject;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import io.github.amerebagatelle.fabricskyboxes.util.JsonObjectWrapper;
import net.minecraft.util.Identifier;

public interface FabricSkyBoxesApi {

    /**
     * @since API v0.0
     */
    static FabricSkyBoxesApi getInstance() {
        return SkyboxManager.getInstance();
    }

    /**
     * Gets the version of this API, This is incremented when changes are implemented
     * without breaking API. Mods can use this to check if given API functionality
     * is available on the current version of installed FabricSkyBoxes.
     *
     * @return The current version of the API
     */
    int getApiVersion();

    /**
     * Gets the state of the FabricSkyBoxes mod.
     *
     * @return Whether FabricSkyBoxes is enabled.
     */
    boolean isEnabled();

    /**
     * Allows mods to set the state of the FabricSkyBoxes.
     *
     * @param enabled State of the FabricSkyBoxes.
     */
    void setEnabled(boolean enabled);

    /**
     * Allows mods to add new skyboxes at runtime.
     *
     * @param identifier Identifier for skybox.
     * @param skybox     Skybox implementation.
     */
    void addSkybox(Identifier identifier, Skybox skybox);

    /**
     * Allows mods to add new skyboxes with a {@link JsonObject} at runtime.
     * This method applies {@link SkyboxManager#parseSkyboxJson(Identifier, JsonObjectWrapper)}
     * serialization and adds the skybox with {@link #addSkybox(Identifier, Skybox)}
     *
     * @param identifier Identifier for skybox.
     * @param jsonObject Json Object.
     */
    void addSkybox(Identifier identifier, JsonObject jsonObject);

    /**
     * Allows mods to add new permanent skyboxes at runtime.
     *
     * @param identifier Identifier for skybox.
     * @param skybox     Skybox implementation.
     */
    void addPermanentSkybox(Identifier identifier, Skybox skybox);

    /**
     * Clears all non-permanent skyboxes.
     */
    void clearSkyboxes();

    /**
     * Gets the current skybox that is being rendered.
     *
     * @return Current skybox being render, returns null of nothing is being rendered.
     */
    Skybox getCurrentSkybox();
}
