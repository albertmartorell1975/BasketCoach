package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.AiAnalysisLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSource
import com.martorell.albert.basketcoach.data.source.remote.AiRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module installing data source bindings into [SingletonComponent].
 *
 * Utilizes abstract [@Binds] methods for zero-cost static delegate generation rather than
 * runtime factory allocations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /**
     * Binds the local game data source implementation [GameLocalDataSourceImpl]
     * to its contract [GameLocalDataSource] as an application-wide singleton.
     *
     * @param impl The concrete implementation reading and caching the static Phase 1 dataset.
     * @return The [GameLocalDataSource] singleton contract.
     */
    @Binds
    @Singleton
    abstract fun bindGameLocalDataSource(
        impl: GameLocalDataSourceImpl
    ): GameLocalDataSource

    /**
     * Binds the in-memory AI analysis local data source implementation [AiAnalysisLocalDataSourceImpl]
     * to its contract [AiAnalysisLocalDataSource] as an application-wide singleton.
     *
     * @param impl The thread-safe in-memory cache implementation.
     * @return The [AiAnalysisLocalDataSource] singleton contract.
     */
    @Binds
    @Singleton
    abstract fun bindAiAnalysisLocalDataSource(
        impl: AiAnalysisLocalDataSourceImpl
    ): AiAnalysisLocalDataSource

    /**
     * Binds the remote AI client data source implementation [AiRemoteDataSourceImpl]
     * to its contract [AiRemoteDataSource] as an application-wide singleton.
     *
     * @param impl The concrete OpenAI/Gemini REST client implementation.
     * @return The [AiRemoteDataSource] singleton contract.
     */
    @Binds
    @Singleton
    abstract fun bindAiRemoteDataSource(
        impl: AiRemoteDataSourceImpl
    ): AiRemoteDataSource
}
