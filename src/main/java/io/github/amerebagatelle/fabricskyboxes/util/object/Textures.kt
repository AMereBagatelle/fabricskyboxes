package io.github.amerebagatelle.fabricskyboxes.util.`object`

data class Textures(
    val north: Texture,
    val south: Texture,
    val east: Texture,
    val west: Texture,
    val top: Texture,
    val bottom: Texture
) {
    private val textureList = listOf(north, south, east, west, top, bottom)

    fun getTexture(index: Int) = textureList[index]
}
