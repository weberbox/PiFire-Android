package com.weberbox.pifire.core.di

import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import com.weberbox.pifire.feature.setup.data.util.ServerDataCacheImpl
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
}