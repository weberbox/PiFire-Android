package com.weberbox.pifire.common.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.icons.Icon
import com.weberbox.pifire.common.icons.filled.Menu
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.common.presentation.util.slideDownFadeEnterTransition
import com.weberbox.pifire.common.presentation.util.slideUpFadeExitTransition
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun AppBarConnectingTitle(
    modifier: Modifier = Modifier,
    title: String,
    isConnecting: Boolean = false,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedVisibility(
            visible = isConnecting,
            enter = slideDownFadeEnterTransition(),
            exit = slideUpFadeExitTransition()
        ) {
            AnimateDottedText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.connecting),
                dotsCount = 6,
            )
        }
        AnimatedVisibility(
            visible = !isConnecting,
            enter = fadeEnterTransition(),
            exit = fadeExitTransition(animationTime = 200)
        ) {
            AnimatedContent(title) {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = it
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
fun AppBarConnectingTitlePreview() {
    PiFireTheme {
        Surface {
            HazeAppBar(
                title = {
                    AppBarConnectingTitle(
                        title = stringResource(R.string.nav_dashboard),
                        isConnecting = true
                    )
                },
                navigationIcon = Icon.Filled.Menu,
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
                hazeState = rememberHazeState()
            )
        }
    }
}