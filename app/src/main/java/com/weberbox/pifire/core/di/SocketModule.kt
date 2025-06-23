package com.weberbox.pifire.core.di

import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.settings.data.util.HeadersManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {

    @Provides
    @Singleton
    fun provideSocketManager(
        sessionStateHolder: SessionStateHolder,
        headersManager: HeadersManager,
        json: Json
    ): SocketManager {
        return SocketManager(sessionStateHolder, headersManager, json)
    }

}