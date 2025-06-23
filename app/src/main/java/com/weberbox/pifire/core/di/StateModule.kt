package com.weberbox.pifire.core.di

import androidx.datastore.core.DataStore
import com.google.android.datatransport.runtime.dagger.Lazy
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.dashboard.presentation.model.DashData
import com.weberbox.pifire.events.presentation.model.EventsData
import com.weberbox.pifire.info.presentation.model.InfoData
import com.weberbox.pifire.pellets.presentation.model.PelletsData
import com.weberbox.pifire.recipes.presentation.model.RecipesData
import com.weberbox.pifire.settings.data.model.local.HeadersData
import com.weberbox.pifire.settings.data.util.ServerDataManager
import com.weberbox.pifire.settings.presentation.model.SettingsData
import com.weberbox.pifire.setup.data.util.ServerDataCache
import com.weberbox.pifire.setup.data.util.ServerDataCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StateModule {

    @Provides
    @Singleton
    fun provideSessionStateHolder(): SessionStateHolder {
        return SessionStateHolder()
    }

    @Provides
    @Singleton
    fun provideServerDataCache(prefs: Prefs, json: Json): ServerDataCache {
        return ServerDataCacheImpl(prefs, json)
    }

    @Provides
    @Singleton
    fun provideServerDataManager(
        settingsDataStore: DataStore<SettingsData>,
        eventsDataStore: DataStore<EventsData>,
        recipesDataStore: DataStore<RecipesData>,
        dashDataStore: DataStore<DashData>,
        pelletsDataStore: DataStore<PelletsData>,
        infoDataStore: DataStore<InfoData>,
        headersDataStore: DataStore<HeadersData>
    ): Lazy<ServerDataManager> {
        return Lazy {
            ServerDataManager(
                settingsDataStore,
                eventsDataStore,
                recipesDataStore,
                dashDataStore,
                pelletsDataStore,
                infoDataStore,
                headersDataStore
            )
        }
    }
}