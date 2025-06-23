package com.weberbox.pifire.recipes.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.recipes.data.model.RecipesDto
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Asset
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Ingredient
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Instruction
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.MetaData
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Step
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Step.TriggerTemps

object RecipesDtoToDataMapper : Mapper<RecipesDto, Recipes> {
    private val defaultAsset = Asset()
    private val defaultMetaData = MetaData()
    private val defaultIngredient = Ingredient()
    private val defaultInstruction = Instruction()
    private val defaultStep = Step()
    private val defaultTriggerTemps = TriggerTemps()

    override fun map(from: RecipesDto) = Recipes(
        uuid = from.uuid.orEmpty(),
        recipes = from.recipeDetails?.map { recipe ->
            Recipe(
                recipeFilename = recipe.filename ?: "",
                assets = recipe.details?.assets?.map { asset ->
                    Asset(
                        filename = asset.filename ?: defaultAsset.filename,
                        id = asset.id ?: defaultAsset.id,
                        encodedImage = asset.encodedImage ?: defaultAsset.encodedImage,
                        encodedThumb = asset.encodedThumb ?: defaultAsset.encodedThumb
                    )
                } ?: emptyList(),
                metadata = MetaData(
                    author = recipe.details?.metadata?.author ?: defaultMetaData.author,
                    cookTime = recipe.details?.metadata?.cookTime ?: defaultMetaData.cookTime,
                    description = recipe.details?.metadata?.description
                        ?: defaultMetaData.description,
                    difficulty = recipe.details?.metadata?.difficulty ?: defaultMetaData.difficulty,
                    foodProbes = recipe.details?.metadata?.foodProbes ?: defaultMetaData.foodProbes,
                    id = recipe.details?.metadata?.id ?: defaultMetaData.id,
                    image = recipe.details?.metadata?.image ?: defaultMetaData.image,
                    prepTime = recipe.details?.metadata?.prepTime ?: defaultMetaData.prepTime,
                    rating = recipe.details?.metadata?.rating ?: defaultMetaData.rating,
                    thumb = recipe.details?.metadata?.thumb ?: defaultMetaData.thumb,
                    thumbnail = recipe.details?.metadata?.thumbnail ?: defaultMetaData.thumbnail,
                    title = recipe.details?.metadata?.title ?: defaultMetaData.title,
                    units = recipe.details?.metadata?.units ?: defaultMetaData.units,
                    version = recipe.details?.metadata?.version ?: defaultMetaData.version
                ),
                ingredients = recipe.details?.recipe?.ingredients?.map { ingredient ->
                    Ingredient(
                        name = ingredient.name ?: defaultIngredient.name,
                        quantity = ingredient.quantity ?: defaultIngredient.quantity
                    )
                } ?: emptyList(),
                instructions = recipe.details?.recipe?.instructions?.map { instruction ->
                    Instruction(
                        ingredients = instruction.ingredients ?: defaultInstruction.ingredients,
                        step = instruction.step ?: defaultInstruction.step,
                        text = instruction.text ?: defaultInstruction.text
                    )
                } ?: emptyList(),
                steps = recipe.details?.recipe?.steps?.map { step ->
                    Step(
                        holdTemp = step.holdTemp ?: defaultStep.holdTemp,
                        message = step.message ?: defaultStep.message,
                        mode = step.mode ?: defaultStep.mode,
                        notify = step.notify ?: defaultStep.notify,
                        pause = step.pause ?: defaultStep.pause,
                        timer = step.timer ?: defaultStep.timer,
                        triggerTemps = TriggerTemps(
                            food = step.triggerTemps?.food ?: defaultTriggerTemps.food,
                            primary = step.triggerTemps?.primary ?: defaultTriggerTemps.primary
                        )
                    )
                } ?: emptyList()
            )
        } ?: emptyList()
    )
}