package io.github.amerebagatelle.fabricskyboxes.util.`object`

import net.minecraft.util.Identifier

data class Decorations(
    val sunTexture: Identifier = Identifier("textures/environment/sun.png"),
    val moonTexture: Identifier = Identifier("textures/environment/moon_phases.png"),
    val showSun: Boolean = true,
    val showMoon: Boolean = true,
    val showStars: Boolean = true,
    val rotation: Rotation = Rotation()
)