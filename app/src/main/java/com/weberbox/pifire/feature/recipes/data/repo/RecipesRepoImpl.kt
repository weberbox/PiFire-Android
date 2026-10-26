package com.weberbox.pifire.feature.recipes.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.model.recipes.RecipesData
import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes
import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes.Recipe
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.repo.ServerDataSync
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.recipes.data.api.RecipesApi
import com.weberbox.pifire.feature.recipes.data.mapper.RecipesDtoToDataMapper
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecipesRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<RecipesData>,
    private val recipesApi: RecipesApi,
    private val json: Json,
) : RecipesRepo {

    private val sync = ServerDataSync(
        currentServerUuid = { sessionStateHolder.currentServerUuid },
        publish = sessionStateHolder::setRecipesData,
        readCache = { serverUuid -> dataStore.data.firstOrNull()?.recipesMap?.get(serverUuid) },
        writeCache = ::updateRecipesData,
    )

    override suspend fun getRecipes(): Result<List<Recipe>, DataError> = sync.fetch(
        request = recipesApi::getRecipes,
        parse = { parseGetResponse(it, json, RecipesDtoToDataMapper) },
    ).map { it.recipes }

    override suspend fun getCachedData(): Result<List<Recipe>, DataError> =
        sync.cached().map { it.recipes }

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
