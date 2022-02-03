package com.weberbox.pifire.interfaces;

public interface RecipeEditCallback {
    void onRecipeUpdated();
    void onRecipeDifficulty(Integer difficulty);
    void onRecipeTime(String hours, String minutes);
}
