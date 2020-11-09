package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;

/**
 * Encapsulates the location of a texture, the
 * minimum u coordinate, maximum u coordinate,
 * minimum v coordinate and maximum v coordinate.
 */
public class Texture implements Cloneable {
	public static final Codec<Texture> CODEC = Identifier.CODEC.xmap(Texture::new, Texture::getTextureId);
	private final Identifier textureId;
	private float minU;
	private float minV;
	private float maxU;
	private float maxV;

	public Texture(Identifier textureId, float minU, float minV, float maxU, float maxV) {
		this.textureId = textureId;
		this.minU = minU;
		this.minV = minV;
		this.maxU = maxU;
		this.maxV = maxV;
	}

	public Texture(Identifier textureId) {
		this(textureId, 0.0F, 0.0F, 1.0F, 1.0F);
	}

	public Identifier getTextureId() {
		return this.textureId;
	}

	public float getMinU() {
		return this.minU;
	}

	public float getMaxU() {
		return this.maxU;
	}

	public float getMinV() {
		return this.minV;
	}

	public float getMaxV() {
		return this.maxV;
	}

	public Texture setMinU(float minU) {
		this.minU = minU;
		return this;
	}

	public Texture setMaxU(float maxU) {
		this.maxU = maxU;
		return this;
	}

	public Texture setMinV(float minV) {
		this.minV = minV;
		return this;
	}

	public Texture setMaxV(float maxV) {
		this.maxV = maxV;
		return this;
	}

	@Override
	public Texture clone() {
		try {
			return (Texture) super.clone();
		} catch (CloneNotSupportedException e) {
			// cant happen
			throw new AssertionError();
		}
	}
}
