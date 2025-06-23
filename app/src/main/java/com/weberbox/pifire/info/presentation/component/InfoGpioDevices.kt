package com.weberbox.pifire.info.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeviceHub
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
import com.weberbox.pifire.info.presentation.model.GPIODeviceData
import com.weberbox.pifire.info.presentation.model.InfoData.Info

@Composable
internal fun InfoGpioDevices(
    info: Info
) {
    HeaderCard(
        title = stringResource(R.string.info_gpio_device),
        headerIcon = Icons.Outlined.DeviceHub
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = MaterialTheme.spacing.smallTwo,
                    end = MaterialTheme.spacing.smallTwo
                )
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.spacing.smallOne),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = stringResource(R.string.info_gpio_device_name),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.info_gpio_device_function),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.info_gpio_device_pin),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
            }
            info.devPins.forEach { pin ->
                Row(modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)) {
                    Text(
                        text = pin.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                    )
                    Text(
                        text = pin.function,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = pin.pin,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun InfoGpioDevicesPreview() {
    val info = remember {
        Info(
            devPins = listOf(
                GPIODeviceData(
                    name = "Input",
                    function = "enter_sw",
                    pin = "21"
                ),
                GPIODeviceData(
                    name = "",
                    function = "up_clk",
                    pin = "16"
                )
            )
        )
    }
    PiFireTheme {
        Surface {
            InfoGpioDevices(
                info = info
            )
        }
    }
}