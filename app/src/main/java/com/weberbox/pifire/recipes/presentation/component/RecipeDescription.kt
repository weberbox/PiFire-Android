package com.weberbox.pifire.recipes.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe

@Composable
internal fun RecipeDescription(
    modifier: Modifier = Modifier,
    description: String
) {
    HeaderCard(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
        title = stringResource(R.string.recipes_description),
        headerIcon = Icons.Outlined.Description,
    ) {
        Box(
            modifier = modifier.padding(horizontal = MaterialTheme.spacing.smallThree)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeDescriptionPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                RecipeDescription(
                    description = buildRecipe()[0].metadata.description
                )
            }
        }
    }
}