package com.weberbox.pifire.feature.events.presentation.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.events.EventType
import com.weberbox.pifire.core.model.events.EventsData.Events.Event
import com.weberbox.pifire.core.ui.base.cardColorStops
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.elevation
import com.weberbox.pifire.core.ui.theme.eventsDebug
import com.weberbox.pifire.core.ui.theme.eventsError
import com.weberbox.pifire.core.ui.theme.eventsInfo
import com.weberbox.pifire.core.ui.theme.eventsModeEnded
import com.weberbox.pifire.core.ui.theme.eventsModeStarted
import com.weberbox.pifire.core.ui.theme.eventsScriptEnded
import com.weberbox.pifire.core.ui.theme.eventsScriptStart
import com.weberbox.pifire.core.ui.theme.eventsWarning
import com.weberbox.pifire.core.ui.theme.spacing
import com.weberbox.pifire.feature.events.presentation.screen.buildEventsList

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
                    .background(event.type.color())
            ) {
                Text(
                    text = stringResource(event.type.badgeRes()),
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


private fun EventType.color(): Color = when (this) {
    EventType.ScriptStarted -> eventsScriptStart
    EventType.ScriptEnded -> eventsScriptEnded
    EventType.ModeStarted -> eventsModeStarted
    EventType.ModeEnded -> eventsModeEnded
    EventType.Error -> eventsError
    EventType.Warning -> eventsWarning
    EventType.Debug -> eventsDebug
    EventType.Info -> eventsInfo
}

private fun EventType.badgeRes(): Int = when (this) {
    EventType.ScriptStarted, EventType.ScriptEnded -> R.string.events_badge_script
    EventType.ModeStarted, EventType.ModeEnded -> R.string.events_badge_mode
    EventType.Error -> R.string.events_badge_error
    EventType.Warning -> R.string.events_badge_warning
    EventType.Debug -> R.string.events_badge_debug
    EventType.Info -> R.string.events_badge_info
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