package com.weberbox.pifire.feature.recipes.presentation.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.toImmutableList
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.feature.recipes.presentation.contract.ImagesContract
import com.weberbox.pifire.feature.recipes.presentation.util.decodeRecipeImages
import com.weberbox.pifire.feature.recipes.presentation.util.recipeLookup
import com.weberbox.pifire.navigation.NavGraph
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeImageViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val recipesRepo: RecipesRepo,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<ImagesContract.Event, ImagesContract.State, ImagesContract.Effect>() {

    override fun setInitialState() = ImagesContract.State(
        imageList = emptyList(),
        imageIndex = 0,
        isInitialLoading = true,
        isDataError = false,
    )

    override fun handleEvents(event: ImagesContract.Event) {
        // Currently None
    }

    init {
        val args = savedStateHandle.toRoute<NavGraph.RecipesDest.Images>()
        collectDataState(filename = args.filename, index = args.index)
    }

    private fun collectDataState(filename: String, index: Int) {
        viewModelScope.launch {
            sessionStateHolder.recipesDataState.recipeLookup(filename).collect { lookup ->
                val recipe = lookup?.recipe
                when {
                    // No recipes data after process death; loading it emits here again
                    lookup == null -> loadRecipes()

                    recipe == null -> setDataStateError()

                    else -> {
                        val images = decodeRecipeImages(recipe)
                        setState {
                            copy(
                                imageList = images.toImmutableList(),
                                imageIndex = index,
                                isInitialLoading = false,
                                isDataError = false,
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadRecipes() {
        if (recipesRepo.getRecipes() is Result.Success) return
        if (recipesRepo.getCachedData() is Result.Error) setDataStateError()
    }

    private fun setDataStateError() {
        setState {
            copy(
                isInitialLoading = false,
                isDataError = true,
            )
        }
    }
}
