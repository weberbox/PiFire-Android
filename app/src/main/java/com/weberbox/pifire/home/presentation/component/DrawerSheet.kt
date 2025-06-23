package com.weberbox.pifire.home.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.PiFireLogo
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.navFontFamily
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.home.presentation.utils.buildNavigationItems
import com.weberbox.pifire.home.presentation.utils.buildStaticNavigationItems
import com.weberbox.pifire.home.presentation.model.NavigationItem
import com.weberbox.pifire.home.presentation.model.StaticNavigationItem
import com.weberbox.pifire.home.presentation.utils.isPermDrawerNavigation

@Composable
internal fun DrawerSheet(
    modifier: Modifier = Modifier,
    currentPage: Int,
    grillName: String = "",
    navigationItems: List<NavigationItem>,
    staticNavigationItems: List<StaticNavigationItem>,
    onNavigationRequested: (NavRequest) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(PaddingValues(bottom = MaterialTheme.spacing.smallOne))
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = MaterialTheme.spacing.smallTwo),
        horizontalAlignment = Alignment.Start
    ) {
        if (!isPermDrawerNavigation()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = MaterialTheme.spacing.mediumTwo),
            ) {
                IconButton(onClick = { onNavigationRequested(NavRequest.Close) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                        contentDescription = stringResource(R.string.nav_drawer_close),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.mediumTwo))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PiFireLogo(
                textStyle = MaterialTheme.typography.headlineLarge
            )
            if (grillName.isNotBlank()) {
                Text(
                    text = grillName,
                    fontWeight = FontWeight.Bold,
                    fontFamily = navFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        navigationItems.forEachIndexed { index, item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (currentPage == index)
                            FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = currentPage == index,
                onClick = { onNavigationRequested(NavRequest.PagerIndex(index)) },
                icon = {
                    Icon(
                        imageVector = if (currentPage == index)
                            item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                badge = {
                    item.badgeCount?.let {
                        Text(
                            text = item.badgeCount.toString()
                        )
                    }
                }
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
                .padding(
                    vertical = MaterialTheme.spacing.small,
                    horizontal = MaterialTheme.spacing.smallTwo
                ),
            thickness = 1.dp
        )
        staticNavigationItems.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold
                    )
                },
                selected = false,
                onClick = { onNavigationRequested(NavRequest.NavRoute(item.route)) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }
            )
        }
    }
}

sealed interface NavRequest {
    data class PagerIndex(val index: Int) : NavRequest
    data class NavRoute(val destination: Any) : NavRequest
    data object Close : NavRequest
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun DrawerContentPreview() {
    val navigationItems = buildNavigationItems()
    val staticNavigationItems = buildStaticNavigationItems()
    PiFireTheme {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            DrawerSheet(
                currentPage = 1,
                navigationItems = navigationItems,
                staticNavigationItems = staticNavigationItems,
                modifier = Modifier.safeDrawingPadding(),
                grillName = "Development",
                onNavigationRequested = {}
            )
        }
    }
}