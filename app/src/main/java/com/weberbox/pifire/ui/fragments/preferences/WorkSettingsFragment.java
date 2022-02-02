package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Versions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.PModeTableDialog;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.VersionUtils;

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
        EditTextPreference fanCycleTime = findPreference(getString(R.string.prefs_work_splus_fan));
        EditTextPreference minSmokeTemp = findPreference(getString(R.string.prefs_work_splus_min));
        EditTextPreference maxSmokeTemp = findPreference(getString(R.string.prefs_work_splus_max));
        EditTextPreference pidCycle = findPreference(getString(R.string.prefs_work_pid_cycle));
        EditTextPreference pidPB = findPreference(getString(R.string.prefs_work_pid_pb));
        EditTextPreference pidTi = findPreference(getString(R.string.prefs_work_pid_ti));
        EditTextPreference pidTd = findPreference(getString(R.string.prefs_work_pid_td));
        EditTextPreference pidUMax = findPreference(getString(R.string.prefs_work_pid_u_max));
        EditTextPreference pidUMin = findPreference(getString(R.string.prefs_work_pid_u_min));
        EditTextPreference pidCenter = findPreference(getString(R.string.prefs_work_pid_center));

        if (getActivity() != null) {

            if (pModeTable != null) {
                pModeTable.setOnPreferenceClickListener(preference -> {
                    PModeTableDialog pModeTableDialog = new PModeTableDialog(getActivity());
                    pModeTableDialog.showDialog();
                    return true;
                });
            }

            if (pidPB != null) {
                pidPB.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (pidTi != null) {
                pidTi.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (pidTd != null) {
                pidTd.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (augerOnTime != null) {
                augerOnTime.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (fanCycleTime != null) {
                fanCycleTime.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (minSmokeTemp != null) {
                minSmokeTemp.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (maxSmokeTemp != null) {
                maxSmokeTemp.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (pidCycle != null) {
                pidCycle.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (pidUMax != null) {
                if (VersionUtils.isSupported(Versions.V_120)) {
                    pidUMax.setOnBindEditTextListener(editText -> {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.addTextChangedListener(
                                new EmptyTextListener(getActivity(), editText));
                    });
                } else {
                    pidUMax.setEnabled(false);
                    pidUMax.setSummaryProvider(null);
                    pidUMax.setSummary(getString(R.string.disabled_option_settings, Versions.V_120));
                }
            }

            if (pidUMin != null) {
                if (VersionUtils.isSupported(Versions.V_120)) {
                    pidUMin.setOnBindEditTextListener(editText -> {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.addTextChangedListener(
                                new EmptyTextListener(getActivity(), editText));
                    });
                } else {
                    pidUMin.setEnabled(false);
                    pidUMin.setSummaryProvider(null);
                    pidUMin.setSummary(getString(R.string.disabled_option_settings, Versions.V_120));
                }
            }

            if (pidCenter != null) {
                if (VersionUtils.isSupported(Versions.V_122)) {
                    pidCenter.setOnBindEditTextListener(editText -> {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                                InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.addTextChangedListener(
                                new EmptyTextListener(getActivity(), editText));
                    });
                } else {
                    pidCenter.setEnabled(false);
                    pidCenter.setSummaryProvider(null);
                    pidCenter.setSummary(getString(R.string.disabled_option_settings, Versions.V_122));
                }
            }
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
                    ServerControl.setAugerTime(socket, (
                            (EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_splus_fan)
                        .equals(preference.getKey())) {
                    ServerControl.setSmokeFan(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
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
                if (preference.getContext().getString(R.string.prefs_work_pid_cycle)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_pid_pb)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDPB(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_pid_ti)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDTi(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_pid_td)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDTd(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_pid_u_max)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDuMax(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_pid_u_min)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDuMin(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_work_pid_center)
                        .equals(preference.getKey())) {
                    ServerControl.setPIDCenter(socket,
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
            }
        }
    }
}
