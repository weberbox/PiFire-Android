package com.weberbox.pifire.common.icons.filled

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.common.icons.Icon

@Suppress("UnusedReceiverParameter")
val Icon.Filled.Flame: ImageVector
    @SuppressLint("SuspiciousIndentation")
    get() {
        if (_flame != null) {
            return _flame!!
        }
        _flame = Builder(
            name = "Flame", 
            defaultWidth = 24.0.dp, 
            defaultHeight = 27.0.dp, 
            viewportWidth = 448.0f, 
            viewportHeight = 512.0f
            ).apply {
                path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(159.3f, 5.4f)
                    curveToRelative(7.8f, -7.3f, 19.9f, -7.2f, 27.7f, 0.1f)
                    curveToRelative(27.6f, 25.9f, 53.5f, 53.8f, 77.7f, 84.0f)
                    curveToRelative(11.0f, -14.4f, 23.5f, -30.1f, 37.0f, -42.9f)
                    curveToRelative(7.9f, -7.4f, 20.1f, -7.4f, 28.0f, 0.1f)
                    curveToRelative(34.6f, 33.0f, 63.9f, 76.6f, 84.5f, 118.0f)
                    curveToRelative(20.3f, 40.8f, 33.8f, 82.5f, 33.8f, 111.9f)
                    curveTo(448.0f, 404.2f, 348.2f, 512.0f, 224.0f, 512.0f)
                    curveTo(98.4f, 512.0f, 0.0f, 404.1f, 0.0f, 276.5f)
                    curveToRelative(0.0f, -38.4f, 17.8f, -85.3f, 45.4f, -131.7f)
                    curveTo(73.3f, 97.7f, 112.7f, 48.6f, 159.3f, 5.4f)
                    close()
                    moveTo(225.7f, 416.0f)
                    curveToRelative(25.3f, 0.0f, 47.7f, -7.0f, 68.8f, -21.0f)
                    curveToRelative(42.1f, -29.4f, 53.4f, -88.2f, 28.1f, -134.4f)
                    curveToRelative(-4.5f, -9.0f, -16.0f, -9.6f, -22.5f, -2.0f)
                    lineToRelative(-25.2f, 29.3f)
                    curveToRelative(-6.6f, 7.6f, -18.5f, 7.4f, -24.7f, -0.5f)
                    curveToRelative(-16.5f, -21.0f, -46.0f, -58.5f, -62.8f, -79.8f)
                    curveToRelative(-6.3f, -8.0f, -18.3f, -8.1f, -24.7f, -0.1f)
                    curveToRelative(-33.8f, 42.5f, -50.8f, 69.3f, -50.8f, 99.4f)
                    curveTo(112.0f, 375.4f, 162.6f, 416.0f, 225.7f, 416.0f)
                    close()
                }
            }
            .build()
            return _flame!!
        }

    private var _flame: ImageVector? = null

    @Preview
    @Composable
    private fun Preview() {
        Box(modifier = Modifier.padding(12.dp)) {
            Image(imageVector = Icon.Filled.Flame, contentDescription = null)
        }
    }
