package com.weberbox.pifire.common.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinearLoadingIndicator(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    contentPadding: PaddingValues
) {
    AnimatedVisibility(
        visible = isLoading,
        enter = fadeEnterTransition(),
        exit = fadeExitTransition()
    ) {
        LinearWavyProgressIndicator(
            modifier = modifier
                .fillMaxWidth()
                .padding(contentPadding)
        )
    }
}