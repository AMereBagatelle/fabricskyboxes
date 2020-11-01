package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;

public class Init implements ClientModInitializer {
    static final SkyboxType<AnEntirelyHardcodedSkybox> TYPE = SkyboxType.Builder.create(AnEntirelyHardcodedSkybox.class, "an-entirely-hardcoded-skybox").add(2, AnEntirelyHardcodedSkybox.CODEC).build();

    @Override
    public void onInitializeClient() {
        Registry.register(SkyboxType.REGISTRY, TYPE.createId("test"), TYPE);
        SkyboxManager.getInstance().addPermanentSkybox(AnEntirelyHardcodedSkybox.INSTANCE);
    }
}
