package com.weberbox.pifire.recipes.presentation.component

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.keyboardAsState
import com.weberbox.pifire.common.presentation.util.showToast
import com.weberbox.pifire.recipes.presentation.model.RecipesData.Recipes.Recipe
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecipesInputField(
    modifier: Modifier = Modifier,
    searchBarState: SearchBarState,
    textFieldState: TextFieldState,
    hazeState: HazeState,
    searchStyle: HazeStyle,
    onNavigate: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchClear: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isKeyboardOpen by keyboardAsState()
    val inputFieldColor by animateColorAsState(
        targetValue = if (searchBarState.targetValue == SearchBarValue.Collapsed)
            Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(durationMillis = 400)
    )
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data = it.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result?.get(0) != null) {
                textFieldState.setTextAndPlaceCursorAtEnd(result[0])
                onSearch(result[0])
            } else {
                context.showToast(R.string.error_no_speech_detected)
            }
        } else {
            context.showToast(R.string.error_recognition_canceled)
        }
    }
    InputField(
        modifier = modifier
            .focusProperties { canFocus = searchBarState.currentValue == SearchBarValue.Expanded }
            .then(
                if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                    Modifier.hazeEffect(
                        state = hazeState,
                        style = searchStyle
                    )
                } else Modifier
            ),
        searchBarState = searchBarState,
        textFieldState = textFieldState,
        onSearch = onSearch,
        placeholder = { Text(text = stringResource(R.string.search_ellipsis)) },
        leadingIcon = {
            IconButton(
                onClick = {
                    if (searchBarState.currentValue == SearchBarValue.Expanded) {
                        scope.launch {
                            keyboardController?.hide()
                            while (isKeyboardOpen) {
                                // Wait until keyboard is closed to animate or it flashes keyboard
                                delay(10)
                            }
                            searchBarState.animateToCollapsed()
                        }
                    } else {
                        onNavigate()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        trailingIcon = {
            val showClear = textFieldState.text.isNotEmpty()
            AnimatedContent(
                targetState = showClear
            ) { clear ->
                if (clear) {
                    IconButton(
                        onClick = {
                            textFieldState.clearText()
                            onSearchClear()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.clear)
                        )
                    }
                } else {
                    val message = stringResource(R.string.recipes_voice_input_message)
                    IconButton(
                        onClick = {
                            if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                                scope.launch { searchBarState.animateToExpanded() }
                            }
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                            intent.putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, message)
                            launcher.launch(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = stringResource(R.string.voice_input)
                        )
                    }
                }
            }
        },
        colors = inputFieldColors(
            focusedContainerColor = inputFieldColor,
            unfocusedContainerColor = inputFieldColor,
            disabledContainerColor = inputFieldColor
        )
    )
}

@Composable
internal fun RecipesSearchResults(
    modifier: Modifier = Modifier,
    recipeData: List<Recipe>,
    searchQuery: CharSequence,
    onResultClick: (title: String) -> Unit
) {
    val filteredList = remember(searchQuery, recipeData) {
        recipeData.filter { recipe ->
            recipe.metadata.title.contains(searchQuery, ignoreCase = true)
        }
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        filteredList.forEach { recipe ->
            ListItem(
                headlineContent = { Text(recipe.metadata.title) },
                supportingContent = {
                    if (recipe.metadata.description.isNotBlank()) {
                        Row(
                            modifier = Modifier.padding(vertical = MaterialTheme.spacing.extraSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                MaterialTheme.spacing.extraSmall
                            )
                        ) {
                            Text(
                                text = recipe.metadata.description,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.LocalDining,
                        contentDescription = null
                    )
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clickable { onResultClick(recipe.metadata.title) }
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.smallThree,
                        vertical = MaterialTheme.spacing.extraSmall
                    )
            )
        }
    }
}