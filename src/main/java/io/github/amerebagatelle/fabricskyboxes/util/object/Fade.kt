package io.github.amerebagatelle.fabricskyboxes.util.`object`

data class Fade(
    val startFadeIn: Int,
    val endFadeIn: Int,
    val startFadeOut: Int,
    val endFadeOut: Int,
    val alwaysOn: Boolean = false
)