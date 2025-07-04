package com.weberbox.pifire.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.weberbox.pifire.common.presentation.base.hazeAppBarStyle
import com.weberbox.pifire.common.presentation.theme.spacing
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppBar(
    title: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigate: (() -> Unit)? = null,
) {
    val windowInsets = WindowInsets.safeDrawing
    LargeTopAppBar(
        title = title,
        modifier = Modifier.fillMaxWidth(),
        windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        navigationIcon = {
            if (onNavigate != null) {
                FilledIconButton(
                    onClick = onNavigate,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Suppress("unused")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardAppBar(
    title: @Composable () -> Unit = {},
    showNavigationIcon: Boolean = true,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationIconDescription: String? = null,
    onNavigate: (() -> Unit)? = null,
) {
    val windowInsets = WindowInsets.safeDrawing
    TopAppBar(
        title = title,
        modifier = Modifier.fillMaxWidth(),
        windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            if (onNavigate != null && showNavigationIcon) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentAppBar(
    title: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
    showNavigationIcon: Boolean = true,
    onNavigate: (() -> Unit)? = null,
) {
    val windowInsets = WindowInsets.safeDrawing
    TopAppBar(
        title = title,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        Color.Transparent
                    )
                )
            ),
        windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        ),
        navigationIcon = {
            if (onNavigate != null && showNavigationIcon) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazeAppBar(
    title: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
    hazeState: HazeState,
    style: HazeStyle = hazeAppBarStyle(),
    showNavigationIcon: Boolean = true,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationIconDescription: String? = null,
    windowInsets: WindowInsets = WindowInsets.safeDrawing,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigate: (() -> Unit)? = null,
) {
    TopAppBar(
        title = title,
        windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        modifier = Modifier
            .hazeEffect(
                state = hazeState,
                style = style
            )
            .fillMaxWidth(),
        actions = actions,
        navigationIcon = {
            if (onNavigate != null && showNavigationIcon) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazeSearchAppBar(
    searchBarState: SearchBarState,
    hazeState: HazeState,
    style: HazeStyle = hazeAppBarStyle(),
    windowInsets: WindowInsets = SearchBarDefaults.windowInsets,
    inputField: @Composable () -> Unit,
    searchResults: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = Modifier.hazeEffect(
            state = hazeState,
            style = style
        ),
        contentAlignment = Alignment.Center
    ) {
        SearchBar(
            state = searchBarState,
            inputField = inputField,
            modifier = Modifier
                .windowInsetsPadding(windowInsets)
                .padding(
                    horizontal = MaterialTheme.spacing.small,
                    vertical = MaterialTheme.spacing.smallTwo
                )
                .fillMaxWidth()
        )
        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField,
        ) {
            searchResults()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazeLargeSearchAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    searchBarState: SearchBarState,
    hazeState: HazeState,
    style: HazeStyle = hazeAppBarStyle(),
    windowInsets: WindowInsets = SearchBarDefaults.windowInsets,
    inputField: @Composable () -> Unit,
    searchResults: @Composable ColumnScope.() -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigate: () -> Unit
) {
    Column(
        modifier = modifier
            .hazeEffect(
                state = hazeState,
                style = style
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            title = title,
            windowInsets = windowInsets.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent,
            ),
            modifier = Modifier
                .hazeEffect(
                    state = hazeState,
                    style = style
                )
                .fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            actions = actions
        )
        SearchBar(
            state = searchBarState,
            inputField = inputField,
            modifier = Modifier
                .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Horizontal))
                .padding(
                    horizontal = MaterialTheme.spacing.small,
                    vertical = MaterialTheme.spacing.smallTwo
                )
                .fillMaxWidth()
        )
        ExpandedFullScreenSearchBar(
            state = searchBarState,
            inputField = inputField,
        ) {
            searchResults()
        }
    }
}