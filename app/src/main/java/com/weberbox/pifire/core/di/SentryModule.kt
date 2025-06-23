package com.weberbox.pifire.core.di

import android.content.Context
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.core.singleton.SentryIO
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SentryModule {

    @Provides
    @Singleton
    fun provideSentry(
        @ApplicationContext appContext: Context,
        settingsRepo: SettingsRepo,
        prefs: Prefs
    ): SentryIO {
        return SentryIO(
            appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            appContext = appContext,
            settingsRepo = settingsRepo,
            prefs = prefs,
        )
    }

}