package com.weberbox.pifire.events.presentation.component

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.weberbox.pifire.common.presentation.base.cardColorStops
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.elevation
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.events.presentation.model.EventsData.Events.Event
import com.weberbox.pifire.events.presentation.screens.buildEventsList

@Composable
internal fun EventItem(
    event: Event,
    modifier: Modifier = Modifier
) {
    var overflowed by remember { mutableStateOf(false) }
    var expanded by remember(overflowed) { mutableStateOf(false) }

    Card(
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = MaterialTheme.elevation.small),
        onClick = {
            when (expanded) {
                true -> expanded = false
                false -> if (overflowed) expanded = true
            }
        }
    ) {
        Row(
            modifier = Modifier
                .background(Brush.horizontalGradient(colorStops = cardColorStops()))
                .fillMaxWidth()
                .animateContentSize()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier
                    .fillMaxHeight()
                    .size(MaterialTheme.spacing.large)
                    .background(Color(event.color.toColorInt()))
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(MaterialTheme.spacing.extraExtraSmall)
                )
            }
            Column(
                modifier = Modifier
                    .padding(MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = event.time,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(1.dp)
                )
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(1.dp)
                )
                Text(
                    text = event.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = if (expanded) 10 else 1,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            overflowed = true
                        }
                    },
                    modifier = modifier.padding(1.dp)
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun EventItemPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(10.dp)
            ) {
                EventItem(
                    event = buildEventsList()[0]
                )
            }
        }
    }
}