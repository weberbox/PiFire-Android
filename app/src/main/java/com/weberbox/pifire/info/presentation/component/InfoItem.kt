package com.weberbox.pifire.info.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.weberbox.pifire.common.presentation.theme.spacing

@Composable
internal fun InfoItemRow(
    title: String,
    summary: String
) {
    Row(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.smallOne
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun InfoItemColumn(
    title: String,
    summary: String
) {
    Column(
        modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne),
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.smallOne
        )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}