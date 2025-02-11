package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.PModeTableDialog;
import com.weberbox.pifire.ui.dialogs.PrefsEditDialog;
import com.weberbox.pifire.ui.dialogs.PrefsListDialog;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;
import io.socket.client.Socket;

public class WorkSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_work_mode_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        Preference pModeTable = findPreference(getString(R.string.prefs_work_pmode_table));
        EditTextPreference augerOnTime = findPreference(getString(R.string.prefs_work_auger_on));
        EditTextPreference augerOffTime = findPreference(getString(R.string.prefs_work_auger_off));
        EditTextPreference minSmokeTemp = findPreference(getString(R.string.prefs_work_splus_min));
        EditTextPreference maxSmokeTemp = findPreference(getString(R.string.prefs_work_splus_max));
        EditTextPreference pidCycle = findPreference(getString(R.string.prefs_work_controller_cycle));
        EditTextPreference pidUMax = findPreference(getString(R.string.prefs_work_controller_u_max));
        EditTextPreference pidUMin = findPreference(getString(R.string.prefs_work_controller_u_min));
        EditTextPreference lidOpenThresh = findPreference(getString(R.string.prefs_work_lid_open_thresh));
        EditTextPreference lidOpenPause = findPreference(getString(R.string.prefs_work_lid_open_pause));
        EditTextPreference keepWarmTemp = findPreference(getString(R.string.prefs_work_keep_warm_temp));
        SwitchPreferenceCompat pwmFanRamp = findPreference(getString(R.string.prefs_work_splus_fan_ramp));
        EditTextPreference fanOnTime = findPreference(getString(R.string.prefs_work_splus_on_time));
        EditTextPreference fanOffTime = findPreference(getString(R.string.prefs_work_splus_off_time));
        EditTextPreference fanDutyCycle = findPreference(getString(R.string.prefs_work_splus_ramp_dc));

        getListView().setClipToPadding(false);

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .margin(WindowInsetsCompat.Type.systemBars(), Side.BOTTOM)
                .applyToView(getListView());

        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);

        if (pModeTable != null) {
            pModeTable.setOnPreferenceClickListener(preference -> {
                PModeTableDialog pModeTableDialog = new PModeTableDialog(requireActivity());
                pModeTableDialog.showDialog();
                return true;
            });
        }

        if (augerOnTime != null) {
            augerOnTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (augerOffTime != null) {
            augerOffTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }


        if (pwmFanRamp != null) {
            if (!Prefs.getBoolean(getString(R.string.prefs_dc_fan))) {
                pwmFanRamp.setVisible(false);
            }
        }

        if (fanDutyCycle != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_dc_fan))) {
                Double minDuty = Double.valueOf(
                        Prefs.getString(getString(R.string.prefs_pwm_min_duty_cycle)));
                fanDutyCycle.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(requireActivity(), minDuty, 100.0,
                                    editText));
                });
            } else {
                fanDutyCycle.setVisible(false);
            }
        }

        if (fanOnTime != null) {
            fanOnTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null,
                                editText));
            });
        }

        if (fanOffTime != null) {
            fanOffTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null,
                                editText));
            });
        }

        if (minSmokeTemp != null) {
            minSmokeTemp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (maxSmokeTemp != null) {
            maxSmokeTemp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (pidCycle != null) {
            pidCycle.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.1, null, editText));
            });
        }

        if (pidUMax != null) {
            pidUMax.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.1, null,
                                editText));
            });
        }

        if (pidUMin != null) {
            pidUMin.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.1, null,
                                editText));
            });
        }

        if (lidOpenThresh != null) {
            lidOpenThresh.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, 80.0,
                                editText));
            });
        }

        if (lidOpenPause != null) {
            lidOpenPause.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 10.0, 1000.0,
                                editText));
            });
        }

        if (keepWarmTemp != null) {
            keepWarmTemp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null,
                                editText));
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_work);
        }
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(requireActivity(),
                            result.getMessage(), false));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && socket != null) {
            if (preference instanceof ListPreference) {
                if (preference.getContext().getString(R.string.prefs_work_pmode_mode)
                        .equals(preference.getKey())) {
                    ServerControl.setPMode(socket, ((ListPreference) preference).getValue(),
                            this::processPostResponse);
                }
            }
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_work_auger_on)
                        .equals(preference.getKey())) {
                    ServerControl.setAugerOnTime(socket, (
                            (EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_auger_off)
                        .equals(preference.getKey())) {
                    ServerControl.setAugerOffTime(socket, (
                            (EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_min)
                        .equals(preference.getKey())) {
                    ServerControl.setSmokeMinTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_max)
                        .equals(preference.getKey())) {
                    ServerControl.setSmokeMaxTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_controller_cycle)
                        .equals(preference.getKey())) {
                    ServerControl.setControllerTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_controller_u_max)
                        .equals(preference.getKey())) {
                    ServerControl.setControlleruMax(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_controller_u_min)
                        .equals(preference.getKey())) {
                    ServerControl.setControlleruMin(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_keep_warm_temp)
                        .equals(preference.getKey())) {
                    ServerControl.setKeepWarmTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_on_time)
                        .equals(preference.getKey())) {
                    ServerControl.setFanOnTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_off_time)
                        .equals(preference.getKey())) {
                    ServerControl.setFanOffTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_ramp_dc)
                        .equals(preference.getKey())) {
                    ServerControl.setFanDutyCycle(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_lid_open_thresh)
                        .equals(preference.getKey())) {
                    ServerControl.setLidOpenThresh(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_lid_open_pause)
                        .equals(preference.getKey())) {
                    ServerControl.setLidOpenPause(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_work_splus_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setSmokePlusDefault(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_fan_ramp)
                        .equals(preference.getKey())) {
                    ServerControl.setSPlusFanRamp(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_keep_warm_s_plus)
                        .equals(preference.getKey())) {
                    ServerControl.setKeepWarmSPlus(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_lid_open_detect)
                        .equals(preference.getKey())) {
                    ServerControl.setLidOpenDetect(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof EditTextPreference) {
            if (getContext() != null) {
                new PrefsEditDialog(getContext(), preference).showDialog();
                return;
            }
        }
        if (preference instanceof ListPreference) {
            if (getContext() != null) {
                new PrefsListDialog(getContext(), preference).showDialog();
                return;
            }
        }
        super.onDisplayPreferenceDialog(preference);
    }
}
