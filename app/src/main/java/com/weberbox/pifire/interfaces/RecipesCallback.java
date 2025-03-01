package com.weberbox.pifire.interfaces;

import org.jetbrains.annotations.NotNull;

public interface RecipesCallback {
    void onRecipeDelete(@NotNull String filename);
    void onRetrieveRecipes();
    void onRunRecipe(@NotNull String filename);
}
