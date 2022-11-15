package io.github.amerebagatelle.fabricskyboxes.util.`object`.internal

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val schemaVersion: Int,
    val type: String
)