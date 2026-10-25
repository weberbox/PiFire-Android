package com.weberbox.pifire.feature.dashboard.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.model.dashboard.DashData
import com.weberbox.pifire.feature.dashboard.data.api.DashApi
import com.weberbox.pifire.feature.dashboard.data.api.DashApiImpl
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepo
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepoImpl
import com.weberbox.pifire.feature.dashboard.data.serializer.DashSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DashDataModule {

    @Binds
    @Singleton
    abstract fun bindDashApi(
        dashApiImpl: DashApiImpl,
    ): DashApi

    @Binds
    @Singleton
    abstract fun bindDashRepo(
        dashRepoImpl: DashRepoImpl,
    ): DashRepo

    companion object {
        @Singleton
        @Provides
        fun provideDashDataStore(@ApplicationContext appContext: Context): DataStore<DashData> {
            return DataStoreFactory.create(
                serializer = DashSerializer,
                produceFile = { appContext.dataStoreFile(AppConfig.DASH_DATA_NAME) },
                corruptionHandler = ReplaceFileCorruptionHandler { DashData() }
            )
        }
    }
}
