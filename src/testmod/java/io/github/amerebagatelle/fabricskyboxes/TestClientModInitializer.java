package io.github.amerebagatelle.fabricskyboxes;

import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.DefaultProperties;
import io.github.amerebagatelle.fabricskyboxes.util.object.Fade;
import io.github.amerebagatelle.fabricskyboxes.util.object.HeightEntry;
import io.github.amerebagatelle.fabricskyboxes.util.object.Rotation;
import io.github.amerebagatelle.fabricskyboxes.util.object.Weather;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;

public class TestClientModInitializer implements ClientModInitializer {
    static final SkyboxType<TestSkybox> TYPE;
	static final DefaultProperties PROPS;
	static final Conditions CONDITIONS;
	static final Decorations DECORATIONS;

	@Override
    public void onInitializeClient() {
        Registry.register(SkyboxType.REGISTRY, TYPE.createId("test"), TYPE);
        SkyboxManager.getInstance().addPermanentSkybox(TestSkybox.INSTANCE);
    }

	static {
		TYPE = SkyboxType.Builder.create(TestSkybox.class, "an-entirely-hardcoded-skybox").add(2, TestSkybox.CODEC).build();
		DECORATIONS = new Decorations(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, SpriteAtlasTexture.PARTICLE_ATLAS_TEX, true, true, false);
		CONDITIONS = new Conditions.Builder()
				.biomes(new Identifier("minecraft:plains"))
				.worlds(new Identifier("minecraft:overworld"))
				.weather(Weather.CLEAR)
				.heights(new HeightEntry(40, 120))
				.build();
		PROPS = new DefaultProperties.Builder()
				.changesFog()
				.rotates()
				.rotation(
						new Rotation(
								new Vector3f(0.1F, 0.0F, 0.1F),
								new Vector3f(0.0F, 0.0F, 0.0F)
						)
				)
				.maxAlpha(0.99F)
				.transitionSpeed(0.7F)
				.fade(new Fade(1000, 2000, 11000, 12000, false))
				.build();
	}
}
