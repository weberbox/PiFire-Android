package com.weberbox.pifire.recipes.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Ingredient
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe

@Composable
fun RecipeIngredients(
    modifier: Modifier = Modifier,
    ingredients: List<Ingredient>
) {
    HeaderCard(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
        title = stringResource(R.string.recipes_ingredients),
        headerIcon = Icons.Outlined.ShoppingCart,
    ) {
        Column(
            modifier = modifier.padding(horizontal = MaterialTheme.spacing.smallThree),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo)
        ) {
            IngredientItem(
                number = stringResource(R.string.recipes_item_number),
                quantity = stringResource(R.string.recipes_item_qty),
                ingredient = stringResource(R.string.recipes_item_ingredient),
                fontWeight = FontWeight.Bold
            )
            ingredients.forEachIndexed { index, ingredient ->
                IngredientItem(
                    number = (index + 1).toString(),
                    quantity = ingredient.quantity,
                    ingredient = ingredient.name
                )
            }
        }
    }
}

@Composable
private fun IngredientItem(
    number: String,
    quantity: String,
    ingredient: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.1f),
            text = number,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight
        )
        Text(
            modifier = Modifier.weight(0.5f),
            text = quantity,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight
        )
        Text(
            modifier = Modifier.weight(0.8f),
            text = ingredient,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
fun RecipeIngredientsPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                RecipeIngredients(
                    ingredients = buildRecipe()[0].ingredients
                )
            }
        }
    }
}