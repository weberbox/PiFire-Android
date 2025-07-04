package com.weberbox.pifire.recipes.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.recipes.data.api.RecipesApi
import com.weberbox.pifire.recipes.domain.RecipesDtoToDataMapper
import com.weberbox.pifire.recipes.presentation.model.RecipesData
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecipesRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<RecipesData>,
    private val recipesApi: RecipesApi,
    private val json: Json
) : RecipesRepo {

    override suspend fun getRecipes(): Result<List<Recipe>, DataError> {
        return recipesApi.getRecipes().let { result ->
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

    override suspend fun getCachedData(): Result<List<Recipe>, DataError> {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        return if (data != null && currentServer.isNotBlank()) {
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
}