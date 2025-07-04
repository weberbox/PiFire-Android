package com.weberbox.pifire.changelog.presentation.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.gradientBackground

@Composable
internal fun LogAnimation(
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true
) {
    var isAnimationVisible by rememberSaveable { mutableStateOf(showAnimation) }

    AnimatedVisibility(
        visible = isAnimationVisible,
        exit = scaleOut(
            targetScale = 4.0f,
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            )
        ) + fadeOut(
            animationSpec = tween(
                durationMillis = 100,
                easing = LinearEasing
            )
        )
    ) {
        Animation(
            modifier = modifier
                .fillMaxSize()
                .size(370.dp)
                .background(gradientBackground())
        ) {
            isAnimationVisible = false
        }
    }
}

@Composable
private fun Animation(
    modifier: Modifier = Modifier,
    onAnimationFinished: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.changelog_anim
        )
    )

    val progress = animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LaunchedEffect(progress.progress) {
        if (progress.progress == 1f) {
            onAnimationFinished()
        }
    }

    LottieAnimation(
        composition = composition,
        progress = { progress.value },
        modifier = modifier
    )
}