package com.weberbox.pifire.recipes.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Speedometer
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.formatMinutes
import com.weberbox.pifire.common.presentation.util.rememberPaletteColorState
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe
import com.weberbox.pifire.recipes.presentation.util.decodeBase64Image
import com.weberbox.pifire.setup.presentation.component.RecipeAsyncImage

@Composable
internal fun RecipeItem(
    details: Recipe,
    modifier: Modifier = Modifier,
    onRecipeClick: (filename: String) -> Unit
) {
    var recipeImage: ByteArray? by remember { mutableStateOf(null) }
    val paletteColor by rememberPaletteColorState()

    LaunchedEffect(Unit) {
        details.assets.forEach { asset ->
            if (asset.filename == details.metadata.thumbnail) {
                if (asset.encodedThumb.isNotBlank()) {
                    recipeImage = decodeBase64Image(asset.encodedThumb)
                    paletteColor.updateStartingColor(recipeImage)
                }
            }
        }
    }

    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = {
            onRecipeClick(details.recipeFilename)
        }
    ) {
        Row(
            modifier = modifier
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .background(Brush.horizontalGradient(colors = paletteColor.brushColor))
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.large)
                    .size(MaterialTheme.spacing.extraExtraLarge)
            ) {
                RecipeAsyncImage(
                    image = recipeImage
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(MaterialTheme.spacing.smallOne)
            ) {
                Text(
                    text = details.metadata.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = stringResource(R.string.recipes_cook_time)
                    )
                    Text(
                        text = formatMinutes(details.metadata.cookTime),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.size(MaterialTheme.spacing.extraExtraSmall))
                    Icon(
                        imageVector = Icon.Filled.Speedometer,
                        contentDescription = stringResource(R.string.recipes_difficulty)
                    )
                    Text(
                        text = details.metadata.difficulty,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                RatingBar(
                    modifier = Modifier.padding(
                        top = MaterialTheme.spacing.extraSmall,
                        start = MaterialTheme.spacing.extraExtraSmall
                    ),
                    value = details.metadata.rating.toFloat(),
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
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeItemPreview() {
    PiFireTheme {
        Box(
            modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
        ) {
            RecipeItem(
                details = buildRecipe()[0],
                onRecipeClick = { }
            )
        }
    }
}

