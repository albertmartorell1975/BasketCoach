package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.data.repository.AiAnalysisRepositoryImpl
import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module providing repository bindings installed in [SingletonComponent].
 *
 * Bridges the data layer implementation [GameRepositoryImpl] in `:data` with the domain
 * contract [GameRepository] in `:domain` following Clean Architecture dependency inversion.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds [GameRepositoryImpl] to the domain interface [GameRepository] as a singleton.
     *
     * @param impl The concrete repository coordinating local data sources and typed domain error mapping.
     * @return The [GameRepository] domain contract.
     */
    @Binds
    @Singleton
    abstract fun bindGameRepository(
        impl: GameRepositoryImpl
    ): GameRepository

    /**
     * Binds the AI analysis repository implementation [AiAnalysisRepositoryImpl]
     * to the domain interface [AiAnalysisRepository] as an application-wide singleton.
     *
     * @param impl The concrete repository coordinating remote AI generation and in-memory caching.
     * @return The [AiAnalysisRepository] domain contract.
     */
    @Binds
    @Singleton
    abstract fun bindAiAnalysisRepository(
        impl: AiAnalysisRepositoryImpl
    ): AiAnalysisRepository
}
