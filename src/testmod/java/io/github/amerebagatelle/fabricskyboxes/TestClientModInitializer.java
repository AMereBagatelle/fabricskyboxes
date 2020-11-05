package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;

public class TestClientModInitializer implements ClientModInitializer {
    static final SkyboxType<TestSkybox> TYPE = SkyboxType.Builder.create(TestSkybox.class, "an-entirely-hardcoded-skybox").add(2, TestSkybox.CODEC).build();

    @Override
    public void onInitializeClient() {
        Registry.register(SkyboxType.REGISTRY, TYPE.createId("test"), TYPE);
        SkyboxManager.getInstance().addPermanentSkybox(TestSkybox.INSTANCE);
    }
}
