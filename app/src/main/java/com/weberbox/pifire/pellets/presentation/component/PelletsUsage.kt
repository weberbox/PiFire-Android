package com.weberbox.pifire.pellets.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets

@Composable
internal fun PelletsUsage(
    pellets: Pellets,
) {
    HeaderCard(
        title = stringResource(R.string.pellets_usage),
        headerIcon = Icons.Filled.KeyboardDoubleArrowRight
    ) {
        Row(
            modifier = Modifier
                .padding(
                    bottom = MaterialTheme.spacing.smallThree,
                    top = MaterialTheme.spacing.extraSmall,
                    start = MaterialTheme.spacing.mediumOne,
                    end = MaterialTheme.spacing.mediumOne
                )
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = pellets.usageImperial,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = pellets.usageMetric,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PelletsUsagePreview() {
    val pellets = remember {
        Pellets(
            usageImperial = "0.16 oz",
            usageMetric = "4.51 g",
        )
    }
    PiFireTheme {
        Surface {
            PelletsUsage(
                pellets = pellets
            )
        }
    }
}