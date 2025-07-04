package com.weberbox.pifire.setup.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SetupBottomNavRow(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector = Icons.AutoMirrored.Default.ArrowForward,
    description: String? = null,
    onBackClick: (() -> Unit)? = null,
    onNextClick: () -> Unit
) {
    Box(
        modifier.padding(
            bottom = MaterialTheme.spacing.smallThree,
            end = MaterialTheme.spacing.smallThree
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBackClick != null) {
                TextButton(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.smallThree),
                    onClick = onBackClick
                ) {
                    Text(
                        text = stringResource(R.string.back),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            FilledIconButton(
                modifier = Modifier
                    .height(MaterialTheme.size.fabSizeNormal)
                    .width(MaterialTheme.size.fabLargeWidth),
                onClick = onNextClick,
                shapes = IconButtonShapes(MaterialTheme.shapes.large),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                enabled = enabled
            ) {
                Row {
                    Text(
                        text = stringResource(R.string.next),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.extraSmallOne))
                    Icon(
                        imageVector = icon,
                        contentDescription = description
                    )
                }

            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun FloatingNavButtonPreview() {
    PiFireTheme {
        Surface {
            Column {
                SetupBottomNavRow(
                    onBackClick = {},
                    onNextClick = {}
                )
            }
        }
    }
}