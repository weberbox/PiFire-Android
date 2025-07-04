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
val Icon.Outlined.ArrowRightCircle: ImageVector
    @SuppressLint("SuspiciousIndentation")
    get() {
        if (_arrowRightCircle != null) {
            return _arrowRightCircle!!
        }
        _arrowRightCircle = Builder(
            name = "ArrowRightCircle", 
            defaultWidth = 24.0.dp, 
            defaultHeight = 24.0.dp, 
            viewportWidth = 24.0f, 
            viewportHeight = 24.0f
            ).apply {
                path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(6.0f, 13.0f)
                    verticalLineTo(11.0f)
                    horizontalLineTo(14.0f)
                    lineTo(10.5f, 7.5f)
                    lineTo(11.92f, 6.08f)
                    lineTo(17.84f, 12.0f)
                    lineTo(11.92f, 17.92f)
                    lineTo(10.5f, 16.5f)
                    lineTo(14.0f, 13.0f)
                    horizontalLineTo(6.0f)
                    moveTo(22.0f, 12.0f)
                    arcTo(10.0f, 10.0f, 0.0f, false, true, 12.0f, 22.0f)
                    arcTo(10.0f, 10.0f, 0.0f, false, true, 2.0f, 12.0f)
                    arcTo(10.0f, 10.0f, 0.0f, false, true, 12.0f, 2.0f)
                    arcTo(10.0f, 10.0f, 0.0f, false, true, 22.0f, 12.0f)
                    moveTo(20.0f, 12.0f)
                    arcTo(8.0f, 8.0f, 0.0f, false, false, 12.0f, 4.0f)
                    arcTo(8.0f, 8.0f, 0.0f, false, false, 4.0f, 12.0f)
                    arcTo(8.0f, 8.0f, 0.0f, false, false, 12.0f, 20.0f)
                    arcTo(8.0f, 8.0f, 0.0f, false, false, 20.0f, 12.0f)
                    close()
                }
            }
            .build()
            return _arrowRightCircle!!
        }

    private var _arrowRightCircle: ImageVector? = null

    @Preview
    @Composable
    private fun Preview() {
        Box(modifier = Modifier.padding(12.dp)) {
            Image(imageVector = Icon.Outlined.ArrowRightCircle, contentDescription = null)
        }
    }
