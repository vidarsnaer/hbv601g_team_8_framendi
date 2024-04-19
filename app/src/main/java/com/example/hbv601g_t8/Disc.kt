package com.example.hbv601g_t8

import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

@Serializable
data class Disc(
    val discid: Int,
    val condition: String,
    val description: String,
    val name: String,
    val price: Int,
    val type: String,
    @Serializable(with = UUIDSerializer::class)
    val user_id: UUID,
    val colour: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class NewDiscCreation(
    val price: Int,
    val condition: String,
    val description: String,
    val name: String,
    val type: String,
    val colour: String,
    @Serializable(with = UUIDSerializer::class)
    val user_id: UUID,
    val latitude: Double,
    val longitude: Double
)
