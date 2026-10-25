package com.weberbox.pifire.feature.info.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.model.info.InfoData
import com.weberbox.pifire.feature.info.data.api.InfoApi
import com.weberbox.pifire.feature.info.data.api.InfoApiImpl
import com.weberbox.pifire.feature.info.data.repo.InfoRepo
import com.weberbox.pifire.feature.info.data.repo.InfoRepoImpl
import com.weberbox.pifire.feature.info.data.serializer.InfoSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InfoDataModule {

    @Binds
    @Singleton
    abstract fun bindInfoApi(
        infoApiImpl: InfoApiImpl,
    ): InfoApi

    @Binds
    @Singleton
    abstract fun bindInfoRepo(
        infoRepoImpl: InfoRepoImpl,
    ): InfoRepo

    companion object {
        @Singleton
        @Provides
        fun provideInfoDataStore(@ApplicationContext appContext: Context): DataStore<InfoData> {
            return DataStoreFactory.create(
                serializer = InfoSerializer,
                produceFile = { appContext.dataStoreFile(AppConfig.INFO_DATA_NAME) },
                corruptionHandler = ReplaceFileCorruptionHandler { InfoData() }
            )
        }
    }
}
