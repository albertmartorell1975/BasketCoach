package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.parser.GameJsonParserImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.InputStream

class GameRepositoryImplTest {

    private lateinit var repository: GameRepositoryImpl

    @Before
    fun setUp() {
        repository = GameRepositoryImpl()
    }

    @Test
    fun getPhase1Games_returnsAll14Games() = runBlocking {
        val result = repository.getPhase1Games()

        assertTrue("Expected Result.Success", result is Result.Success)
        val games = (result as Result.Success).data
        assertEquals(14, games.size)
        assertEquals(1, games.first().round)
        assertEquals(14, games.last().round)
    }

    @Test
    fun getGameByRound_validRounds_returnsCorrectGame() = runBlocking {
        for (round in 1..14) {
            val result = repository.getGameByRound(round)
            assertTrue("Expected success for round $round", result is Result.Success)
            val game = (result as Result.Success).data
            assertEquals(round, game.round)
        }
    }

    @Test
    fun getGameByRound_missingRound_returnsNotFound() = runBlocking {
        val result = repository.getGameByRound(99)

        assertTrue("Expected Result.Error", result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue("Expected NotFound error", error is DomainError.Local.NotFound)
    }

    @Test
    fun getPhase1Games_missingStream_returnsNotFound() = runBlocking {
        val emptyRepo = GameRepositoryImpl(
            parser = GameJsonParserImpl(),
            jsonStreamProvider = { null }
        )
        val result = emptyRepo.getPhase1Games()

        assertTrue("Expected Result.Error", result is Result.Error)
        val error = (result as Result.Error).error
        assertTrue("Expected NotFound error", error is DomainError.Local.NotFound)
    }

    @Test
    fun getPhase1Games_cachesResultInMemory() = runBlocking {
        var invocations = 0
        val mockStreamProvider: () -> InputStream? = {
            invocations++
            javaClass.classLoader?.getResourceAsStream("dataset.json")
        }
        val cachingRepo = GameRepositoryImpl(
            parser = GameJsonParserImpl(),
            jsonStreamProvider = mockStreamProvider
        )

        val firstCall = cachingRepo.getPhase1Games()
        val secondCall = cachingRepo.getPhase1Games()

        assertTrue(firstCall is Result.Success)
        assertTrue(secondCall is Result.Success)
        assertEquals(1, invocations)
    }
}
