package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.json.JsonClassDiscriminator;
import net.minecraft.util.Util;

@Serializable
@JsonClassDiscriminator(discriminator = "single-sprite-square-textured")
public class SingleSpriteSquareTexturedSkybox extends SquareTexturedSkybox {
	protected Texture texture;

	public SingleSpriteSquareTexturedSkybox(Properties properties, Conditions conditions, Decorations decorations, Blend blend, Texture texture) {
		super(properties, conditions, decorations, blend, Util.make(() -> new Textures(
				texture.withUV(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1),
				texture.withUV(2.0F / 3.0F, 0, 1, 1.0F / 2.0F),
				texture.withUV(2.0F / 3.0F, 1.0F / 2.0F, 1, 1),
				texture.withUV(0, 1.0F / 2.0F, 1.0F / 3.0F, 1),
				texture.withUV(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F),
				texture.withUV(0, 0, 1.0F / 3.0F, 1.0F / 2.0F)
		)));
		this.texture = texture;
	}

	public Texture getTexture() {
		return this.texture;
	}
}
