package com.weberbox.pifire.feature.landing.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.elevation
import com.weberbox.pifire.core.ui.theme.serverOnline
import com.weberbox.pifire.core.ui.theme.size
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.feature.landing.presentation.model.ServerData.Server
import com.weberbox.pifire.feature.landing.presentation.screen.buildServerData
import com.weberbox.pifire.feature.landing.presentation.util.displayName

@Composable
internal fun ServerItem(
    server: Server,
    modifier: Modifier = Modifier,
    onClick: (uuid: String) -> Unit,
    onEdit: (uuid: String) -> Unit,
    onDelete: (uuid: String, name: String) -> Unit,
) {
    val displayName = server.displayName(LocalContext.current)
    val colorStops = arrayOf(
        0.0f to MaterialTheme.colorScheme.surfaceContainerHigh,
        1f to MaterialTheme.colorScheme.surfaceContainer,
    )
    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = { onClick(server.uuid) },
    ) {
        Row(
            modifier = modifier
                .background(Brush.horizontalGradient(colorStops = colorStops))
                .fillMaxWidth()
                .padding(end = MaterialTheme.spacing.medium)
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(MaterialTheme.size.smallThree)
                    .fillMaxHeight()
                    .background(
                        if (server.online) {
                            serverOnline
                        } else {
                            MaterialTheme.colorScheme.error
                        },
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.6f)
                    .padding(MaterialTheme.spacing.smallTwo),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = server.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallTwo),
                ) {
                    Text(
                        text = stringResource(R.string.status),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (server.online) {
                            stringResource(R.string.online)
                        } else {
                            stringResource(R.string.offline)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Row(
                modifier = Modifier.weight(0.25f),
                horizontalArrangement = Arrangement.End,
            ) {
                OutlinedIconButton(
                    onClick = { onEdit(server.uuid) },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                    )
                }
                OutlinedIconButton(
                    onClick = { onDelete(server.uuid, displayName) },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ServerItemPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne),
            ) {
                ServerItem(
                    server = buildServerData().servers[0],
                    onClick = { },
                    onEdit = { },
                    onDelete = { _, _ -> },
                )
            }
        }
    }
}
