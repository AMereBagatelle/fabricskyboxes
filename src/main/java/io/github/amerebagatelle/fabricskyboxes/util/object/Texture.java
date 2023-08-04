package io.github.amerebagatelle.fabricskyboxes.util.object;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

/**
 * Encapsulates the location of a texture, the
 * minimum u coordinate, maximum u coordinate,
 * minimum v coordinate and maximum v coordinate.
 */
public class Texture extends UVRange implements Cloneable {
    public static final Codec<Texture> CODEC = Identifier.CODEC.xmap(Texture::new, Texture::getTextureId);
    private final Identifier textureId;

    public Texture(Identifier textureId, float minU, float minV, float maxU, float maxV) {
        super(minU, minV, maxU, maxV);
        this.textureId = textureId;
    }

    public Texture(Identifier textureId) {
        this(textureId, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    public Identifier getTextureId() {
        return this.textureId;
    }

    public Texture withUV(float minU, float minV, float maxU, float maxV) {
        return new Texture(this.getTextureId(), minU, minV, maxU, maxV);
    }
}
