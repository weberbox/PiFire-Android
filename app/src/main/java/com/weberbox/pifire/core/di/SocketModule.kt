package com.weberbox.pifire.core.di

import com.weberbox.pifire.core.network.SocketManager
import com.weberbox.pifire.core.network.headers.HeadersManager
import com.weberbox.pifire.core.session.SessionStateHolder
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