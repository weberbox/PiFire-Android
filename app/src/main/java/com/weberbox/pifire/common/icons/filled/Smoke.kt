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
val Icon.Filled.Smoke: ImageVector
    @SuppressLint("SuspiciousIndentation")
    get() {
        if (_smoke != null) {
            return _smoke!!
        }
        _smoke = Builder(
            name = "Smoke", 
            defaultWidth = 24.0.dp, 
            defaultHeight = 24.0.dp, 
            viewportWidth = 24.0f, 
            viewportHeight = 24.0f
            ).apply {
                path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                        strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                        pathFillType = NonZero) {
                    moveTo(21.0f, 5.0f)
                    horizontalLineTo(3.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, true, 2.0f, 4.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, true, 3.0f, 3.0f)
                    horizontalLineTo(21.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, true, 22.0f, 4.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, true, 21.0f, 5.0f)
                    moveTo(20.0f, 8.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 19.0f, 7.0f)
                    horizontalLineTo(5.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 4.0f, 8.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 5.0f, 9.0f)
                    horizontalLineTo(19.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 20.0f, 8.0f)
                    moveTo(21.0f, 12.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 20.0f, 11.0f)
                    horizontalLineTo(10.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 9.0f, 12.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 10.0f, 13.0f)
                    horizontalLineTo(20.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 21.0f, 12.0f)
                    moveTo(16.0f, 16.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 15.0f, 15.0f)
                    horizontalLineTo(9.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 8.0f, 16.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 9.0f, 17.0f)
                    horizontalLineTo(15.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 16.0f, 16.0f)
                    moveTo(13.0f, 20.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 12.0f, 19.0f)
                    horizontalLineTo(10.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 9.0f, 20.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 10.0f, 21.0f)
                    horizontalLineTo(12.0f)
                    arcTo(1.0f, 1.0f, 0.0f, false, false, 13.0f, 20.0f)
                    close()
                }
            }
            .build()
            return _smoke!!
        }

    private var _smoke: ImageVector? = null

    @Preview
    @Composable
    private fun Preview() {
        Box(modifier = Modifier.padding(12.dp)) {
            Image(imageVector = Icon.Filled.Smoke, contentDescription = null)
        }
    }
