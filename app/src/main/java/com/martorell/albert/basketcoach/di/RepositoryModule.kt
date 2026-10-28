package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.data.repository.GameRepositoryImpl
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.domain.repository.GameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGameRepository(
        localDataSource: GameLocalDataSource
    ): GameRepository {
        return GameRepositoryImpl(localDataSource)
    }
}
