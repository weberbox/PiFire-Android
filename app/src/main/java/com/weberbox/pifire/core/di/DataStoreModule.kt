package com.weberbox.pifire.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.data.datastore.PushRegistrationSerializer
import com.weberbox.pifire.core.data.datastore.SettingsSerializer
import com.weberbox.pifire.core.model.push.PushRegistration
import com.weberbox.pifire.core.model.settings.SettingsData
import com.weberbox.pifire.core.network.headers.HeadersData
import com.weberbox.pifire.core.network.headers.HeadersSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext appContext: Context): DataStore<SettingsData> = DataStoreFactory.create(
        serializer = SettingsSerializer,
        produceFile = { appContext.dataStoreFile(AppConfig.SETTINGS_DATA_NAME) },
        corruptionHandler = ReplaceFileCorruptionHandler { SettingsData() },
    )

    @Singleton
    @Provides
    fun provideHeadersDataStore(@ApplicationContext appContext: Context): DataStore<HeadersData> = DataStoreFactory.create(
        serializer = HeadersSerializer,
        produceFile = { appContext.dataStoreFile(AppConfig.HEADERS_DATA_NAME) },
        corruptionHandler = ReplaceFileCorruptionHandler { HeadersData() },
    )

    @Singleton
    @Provides
    fun providePushRegistrationDataStore(@ApplicationContext appContext: Context): DataStore<PushRegistration> = DataStoreFactory.create(
        serializer = PushRegistrationSerializer,
        produceFile = { appContext.dataStoreFile(AppConfig.PUSH_REGISTRATION_DATA_NAME) },
        corruptionHandler = ReplaceFileCorruptionHandler { PushRegistration() },
    )
}
