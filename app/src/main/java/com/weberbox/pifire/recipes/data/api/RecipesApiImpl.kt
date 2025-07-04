package com.weberbox.pifire.recipes.data.api

import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto
import com.weberbox.pifire.common.data.model.PostDto.RecipeDto
import com.weberbox.pifire.core.singleton.SocketManager
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecipesApiImpl @Inject constructor(
    private val socketManager: SocketManager,
    private val json: Json
) : RecipesApi {

    override suspend fun getRecipes(): Result<String, DataError> {
        return socketManager.emitGet(
            ServerConstants.GA_RECIPE_DATA,
            ServerConstants.GA_RECIPE_DETAILS
        )
    }

    override suspend fun deleteRecipe(filename: String): Result<Unit, DataError> {
        val json = json.encodeToString(
            PostDto(recipeDto = RecipeDto(filename = filename))
        )
        return socketManager.emitPost(
            ServerConstants.PA_RECIPES_ACTION,
            ServerConstants.PT_RECIPE_DELETE,
            json
        )
    }

    override suspend fun startRecipe(filename: String): Result<Unit, DataError> {
        val json = json.encodeToString(
            PostDto(recipeDto = RecipeDto(filename = filename))
        )
        return socketManager.emitPost(
            ServerConstants.PA_RECIPES_ACTION,
            ServerConstants.PT_RECIPE_START,
            json
        )
    }
}