package com.weberbox.pifire.record;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public record RecipeImageRecord(@NotNull String encodedImage, @NotNull String recipeImage,
                                @NotNull String recipeFilename) {

    public RecipeImageRecord(@NonNull String encodedImage, @NotNull String recipeImage,
                             @NonNull String recipeFilename) {
        this.encodedImage = encodedImage;
        this.recipeImage = recipeImage;
        this.recipeFilename = recipeFilename;
    }

    @Override
    @NonNull
    public String encodedImage() {
        return encodedImage;
    }

    @Override
    @NonNull
    public String recipeImage() {
        return recipeImage;
    }

    @Override
    @NonNull
    public String recipeFilename() {
        return recipeFilename;
    }
}
