package com.weberbox.pifire.feature.recipes.presentation.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.ui.base.BaseViewModel
import com.weberbox.pifire.core.ui.util.AsUiText.asUiText
import com.weberbox.pifire.core.ui.util.UiText
import com.weberbox.pifire.feature.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.feature.recipes.presentation.contract.RecipesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipesRepo: RecipesRepo,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<RecipesContract.Event, RecipesContract.State, RecipesContract.Effect>() {

    init {
        getRecipesData(forced = false)
        observeRefreshRequest()
    }

    override fun setInitialState() = RecipesContract.State(
        recipesList = emptyList(),
        isInitialLoading = true,
        isRefreshing = false,
        isDataError = false,
    )

    override fun handleEvents(event: RecipesContract.Event) {
        when (event) {
            is RecipesContract.Event.Refresh -> getRecipesData(forced = true)
        }
    }

    private fun getRecipesData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = recipesRepo.getRecipes()) {
                is Result.Error -> {
                    if (forced) {
                        setState { copy(isInitialLoading = false, isRefreshing = false) }
                        setEffect {
                            RecipesContract.Effect.Notification(
                                text = result.error.asUiText(),
                                error = true,
                            )
                        }
                    } else {
                        getCachedData()
                    }
                }

                is Result.Success -> {
                    setState {
                        copy(
                            recipesList = result.data,
                            isInitialLoading = false,
                            isRefreshing = false,
                            isDataError = false,
                        )
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        when (val result = recipesRepo.getCachedData()) {
            is Result.Error -> {
                setState {
                    copy(
                        recipesList = emptyList(),
                        isInitialLoading = false,
                        isRefreshing = false,
                        isDataError = true,
                    )
                }
            }

            is Result.Success -> {
                setState {
                    copy(
                        recipesList = result.data,
                        isInitialLoading = false,
                        isRefreshing = false,
                        isDataError = false,
                    )
                }
                setEffect {
                    RecipesContract.Effect.Notification(
                        text = UiText(R.string.alerter_cached_results),
                        error = true,
                    )
                }
            }
        }
    }

    // Set by RecipeDetails through navBackWithResult on this screen's back stack entry.
    private fun observeRefreshRequest() {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Boolean?>(key = REFRESH_KEY, initialValue = null)
                .collect { refresh ->
                    if (refresh == true) {
                        savedStateHandle[REFRESH_KEY] = null
                        setEvent(RecipesContract.Event.Refresh)
                    }
                }
        }
    }

    companion object {
        // RecipeDetails sets it on this screen's back stack entry
        internal const val REFRESH_KEY = "refresh"
    }
}
