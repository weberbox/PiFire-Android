package com.weberbox.pifire.core.di

import androidx.datastore.core.DataStore
import com.weberbox.pifire.settings.data.model.local.HeadersData
import com.weberbox.pifire.settings.data.util.HeadersManager
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
    fun provideHeadersManager(dataStore: DataStore<HeadersData>): HeadersManager {
        return HeadersManager(dataStore)
    }

}