package com.weberbox.pifire.recipes.presentation.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import com.weberbox.pifire.recipes.presentation.model.FabItem
import com.weberbox.pifire.recipes.presentation.model.FabItem.Action

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun FloatingActionMenu(
    modifier: Modifier = Modifier,
    scrollState: ScrollState,
    onFabAction: (action: Action) -> Unit
) {
    var fabVisible by remember { mutableStateOf(true) }
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var previousScrollValue by remember { mutableIntStateOf(0) }

    val fabItems by remember {
        mutableStateOf(
            listOf(
                FabItem(Icons.Filled.Delete, R.string.delete, Action.Delete),
                FabItem(Icons.Filled.Share, R.string.share, Action.Share),
                FabItem(Icons.Filled.Print, R.string.print, Action.Print),
                FabItem(Icons.Filled.PlayArrow, R.string.run, Action.Run)
            )
        )
    }

    LaunchedEffect(scrollState.value) {
        if (scrollState.isScrollInProgress && fabMenuExpanded) fabMenuExpanded = false
        if (scrollState.value != previousScrollValue) {
            fabVisible = scrollState.value <= previousScrollValue
            previousScrollValue = scrollState.value
        }
    }

    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

    FloatingActionButtonMenu(
        expanded = fabMenuExpanded,
        button = {
            ToggleFloatingActionButton(
                modifier = modifier
                    .animateFloatingActionButton(
                        visible = fabVisible || fabMenuExpanded,
                        alignment = Alignment.BottomEnd
                    ),
                checked = fabMenuExpanded,
                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded }
            ) {
                val imageVector by remember {
                    derivedStateOf {
                        if (checkedProgress > 0.5f)
                            Icons.Filled.Close else Icons.Filled.Menu
                    }
                }
                Icon(
                    painter = rememberVectorPainter(imageVector),
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress })
                )
            }
        }
    ) {
        fabItems.forEach { item ->
            FloatingActionButtonMenuItem(
                onClick = {
                    fabMenuExpanded = false
                    onFabAction(item.action)
                },
                icon = { Icon(item.icon, contentDescription = null) },
                text = { Text(text = stringResource(item.title)) },
            )
        }
    }
}