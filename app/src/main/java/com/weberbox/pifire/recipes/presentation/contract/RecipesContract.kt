package com.weberbox.pifire.recipes.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe

class RecipesContract {

    sealed class Event : ViewEvent {
        data object Refresh: Event()
    }

    data class State(
        val recipesList: List<Recipe>,
        val isInitialLoading: Boolean,
        val isRefreshing: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data class RecipeDetails(val filename: String) : Navigation()
        }
    }
}