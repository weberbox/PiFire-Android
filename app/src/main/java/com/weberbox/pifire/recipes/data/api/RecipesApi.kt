package com.weberbox.pifire.recipes.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result

interface RecipesApi {
    suspend fun getRecipes(): Result<String, DataError>
    suspend fun deleteRecipe(filename: String): Result<Unit, DataError>
    suspend fun startRecipe(filename: String): Result<Unit, DataError>
}