package com.weberbox.pifire.recipes.presentation.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Analytics
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.domain.AnalyticsEvent
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.recipes.presentation.contract.RecipesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipesRepo: RecipesRepo,
    private val analytics: Analytics,
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
        isDataError = false
    )

    override fun handleEvents(event: RecipesContract.Event) {
        when (event) {
            is RecipesContract.Event.Refresh -> getRecipesData(forced = true)
            is RecipesContract.Event.VoiceSearch -> {
                analytics.logEvent(
                    name = AnalyticsEvent.VoiceSearch.name
                )
            }
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
                                error = true
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
                        isDataError = true
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
                        error = true
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

    private companion object {
        const val REFRESH_KEY = "refresh"
    }
}
