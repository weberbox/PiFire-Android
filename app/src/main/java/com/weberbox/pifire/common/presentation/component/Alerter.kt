package com.weberbox.pifire.common.presentation.component

import android.app.Activity
import android.view.Gravity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.tapadoo.alerter.Alerter
import com.weberbox.pifire.R

@Composable
fun Alerter(
    isShown: Boolean, onChanged: (isShown: Boolean) -> Unit,
    backgroundColor: Color = Color.Transparent,
    duration: Long = 3000,
    enableVibration: Boolean = true,
    enableSwipeToDismiss: Boolean = false,
    disableOutsideTouch: Boolean = false,
    enableInfiniteDuration: Boolean = false,
    gravity: Int = Gravity.TOP,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = isShown) {

        if (isShown) {
            Alerter.create(context as Activity, R.layout.compose_view)
                .enableVibration(enableVibration)
                .enableInfiniteDuration(enableInfiniteDuration)
                .setLayoutGravity(gravity)
                .setBackgroundColorInt(backgroundColor.toArgb())
                .also { alerter ->
                    if (enableSwipeToDismiss) alerter.enableSwipeToDismiss()
                    if (disableOutsideTouch) alerter.disableOutsideTouch()

                    val customView: ComposeView? =
                        alerter.getLayoutContainer()?.findViewById(R.id.composeView)
                    customView?.apply {
                        setViewCompositionStrategy(
                            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                        )
                        setContent {
                            MaterialTheme {
                                content()
                            }
                        }
                    }
                }
                .setOnShowListener {
                    onChanged(true)
                }
                .setOnHideListener {
                    onChanged(false)
                }
                .setDuration(duration)
                .show()
        } else {
            if (Alerter.isShowing) Alerter.hide()
        }
    }


}

fun Modifier.iconPulse() = composed {

    val transition = rememberInfiniteTransition(label = "")

    val animateScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 820, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Modifier
        .scale(animateScale)

}