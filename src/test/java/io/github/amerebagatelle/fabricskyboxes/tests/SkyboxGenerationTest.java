package io.github.amerebagatelle.fabricskyboxes.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.AnimatedSquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class SkyboxGenerationTest {
	@Test
	public void init() throws IOException {
		DefaultProperties props = new DefaultProperties.Builder()
				.changesFog()
				.rotates()
				.rotation(
						new Rotation(
								new Vector3f(0.1F, 0.0F, 0.1F),
								new Vector3f(0.0F, 0.0F, 0.0F),
								1
						)
				)
				.maxAlpha(0.99F)
				.transitionSpeed(0.7F)
				.fade(new Fade(1000, 2000, 11000, 12000, false))
				.build();
		Conditions conditions = new Conditions.Builder()
				.biomes(new Identifier("minecraft:plains"))
				.worlds(new Identifier("minecraft:overworld"))
				.weather(Weather.CLEAR)
				.heights(new HeightEntry(40, 120))
				.build();
		Decorations decorations = new Decorations(
				PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
				SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
				true,
				true,
				false,
				Rotation.DEFAULT
		);

		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setLenient().create();
		this.test(gson, MonoColorSkybox.CODEC, new MonoColorSkybox(props, conditions, decorations, new RGBA(0.5F, 0.8F, 0.6F, 0.99F)));
		this.test(gson, SquareTexturedSkybox.CODEC, new SquareTexturedSkybox(props, conditions, decorations, Blend.DEFAULT, new Textures(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, new Identifier("missingno"))));
		this.test(gson, AnimatedSquareTexturedSkybox.CODEC, new AnimatedSquareTexturedSkybox(props, conditions, decorations, Blend.DEFAULT, Arrays.asList(
				new Textures(
						PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
						PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
						PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
						PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT,
						PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE,
						new Identifier("missingno")
				),
				new Textures(
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						new Identifier("missingno"),
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX
				),
				new Textures(
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
						PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX,
						SpriteAtlasTexture.PARTICLE_ATLAS_TEX
				)
		), 0.2F));
	}

	private <T extends AbstractSkybox> void test(Gson gson, Codec<T> codec, T input) throws IOException {
		Path configDir = Paths.get(System.getProperty("user.dir")).resolve("run").resolve("config");
		if (!Files.exists(configDir)) {
			Files.createDirectories(configDir);
		}
		Path path = configDir.resolve(input.getClass().getSimpleName() + ".json");
		if (!Files.exists(path)) {
			Files.createFile(path);
		}
		String jsonString = gson.toJson(codec.encodeStart(JsonOps.INSTANCE, input).getOrThrow(false, System.err::println));
		Files.write(path, (jsonString + "\n").getBytes(StandardCharsets.UTF_8));
	}
}
