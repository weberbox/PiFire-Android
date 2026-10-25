package com.weberbox.pifire.feature.events.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.model.events.EventsData
import com.weberbox.pifire.feature.events.data.api.EventsApi
import com.weberbox.pifire.feature.events.data.api.EventsApiImpl
import com.weberbox.pifire.feature.events.data.repo.EventsRepo
import com.weberbox.pifire.feature.events.data.repo.EventsRepoImpl
import com.weberbox.pifire.feature.events.data.serializer.EventsSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class EventsDataModule {

    @Binds
    @Singleton
    abstract fun bindEventsApi(
        eventsApiImpl: EventsApiImpl,
    ): EventsApi

    @Binds
    @Singleton
    abstract fun bindEventsRepo(
        eventsRepoImpl: EventsRepoImpl,
    ): EventsRepo

    companion object {
        @Singleton
        @Provides
        fun provideEventsDataStore(@ApplicationContext appContext: Context): DataStore<EventsData> {
            return DataStoreFactory.create(
                serializer = EventsSerializer,
                produceFile = { appContext.dataStoreFile(AppConfig.EVENTS_DATA_NAME) },
                corruptionHandler = ReplaceFileCorruptionHandler { EventsData() }
            )
        }
    }
}
