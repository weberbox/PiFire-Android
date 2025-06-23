package com.weberbox.pifire.pellets.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.pellets.presentation.screens.buildPellets

@Composable
internal fun LogItem(
    log: PelletLog,
    isConnected: Boolean,
    onEventSent: (event: PelletsContract.Event) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = {
                if (isConnected) {
                    onEventSent(
                        PelletsContract.Event.DeleteLogDialog(log)
                    )
                }
            })
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.smallTwo)
                .height(MaterialTheme.size.largeThree)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = log.pelletDate,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = MaterialTheme.spacing.smallOne)
            )
            Text(
                text = log.pelletName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            RatingBar(
                value = log.pelletRating.toFloat(),
                style = RatingBarStyle.Fill(
                    activeColor = MaterialTheme.colorScheme.primary,
                    inActiveColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                size = MaterialTheme.size.smallOne,
                spaceBetween = MaterialTheme.spacing.extraSmall,
                isIndicator = true,
                onValueChange = {},
                onRatingChanged = {}
            )
            Spacer(
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.extraSmall)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun LogItemPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                LogItem(
                    log = buildPellets().logsList[0],
                    isConnected = true,
                    onEventSent = { }
                )
            }
        }
    }
}