package com.weberbox.pifire.recipes.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe.Instruction
import com.weberbox.pifire.recipes.presentation.screens.buildRecipe
import com.weberbox.pifire.recipes.presentation.util.toBulletedList

@Composable
internal fun RecipeInstructions(
    modifier: Modifier = Modifier,
    instructions: List<Instruction>
) {
    var limit by rememberSaveable { mutableIntStateOf(AppConfig.LIST_VIEW_LIMIT) }
    HeaderCard(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
        title = stringResource(R.string.recipes_instructions),
        headerIcon = Icons.Outlined.FormatListNumbered,
        listSize = instructions.size,
        listLimit = limit,
        viewAllClick = {
            limit = instructions.size + 1
        }
    ) {
        Column(
            modifier = modifier.padding(horizontal = MaterialTheme.spacing.extraSmallOne),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo)
        ) {
            InstructionsItem(
                checked = true,
                checkEnabled = false,
                fontWeight = FontWeight.Bold,
                direction = stringResource(R.string.recipes_item_direction),
                ingredients = stringResource(R.string.recipes_ingredients),
                step = stringResource(R.string.recipes_item_step)
            )
            for (item in instructions.take(limit)) {
                InstructionsItem(
                    direction = item.text,
                    ingredients = item.ingredients.toBulletedList(),
                    step = item.step.toString()
                )
            }
        }
    }
}

@Composable
private fun InstructionsItem(
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    checkEnabled: Boolean = true,
    fontWeight: FontWeight = FontWeight.Normal,
    direction: String,
    ingredients: String,
    step: String,
) {
    var itemChecked by rememberSaveable { mutableStateOf(checked) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .clickable(
                enabled = checkEnabled,
                onClick = { itemChecked = !itemChecked }
            ),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            modifier = Modifier.weight(0.12f),
            enabled = checkEnabled,
            checked = itemChecked,
            onCheckedChange = { itemChecked = it }
        )
        Text(
            modifier = Modifier.weight(0.5f),
            text = direction,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = fontWeight
        )
        Text(
            modifier = Modifier.weight(0.3f),
            text = ingredients,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = fontWeight
        )
        Text(
            modifier = Modifier.weight(0.2f),
            text = step,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
            fontWeight = fontWeight
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun RecipeInstructionsPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                RecipeInstructions(
                    instructions = buildRecipe()[0].instructions
                )
            }
        }
    }
}