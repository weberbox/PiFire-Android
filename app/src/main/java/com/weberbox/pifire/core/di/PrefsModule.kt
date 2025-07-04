package com.weberbox.pifire.core.di

import android.content.Context
import com.weberbox.pifire.core.singleton.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {

    @Provides
    @Singleton
    fun providePrefs(@ApplicationContext appContext: Context): Prefs {
        return Prefs(appContext)
    }

}