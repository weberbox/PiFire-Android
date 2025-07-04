package com.weberbox.pifire.common.presentation.sheets

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowInsetsControllerCompat
import com.composables.core.DragIndication
import com.composables.core.ModalBottomSheet
import com.composables.core.ModalBottomSheetState
import com.composables.core.Sheet
import com.composables.core.SheetDetent
import com.composables.core.rememberModalBottomSheetState
import com.composeunstyled.LocalModalWindow
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.dashboard.presentation.sheets.ModeSheet

@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    contentColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    content: @Composable () -> Unit = {}
) {
    val windowInsets by remember { mutableStateOf(WindowInsets) }
    ModalBottomSheet(state = sheetState) {
        AnimatedVisibility(
            visible = sheetState.targetDetent != SheetDetent.Hidden,
            enter = fadeEnterTransition(),
            exit = fadeExitTransition()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .focusable(false)
                    .background(Color.Black.copy(0.6f))
            )
        }
        Sheet(
            modifier = Modifier
                .windowInsetsPadding(windowInsets.ime)
                .shadow(
                    elevation = MaterialTheme.spacing.small,
                    shape = RoundedCornerShape(
                        topStart = MaterialTheme.spacing.mediumThree,
                        topEnd = MaterialTheme.spacing.mediumThree
                    ),
                )
                .clip(
                    RoundedCornerShape(
                        topStart = MaterialTheme.spacing.mediumThree,
                        topEnd = MaterialTheme.spacing.mediumThree
                    )
                )
                .widthIn(max = MaterialTheme.size.maxWidth)
                .background(contentColor)
        ) {
            val window = LocalModalWindow.current
            LaunchedEffect(Unit) {
                val windowInsetsController =
                    WindowInsetsControllerCompat(window, window.decorView)
                windowInsetsController.isAppearanceLightNavigationBars = true
            }
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        windowInsets.navigationBars.only(
                            WindowInsetsSides.Vertical
                        )
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DragIndication(
                    modifier = Modifier
                        .padding(
                            top = MaterialTheme.spacing.smallThree,
                            bottom = MaterialTheme.spacing.small
                        )
                        .background(
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            RoundedCornerShape(100)
                        )
                        .width(MaterialTheme.size.large)
                        .height(MaterialTheme.size.extraSmall)
                )
                content()
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun BottomSheetPreview() {
    PiFireTheme {
        Surface {
            BottomSheet(
                sheetState = rememberModalBottomSheetState(
                    initialDetent = SheetDetent.FullyExpanded
                ),
                content = {
                    ModeSheet(
                        currentMode = "Stop",
                        recipePaused = false,
                        startupCheck = false,
                        onEvent = {},
                        onHold = {},
                        onPrime = {}
                    )
                }
            )
        }
    }
}