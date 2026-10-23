package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.data.dto.GameDto

/**
 * Local data source contract providing the bundled Phase 1 fixtures as raw [GameDto] objects.
 *
 * Implementations throw technical exceptions on failure (for example [java.io.FileNotFoundException]
 * when the dataset is missing or [kotlinx.serialization.SerializationException] when it is malformed).
 * Translating them into domain errors is the responsibility of the calling repository via `safeCall`.
 */
interface GameLocalDataSource {
    /**
     * Retrieves all Phase 1 game fixtures as DTOs.
     *
     * @return The deserialized [GameDto] list, cached after the first successful read.
     * @throws java.io.FileNotFoundException if the bundled dataset cannot be opened.
     * @throws kotlinx.serialization.SerializationException if the dataset cannot be deserialized.
     */
    suspend fun getGames(): List<GameDto>
}
