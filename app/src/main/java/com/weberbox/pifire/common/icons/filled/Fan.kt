package com.weberbox.pifire.common.icons.filled

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
val Icon.Filled.Fan: ImageVector
    get() {
        if (_fan != null) {
            return _fan!!
        }
        _fan = Builder(
            name = "Fan",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 512.0f,
            viewportHeight = 512.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(258.6f, 0.0f)
                curveToRelative(-1.7f, 0.0f, -3.4f, 0.1f, -5.1f, 0.5f)
                curveTo(168.0f, 17.0f, 115.6f, 102.3f, 130.5f, 189.3f)
                curveToRelative(2.9f, 17.0f, 8.4f, 32.9f, 15.9f, 47.4f)
                lineTo(32.0f, 224.0f)
                lineToRelative(-2.6f, 0.0f)
                curveTo(13.2f, 224.0f, 0.0f, 237.2f, 0.0f, 253.4f)
                curveToRelative(0.0f, 1.7f, 0.1f, 3.4f, 0.5f, 5.1f)
                curveTo(17.0f, 344.0f, 102.3f, 396.4f, 189.3f, 381.5f)
                curveToRelative(17.0f, -2.9f, 32.9f, -8.4f, 47.4f, -15.9f)
                lineTo(224.0f, 480.0f)
                lineToRelative(0.0f, 2.6f)
                curveToRelative(0.0f, 16.2f, 13.2f, 29.4f, 29.4f, 29.4f)
                curveToRelative(1.7f, 0.0f, 3.4f, -0.1f, 5.1f, -0.5f)
                curveTo(344.0f, 495.0f, 396.4f, 409.7f, 381.5f, 322.7f)
                curveToRelative(-2.9f, -17.0f, -8.4f, -32.9f, -15.9f, -47.4f)
                lineTo(480.0f, 288.0f)
                lineToRelative(2.6f, 0.0f)
                curveToRelative(16.2f, 0.0f, 29.4f, -13.2f, 29.4f, -29.4f)
                curveToRelative(0.0f, -1.7f, -0.1f, -3.4f, -0.5f, -5.1f)
                curveTo(495.0f, 168.0f, 409.7f, 115.6f, 322.7f, 130.5f)
                curveToRelative(-17.0f, 2.9f, -32.9f, 8.4f, -47.4f, 15.9f)
                lineTo(288.0f, 32.0f)
                lineToRelative(0.0f, -2.6f)
                curveTo(288.0f, 13.2f, 274.8f, 0.0f, 258.6f, 0.0f)
                close()
                moveTo(256.0f, 224.0f)
                arcToRelative(
                    32.0f, 32.0f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = 64.0f
                )
                arcToRelative(
                    32.0f, 32.0f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 0.0f,
                    dy1 = -64.0f
                )
                close()
            }
        }
            .build()
        return _fan!!
    }

@Suppress("ObjectPropertyName")
private var _fan: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icon.Filled.Fan, contentDescription = null)
    }
}
