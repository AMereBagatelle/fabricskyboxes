package io.github.amerebagatelle.fabricskyboxes.util

import io.github.amerebagatelle.fabricskyboxes.skyboxes.AbstractSkybox
import io.github.amerebagatelle.fabricskyboxes.skyboxes.MonoColorSkybox
import io.github.amerebagatelle.fabricskyboxes.skyboxes.SkyboxType
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.AnimatedSquareTexturedSkybox
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SingleSpriteAnimatedSquareTexturedSkybox
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SingleSpriteSquareTexturedSkybox
import io.github.amerebagatelle.fabricskyboxes.skyboxes.textured.SquareTexturedSkybox
import io.github.amerebagatelle.fabricskyboxes.util.`object`.Conditions
import io.github.amerebagatelle.fabricskyboxes.util.`object`.Decorations
import io.github.amerebagatelle.fabricskyboxes.util.`object`.RGBA
import io.github.amerebagatelle.fabricskyboxes.util.`object`.internal.Metadata
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.minecraft.util.Identifier
import java.util.Properties

private val module = SerializersModule {
    polymorphic(AbstractSkybox::class) {
        subclass(MonoColorSkybox::class)
        subclass(SquareTexturedSkybox::class)
        subclass(AnimatedSquareTexturedSkybox::class)
        subclass(SingleSpriteSquareTexturedSkybox::class)
        subclass(SingleSpriteAnimatedSquareTexturedSkybox::class)
    }
}

private val serializer = Json {
    ignoreUnknownKeys = true
    serializersModule = module
}

fun deserializeJson(json: String): AbstractSkybox {
    val metadata = serializer.decodeFromString<Metadata>(json)
    SkyboxType.getType(metadata.type)

    return serializer.decodeFromString(json)
}