package io.github.amerebagatelle.fabricskyboxes.api;

import com.google.gson.JsonObject;
import io.github.amerebagatelle.fabricskyboxes.SkyboxManager;
import io.github.amerebagatelle.fabricskyboxes.api.skyboxes.Skybox;
import net.minecraft.util.Identifier;

public interface FabricSkyBoxesApi {

    /**
     * @since API v0.0
     */
    static FabricSkyBoxesApi getInstance() {
        return SkyboxManager.getInstance();
    }

    boolean isEnabled();
    void setEnabled(boolean enabled);
    void addSkyBox(Identifier identifier, Skybox skyBox);
    void addSkyBox(Identifier identifier, JsonObject jsonObject);
    void addPermanentSkyBox(Identifier identifier, Skybox skyBox);
    void clearSkyboxes();
}
