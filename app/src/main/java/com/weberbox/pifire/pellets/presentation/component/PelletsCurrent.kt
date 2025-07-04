package com.weberbox.pifire.pellets.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarStyle
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.pellets.presentation.screens.buildPellets

@Composable
internal fun PelletsCurrent(
    pellets: Pellets,
    isConnected: Boolean,
    onEventSent: (event: PelletsContract.Event) -> Unit,
) {
    HeaderCard(
        title = stringResource(R.string.dash_state_current),
        headerIcon = Icons.Filled.Park,
        buttonIcon = Icons.Filled.SaveAlt,
        onButtonClick = {
            if (isConnected) {
                onEventSent(PelletsContract.Event.CurrentDialog(pellets.currentPelletId))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(
                    top = MaterialTheme.spacing.extraSmallOne,
                    start = MaterialTheme.spacing.smallTwo,
                    end = MaterialTheme.spacing.smallTwo
                )
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree)
            ) {
                Text(
                    text = stringResource(R.string.pellets_current_brand),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pellets.currentBrand,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree)
            ) {
                Text(
                    text = stringResource(R.string.pellets_current_wood),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pellets.currentWood,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree)
            ) {
                Text(
                    text = stringResource(R.string.pellets_current_rating),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                RatingBar(
                    value = pellets.currentRating.toFloat(),
                    style = RatingBarStyle.Fill(
                        activeColor = MaterialTheme.colorScheme.primary,
                        inActiveColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    size = MaterialTheme.spacing.smallOne,
                    spaceBetween = MaterialTheme.spacing.extraSmall,
                    isIndicator = true,
                    onValueChange = {},
                    onRatingChanged = {}
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smallThree)
            ) {
                Text(
                    text = stringResource(R.string.pellets_current_date),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pellets.dateLoaded,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(
                modifier = Modifier
                    .padding(bottom = MaterialTheme.spacing.smallThree)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.pellets_current_comments),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = pellets.currentComments,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.smallOne)
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PelletsCurrentPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                PelletsCurrent(
                    pellets = buildPellets(),
                    isConnected = true,
                    onEventSent = {}
                )
            }
        }
    }
}