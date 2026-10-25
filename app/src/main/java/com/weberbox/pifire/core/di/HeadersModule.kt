package com.weberbox.pifire.core.di

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.network.headers.HeadersData
import com.weberbox.pifire.core.network.headers.HeadersManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HeadersModule {

    @Provides
    @Singleton
    fun provideHeadersManager(dataStore: DataStore<HeadersData>): HeadersManager = HeadersManager(dataStore)
}
