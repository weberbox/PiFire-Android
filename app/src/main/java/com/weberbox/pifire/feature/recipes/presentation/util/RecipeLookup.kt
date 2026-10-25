package com.weberbox.pifire.feature.recipes.presentation.util

import com.weberbox.pifire.feature.recipes.presentation.model.RecipesData.Recipes
import com.weberbox.pifire.feature.recipes.presentation.model.RecipesData.Recipes.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** A recipe looked up in loaded recipes data; [recipe] is null when it isn't there. */
internal data class RecipeLookup(val recipe: Recipe?)

/**
 * Follows one recipe in the recipes data: null while no data is loaded (e.g. after process death).
 * Emits only when that recipe changes, so its images aren't decoded again for unrelated updates.
 */
internal fun Flow<Recipes?>.recipeLookup(filename: String): Flow<RecipeLookup?> =
    map { data ->
        data?.let { RecipeLookup(recipe = it.recipes.find { recipe -> recipe.recipeFilename == filename }) }
    }.distinctUntilChanged()

/** Decodes every full-size image of [recipe] off the main thread. */
internal suspend fun decodeRecipeImages(recipe: Recipe): List<ByteArray?> =
    withContext(Dispatchers.Default) {
        recipe.assets.map { asset -> decodeBase64Image(asset.encodedImage) }
    }
