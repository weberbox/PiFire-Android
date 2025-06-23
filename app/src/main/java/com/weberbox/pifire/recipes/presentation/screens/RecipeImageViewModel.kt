package com.weberbox.pifire.recipes.presentation.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.recipes.presentation.contract.ImagesContract
import com.weberbox.pifire.recipes.presentation.util.decodeBase64Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.toImmutableList
import javax.inject.Inject

@HiltViewModel
class RecipeImageViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    savedStateHandle: SavedStateHandle
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
        collectDataState(filename = args.filename, args.index)
    }

    private fun collectDataState(filename: String, index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionStateHolder.recipesDataState.collect { stateData ->
                stateData?.also { data ->
                    data.recipes.find { it.recipeFilename == filename }?.also { recipe ->
                        val images = recipe.assets.map { decodeBase64Image(it.encodedImage) }
                        withContext(Dispatchers.Main) {
                            setState {
                                copy(
                                    imageList = images.toImmutableList(),
                                    imageIndex = index,
                                    isInitialLoading = false,
                                    isDataError = false
                                )
                            }
                        }
                    } ?: setDataStateError()
                } ?: setDataStateError()
            }
        }
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