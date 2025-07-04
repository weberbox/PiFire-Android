package com.weberbox.pifire.core.di

import com.weberbox.pifire.dashboard.data.api.DashApi
import com.weberbox.pifire.dashboard.data.api.DashApiImpl
import com.weberbox.pifire.events.data.api.EventsApi
import com.weberbox.pifire.events.data.api.EventsApiImpl
import com.weberbox.pifire.info.data.api.InfoApi
import com.weberbox.pifire.info.data.api.InfoApiImpl
import com.weberbox.pifire.pellets.data.api.PelletsApi
import com.weberbox.pifire.pellets.data.api.PelletsApiImpl
import com.weberbox.pifire.recipes.data.api.RecipesApi
import com.weberbox.pifire.recipes.data.api.RecipesApiImpl
import com.weberbox.pifire.settings.data.api.SettingsApi
import com.weberbox.pifire.settings.data.api.SettingsApiImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class ApiModule {

    @Binds
    @Singleton
    abstract fun bindDashApi(
        dashApiImpl: DashApiImpl
    ): DashApi

    @Binds
    @Singleton
    abstract fun bindPelletsApi(
        pelletsApiImpl: PelletsApiImpl
    ): PelletsApi

    @Binds
    @Singleton
    abstract fun bindEventsApi(
        eventsApiImpl: EventsApiImpl
    ): EventsApi

    @Binds
    @Singleton
    abstract fun bindInfoApi(
        infoApiImpl: InfoApiImpl
    ): InfoApi

    @Binds
    @Singleton
    abstract fun bindSettingsApi(
        settingsApiImpl: SettingsApiImpl
    ): SettingsApi

    @Binds
    @Singleton
    abstract fun bindRecipesApi(
        recipesApiImpl: RecipesApiImpl
    ): RecipesApi

}