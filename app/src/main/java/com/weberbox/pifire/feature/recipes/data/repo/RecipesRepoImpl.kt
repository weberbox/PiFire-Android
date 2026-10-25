package com.weberbox.pifire.feature.recipes.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.recipes.RecipesData
import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes
import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes.Recipe
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.recipes.data.api.RecipesApi
import com.weberbox.pifire.feature.recipes.data.mapper.RecipesDtoToDataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecipesRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<RecipesData>,
    private val recipesApi: RecipesApi,
    private val json: Json,
) : RecipesRepo {

    override suspend fun getRecipes(): Result<List<Recipe>, DataError> = withContext(Dispatchers.IO) {
        recipesApi.getRecipes().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)

                is Result.Success -> {
                    parseGetResponse(result.data, json, RecipesDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)

                            is Result.Success -> {
                                sessionStateHolder.setRecipesData(data.data)
                                updateRecipesData(data.data)
                                Result.Success(data.data.recipes)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<List<Recipe>, DataError> = withContext(Dispatchers.IO) {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        if (data != null && currentServer.isNotBlank()) {
            data.recipesMap[currentServer]?.let { recipes ->
                sessionStateHolder.setRecipesData(recipes)
                Result.Success(recipes.recipes)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun updateRecipesData(recipes: Recipes) {
        dataStore.updateData { data ->
            if (recipes.uuid.isBlank()) return@updateData data

            val updatedMap = data.recipesMap.toMutableMap().apply {
                put(recipes.uuid, recipes)
            }

            data.copy(recipesMap = updatedMap)
        }
    }

    override suspend fun deleteRecipe(filename: String): Result<Unit, DataError> =
        recipesApi.deleteRecipe(filename = filename)

    override suspend fun startRecipe(filename: String): Result<Unit, DataError> =
        recipesApi.startRecipe(filename = filename)
}
