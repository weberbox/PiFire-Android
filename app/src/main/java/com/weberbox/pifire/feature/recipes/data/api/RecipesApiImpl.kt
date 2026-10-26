package com.weberbox.pifire.feature.recipes.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.network.model.PostDto
import com.weberbox.pifire.core.network.model.PostDto.RecipeDto
import com.weberbox.pifire.core.network.transport.PiFireTransport
import kotlinx.serialization.json.Json
import javax.inject.Inject

class RecipesApiImpl @Inject constructor(
    private val transport: PiFireTransport,
    private val json: Json,
) : RecipesApi {

    override suspend fun getRecipes(): Result<String, DataError> = transport.get(
        ServerConstants.GA_RECIPE_DATA,
        ServerConstants.GA_RECIPE_DETAILS,
    )

    override suspend fun deleteRecipe(filename: String): Result<Unit, DataError> {
        val json = json.encodeToString(
            PostDto(recipeDto = RecipeDto(filename = filename)),
        )
        return transport.post(
            ServerConstants.PA_RECIPES_ACTION,
            ServerConstants.PT_RECIPE_DELETE,
            json,
        )
    }

    override suspend fun startRecipe(filename: String): Result<Unit, DataError> {
        val json = json.encodeToString(
            PostDto(recipeDto = RecipeDto(filename = filename)),
        )
        return transport.post(
            ServerConstants.PA_RECIPES_ACTION,
            ServerConstants.PT_RECIPE_START,
            json,
        )
    }
}
