package io.github.amerebagatelle.fabricskyboxes.util.`object`

import net.minecraft.util.math.Vec3f

data class Rotation(
    val static: Vec3f = Vec3f(0F, 0F, 0F),
    val axis: Vec3f = Vec3f(0F, 0F, 0F),
    val rotationSpeed: Float = 1F
)
