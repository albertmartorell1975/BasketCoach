package com.martorell.albert.basketcoach.data.parser

import com.martorell.albert.basketcoach.data.dto.GameDto
import com.martorell.albert.basketcoach.data.mapper.toDomain
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.serialization.json.Json
import java.io.InputStream

/**
 * Implementation of [GameJsonParser] powered by `kotlinx.serialization`.
 *
 * Configured to ignore unknown keys and accept lenient JSON syntax. Safely encapsulates all
 * I/O and deserialization exceptions, converting them into typed [DomainError.Local.DatabaseError] instances.
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
     * @return [Result.Success] containing mapped domain [Game] models, or
     *   [Result.Error] with [DomainError.Local.DatabaseError] on syntax or format errors.
     */
    override fun parseGames(jsonContent: String): Result<List<Game>, DomainError> {
        return try {
            val dtos = json.decodeFromString<List<GameDto>>(jsonContent)
            Result.Success(dtos.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(DomainError.Local.DatabaseError)
        }
    }

    /**
     * Reads and parses an [InputStream] containing JSON data, ensuring stream closure via [use].
     *
     * @param inputStream The open stream supplying JSON content.
     * @return [Result.Success] containing mapped domain [Game] models, or
     *   [Result.Error] with [DomainError.Local.DatabaseError] on stream reading or parsing errors.
     */
    override fun parseGames(inputStream: InputStream): Result<List<Game>, DomainError> {
        return try {
            val content = inputStream.bufferedReader().use { it.readText() }
            parseGames(content)
        } catch (e: Exception) {
            Result.Error(DomainError.Local.DatabaseError)
        }
    }
}
