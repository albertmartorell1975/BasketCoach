package com.martorell.albert.basketcoach.data.parser

import com.martorell.albert.basketcoach.domain.model.Game
import java.io.InputStream

/**
 * Contract for parsing raw JSON payloads representing basketball fixtures into domain [Game] entities.
 *
 * Implementations throw technical exceptions (e.g. [kotlinx.serialization.SerializationException],
 * [java.io.IOException]) on failure. Translating them into typed domain errors is the responsibility
 * of the calling repository via `safeCall`.
 */
interface GameJsonParser {
    /**
     * Parses a raw JSON string into a list of domain [Game] entities.
     *
     * @param jsonContent String containing the JSON array of game fixtures.
     * @return The parsed domain [Game] models.
     * @throws kotlinx.serialization.SerializationException if deserialization fails.
     */
    fun parseGames(jsonContent: String): List<Game>

    /**
     * Parses an [InputStream] containing JSON content into a list of domain [Game] entities.
     *
     * The input stream is read fully and closed by the parser.
     *
     * @param inputStream Open stream providing JSON content.
     * @return The parsed domain [Game] models.
     * @throws java.io.IOException if reading fails.
     * @throws kotlinx.serialization.SerializationException if deserialization fails.
     */
    fun parseGames(inputStream: InputStream): List<Game>
}
