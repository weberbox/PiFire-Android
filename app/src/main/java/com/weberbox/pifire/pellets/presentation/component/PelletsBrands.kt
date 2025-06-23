package com.weberbox.pifire.pellets.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.component.HeaderCard
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets
import com.weberbox.pifire.pellets.presentation.screens.buildPellets

@Composable
internal fun PelletsBrands(
    pellets: Pellets,
    isConnected: Boolean,
    onEventSent: (event: PelletsContract.Event) -> Unit,
) {
    val limit by remember { mutableIntStateOf(AppConfig.LIST_VIEW_LIMIT) }
    HeaderCard(
        title = stringResource(R.string.pellets_brands),
        headerIcon = Icons.Outlined.EditNote,
        buttonIcon = Icons.Filled.Add,
        listSize = pellets.brandsList.size,
        onButtonClick = {
            if (isConnected) {
                onEventSent(PelletsContract.Event.AddBrandDialog)
            }
        },
        viewAllClick = {
            onEventSent(PelletsContract.Event.BrandsViewAll)
        }
    ) {
        Column(
            modifier = Modifier
                .offset(y = (-5).dp)
                .fillMaxWidth(),
        ) {
            for (brand in pellets.brandsList.take(limit)) {
                WoodBrandItem(
                    item = brand,
                    onItemDelete = {
                        if (isConnected) {
                            onEventSent(
                                PelletsContract.Event.DeleteBrandDialog(brand)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PelletsBrandPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                PelletsBrands(
                    pellets = buildPellets(),
                    isConnected = true,
                    onEventSent = {}
                )
            }
        }
    }
}