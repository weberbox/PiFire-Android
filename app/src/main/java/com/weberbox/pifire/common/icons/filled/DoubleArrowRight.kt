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
val Icon.Filled.DoubleArrowRight: ImageVector
    @SuppressLint("SuspiciousIndentation")
    get() {
        if (_doubleArrowRight != null) {
            return _doubleArrowRight!!
        }
        _doubleArrowRight = Builder(
            name = "DoubleArrowRight", 
            defaultWidth = 24.0.dp, 
            defaultHeight = 24.0.dp, 
            viewportWidth = 512.0f, 
            viewportHeight = 512.0f
            ).apply {
                path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(470.6f, 278.6f)
                    curveToRelative(12.5f, -12.5f, 12.5f, -32.8f, 0.0f, -45.3f)
                    lineToRelative(-160.0f, -160.0f)
                    curveToRelative(-12.5f, -12.5f, -32.8f, -12.5f, -45.3f, 0.0f)
                    reflectiveCurveToRelative(-12.5f, 32.8f, 0.0f, 45.3f)
                    lineTo(402.7f, 256.0f)
                    lineTo(265.4f, 393.4f)
                    curveToRelative(-12.5f, 12.5f, -12.5f, 32.8f, 0.0f, 45.3f)
                    reflectiveCurveToRelative(32.8f, 12.5f, 45.3f, 0.0f)
                    lineToRelative(160.0f, -160.0f)
                    close()
                    moveTo(118.6f, 438.6f)
                    lineToRelative(160.0f, -160.0f)
                    curveToRelative(12.5f, -12.5f, 12.5f, -32.8f, 0.0f, -45.3f)
                    lineToRelative(-160.0f, -160.0f)
                    curveToRelative(-12.5f, -12.5f, -32.8f, -12.5f, -45.3f, 0.0f)
                    reflectiveCurveToRelative(-12.5f, 32.8f, 0.0f, 45.3f)
                    lineTo(210.7f, 256.0f)
                    lineTo(73.4f, 393.4f)
                    curveToRelative(-12.5f, 12.5f, -12.5f, 32.8f, 0.0f, 45.3f)
                    reflectiveCurveToRelative(32.8f, 12.5f, 45.3f, 0.0f)
                    close()
                }
            }
            .build()
            return _doubleArrowRight!!
        }

    private var _doubleArrowRight: ImageVector? = null

    @Preview
    @Composable
    private fun Preview() {
        Box(modifier = Modifier.padding(12.dp)) {
            Image(imageVector = Icon.Filled.DoubleArrowRight, contentDescription = null)
        }
    }
