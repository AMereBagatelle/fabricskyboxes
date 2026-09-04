package me.flashyreese.mods.nuit.api;

import com.google.gson.JsonObject;
import me.flashyreese.mods.nuit.SkyboxManager;
import me.flashyreese.mods.nuit.api.skyboxes.Skybox;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NuitApi {
    /**
     * @since API v0.0
     */
    static NuitApi getInstance() {
        return SkyboxManager.getInstance();
    }

    /**
     * Gets the version of this API, This is incremented when changes are implemented
     * without breaking API. Mods can use this to check if given API functionality
     * is available on the current version of installed Nuit.
     *
     * @return The current version of the API
     */
    static int getApiVersion() {
        return 1;
    }

    /**
     * Allows mods to add new skyboxes at runtime.
     *
     * @param resourceLocation Identifier for skybox.
     * @param skybox           Skybox implementation.
     */
    void addSkybox(ResourceLocation resourceLocation, Skybox skybox);

    /**
     * Allows mods to add new skyboxes with a {@link JsonObject} at runtime.
     * This method applies {@link SkyboxManager#parseSkyboxJson(ResourceLocation, JsonObject)}
     * serialization and adds the skybox with {@link #addSkybox(ResourceLocation, Skybox)}
     *
     * @param resourceLocation Identifier for skybox.
     * @param jsonObject       Json Object.
     */
    void addSkybox(ResourceLocation resourceLocation, JsonObject jsonObject);

    /** Parses a skybox without adding it to the manager. */
    Optional<Skybox> parseSkybox(ResourceLocation resourceLocation, JsonObject jsonObject);

    /**
     * Allows mods to add new permanent skyboxes at runtime.
     *
     * @param resourceLocation Identifier for skybox.
     * @param skybox           Skybox implementation.
     */
    void addPermanentSkybox(ResourceLocation resourceLocation, Skybox skybox);

    /** Removes a non-permanent skybox. */
    boolean removeSkybox(ResourceLocation resourceLocation);

    /** Removes a permanent skybox. */
    boolean removePermanentSkybox(ResourceLocation resourceLocation);

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

    /** Gets a skybox by id from either the reloadable or permanent collection. */
    Optional<Skybox> getSkybox(ResourceLocation resourceLocation);

    /** Gets a read-only view of reloadable skyboxes. */
    Map<ResourceLocation, Skybox> getSkyboxes();

    /**
     * Gets a list of active skyboxes.
     *
     * @return Current list of active skyboxes.
     */
    List<Skybox> getActiveSkyboxes();
}
