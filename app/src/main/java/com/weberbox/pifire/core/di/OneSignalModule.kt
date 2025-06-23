package com.weberbox.pifire.core.di

import android.content.Context
import com.weberbox.pifire.core.util.OneSignalManager
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OneSignalModule {

    @Provides
    @Singleton
    fun provideOneSignalManager(
        @ApplicationContext appContext: Context,
        settingsRepo: SettingsRepo,
    ): OneSignalManager {
        return OneSignalManager(
            appContext = appContext,
            settingsRepo = settingsRepo
        )
    }

}