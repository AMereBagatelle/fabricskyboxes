package io.github.amerebagatelle.fabricskyboxes.util.`object`

import net.minecraft.util.Identifier

data class Texture(
    val textureId: Identifier,
    val minU: Float = 0F,
    val minV: Float = 0F,
    val maxU: Float = 1F,
    val maxV: Float = 1F
) {
    fun withUV(minU: Float, minV: Float, maxU: Float, maxV: Float): Texture {
        return Texture(textureId, minU, minV, maxU, maxV)
    }
}
