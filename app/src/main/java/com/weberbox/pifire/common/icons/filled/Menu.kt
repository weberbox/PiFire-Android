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
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.common.icons.Icon

@Suppress("UnusedReceiverParameter")
val Icon.Filled.Menu: ImageVector
    get() {
        if (_menu != null) {
            return _menu!!
        }
        _menu = Builder(
            name = "Menu",
            defaultWidth = 18.0.dp,
            defaultHeight = 18.0.dp,
            viewportWidth = 124.0f,
            viewportHeight = 124.0f
        ).apply {
            group {
                path(
                    fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth =
                        0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter =
                        4.0f, pathFillType = NonZero
                ) {
                    moveTo(112.0f, 0.0f)
                    horizontalLineTo(12.0f)
                    curveTo(5.4f, 0.0f, 0.0f, 3.88f, 0.0f, 8.636f)
                    curveToRelative(0.0f, 4.743f, 5.4f, 8.628f, 12.0f, 8.628f)
                    horizontalLineToRelative(100.0f)
                    curveToRelative(6.6f, 0.0f, 12.0f, -3.885f, 12.0f, -8.628f)
                    curveTo(124.0f, 3.88f, 118.6f, 0.0f, 112.0f, 0.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth =
                        0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter =
                        4.0f, pathFillType = NonZero
                ) {
                    moveTo(70.903f, 46.918f)
                    horizontalLineTo(7.597f)
                    curveTo(3.418f, 46.918f, 0.0f, 50.807f, 0.0f, 55.554f)
                    curveToRelative(0.0f, 4.752f, 3.418f, 8.627f, 7.597f, 8.627f)
                    horizontalLineToRelative(63.307f)
                    curveToRelative(4.178f, 0.0f, 7.597f, -3.875f, 7.597f, -8.627f)
                    curveTo(78.5f, 50.807f, 75.081f, 46.918f, 70.903f, 46.918f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth =
                        0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter =
                        4.0f, pathFillType = NonZero
                ) {
                    moveTo(112.0f, 92.729f)
                    horizontalLineTo(12.0f)
                    curveToRelative(-6.6f, 0.0f, -12.0f, 3.893f, -12.0f, 8.645f)
                    curveTo(0.0f, 106.104f, 5.4f, 110.0f, 12.0f, 110.0f)
                    horizontalLineToRelative(100.0f)
                    curveToRelative(6.6f, 0.0f, 12.0f, -3.896f, 12.0f, -8.627f)
                    curveTo(124.0f, 96.621f, 118.6f, 92.729f, 112.0f, 92.729f)
                    close()
                }
            }
        }
            .build()
        return _menu!!
    }

@Suppress("ObjectPropertyName")
private var _menu: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icon.Filled.Menu, contentDescription = null)
    }
}
