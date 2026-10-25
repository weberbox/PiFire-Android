package com.weberbox.pifire.feature.recipes.data.mapper

import com.weberbox.pifire.core.di.JsonModule
import com.weberbox.pifire.feature.recipes.data.model.RecipesDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipesDtoToDataMapperTest {

    private val json = JsonModule.provideJson()

    private fun map(body: String) = RecipesDtoToDataMapper.map(json.decodeFromString<RecipesDto>(body))

    @Test
    fun `Celsius decimals in step temps round to whole degrees`() {
        val recipes = map(
            """
            {
              "recipe_details": [{
                "filename": "brisket.pfrecipe",
                "details": {
                  "metadata": {"title": "Brisket", "units": "C", "unknown_field": 1},
                  "recipe": {
                    "steps": [{
                      "hold_temp": 107.5,
                      "mode": "Hold",
                      "trigger_temps": {"food": [93.4, 60], "primary": 0}
                    }]
                  }
                }
              }]
            }
            """,
        )
        val recipe = recipes.recipes.single()
        assertEquals("brisket.pfrecipe", recipe.recipeFilename)
        assertEquals("Brisket", recipe.metadata.title)
        val step = recipe.steps.single()
        assertEquals(108, step.holdTemp)
        assertEquals(listOf(93, 60), step.triggerTemps.food)
        assertEquals(0, step.triggerTemps.primary)
    }

    @Test
    fun `a recipe without details still maps, with defaults`() {
        val recipes = map("""{"recipe_details": [{"filename": "empty.pfrecipe"}]}""")
        val recipe = recipes.recipes.single()
        assertEquals("empty.pfrecipe", recipe.recipeFilename)
        assertTrue(recipe.steps.isEmpty())
        assertTrue(recipe.ingredients.isEmpty())
    }

    @Test
    fun `a bad temp value drops only that field`() {
        val recipes = map(
            """
            {"recipe_details": [{"details": {"recipe": {"steps": [
              {"hold_temp": "hot", "timer": 30, "trigger_temps": {"food": [60, "x"]}}
            ]}}}]}
            """,
        )
        val step = recipes.recipes.single().steps.single()
        assertEquals(0, step.holdTemp)
        assertEquals(30, step.timer)
        assertTrue(step.triggerTemps.food.isEmpty())
    }
}
