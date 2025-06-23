package com.weberbox.pifire.pellets.presentation.component

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.size
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.pellets.presentation.screens.buildPellets

@Composable
internal fun ProfileItem(
    profile: PelletProfile,
    isConnected: Boolean,
    onEventSent: (event: PelletsContract.Event) -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = {
                if (isConnected) {
                    onEventSent(
                        PelletsContract.Event.EditProfileDialog(profile)
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
            Icon(
                imageVector = Icons.Filled.ExpandLess,
                contentDescription = stringResource(R.string.edit),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(end = MaterialTheme.spacing.smallThree)
            )
            Text(
                text = profile.brand + " " + profile.wood,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            IconButton(
                modifier = Modifier
                    .offset(x = MaterialTheme.spacing.extraSmall)
                    .size(MaterialTheme.size.largeTwo),
                onClick = {
                    if (isConnected) {
                        onEventSent(
                            PelletsContract.Event.DeleteProfileDialog(profile)
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun ProfileItemPreview() {
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.padding(MaterialTheme.spacing.smallOne)
            ) {
                ProfileItem(
                    profile = buildPellets().profilesList[0],
                    isConnected = true,
                    onEventSent = {}
                )
            }
        }
    }
}