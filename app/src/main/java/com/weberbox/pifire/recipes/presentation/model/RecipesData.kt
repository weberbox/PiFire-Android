package com.weberbox.pifire.recipes.presentation.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.weberbox.pifire.common.domain.Uuid
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class RecipesData(
    val recipesMap: Map<Uuid, Recipes> = emptyMap()
) {
    @Immutable
    @Serializable
    data class Recipes(
        val uuid: String = "",
        val recipes: List<Recipe> = emptyList()
    ) {
        @Serializable
        data class Recipe(
            val recipeFilename: String = "",
            val assets: List<Asset> = emptyList(),
            val metadata: MetaData = MetaData(),
            val ingredients: List<Ingredient> = emptyList(),
            val instructions: List<Instruction> = emptyList(),
            val steps: List<Step> = emptyList()
        ) {
            @Parcelize
            @Serializable
            data class Asset(
                val filename: String = "",
                val id: String = "",
                val encodedImage: String = "",
                val encodedThumb: String = ""
            ) : Parcelable

            @Serializable
            data class MetaData(
                val author: String = "",
                val cookTime: Int = 0,
                val description: String = "",
                val difficulty: String = "",
                val foodProbes: Int = 2,
                val id: String = "",
                val image: String = "",
                val prepTime: Int = 0,
                val rating: Int = 5,
                val thumb: String = "",
                val thumbnail: String = "",
                val title: String = "",
                val units: String = "F",
                val version: String = ""
            )

            @Serializable
            data class Ingredient(
                val name: String = "",
                val quantity: String = ""
            )

            @Serializable
            data class Instruction(
                val ingredients: List<String> = emptyList(),
                val step: Int = 0,
                val text: String = ""
            )

            @Serializable
            data class Step(
                val holdTemp: Int = 0,
                val message: String = "",
                val mode: String = "",
                val notify: Boolean = false,
                val pause: Boolean = false,
                val timer: Int = 0,
                val triggerTemps: TriggerTemps = TriggerTemps(),
            ) {
                @Serializable
                data class TriggerTemps(
                    val food: List<Int> = emptyList(),
                    val primary: Int = 0
                )
            }
        }
    }
}
