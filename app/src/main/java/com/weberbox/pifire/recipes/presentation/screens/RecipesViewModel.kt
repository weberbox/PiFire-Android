package com.weberbox.pifire.recipes.presentation.screens

import androidx.lifecycle.viewModelScope
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.presentation.AsUiText.asUiText
import com.weberbox.pifire.common.presentation.base.BaseViewModel
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.recipes.presentation.contract.RecipesContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipesRepo: RecipesRepo
) : BaseViewModel<RecipesContract.Event, RecipesContract.State, RecipesContract.Effect>() {

    init {
        getRecipesData(forced = false)
    }

    override fun setInitialState() = RecipesContract.State(
        recipesList = emptyList(),
        isInitialLoading = true,
        isRefreshing = false,
        isDataError = false
    )

    override fun handleEvents(event: RecipesContract.Event) {
        when (event) {
            is RecipesContract.Event.Refresh -> getRecipesData(true)
        }
    }

    private fun getRecipesData(forced: Boolean) {
        if (forced) setState { copy(isRefreshing = true) }
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = recipesRepo.getRecipes()) {
                is Result.Error -> {
                    if (forced) {
                        withContext(Dispatchers.Main) {
                            setEffect {
                                RecipesContract.Effect.Notification(
                                    text = result.error.asUiText(),
                                    error = true
                                )
                            }
                        }
                    } else {
                        getCachedData()
                    }
                }

                is Result.Success -> {
                    withContext(Dispatchers.Main) {
                        setState {
                            copy(
                                recipesList = result.data,
                                isInitialLoading = false,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun getCachedData() {
        val result = recipesRepo.getCachedData()
        withContext(Dispatchers.Main) {
            when (result) {
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
                    setState { copy(recipesList = result.data, isInitialLoading = false) }
                    setEffect {
                        RecipesContract.Effect.Notification(
                            text = UiText(R.string.alerter_cached_results),
                            error = true
                        )
                    }
                }
            }
        }
    }
}