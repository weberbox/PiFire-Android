package com.weberbox.pifire.home.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.home.presentation.model.NavigationItem
import com.weberbox.pifire.home.presentation.utils.buildNavigationItems

private typealias index = Int

@Composable
internal fun BottomBar(
    navigationItems: List<NavigationItem>,
    pagerState: PagerState,
    onClick: (index) -> Unit
) {
    NavigationBar(
        modifier = Modifier.clip(
                RoundedCornerShape(
                    topStart = MaterialTheme.spacing.mediumTwo,
                    topEnd = MaterialTheme.spacing.mediumTwo
                )
            )
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = pagerState.currentPage == index,
                onClick = {
                    onClick(index)
                },
                icon = {
                    BottomBarIconView(
                        isSelected = pagerState.currentPage == index,
                        selectedIcon = item.selectedIcon,
                        unselectedIcon = item.unselectedIcon,
                        title = item.title,
                        badgeCount = item.badgeCount
                    )
                },
                label = {
                    Text(
                        text = item.title
                    )
                }
            )
        }
    }
}

@Composable
private fun BottomBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeCount: Int? = null
) {
    BadgedBox(
        badge = {
            BottomBarBadgeView(badgeCount)
        }
    ) {
        Icon(
            imageVector = if (isSelected) {
                selectedIcon
            } else {
                unselectedIcon
            },
            contentDescription = title
        )
    }
}

@Composable
private fun BottomBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(
                text = count.toString()
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun BottomBarPreview() {
    val navigationItems = buildNavigationItems()
    PiFireTheme {
        Surface {
            BottomBar(
                navigationItems = navigationItems,
                pagerState = rememberPagerState(
                    initialPage = 1,
                    pageCount = { navigationItems.size }
                ),
                onClick = { }
            )
        }
    }
}