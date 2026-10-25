package com.weberbox.pifire.feature.events.data.di

import com.weberbox.pifire.feature.events.data.api.EventsApi
import com.weberbox.pifire.feature.events.data.api.EventsApiImpl
import com.weberbox.pifire.feature.events.data.repo.EventsRepo
import com.weberbox.pifire.feature.events.data.repo.EventsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}
