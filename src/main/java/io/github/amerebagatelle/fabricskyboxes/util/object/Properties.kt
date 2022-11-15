package io.github.amerebagatelle.fabricskyboxes.util.`object`

data class Properties(
    val priority: Int = 0,
    val fade: Fade,
    val maxAlpha: Float = 1.0F,
    val transitionSpeed: Float = 1.0F,
    val changeFog: Boolean = false,
    val fogColors: RGBA = RGBA(0F, 0F, 0F),
    val sunSkyTint: Boolean = false,
    val shouldRotate: Boolean = false,
    val rotation: Rotation = Rotation()
)