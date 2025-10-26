package com.weberbox.pifire.core.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.common.data.interfaces.Analytics
import com.weberbox.pifire.core.singleton.AnalyticsTracker
import com.weberbox.pifire.core.singleton.NoOpAnalyticsTracker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(
        @ApplicationContext context: Context
    ): FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Provides
    @Singleton
    fun provideAnalytics(
        firebaseAnalytics: FirebaseAnalytics
    ): Analytics {
        return if (BuildConfig.ENABLE_ANALYTICS) {
            AnalyticsTracker(firebaseAnalytics)
        } else {
            NoOpAnalyticsTracker()
        }
    }
}