package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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

import io.socket.client.Socket;

public class WorkSettingsFragment extends PreferenceFragmentCompat implements
        EditTextPreference.OnBindEditTextListener, SharedPreferences.OnSharedPreferenceChangeListener {

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

        Preference pModeTable = (Preference) findPreference(getString(R.string.prefs_work_pmode_table));
        EditTextPreference augerOnTime = (EditTextPreference) findPreference(getString(R.string.prefs_work_auger_on));
        EditTextPreference fanCycleTime = (EditTextPreference) findPreference(getString(R.string.prefs_work_splus_fan));
        EditTextPreference minSmokeTemp = (EditTextPreference) findPreference(getString(R.string.prefs_work_splus_min));
        EditTextPreference maxSmokeTemp = (EditTextPreference) findPreference(getString(R.string.prefs_work_splus_max));
        EditTextPreference pidCycle = (EditTextPreference) findPreference(getString(R.string.prefs_work_pid_cycle));
        EditTextPreference pidPB = (EditTextPreference) findPreference(getString(R.string.prefs_work_pid_pb));
        EditTextPreference pidTi = (EditTextPreference) findPreference(getString(R.string.prefs_work_pid_ti));
        EditTextPreference pidTd = (EditTextPreference) findPreference(getString(R.string.prefs_work_pid_td));


        if (pModeTable != null) {
            pModeTable.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (getActivity() != null) {
                        PModeTableDialog pModeTableDialog = new PModeTableDialog(getActivity());
                        pModeTableDialog.showDialog();
                    }
                    return true;
                }
            });
        }

        if (pidPB != null) {
            pidPB.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) {
                                editText.setError(getString(R.string.settings_blank_error));
                            } else if (s.toString().equals("0")) {
                                editText.setError(getString(R.string.settings_zero_error));
                            } else {
                                editText.setError(null);
                            }

                        }
                    });
                }
            });
        }

        if (pidTi != null) {
            pidTi.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) {
                                editText.setError(getString(R.string.settings_blank_error));
                            } else if (s.toString().equals("0")) {
                                editText.setError(getString(R.string.settings_zero_error));
                            } else {
                                editText.setError(null);
                            }

                        }
                    });
                }
            });
        }

        if (pidTd != null) {
            pidTd.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) {
                                editText.setError(getString(R.string.settings_blank_error));
                            } else if (s.toString().equals("0")) {
                                editText.setError(getString(R.string.settings_zero_error));
                            } else {
                                editText.setError(null);
                            }

                        }
                    });
                }
            });
        }

        if (augerOnTime != null) {
            augerOnTime.setOnBindEditTextListener(this);
        }

        if (fanCycleTime != null) {
            fanCycleTime.setOnBindEditTextListener(this);
        }

        if (minSmokeTemp != null) {
            minSmokeTemp.setOnBindEditTextListener(this);
        }

        if (maxSmokeTemp != null) {
            maxSmokeTemp.setOnBindEditTextListener(this);
        }

        if (pidCycle != null) {
            pidCycle.setOnBindEditTextListener(this);
        }

        return view;
    }

    @Override
    public void onBindEditText(@NonNull EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    editText.setError(getString(R.string.settings_blank_error));
                } else if (s.toString().equals("0")) {
                    editText.setError(getString(R.string.settings_zero_error));
                } else {
                    editText.setError(null);
                }

            }
        });
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
                        GrillControl.setAugerTime(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_splus_fan)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokeFan(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_splus_min)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokeMinTemp(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_splus_max)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokeMaxTemp(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_cycle)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDTime(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_pb)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDPB(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_ti)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDTi(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_work_pid_td)
                            .equals(preference.getKey())) {
                        GrillControl.setPIDTd(mSocket, ((EditTextPreference) preference).getText());
                    }
                }
                if (preference instanceof SwitchPreferenceCompat) {
                    if (preference.getContext().getString(R.string.prefs_work_splus_enabled)
                            .equals(preference.getKey())) {
                        GrillControl.setSmokePlusDefault(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
            }
        }
    }
}
