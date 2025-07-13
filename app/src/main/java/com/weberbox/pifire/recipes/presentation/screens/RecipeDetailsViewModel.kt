package com.weberbox.pifire.recipes.presentation.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.navigation.NavGraph
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.toImmutableList
import com.weberbox.pifire.recipes.data.api.RecipesApi
import com.weberbox.pifire.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.recipes.presentation.contract.DetailsContract
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import com.weberbox.pifire.recipes.presentation.util.decodeBase64Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val recipesRepo: RecipesRepo,
    private val recipesApi: RecipesApi,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<DetailsContract.Event, DetailsContract.State, DetailsContract.Effect>() {

    private var currentRecipe by mutableStateOf(Recipe())

    init {
        val args = savedStateHandle.toRoute<NavGraph.RecipesDest.Details>()
        collectDataState(filename = args.filename, args.step)
    }

    override fun setInitialState() = DetailsContract.State(
        recipeData = Recipe(),
        recipeStep = -1,
        recipeImages = emptyList(),
        isInitialLoading = true,
        isLoading = false,
        isDataError = false
    )

    override fun handleEvents(event: DetailsContract.Event) {
        when (event) {
            is DetailsContract.Event.DeleteRecipe -> deleteRecipe()
            is DetailsContract.Event.RunRecipe -> startRecipe()
            is DetailsContract.Event.DeleteRecipeDialog -> handleDeleteRequest()
        }
    }

    private fun collectDataState(filename: String, step: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionStateHolder.recipesDataState.collect { stateData ->
                stateData?.also { data ->
                    data.recipes.find { it.recipeFilename == filename }?.also { recipe ->
                        currentRecipe = recipe
                        val images = recipe.assets.map { decodeBase64Image(it.encodedImage) }
                        withContext(Dispatchers.Main) {
                            setState {
                                copy(
                                    recipeData = recipe,
                                    recipeStep = step,
                                    recipeImages = images.toImmutableList(),
                                    isInitialLoading = false,
                                    isLoading = false,
                                    isDataError = false
                                )
                            }
                        }
                    } ?: setDataStateError()
                } ?: getRecipesData(filename, step)
            }
        }
    }

    private fun getRecipesData(filename: String, step: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = recipesRepo.getRecipes()) {
                is Result.Error -> {
                    getCachedData(filename, step)
                }

                is Result.Success -> {
                    result.data.find { it.recipeFilename == filename }?.also { recipe ->
                        currentRecipe = recipe
                        val images = recipe.assets.map { decodeBase64Image(it.encodedImage) }
                        withContext(Dispatchers.Main) {
                            setState {
                                copy(
                                    recipeData = recipe,
                                    recipeStep = step,
                                    recipeImages = images.toImmutableList(),
                                    isInitialLoading = false,
                                    isLoading = false,
                                    isDataError = false
                                )
                            }
                        }
                    } ?: setDataStateError()
                }
            }
        }
    }

    private fun getCachedData(filename: String, step: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = recipesRepo.getCachedData()) {
                is Result.Error -> {
                    withContext(Dispatchers.Main) {
                        setState {
                            copy(
                                recipeStep = step,
                                isInitialLoading = false,
                                isLoading = false,
                                isDataError = true
                            )
                        }
                    }
                }

                is Result.Success -> {
                    result.data.find { it.recipeFilename == filename }?.also { recipe ->
                        currentRecipe = recipe
                        val images = recipe.assets.map { decodeBase64Image(it.encodedImage) }
                        withContext(Dispatchers.Main) {
                            withContext(Dispatchers.Main) {
                                setState {
                                    copy(
                                        recipeData = recipe,
                                        recipeStep = step,
                                        recipeImages = images.toImmutableList(),
                                        isInitialLoading = false,
                                        isLoading = false,
                                        isDataError = false
                                    )
                                }
                                setEffect {
                                    DetailsContract.Effect.Notification(
                                        text = UiText(R.string.alerter_cached_results),
                                        error = true
                                    )
                                }
                            }
                        }
                    } ?: setDataStateError()
                }
            }
        }
    }

    private fun startRecipe() {
        setLoadingStateTrue()
        viewModelScope.launch(Dispatchers.IO) {
            val result = recipesApi.startRecipe(currentRecipe.recipeFilename)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect {
                            DetailsContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    }

                    is Result.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect {
                            DetailsContract.Effect.Navigation.DashHome
                        }
                    }
                }
            }
        }
    }

    private fun deleteRecipe() {
        setLoadingStateTrue()
        viewModelScope.launch(Dispatchers.IO) {
            val result = recipesApi.deleteRecipe(currentRecipe.recipeFilename)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Error -> {
                        setState { copy(isLoading = false) }
                        setEffect {
                            DetailsContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true
                            )
                        }
                    }

                    is Result.Success -> {
                        setState { copy(isLoading = false) }
                        setEffect { DetailsContract.Effect.Navigation.BackRefresh }
                    }
                }
            }
        }
    }

    private fun handleDeleteRequest() {
        if (viewState.value.recipeStep == -1) {
            setEffect { DetailsContract.Effect.DeleteDialog }
        } else {
            setEffect {
                DetailsContract.Effect.Notification(
                    text = UiText(R.string.recipes_delete_running),
                    error = true
                )
            }
        }
    }

    private fun setLoadingStateTrue() {
        setState { copy(isLoading = true) }
    }

    private suspend fun setDataStateError() {
        withContext(Dispatchers.Main) {
            setState {
                copy(
                    isInitialLoading = false,
                    isLoading = false,
                    isDataError = true
                )
            }
        }
    }
}