package com.weberbox.pifire.pellets.presentation.sheets

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Park
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.outlinedTextFieldColors
import com.weberbox.pifire.common.presentation.component.DropdownTextField
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.theme.spacing
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.pellets.presentation.model.ProfileRatings
import com.weberbox.pifire.pellets.presentation.model.ProfilesData
import com.weberbox.pifire.pellets.presentation.screens.buildPellets

@Composable
internal fun ProfilesEditSheet(
    title: String,
    profilesData: ProfilesData,
    onConfirm: (PelletProfile, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentBrand by remember { mutableStateOf(profilesData.currentBrand) }
    var currentWood by remember { mutableStateOf(profilesData.currentWood) }
    var rating by remember { mutableIntStateOf(profilesData.rating) }
    var comments by remember { mutableStateOf(profilesData.comments) }
    var pelletProfile by remember { mutableStateOf(PelletProfile()) }

    Column(
        modifier = Modifier
            .padding(
                start = MaterialTheme.spacing.mediumOne,
                end = MaterialTheme.spacing.mediumOne,
                bottom = MaterialTheme.spacing.smallThree
            )
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = MaterialTheme.spacing.smallOne)
        )
        DropdownTextField(
            modifier = Modifier.fillMaxWidth(),
            selectedValue = currentBrand,
            options = profilesData.brands,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.LocalOffer,
                    contentDescription = stringResource(R.string.pellets_brand)
                )
            },
            onValueChangedEvent = { currentBrand = it }
        )
        DropdownTextField(
            modifier = Modifier.fillMaxWidth(),
            selectedValue = currentWood,
            options = profilesData.woods,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Park,
                    contentDescription = stringResource(R.string.pellets_wood)
                )
            },
            onValueChangedEvent = { currentWood = it }
        )
        DropdownTextField(
            modifier = Modifier
                .padding(bottom = MaterialTheme.spacing.smallOne)
                .fillMaxWidth(),
            selectedValue = ProfileRatings.from(rating).string,
            options = ProfileRatings.entries.map { it.string },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.StarRate,
                    contentDescription = stringResource(R.string.pellets_rating)
                )
            },
            onValueChangedEvent = { rating = ProfileRatings.get(it).rating }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = comments,
            shape = MaterialTheme.shapes.small,
            colors = outlinedTextFieldColors(),
            minLines = 3,
            onValueChange = { comments = it },
            placeholder = { Text(text = stringResource(R.string.pellets_comments)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.mediumTwo),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { onDismiss() },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(end = MaterialTheme.spacing.smallOne)
                    .weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold
                )
            }
            if (profilesData.id.isBlank()) {
                OutlinedButton(
                    onClick = {
                        pelletProfile = PelletProfile(
                            brand = currentBrand,
                            wood = currentWood,
                            rating = rating,
                            comments = comments,
                            id = profilesData.id
                        )
                        onConfirm(pelletProfile, true)
                    },
                    enabled = currentBrand.isNotBlank() && currentWood.isNotBlank(),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .padding(end = MaterialTheme.spacing.smallOne)
                        .weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.load),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                onClick = {
                    pelletProfile = PelletProfile(
                        brand = currentBrand,
                        wood = currentWood,
                        rating = rating,
                        comments = comments,
                        id = profilesData.id
                    )
                    onConfirm(pelletProfile, false)
                },
                enabled = currentBrand.isNotBlank() && currentWood.isNotBlank(),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun ProfilesEditSheetPreview() {
    val profilesData by remember {
        mutableStateOf(
            ProfilesData(
                brands = buildPellets().brandsList,
                woods = buildPellets().woodsList
            )
        )
    }
    PiFireTheme {
        Surface {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                ProfilesEditSheet(
                    title = stringResource(R.string.pellets_add_profile),
                    profilesData = profilesData,
                    onConfirm = { _,_ -> },
                    onDismiss = {}
                )
            }
        }
    }
}