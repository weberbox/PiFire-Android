package com.weberbox.pifire.feature.recipes.data.di

import com.weberbox.pifire.feature.recipes.data.api.RecipesApi
import com.weberbox.pifire.feature.recipes.data.api.RecipesApiImpl
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}
