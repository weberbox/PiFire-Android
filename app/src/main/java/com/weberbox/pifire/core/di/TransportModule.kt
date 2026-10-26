package com.weberbox.pifire.core.di

import com.weberbox.pifire.core.network.transport.PiFireConnection
import com.weberbox.pifire.core.network.transport.PiFireTransport
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TransportModule {

    @Binds
    @Singleton
    abstract fun bindPiFireTransport(
        piFireConnection: PiFireConnection,
    ): PiFireTransport
}
