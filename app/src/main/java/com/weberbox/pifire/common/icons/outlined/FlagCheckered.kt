package com.weberbox.pifire.common.icons.outlined

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
val Icon.Outlined.FlagCheckered: ImageVector
    @SuppressLint("SuspiciousIndentation")
    get() {
        if (_flagCheckered != null) {
            return _flagCheckered!!
        }
        _flagCheckered = Builder(
            name = "FlagCheckered", 
            defaultWidth = 24.0.dp, 
            defaultHeight = 24.0.dp, 
            viewportWidth = 24.0f, 
            viewportHeight = 24.0f
            ).apply {
                path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(14.4f, 6.0f)
                    horizontalLineTo(20.0f)
                    verticalLineTo(16.0f)
                    horizontalLineTo(13.0f)
                    lineTo(12.6f, 14.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(21.0f)
                    horizontalLineTo(5.0f)
                    verticalLineTo(4.0f)
                    horizontalLineTo(14.0f)
                    lineTo(14.4f, 6.0f)
                    moveTo(14.0f, 14.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(18.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(10.0f)
                    lineTo(13.0f, 8.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(6.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(7.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(9.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(10.0f)
                    lineTo(14.0f, 12.0f)
                    verticalLineTo(14.0f)
                    moveTo(11.0f, 10.0f)
                    verticalLineTo(8.0f)
                    horizontalLineTo(13.0f)
                    verticalLineTo(10.0f)
                    horizontalLineTo(11.0f)
                    moveTo(14.0f, 10.0f)
                    horizontalLineTo(16.0f)
                    verticalLineTo(12.0f)
                    horizontalLineTo(14.0f)
                    verticalLineTo(10.0f)
                    close()
                }
            }
            .build()
            return _flagCheckered!!
        }

    private var _flagCheckered: ImageVector? = null

    @Preview
    @Composable
    private fun Preview() {
        Box(modifier = Modifier.padding(12.dp)) {
            Image(imageVector = Icon.Outlined.FlagCheckered, contentDescription = null)
        }
    }
