package com.weberbox.pifire.recipes.presentation.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.common.presentation.util.toImmutableList
import com.weberbox.pifire.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.recipes.presentation.contract.ImagesContract
import com.weberbox.pifire.recipes.presentation.util.decodeRecipeImages
import com.weberbox.pifire.recipes.presentation.util.recipeLookup
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
        isDataError = false
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
                                isDataError = false
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
                isDataError = true
            )
        }
    }
}
