package com.weberbox.pifire.feature.pellets.presentation.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.core.model.pellets.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.core.ui.components.sheets.BottomSheet
import com.weberbox.pifire.core.ui.components.sheets.CircularPickerSheet
import com.weberbox.pifire.core.ui.components.sheets.ConfirmSheet
import com.weberbox.pifire.core.ui.components.sheets.InputValidationSheet
import com.weberbox.pifire.core.ui.state.CustomModalBottomSheetState
import com.weberbox.pifire.core.ui.state.InputModalBottomSheetState
import com.weberbox.pifire.feature.pellets.presentation.components.sheets.ProfilesEditSheet
import com.weberbox.pifire.feature.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.feature.pellets.presentation.model.PelletsEvent
import com.weberbox.pifire.feature.pellets.presentation.model.ProfilesData

@Composable
internal fun PelletsBottomSheets(
    state: PelletsContract.State,
    currentSheet: CustomModalBottomSheetState,
    brandAddSheet: CustomModalBottomSheetState,
    brandDeleteSheet: InputModalBottomSheetState<String>,
    woodAddSheet: CustomModalBottomSheetState,
    woodDeleteSheet: InputModalBottomSheetState<String>,
    profileAddSheet: InputModalBottomSheetState<ProfilesData>,
    profileEditSheet: InputModalBottomSheetState<ProfilesData>,
    profileDeleteSheet: InputModalBottomSheetState<PelletProfile>,
    logDeleteSheet: InputModalBottomSheetState<PelletLog>,
    onEventSent: (event: PelletsContract.Event) -> Unit,
) {
    BottomSheet(
        sheetState = currentSheet.sheetState,
    ) {
        val profiles = state.pellets.profilesList
        val initialProfile = profiles.find { it.id == state.pellets.currentPelletId }
            ?: profiles.firstOrNull()
        // No profiles means nothing to pick; the picker can't start on an empty list
        if (initialProfile != null) {
            CircularPickerSheet(
                items = profiles,
                initialItem = initialProfile,
                itemToString = { it.brand + " " + it.wood },
                positiveButtonText = stringResource(R.string.load),
                onNegative = { currentSheet.close() },
                onPositive = {
                    onEventSent(PelletsContract.Event.SendEvent(PelletsEvent.LoadProfile(it.id)))
                    currentSheet.close()
                }
            )
        }
    }
    BottomSheet(
        sheetState = brandAddSheet.sheetState
    ) {
        InputValidationSheet(
            input = "",
            title = stringResource(R.string.pellets_add_brand),
            placeholder = stringResource(R.string.pellets_brand),
            onUpdate = {
                onEventSent(PelletsContract.Event.SendEvent(PelletsEvent.AddBrand(it)))
                brandAddSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = brandDeleteSheet.sheetState
    ) {
        ConfirmSheet(
            title = stringResource(R.string.dialog_confirm_action),
            message = stringResource(
                R.string.dialog_confirm_delete_item,
                brandDeleteSheet.data
            ),
            negativeButtonText = stringResource(R.string.cancel),
            positiveButtonText = stringResource(R.string.delete),
            positiveButtonColor = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onPositive = {
                onEventSent(
                    PelletsContract.Event.SendEvent(
                        PelletsEvent.DeleteBrand(brand = brandDeleteSheet.data)
                    )
                )
                brandDeleteSheet.close()
            },
            onNegative = { brandDeleteSheet.close() }
        )
    }
    BottomSheet(
        sheetState = woodAddSheet.sheetState
    ) {
        InputValidationSheet(
            input = "",
            title = stringResource(R.string.pellets_add_wood),
            placeholder = stringResource(R.string.pellets_wood),
            onUpdate = {
                onEventSent(PelletsContract.Event.SendEvent(PelletsEvent.AddWood(it)))
                woodAddSheet.close()
            }
        )
    }
    BottomSheet(
        sheetState = woodDeleteSheet.sheetState
    ) {
        ConfirmSheet(
            title = stringResource(R.string.dialog_confirm_action),
            message = stringResource(
                R.string.dialog_confirm_delete_item,
                woodDeleteSheet.data
            ),
            negativeButtonText = stringResource(R.string.cancel),
            positiveButtonText = stringResource(R.string.delete),
            positiveButtonColor = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onPositive = {
                onEventSent(
                    PelletsContract.Event.SendEvent(
                        PelletsEvent.DeleteWood(wood = woodDeleteSheet.data)
                    )
                )
                woodDeleteSheet.close()
            },
            onNegative = { woodDeleteSheet.close() }
        )
    }
    BottomSheet(
        sheetState = profileAddSheet.sheetState
    ) {
        ProfilesEditSheet(
            title = stringResource(R.string.pellets_add_profile),
            profilesData = profileAddSheet.data,
            onConfirm = { profile, load ->
                onEventSent(
                    PelletsContract.Event.SendEvent(
                        PelletsEvent.AddProfile(
                            profile = profile,
                            load = load
                        )
                    )
                )
                profileAddSheet.close()
            },
            onDismiss = { profileAddSheet.close() }
        )
    }
    BottomSheet(
        sheetState = profileEditSheet.sheetState
    ) {
        ProfilesEditSheet(
            title = stringResource(R.string.pellets_editor),
            profilesData = profileEditSheet.data,
            onConfirm = { profile, _ ->
                onEventSent(
                    PelletsContract.Event.SendEvent(
                        PelletsEvent.EditProfile(
                            profile = profile
                        )
                    )
                )
                profileEditSheet.close()
            },
            onDismiss = { profileEditSheet.close() }
        )
    }
    BottomSheet(
        sheetState = profileDeleteSheet.sheetState
    ) {
        ConfirmSheet(
            title = stringResource(R.string.dialog_confirm_action),
            message = stringResource(
                R.string.dialog_confirm_delete_item,
                profileDeleteSheet.data.brand + " " + profileDeleteSheet.data.wood
            ),
            negativeButtonText = stringResource(R.string.cancel),
            positiveButtonText = stringResource(R.string.delete),
            positiveButtonColor = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onPositive = {
                onEventSent(
                    PelletsContract.Event.SendEvent(
                        PelletsEvent.DeleteProfile(profile = profileDeleteSheet.data.id)
                    )
                )
                profileDeleteSheet.close()
            },
            onNegative = { profileDeleteSheet.close() }
        )
    }
    BottomSheet(
        sheetState = logDeleteSheet.sheetState
    ) {
        ConfirmSheet(
            title = stringResource(R.string.dialog_confirm_action),
            message = stringResource(
                R.string.dialog_confirm_delete_item,
                logDeleteSheet.data.pelletName
            ),
            negativeButtonText = stringResource(R.string.cancel),
            positiveButtonText = stringResource(R.string.delete),
            positiveButtonColor = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onPositive = {
                onEventSent(
                    PelletsContract.Event.SendEvent(
                        PelletsEvent.DeleteLog(logDate = logDeleteSheet.data.logDate)
                    )
                )
                logDeleteSheet.close()
            },
            onNegative = { logDeleteSheet.close() }
        )
    }
}