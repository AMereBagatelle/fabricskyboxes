package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

public class TestClientModInitializer implements ClientModInitializer {
    static final SkyboxType<TestSkybox> TYPE;
    static final Properties PROPS;
    static final Conditions CONDITIONS;
    static final Decorations DECORATIONS;

    static {
        TYPE = SkyboxType.Builder.create(
                TestSkybox.class,
                "an-entirely-hardcoded-skybox"
        ).add(2, TestSkybox.CODEC).build();
        DECORATIONS = new Decorations(
                PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
                SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE,
                true,
                true,
                false,
                Rotation.DEFAULT,
                Blend.DECORATIONS
        );
        CONDITIONS = new Conditions.Builder()
                .biomes(new Identifier("minecraft:plains"))
                .worlds(new Identifier("minecraft:overworld"))
                .weather(Weather.CLEAR)
                .yRanges(new MinMaxEntry(40, 120))
                .build();
        PROPS = new Properties.Builder()
                .changesFog()
                .rotation(
                        new Rotation(
                                true,
                                new Vec3f(0.1F, 0.0F, 0.1F),
                                new Vec3f(0.0F, 0.0F, 0.0F),
                                0,
                                1,
                                0
                        )
                )
                .maxAlpha(0.99F)
                .transitionInDuration(15)
                .transitionOutDuration(15)
                .fade(new Fade(1000, 2000, 11000, 12000, false))
                .build();
    }

    @Override
    public void onInitializeClient() {
        Registry.register(SkyboxType.REGISTRY, TYPE.createId("test"), TYPE);
        SkyboxManager.getInstance().addPermanentSkybox(new Identifier("fabricskyboxes_testmod", "test_skybox"), TestSkybox.INSTANCE);
    }
}
