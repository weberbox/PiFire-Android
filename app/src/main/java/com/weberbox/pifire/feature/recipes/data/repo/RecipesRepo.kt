package com.weberbox.pifire.feature.recipes.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes
import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes.Recipe

interface RecipesRepo {
    suspend fun getRecipes(): Result<List<Recipe>, DataError>
    suspend fun getCachedData(): Result<List<Recipe>, DataError>
    suspend fun updateRecipesData(recipes: Recipes)

    suspend fun deleteRecipe(filename: String): Result<Unit, DataError>
    suspend fun startRecipe(filename: String): Result<Unit, DataError>
}
