package com.weberbox.pifire.feature.info.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeveloperBoard
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
import com.weberbox.pifire.core.model.info.GPIOInOutData
import com.weberbox.pifire.core.model.info.InfoData.Info
import com.weberbox.pifire.core.ui.components.HeaderCard
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.theme.spacing

@Composable
internal fun InfoGpioInOut(
    info: Info,
) {
    HeaderCard(
        title = stringResource(R.string.info_gpio_in_out),
        headerIcon = Icons.Outlined.DeveloperBoard,
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = MaterialTheme.spacing.smallTwo,
                    end = MaterialTheme.spacing.smallTwo,
                )
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.smallOne),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = stringResource(R.string.info_gpio_output_name),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.info_gpio_output_pin),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }
            info.outPins.forEach { pin ->
                Row(modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)) {
                    Text(
                        text = pin.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = pin.pin,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.smallOne),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Text(
                    text = stringResource(R.string.info_gpio_input_name),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.info_gpio_input_pin),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
            }
            info.inPins.forEach { pin ->
                Row(modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)) {
                    Text(
                        text = pin.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = pin.pin,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoGpioInOutPreview() {
    val info = remember {
        Info(
            outPins = listOf(
                GPIOInOutData(
                    name = "fan",
                    pin = "15",
                ),
                GPIOInOutData(
                    name = "dc_fan",
                    pin = "26",
                ),
                GPIOInOutData(
                    name = "pwm",
                    pin = "13",
                ),
                GPIOInOutData(
                    name = "igniter",
                    pin = "18",
                ),
            ),
            inPins = listOf(
                GPIOInOutData(
                    name = "selector",
                    pin = "17",
                ),
                GPIOInOutData(
                    name = "shutdown",
                    pin = "",
                ),
            ),
        )
    }
    PiFireTheme {
        Surface {
            InfoGpioInOut(
                info = info,
            )
        }
    }
}
