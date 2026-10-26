package com.weberbox.pifire.core.di

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.weberbox.pifire.core.remoteconfig.RemoteConfigRepo
import com.weberbox.pifire.core.remoteconfig.RemoteConfigRepoImpl
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
        // Config settings are applied (and awaited) by RemoteConfigRepoImpl before each fetch
        return FirebaseRemoteConfig.getInstance()
    }

    @Provides
    @Singleton
    fun provideRemoteConfigRepo(
        impl: RemoteConfigRepoImpl,
    ): RemoteConfigRepo = impl
}
