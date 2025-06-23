package com.weberbox.pifire.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.dashboard.data.serializer.DashSerializer
import com.weberbox.pifire.dashboard.presentation.model.DashData
import com.weberbox.pifire.events.data.serializer.EventsSerializer
import com.weberbox.pifire.events.presentation.model.EventsData
import com.weberbox.pifire.info.data.serializer.InfoSerializer
import com.weberbox.pifire.info.presentation.model.InfoData
import com.weberbox.pifire.pellets.data.serializer.PelletsSerializer
import com.weberbox.pifire.pellets.presentation.model.PelletsData
import com.weberbox.pifire.recipes.data.serializer.RecipesSerializer
import com.weberbox.pifire.recipes.presentation.model.RecipesData
import com.weberbox.pifire.settings.data.model.local.HeadersData
import com.weberbox.pifire.settings.data.serializer.HeadersSerializer
import com.weberbox.pifire.settings.data.serializer.SettingsSerializer
import com.weberbox.pifire.settings.presentation.model.SettingsData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideEventsDataStore(@ApplicationContext appContext: Context): DataStore<EventsData> {
        return DataStoreFactory.create(
            serializer = EventsSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.EVENTS_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { EventsData() }
        )
    }

    @Singleton
    @Provides
    fun providePelletsDataStore(@ApplicationContext appContext: Context): DataStore<PelletsData> {
        return DataStoreFactory.create(
            serializer = PelletsSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.PELLETS_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { PelletsData() }
        )
    }

    @Singleton
    @Provides
    fun provideRecipesDataStore(@ApplicationContext appContext: Context): DataStore<RecipesData> {
        return DataStoreFactory.create(
            serializer = RecipesSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.RECIPES_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { RecipesData() }
        )
    }

    @Singleton
    @Provides
    fun provideInfoDataStore(@ApplicationContext appContext: Context): DataStore<InfoData> {
        return DataStoreFactory.create(
            serializer = InfoSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.INFO_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { InfoData() }
        )
    }

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext appContext: Context): DataStore<SettingsData> {
        return DataStoreFactory.create(
            serializer = SettingsSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.SETTINGS_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { SettingsData() }
        )
    }

    @Singleton
    @Provides
    fun provideDashDataStore(@ApplicationContext appContext: Context): DataStore<DashData> {
        return DataStoreFactory.create(
            serializer = DashSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.DASH_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { DashData() }
        )
    }

    @Singleton
    @Provides
    fun provideHeadersDataStore(@ApplicationContext appContext: Context): DataStore<HeadersData> {
        return DataStoreFactory.create(
            serializer = HeadersSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.HEADERS_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { HeadersData() }
        )
    }
}