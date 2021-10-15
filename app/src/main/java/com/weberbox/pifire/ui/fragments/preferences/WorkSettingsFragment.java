package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.ui.dialogs.PModeTableDialog;
import com.weberbox.pifire.ui.preferences.EmptyTextListener;

import io.socket.client.Socket;

public class WorkSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_work_mode_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

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

            if (pidTd != null) {
                pidTd.setOnBindEditTextListener(editText -> {
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
                pidUMax.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }

            if (pidUMin != null) {
                pidUMin.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            }
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket = null;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (mSocket != null) {
                if (preference instanceof ListPreference) {
                    if (preference.getContext().getString(R.string.prefs_work_pmode_mode)
                            .equals(preference.getKey())) {
                        GrillControl.setPMode(mSocket, ((ListPreference) preference).getValue());
                    }
                }
                if (preference instanceof EditTextPreference) {
                    if (preference.getContext().getString(R.string.prefs_work_auger_on)
                            .equals(preference.getKey())) {
                        GrillControl.setAugerTime(mSocket, (
                                (EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_splus_fan)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokeFan(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_splus_min)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokeMinTemp(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_splus_max)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokeMaxTemp(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_cycle)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDTime(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_pb)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDPB(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_ti)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDTi(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_td)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDTd(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_u_max)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDuMax(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_u_min)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDuMin(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                }
                if (preference instanceof SwitchPreferenceCompat) {
                    if (preference.getContext().getString(R.string.prefs_work_splus_enabled)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokePlusDefault(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
            }
        }
    }
}
