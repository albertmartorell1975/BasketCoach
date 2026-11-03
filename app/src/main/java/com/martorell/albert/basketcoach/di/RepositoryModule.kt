package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.data.repository.AiAnalysisRepositoryImpl
import com.martorell.albert.basketcoach.data.repository.ChatRepositoryImpl
import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.domain.repository.AiAnalysisRepository
import com.martorell.albert.basketcoach.domain.repository.ChatRepository
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module providing repository bindings installed in [SingletonComponent].
 *
 * Bridges the data layer implementations in `:data` with the domain
 * contracts in `:domain` following Clean Architecture dependency inversion.
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

    /**
     * Binds the chat repository implementation [ChatRepositoryImpl]
     * to the domain interface [ChatRepository] as an application-wide singleton.
     *
     * @param impl The concrete repository coordinating remote AI chat and in-memory thread storage.
     * @return The [ChatRepository] domain contract.
     */
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}
