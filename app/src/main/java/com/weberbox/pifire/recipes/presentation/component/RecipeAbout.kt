package com.weberbox.pifire.recipes.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.formatMinutes
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe

@Composable
internal fun RecipeAbout(
    modifier: Modifier = Modifier,
    recipe: Recipe
) {
    HeaderCard(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
        title = stringResource(R.string.recipes_header_about),
        headerIcon = Icons.Outlined.Info,
    ) {
        Column(
            modifier = modifier.padding(horizontal = MaterialTheme.spacing.smallThree),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo)
        ) {
            TextRowItem(
                title = stringResource(R.string.recipes_author),
                text = recipe.metadata.author
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo)
            ) {
                Text(
                    text = stringResource(R.string.recipes_rating),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                RatingBar(
                    modifier = Modifier.padding(
                        top = MaterialTheme.spacing.extraSmall,
                        start = MaterialTheme.spacing.extraExtraSmall
                    ),
                    value = recipe.metadata.rating.toFloat(),
                    style = RatingBarStyle.Fill(
                        activeColor = MaterialTheme.colorScheme.primary,
                        inActiveColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    size = MaterialTheme.spacing.smallOne,
                    spaceBetween = MaterialTheme.spacing.extraSmall,
                    isIndicator = true,
                    onValueChange = {},
                    onRatingChanged = {}
                )
            }
            TextRowItem(
                title = stringResource(R.string.recipes_prep_time),
                text = formatMinutes(recipe.metadata.prepTime)
            )
            TextRowItem(
                title = stringResource(R.string.recipes_cook_time),
                text = formatMinutes(recipe.metadata.cookTime)
            )
            TextRowItem(
                title = stringResource(R.string.recipes_difficulty),
                text = recipe.metadata.difficulty
            )
            TextRowItem(
                title = stringResource(R.string.recipes_probes),
                text = recipe.metadata.foodProbes.toString()
            )
        }
    }
}

@Composable
private fun TextRowItem(
    title: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeAboutPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                RecipeAbout(
                    recipe = buildRecipe()[0]
                )
            }
        }
    }
}