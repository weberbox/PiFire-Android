package com.weberbox.pifire.feature.settings.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.weberbox.pifire.R
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.ui.state.CustomModalBottomSheetState
import com.weberbox.pifire.core.ui.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.core.ui.theme.PiFireTheme
import com.weberbox.pifire.core.ui.util.slideDownExpandEnterTransition
import com.weberbox.pifire.core.ui.util.slideDownShrinkExitTransition
import com.weberbox.pifire.feature.settings.presentation.contract.WorkContract
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@Composable
internal fun PidAcControlSettings(
    state: WorkContract.State,
    pidPbSheet: CustomModalBottomSheetState,
    pidTdSheet: CustomModalBottomSheetState,
    pidTiSheet: CustomModalBottomSheetState,
    pidCenterSheet: CustomModalBottomSheetState,
    pidStableSheet: CustomModalBottomSheetState,
) {
    AnimatedVisibility(
        visible = state.serverData.settings.cntrlrSelected == ServerConstants.CNTRLR_PID_AC,
        enter = slideDownExpandEnterTransition(),
        exit = slideDownShrinkExitTransition()
    ) {
        Column {
            Preference(
                title = { Text(text = stringResource(R.string.settings_pid_cntrlr_pb)) },
                summary = {
                    Text(
                        text = getSummary(
                            state.serverData.settings.cntrlrPidAcPb.toString()
                        )
                    )
                },
                onClick = { pidPbSheet.open() }
            )
            PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_pb_note))
            Preference(
                title = { Text(text = stringResource(R.string.settings_pid_cntrlr_td)) },
                summary = {
                    Text(
                        text = getSummary(
                            state.serverData.settings.cntrlrPidAcTd.toString()
                        )
                    )
                },
                onClick = { pidTdSheet.open() }
            )
            PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_td_note))
            Preference(
                title = { Text(text = stringResource(R.string.settings_pid_cntrlr_ti)) },
                summary = {
                    Text(
                        text = getSummary(
                            state.serverData.settings.cntrlrPidAcTi.toString()
                        )
                    )
                },
                onClick = { pidTiSheet.open() }
            )
            PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_ti_note))
            Preference(
                title = { Text(text = stringResource(R.string.settings_pid_cntrlr_sw)) },
                summary = {
                    Text(
                        text = getSummary(
                            state.serverData.settings.cntrlrPidAcStable.toString()
                        )
                    )
                },
                onClick = { pidStableSheet.open() }
            )
            PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_sw_note))
            Preference(
                title = { Text(text = stringResource(R.string.settings_pid_cntrlr_center_factor)) },
                summary = {
                    Text(
                        text = getSummary(
                            state.serverData.settings.cntrlrPidAcCenter.toString()
                        )
                    )
                },
                onClick = { pidCenterSheet.open() }
            )
            PreferenceInfo(stringResource(R.string.settings_pid_cntrlr_center_note_auto))
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
private fun PidAcControlSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceTheme {
            Surface {
                PidAcControlSettings(
                    state = WorkContract.State(
                        serverData = Server(),
                        isInitialLoading = false,
                        isLoading = false,
                        isDataError = false
                    ),
                    pidPbSheet = rememberCustomModalBottomSheetState(),
                    pidTdSheet = rememberCustomModalBottomSheetState(),
                    pidTiSheet = rememberCustomModalBottomSheetState(),
                    pidCenterSheet = rememberCustomModalBottomSheetState(),
                    pidStableSheet = rememberCustomModalBottomSheetState()
                )
            }
        }
    }
}