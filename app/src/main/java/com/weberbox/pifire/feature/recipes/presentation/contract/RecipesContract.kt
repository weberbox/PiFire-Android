package com.weberbox.pifire.feature.recipes.presentation.contract

import com.weberbox.pifire.core.model.recipes.RecipesData.Recipes.Recipe
import com.weberbox.pifire.core.ui.base.ViewEvent
import com.weberbox.pifire.core.ui.base.ViewSideEffect
import com.weberbox.pifire.core.ui.base.ViewState
import com.weberbox.pifire.core.ui.util.UiText

class RecipesContract {

    sealed class Event : ViewEvent {
        data object Refresh : Event()
    }

    data class State(
        val recipesList: List<Recipe>,
        val isInitialLoading: Boolean,
        val isRefreshing: Boolean,
        val isDataError: Boolean,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data class RecipeDetails(val filename: String) : Navigation()
        }
    }
}
