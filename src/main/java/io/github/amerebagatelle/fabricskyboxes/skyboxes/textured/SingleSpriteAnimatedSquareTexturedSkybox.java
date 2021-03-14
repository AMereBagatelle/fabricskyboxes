package io.github.amerebagatelle.fabricskyboxes.skyboxes.textured;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox;
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType;
import io.github.amerebagatelle.fabricskyboxes.util.object.*;

import java.util.List;
import java.util.stream.Collectors;

public class SingleSpriteAnimatedSquareTexturedSkybox extends AnimatedSquareTexturedSkybox {
	public static Codec<SingleSpriteAnimatedSquareTexturedSkybox> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DefaultProperties.CODEC.fieldOf("properties").forGetter(AbstractSkybox::getDefaultProperties),
			Conditions.CODEC.optionalFieldOf("conditions", Conditions.NO_CONDITIONS).forGetter(AbstractSkybox::getConditions),
			Decorations.CODEC.optionalFieldOf("decorations", Decorations.DEFAULT).forGetter(AbstractSkybox::getDecorations),
			Blend.CODEC.optionalFieldOf("blend", Blend.DEFAULT).forGetter(TexturedSkybox::getBlend),
			Texture.CODEC.listOf().fieldOf("animationTextures").forGetter(SingleSpriteAnimatedSquareTexturedSkybox::getAnimationTextureList),
			Codec.FLOAT.fieldOf("fps").forGetter(SingleSpriteAnimatedSquareTexturedSkybox::getFps)
	).apply(instance, SingleSpriteAnimatedSquareTexturedSkybox::new));

	public SingleSpriteAnimatedSquareTexturedSkybox(DefaultProperties properties, Conditions conditions, Decorations decorations, Blend blend, List<Texture> animationTextures, float fps) {
		super(
				properties,
				conditions,
				decorations,
				blend,
				animationTextures.stream().map(texture -> new Textures(
						texture.withUV(1.0F / 3.0F, 1.0F / 2.0F, 2.0F / 3.0F, 1),
						texture.withUV(2.0F / 3.0F, 0, 1, 1.0F / 2.0F),
						texture.withUV(2.0F / 3.0F, 1.0F / 2.0F, 1, 1),
						texture.withUV(0, 1.0F / 2.0F, 1.0F / 3.0F, 1),
						texture.withUV(1.0F / 3.0F, 0, 2.0F / 3.0F, 1.0F / 2.0F),
						texture.withUV(0, 0, 1.0F / 3.0F, 1.0F / 2.0F)
				)).collect(Collectors.toList()),
				fps
		);
	}

	@Override
	public SkyboxType<? extends AbstractSkybox> getType() {
		return SkyboxType.SINGLE_SPRITE_ANIMATED_SQUARE_TEXTURED_SKYBOX;
	}

	public List<Texture> getAnimationTextureList() {
		// Intentionally not stored in a list field because this method is rarely called and fields waste memory
		return this.getAnimationTextures().stream().map(/* The texture should be the same for all directions */Textures::getNorth).collect(Collectors.toList());
	}
}
