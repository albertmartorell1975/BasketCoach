package com.martorell.albert.basketcoach.data.source.local

import com.martorell.albert.basketcoach.data.dto.GameDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.FileNotFoundException
import java.io.InputStream
import javax.inject.Inject

/**
 * Implementation of [GameLocalDataSource] reading the bundled classpath resource (`dataset.json`).
 *
 * The dataset is static and cheap to parse, so the result is cached in a `@Volatile` field without
 * locking: concurrent first calls may parse twice, which is harmless and yields identical data.
 *
 * @param json The [Json] instance configured for lenient and forward-compatible decoding.
 * @param jsonStreamProvider Supplies an [InputStream] to the dataset, or null if it cannot be found.
 */
class GameLocalDataSourceImpl(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    },
    private val jsonStreamProvider: () -> InputStream? = {
        GameLocalDataSourceImpl::class.java.classLoader?.getResourceAsStream(DATASET_FILE_NAME)
    }
) : GameLocalDataSource {

    @Inject
    constructor() : this(
        json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        },
        jsonStreamProvider = {
            GameLocalDataSourceImpl::class.java.classLoader?.getResourceAsStream(DATASET_FILE_NAME)
        }
    )

    @Volatile
    private var cachedGames: List<GameDto>? = null

    override suspend fun getGames(): List<GameDto> {
        cachedGames?.let { return it }

        return withContext(Dispatchers.IO) {
            val inputStream = jsonStreamProvider()
                ?: throw FileNotFoundException("$DATASET_FILE_NAME not found on classpath")
            val content = inputStream.bufferedReader().use { it.readText() }
            json.decodeFromString<List<GameDto>>(content).also { cachedGames = it }
        }
    }

    private companion object {
        const val DATASET_FILE_NAME: String = "dataset.json"
    }
}
