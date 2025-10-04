package com.weberbox.pifire.core.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.weberbox.pifire.common.data.interfaces.Analytics
import com.weberbox.pifire.core.singleton.AnalyticsTracker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalytics(tracker: AnalyticsTracker): Analytics

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(
            @ApplicationContext context: Context
        ): FirebaseAnalytics {
            return FirebaseAnalytics.getInstance(context)
        }
    }
}