package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
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
}
