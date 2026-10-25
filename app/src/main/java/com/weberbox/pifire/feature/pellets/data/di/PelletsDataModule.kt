package com.weberbox.pifire.feature.pellets.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.model.pellets.PelletsData
import com.weberbox.pifire.feature.pellets.data.api.PelletsApi
import com.weberbox.pifire.feature.pellets.data.api.PelletsApiImpl
import com.weberbox.pifire.feature.pellets.data.repo.PelletsRepo
import com.weberbox.pifire.feature.pellets.data.repo.PelletsRepoImpl
import com.weberbox.pifire.feature.pellets.data.serializer.PelletsSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PelletsDataModule {

    @Binds
    @Singleton
    abstract fun bindPelletsApi(
        pelletsApiImpl: PelletsApiImpl,
    ): PelletsApi

    @Binds
    @Singleton
    abstract fun bindPelletsRepo(
        pelletsRepoImpl: PelletsRepoImpl,
    ): PelletsRepo

    companion object {
        @Singleton
        @Provides
        fun providePelletsDataStore(@ApplicationContext appContext: Context): DataStore<PelletsData> {
            return DataStoreFactory.create(
                serializer = PelletsSerializer,
                produceFile = { appContext.dataStoreFile(AppConfig.PELLETS_DATA_NAME) },
                corruptionHandler = ReplaceFileCorruptionHandler { PelletsData() }
            )
        }
    }
}
