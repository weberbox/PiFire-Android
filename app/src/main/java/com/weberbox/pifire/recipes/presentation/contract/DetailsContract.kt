package com.weberbox.pifire.recipes.presentation.contract

import com.weberbox.pifire.common.presentation.base.ViewEvent
import com.weberbox.pifire.common.presentation.base.ViewSideEffect
import com.weberbox.pifire.common.presentation.base.ViewState
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe

class DetailsContract {

    sealed class Event : ViewEvent {
        data object DeleteRecipeDialog : Event()
        data object DeleteRecipe : Event()
        data object RunRecipe : Event()
    }

    data class State(
        val recipeData: Recipe,
        val recipeStep: Int,
        val recipeImages: List<ByteArray?>,
        val isInitialLoading: Boolean,
        val isLoading: Boolean,
        val isDataError: Boolean
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data class Notification(val text: UiText, val error: Boolean) : Effect()
        data object DeleteDialog : Effect()

        sealed class Navigation : Effect() {
            data object Back : Navigation()
            data object BackRefresh : Navigation()
            data object DashHome : Navigation()
            data class NavRoute(val route: Any, val popUp: Boolean = false) : Navigation()
        }
    }
}