package com.weberbox.pifire.recipes.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RecipesDto(
    @SerialName("uuid")
    val uuid: String? = null,

    @SerialName("recipe_details")
    val recipeDetails: List<RecipeDetails>? = null
) {
    @Serializable
    data class RecipeDetails(
        @SerialName("details")
        val details: Details? = null,

        @SerialName("filename")
        val filename: String? = null
    ) {
        @Serializable
        data class Details(
            @SerialName("assets")
            val assets: List<Asset>? = null,

            @SerialName("comments")
            val comments: List<String>? = null,

            @SerialName("metadata")
            val metadata: MetaData? = null,

            @SerialName("recipe")
            val recipe: Recipe? = null
        ) {
            @Serializable
            data class Asset(
                @SerialName("filename")
                val filename: String? = null,

                @SerialName("id")
                val id: String? = null,

                @SerialName("type")
                val type: String? = null,

                @SerialName("encoded_image")
                val encodedImage: String? = null,

                @SerialName("encoded_thumb")
                val encodedThumb: String? = null
            )

            @Serializable
            data class MetaData(
                @SerialName("author")
                val author: String? = null,

                @SerialName("cook_time")
                val cookTime: Int? = null,

                @SerialName("description")
                val description: String? = null,

                @SerialName("difficulty")
                val difficulty: String? = null,

                @SerialName("food_probes")
                val foodProbes: Int? = null,

                @SerialName("id")
                val id: String? = null,

                @SerialName("image")
                val image: String? = null,

                @SerialName("prep_time")
                val prepTime: Int? = null,

                @SerialName("rating")
                val rating: Int? = null,

                @SerialName("thumb")
                val thumb: String? = null,

                @SerialName("thumbnail")
                val thumbnail: String? = null,

                @SerialName("title")
                val title: String? = null,

                @SerialName("units")
                val units: String? = null,

                @SerialName("username")
                val username: String? = null,

                @SerialName("version")
                val version: String? = null
            )

            @Serializable
            data class Recipe(
                @SerialName("ingredients")
                val ingredients: List<Ingredient>? = null,

                @SerialName("instructions")
                val instructions: List<Instruction>? = null,

                @SerialName("steps")
                val steps: List<Step>? = null,
            ) {
                @Serializable
                data class Ingredient(
                    @SerialName("assets")
                    val assets: List<String>? = null,

                    @SerialName("name")
                    val name: String? = null,

                    @SerialName("quantity")
                    val quantity: String? = null
                )

                @Serializable
                data class Instruction(
                    @SerialName("assets")
                    val assets: List<String>? = null,

                    @SerialName("ingredients")
                    val ingredients: List<String>? = null,

                    @SerialName("step")
                    val step: Int? = null,

                    @SerialName("text")
                    val text: String? = null
                )

                @Serializable
                data class Step(
                    @SerialName("hold_temp")
                    val holdTemp: Int? = null,

                    @SerialName("message")
                    val message: String? = null,

                    @SerialName("mode")
                    val mode: String? = null,

                    @SerialName("notify")
                    val notify: Boolean? = null,

                    @SerialName("pause")
                    val pause: Boolean? = null,

                    @SerialName("timer")
                    val timer: Int? = null,

                    @SerialName("trigger_temps")
                    val triggerTemps: TriggerTemps? = null,

                    @Transient
                    var currentStep: Boolean? = null
                ) {
                    @Serializable
                    data class TriggerTemps(
                        @SerialName("food")
                        val food: List<Int>? = null,

                        @SerialName("primary")
                        val primary: Int? = null
                    )
                }
            }
        }
    }
}
