package com.martorell.albert.basketcoach.usecases.fakes

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result
import com.martorell.albert.basketcoach.domain.repository.GameRepository

class FakeGameRepository : GameRepository {
    var gamesToReturn: Result<List<Game>, DomainError> = Result.Success(emptyList())
    var singleGameToReturn: Result<Game, DomainError>? = null
    var getPhase1GamesCallCount: Int = 0
    var lastRequestedRound: Int? = null

    override suspend fun getPhase1Games(): Result<List<Game>, DomainError> {
        getPhase1GamesCallCount++
        return gamesToReturn
    }

    override suspend fun getGameByRound(round: Int): Result<Game, DomainError> {
        lastRequestedRound = round
        singleGameToReturn?.let { return it }

        return when (val result = gamesToReturn) {
            is Result.Success -> {
                val found = result.data.find { it.round == round }
                if (found != null) {
                    Result.Success(found)
                } else {
                    Result.Error(DomainError.Local.NotFound)
                }
            }
            is Result.Error -> result
        }
    }
}
