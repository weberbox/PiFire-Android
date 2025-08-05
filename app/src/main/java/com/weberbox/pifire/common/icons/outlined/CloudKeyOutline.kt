package com.weberbox.pifire.common.icons.outlined

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
val Icon.Outlined.CloudKeyOutline: ImageVector
    get() {
        if (_cloudKeyOutline != null) {
            return _cloudKeyOutline!!
        }
        _cloudKeyOutline = Builder(
            name = "CloudKeyOutline",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(9.41f, 20.0f)
                horizontalLineTo(6.5f)
                curveTo(5.0f, 20.0f, 3.68f, 19.5f, 2.61f, 18.43f)
                curveTo(1.54f, 17.38f, 1.0f, 16.09f, 1.0f, 14.58f)
                curveTo(1.0f, 13.28f, 1.39f, 12.12f, 2.17f, 11.1f)
                curveTo(2.96f, 10.08f, 4.0f, 9.43f, 5.25f, 9.15f)
                curveTo(5.67f, 7.62f, 6.5f, 6.38f, 7.75f, 5.43f)
                curveTo(9.0f, 4.5f, 10.42f, 4.0f, 12.0f, 4.0f)
                curveTo(13.95f, 4.0f, 15.61f, 4.68f, 16.96f, 6.04f)
                curveTo(18.32f, 7.39f, 19.0f, 9.05f, 19.0f, 11.0f)
                curveTo(20.15f, 11.13f, 21.11f, 11.63f, 21.86f, 12.5f)
                curveTo(22.5f, 13.23f, 22.86f, 14.06f, 22.96f, 15.0f)
                horizontalLineTo(20.96f)
                curveTo(20.86f, 14.5f, 20.64f, 14.09f, 20.27f, 13.73f)
                curveTo(19.79f, 13.24f, 19.2f, 13.0f, 18.5f, 13.0f)
                horizontalLineTo(17.0f)
                verticalLineTo(11.0f)
                curveTo(17.0f, 9.62f, 16.5f, 8.44f, 15.54f, 7.46f)
                curveTo(14.57f, 6.5f, 13.39f, 6.0f, 12.0f, 6.0f)
                curveTo(10.62f, 6.0f, 9.44f, 6.5f, 8.46f, 7.46f)
                curveTo(7.5f, 8.44f, 7.0f, 9.62f, 7.0f, 11.0f)
                horizontalLineTo(6.5f)
                curveTo(5.53f, 11.0f, 4.71f, 11.34f, 4.03f, 12.03f)
                curveTo(3.34f, 12.71f, 3.0f, 13.53f, 3.0f, 14.5f)
                reflectiveCurveTo(3.34f, 16.3f, 4.03f, 17.0f)
                curveTo(4.71f, 17.67f, 5.53f, 18.0f, 6.5f, 18.0f)
                horizontalLineTo(9.0f)
                curveTo(9.0f, 18.72f, 9.15f, 19.39f, 9.41f, 20.0f)
                moveTo(23.0f, 17.0f)
                verticalLineTo(19.0f)
                horizontalLineTo(21.0f)
                verticalLineTo(21.0f)
                horizontalLineTo(19.0f)
                verticalLineTo(19.0f)
                horizontalLineTo(16.8f)
                curveTo(16.4f, 20.2f, 15.3f, 21.0f, 14.0f, 21.0f)
                curveTo(12.3f, 21.0f, 11.0f, 19.7f, 11.0f, 18.0f)
                reflectiveCurveTo(12.3f, 15.0f, 14.0f, 15.0f)
                curveTo(15.3f, 15.0f, 16.4f, 15.8f, 16.8f, 17.0f)
                horizontalLineTo(23.0f)
                moveTo(15.0f, 18.0f)
                curveTo(15.0f, 17.5f, 14.6f, 17.0f, 14.0f, 17.0f)
                reflectiveCurveTo(13.0f, 17.5f, 13.0f, 18.0f)
                reflectiveCurveTo(13.4f, 19.0f, 14.0f, 19.0f)
                reflectiveCurveTo(15.0f, 18.5f, 15.0f, 18.0f)
                close()
            }
        }
            .build()
        return _cloudKeyOutline!!
    }

@Suppress("ObjectPropertyName")
private var _cloudKeyOutline: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icon.Outlined.CloudKeyOutline, contentDescription = null)
    }
}
