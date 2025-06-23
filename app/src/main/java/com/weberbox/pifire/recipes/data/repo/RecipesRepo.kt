package com.weberbox.pifire.recipes.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe

interface RecipesRepo {
    suspend fun getRecipes(): Result<List<Recipe>, DataError>
    suspend fun getCachedData(): Result<List<Recipe>, DataError>
    suspend fun updateRecipesData(recipes: Recipes)
}