package io.github.amerebagatelle.fabricskyboxes.util.`object`

import net.minecraft.util.Identifier
import net.minecraft.world.biome.BiomeEffects
import java.util.Locale.LanguageRange

data class Conditions(
    val biomes: List<Identifier> = listOf(),
    val worlds: List<Identifier> = listOf(),
    val effects: List<Identifier> = listOf(),
    val weathers: List<Weather> = listOf(),
    val xRanges: List<MinMaxEntry> = listOf(),
    val yRanges: List<MinMaxEntry> = listOf(),
    val zRanges: List<MinMaxEntry> = listOf(),
    val loop: Loop = Loop()
)