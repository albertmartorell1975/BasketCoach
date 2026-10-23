package com.martorell.albert.basketcoach.data.parser

import com.martorell.albert.basketcoach.data.dto.GameDto
import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.domain.model.Game
import kotlinx.serialization.json.Json
import java.io.InputStream

/**
 * Implementation of [GameJsonParser] powered by `kotlinx.serialization`.
 *
 * Configured to ignore unknown keys and accept lenient JSON syntax. Technical exceptions are
 * propagated to the caller, which maps them to typed domain errors.
 *
 * @property json The [Json] serializer instance configured for lenient and forward-compatible decoding.
 */
class GameJsonParserImpl(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
) : GameJsonParser {

    /**
     * Parses a JSON string array of [GameDto] objects and maps each item to a domain [Game].
     *
     * @param jsonContent Raw JSON string to deserialize.
     * @return The mapped domain [Game] models.
     * @throws kotlinx.serialization.SerializationException on syntax or format errors.
     */
    override fun parseGames(jsonContent: String): List<Game> {
        val dtos = json.decodeFromString<List<GameDto>>(jsonContent)
        return dtos.map { it.toDomain() }
    }

    /**
     * Reads and parses an [InputStream] containing JSON data, ensuring stream closure via [use].
     *
     * @param inputStream The open stream supplying JSON content.
     * @return The mapped domain [Game] models.
     * @throws java.io.IOException on stream reading errors.
     * @throws kotlinx.serialization.SerializationException on parsing errors.
     */
    override fun parseGames(inputStream: InputStream): List<Game> {
        val content = inputStream.bufferedReader().use { it.readText() }
        return parseGames(content)
    }
}
