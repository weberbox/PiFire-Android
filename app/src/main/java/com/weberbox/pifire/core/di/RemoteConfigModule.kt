package com.weberbox.pifire.core.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.weberbox.pifire.common.data.repo.RemoteConfigRepository
import com.weberbox.pifire.common.data.repo.RemoteConfigRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig {
        // Config settings are applied (and awaited) by RemoteConfigRepositoryImpl before each fetch
        return FirebaseRemoteConfig.getInstance()
    }

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(
        impl: RemoteConfigRepositoryImpl
    ): RemoteConfigRepository = impl
}
