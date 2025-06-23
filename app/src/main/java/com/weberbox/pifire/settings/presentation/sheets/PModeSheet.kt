package com.weberbox.pifire.settings.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.settings.presentation.model.PMode

@Composable
internal fun PModeSheet(
    augerOn: String
) {
    val context = LocalContext.current
    val pmodes = context.resources.getStringArray(R.array.pmode_values).toList()
    val pmodeTimes = context.resources.getStringArray(R.array.pmode_times).toList()
    val pmodesList = createPModeList(augerOn, pmodes, pmodeTimes)

    Column(
        modifier = Modifier.padding(
            start = MaterialTheme.spacing.mediumOne,
            end = MaterialTheme.spacing.mediumOne
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.extraSmallOne),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.settings_pmode_mode),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.extraSmallOne)
                    .weight(1f)
            )
            Text(
                text = stringResource(R.string.settings_pmode_auger_on),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.extraSmallOne)
                    .weight(1f)
            )
            Text(
                text = stringResource(R.string.settings_pmode_auger_off),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.extraSmallOne)
                    .weight(1f)
            )
        }
        LazyColumn {
            itemsIndexed(pmodesList) { _, item ->
                Card(
                    modifier = Modifier.padding(
                        bottom = MaterialTheme.spacing.extraSmall
                    ),
                    shape = MaterialTheme.shapes.small
                ) {
                    Row(
                        modifier = Modifier
                            .padding(MaterialTheme.spacing.smallOne)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.pMode,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                        Text(
                            text = item.augerOn,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                        Text(
                            text = item.augerOff,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

private fun createPModeList(
    augerOn: String,
    pmodes: List<String>,
    pmodeTimes: List<String>
): List<PMode> {
    val pModeList: MutableList<PMode> = mutableListOf()
    for (i in pmodes.indices) {
        val mode = PMode(
            pmodes[i], augerOn,
            pmodeTimes[i]
        )
        pModeList.add(mode)
    }
    return pModeList
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PModeSheetPreview() {
    PiFireTheme {
        Surface {
            PModeSheet(augerOn = "5")
        }
    }
}