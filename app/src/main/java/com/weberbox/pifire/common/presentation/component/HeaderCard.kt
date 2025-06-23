package com.weberbox.pifire.common.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.info.presentation.component.LicenseItem
import com.weberbox.pifire.info.presentation.screens.buildLicenseData

@Composable
fun HeaderCard(
    modifier: Modifier = Modifier,
    title: String,
    headerIcon: ImageVector,
    buttonIcon: ImageVector = Icons.Filled.Add,
    color: Color = MaterialTheme.colorScheme.tertiary,
    listSize: Int = AppConfig.LIST_VIEW_LIMIT,
    listLimit: Int = AppConfig.LIST_VIEW_LIMIT,
    onButtonClick: (() -> Unit)? = null,
    viewAllClick: (() -> Unit)? = null,
    headerBottomPadding: Dp = MaterialTheme.spacing.smallOne,
    content: @Composable () -> Unit = {},
) {
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(MaterialTheme.size.largeThree)
                    .clickable(
                        enabled = onButtonClick != null,
                        onClick = {
                            onButtonClick?.invoke()
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.padding(start = MaterialTheme.spacing.smallTwo),
                    imageVector = headerIcon,
                    contentDescription = null,
                    tint = color
                )
                Text(
                    text = title,
                    modifier = Modifier.padding(start = MaterialTheme.spacing.smallOne),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                if (onButtonClick != null) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .size(MaterialTheme.size.largeTwo)
                    ) {
                        Icon(
                            imageVector =  buttonIcon,
                            contentDescription = null,
                            tint = color
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .padding(end = MaterialTheme.spacing.small)
                            .fillMaxHeight()
                    )
                }
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.padding(bottom = headerBottomPadding),
                thickness = 1.dp
            )
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = modifier.fillMaxWidth()
                ) {
                    content()
                }
                if (viewAllClick != null && listSize >= listLimit) {
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.size.extraLargeThree)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .height(MaterialTheme.size.largeTwo)
                                .clip(MaterialTheme.shapes.small)
                                .clickable(onClick = { viewAllClick() }),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = modifier.fillMaxWidth(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.view_all),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = color,
                                )
                                Icon(
                                    imageVector = Icons.Outlined.ArrowCircleRight,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier
                                        .size(MaterialTheme.size.mediumTwo)
                                        .padding(start = MaterialTheme.spacing.small)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun HeaderCardPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.smallOne,
                    end = MaterialTheme.spacing.smallOne,
                    top = MaterialTheme.spacing.extraSmall,
                    bottom = MaterialTheme.spacing.extraSmall
                )
            ) {
                HeaderCard(
                    title = "Test Title",
                    headerIcon = Icons.Outlined.Description,
                    buttonIcon = Icons.Filled.Add,
                    onButtonClick = { },
                    viewAllClick = { }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(
                                start = MaterialTheme.spacing.smallTwo,
                                end = MaterialTheme.spacing.smallTwo,
                                bottom = MaterialTheme.spacing.smallOne
                            )
                            .fillMaxWidth()
                    ) {
                        buildLicenseData().list.forEach { item ->
                            LicenseItem(item) {}
                        }
                    }
                }
            }
        }
    }
}
