package com.martorell.albert.basketcoach.di

import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSource
import com.martorell.albert.basketcoach.data.source.local.GameLocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    fun provideGameLocalDataSource(): GameLocalDataSource {
        return GameLocalDataSourceImpl()
    }
}
