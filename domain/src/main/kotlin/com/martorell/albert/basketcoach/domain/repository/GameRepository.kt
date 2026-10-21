package com.martorell.albert.basketcoach.domain.repository

import com.martorell.albert.basketcoach.domain.model.DomainError
import com.martorell.albert.basketcoach.domain.model.Game
import com.martorell.albert.basketcoach.domain.model.Result

interface GameRepository {
    suspend fun getPhase1Games(): Result<List<Game>, DomainError>
    suspend fun getGameByRound(round: Int): Result<Game, DomainError>
}
