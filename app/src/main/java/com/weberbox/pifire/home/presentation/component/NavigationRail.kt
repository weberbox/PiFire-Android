package com.weberbox.pifire.home.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.home.presentation.utils.buildNavigationItems
import com.weberbox.pifire.home.presentation.model.NavigationItem

@Composable
internal fun NavSideRail(
    modifier: Modifier = Modifier,
    navigationItems: List<NavigationItem>,
    currentPage: Int,
    itemAlignment: Alignment.Vertical = Alignment.CenterVertically,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    onNavigationRequested: (NavRequest) -> Unit
) {
    NavigationRail(
        containerColor = containerColor,
        windowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Start +
                    WindowInsetsSides.Vertical
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(MaterialTheme.spacing.smallOne),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.smallTwo, itemAlignment
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            navigationItems.forEachIndexed { index, item ->
                NavigationRailItem(
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
                    }
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun NavigationRailPreview() {
    PiFireTheme {
        Surface {
            NavSideRail(
                navigationItems = buildNavigationItems(),
                currentPage = 1,
                onNavigationRequested = {}

            )
        }
    }
}