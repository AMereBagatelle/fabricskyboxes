package me.flashyreese.mods.nuit.skybox;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Optional contract for skyboxes that want Nuit to preload and release their textures.
 */
public interface TextureRegistrar {
    /**
     * @return texture identifiers required by this skybox.
     */
    List<ResourceLocation> getTexturesToRegister();
}
