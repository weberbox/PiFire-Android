package com.weberbox.pifire.pellets.presentation.component

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.CircularPickerSheet
import com.weberbox.pifire.common.presentation.sheets.ConfirmSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.state.CustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.state.InputModalBottomSheetState
import com.weberbox.pifire.pellets.presentation.contract.PelletsContract
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletLog
import com.weberbox.pifire.pellets.presentation.model.PelletsData.Pellets.PelletProfile
import com.weberbox.pifire.pellets.presentation.model.PelletsEvent
import com.weberbox.pifire.pellets.presentation.model.ProfilesData
import com.weberbox.pifire.pellets.presentation.sheets.ProfilesEditSheet

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
        CircularPickerSheet(
            items = state.pellets.profilesList,
            initialItem = state.pellets.profilesList.find {
                it.id == state.pellets.currentPelletId
            } ?: state.pellets.profilesList[0],
            itemToString = { it.brand + " " + it.wood },
            positiveButtonText = stringResource(R.string.load),
            onNegative = { currentSheet.close() },
            onPositive = {
                onEventSent(PelletsContract.Event.SendEvent(PelletsEvent.LoadProfile(it.id)))
                currentSheet.close()
            }
        )
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