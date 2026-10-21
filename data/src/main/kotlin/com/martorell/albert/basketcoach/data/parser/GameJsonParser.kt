package com.martorell.albert.basketcoach.data.parser

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import java.io.InputStream

/**
 * Contract for parsing raw JSON payloads representing basketball fixtures into domain [Game] entities.
 *
 * Implementations must encapsulate all JSON serialization exceptions and translate failures into
 * typed [DomainError] results without leaking parsing framework specifics or throwing unhandled exceptions.
 */
interface GameJsonParser {
    /**
     * Parses a raw JSON string into a list of domain [Game] entities.
     *
     * @param jsonContent String containing the JSON array of game fixtures.
     * @return [Result.Success] containing the parsed domain [Game] models, or
     *   [Result.Error] with [DomainError.Local.DatabaseError] if deserialization fails.
     */
    fun parseGames(jsonContent: String): Result<List<Game>, DomainError>

    /**
     * Parses an [InputStream] containing JSON content into a list of domain [Game] entities.
     *
     * The input stream must be safely read and closed by the parser.
     *
     * @param inputStream Open stream providing JSON content.
     * @return [Result.Success] containing the parsed domain [Game] models, or
     *   [Result.Error] with [DomainError.Local.DatabaseError] if reading or deserialization fails.
     */
    fun parseGames(inputStream: InputStream): Result<List<Game>, DomainError>
}
