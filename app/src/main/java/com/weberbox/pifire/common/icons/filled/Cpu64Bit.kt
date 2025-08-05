package com.weberbox.pifire.common.icons.filled

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.common.icons.Icon

@Suppress("UnusedReceiverParameter")
val Icon.Filled.Cpu64Bit: ImageVector
    get() {
        if (_Cpu64Bit != null) {
            return _Cpu64Bit!!
        }
        _Cpu64Bit = ImageVector.Builder(
            name = "Cpu64Bit",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9f, 3f)
                verticalLineTo(5f)
                horizontalLineTo(7f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, 7f)
                verticalLineTo(9f)
                horizontalLineTo(3f)
                verticalLineTo(11f)
                horizontalLineTo(5f)
                verticalLineTo(13f)
                horizontalLineTo(3f)
                verticalLineTo(15f)
                horizontalLineTo(5f)
                verticalLineTo(17f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7f, 19f)
                horizontalLineTo(9f)
                verticalLineTo(21f)
                horizontalLineTo(11f)
                verticalLineTo(19f)
                horizontalLineTo(13f)
                verticalLineTo(21f)
                horizontalLineTo(15f)
                verticalLineTo(19f)
                horizontalLineTo(17f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 19f, 17f)
                verticalLineTo(15f)
                horizontalLineTo(21f)
                verticalLineTo(13f)
                horizontalLineTo(19f)
                verticalLineTo(11f)
                horizontalLineTo(21f)
                verticalLineTo(9f)
                horizontalLineTo(19f)
                verticalLineTo(7f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 17f, 5f)
                horizontalLineTo(15f)
                verticalLineTo(3f)
                horizontalLineTo(13f)
                verticalLineTo(5f)
                horizontalLineTo(11f)
                verticalLineTo(3f)
                moveTo(8f, 9f)
                horizontalLineTo(11.5f)
                verticalLineTo(10.5f)
                horizontalLineTo(8.5f)
                verticalLineTo(11.25f)
                horizontalLineTo(10.5f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11.5f, 12.25f)
                verticalLineTo(14f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10.5f, 15f)
                horizontalLineTo(8f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7f, 14f)
                verticalLineTo(10f)
                arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 9f)
                moveTo(12.5f, 9f)
                horizontalLineTo(14f)
                verticalLineTo(11f)
                horizontalLineTo(15.5f)
                verticalLineTo(9f)
                horizontalLineTo(17f)
                verticalLineTo(15f)
                horizontalLineTo(15.5f)
                verticalLineTo(12.5f)
                horizontalLineTo(12.5f)
                moveTo(8.5f, 12.75f)
                verticalLineTo(13.5f)
                horizontalLineTo(10f)
                verticalLineTo(12.75f)
            }
        }.build()

        return _Cpu64Bit!!
    }

@Suppress("ObjectPropertyName")
private var _Cpu64Bit: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = Icon.Filled.Cpu64Bit, contentDescription = null)
    }
}