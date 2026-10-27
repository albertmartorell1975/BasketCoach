package com.martorell.albert.basketcoach.data.repository

import com.martorell.albert.basketcoach.data.dto.GameDto
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.FileNotFoundException

class GameRepositoryImplTest {

    private lateinit var repository: GameRepositoryImpl

    @Before
    fun setUp() {
        repository = GameRepositoryImpl(GameLocalDataSourceImpl())
    }

    @Test
    fun getPhase1Games_andGetGameByRound_returnSortedDomainGames() = runBlocking {
        val allGamesResult = repository.getPhase1Games()
        assertTrue(allGamesResult is Result.Success)
        val games = (allGamesResult as Result.Success).data

        assertEquals(14, games.size)
        assertEquals((1..14).toList(), games.map { it.round })

        val round3Result = repository.getGameByRound(3)
        assertTrue(round3Result is Result.Success)
        assertEquals(3, (round3Result as Result.Success).data.round)
    }

    @Test
    fun getGameByRound_missingRoundOrMissingDataset_mapsToNotFoundError() = runBlocking {
        val missingRound = repository.getGameByRound(99)
        assertTrue(missingRound is Result.Error)
        assertEquals(DomainError.Local.NotFound, (missingRound as Result.Error).error)

        val failingSource = object : GameLocalDataSource {
            override suspend fun getGames(): List<GameDto> = throw FileNotFoundException("missing")
        }
        val missingDataset = GameRepositoryImpl(failingSource).getPhase1Games()
        assertTrue(missingDataset is Result.Error)
        assertEquals(DomainError.Local.NotFound, (missingDataset as Result.Error).error)
    }
}
