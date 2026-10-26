package com.weberbox.pifire.feature.setup.data.di

import com.weberbox.pifire.feature.setup.data.api.SetupApi
import com.weberbox.pifire.feature.setup.data.repo.SetupRepo
import com.weberbox.pifire.feature.setup.data.repo.SetupRepoImpl
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import com.weberbox.pifire.feature.setup.data.util.ServerDataCacheImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SetupDataModule {

    @Binds
    @Singleton
    abstract fun bindSetupRepo(
        setupRepoImpl: SetupRepoImpl,
    ): SetupRepo

    @Binds
    @Singleton
    abstract fun bindServerDataCache(
        serverDataCacheImpl: ServerDataCacheImpl,
    ): ServerDataCache

    companion object {
        @Provides
        @Singleton
        fun provideSetupApi(retrofit: Retrofit): SetupApi =
            retrofit.create(SetupApi::class.java)
    }
}
