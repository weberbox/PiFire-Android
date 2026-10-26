package com.weberbox.pifire.feature.recipes.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.model.recipes.RecipesData
import com.weberbox.pifire.feature.recipes.data.api.RecipesApi
import com.weberbox.pifire.feature.recipes.data.api.RecipesApiImpl
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepoImpl
import com.weberbox.pifire.feature.recipes.data.serializer.RecipesSerializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecipesDataModule {

    @Binds
    @Singleton
    abstract fun bindRecipesApi(
        recipesApiImpl: RecipesApiImpl,
    ): RecipesApi

    @Binds
    @Singleton
    abstract fun bindRecipesRepo(
        recipesRepoImpl: RecipesRepoImpl,
    ): RecipesRepo

    companion object {
        @Singleton
        @Provides
        fun provideRecipesDataStore(@ApplicationContext appContext: Context): DataStore<RecipesData> = DataStoreFactory.create(
            serializer = RecipesSerializer,
            produceFile = { appContext.dataStoreFile(AppConfig.RECIPES_DATA_NAME) },
            corruptionHandler = ReplaceFileCorruptionHandler { RecipesData() },
        )
    }
}
