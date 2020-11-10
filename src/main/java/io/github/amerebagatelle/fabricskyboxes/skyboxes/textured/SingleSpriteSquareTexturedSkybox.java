package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.Conditions;
import io.github.amerebagatelle.fabricskyboxes.util.object.Decorations;
import io.github.amerebagatelle.fabricskyboxes.util.object.DefaultProperties;
import io.github.amerebagatelle.fabricskyboxes.util.object.Texture;
import io.github.amerebagatelle.fabricskyboxes.util.object.Textures;

import net.minecraft.util.Util;

public class SingleSpriteSquareTexturedSkybox extends SquareTexturedSkybox {
	public static Codec<SingleSpriteSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DefaultProperties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getDefaultProperties),
			Conditions.CODEC.optionalFieldOf("conditions", Conditions.NO_CONDITIONS).forGetter(AbstractSkybox::getConditions),
			Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
			Codec.BOOL.fieldOf("blend").forGetter(TexturedSkybox::isBlend),
			Texture.CODEC.fieldOf("texture").forGetter(SingleSpriteSquareTexturedSkybox::getTexture)
	).apply(instance, SingleSpriteSquareTexturedSkybox::new));
	private final Texture texture;

	public SingleSpriteSquareTexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, boolean blend, Texture texture) {
		super(properties, conditions, decorations, blend, Util.make(() -> new Textures(
				texture.withUV(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1),
				texture.withUV(2.0F / 3.0F, 0, 1, 1.0F / 2.0F),
				texture.withUV(2.0F / 3.0F, 1.0F / 2.0F, 1, 1),
				texture.withUV(0, 1.0F / 2.0F, 1.0F / 2.0F, 1),
				texture.withUV(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F),
				texture.withUV(0, 0, 1.0F / 3.0F, 1.0F / 2.0F)
		)));
		this.texture = texture;
	}

	@Override
	public SkyboxType<? extends AbstractSkybox> getType() {
		return SkyboxType.SINGLE_SPRITE_SQUARE_TEXTURED_SKYBOX;
	}

	public Texture getTexture() {
		return this.texture;
	}
}
