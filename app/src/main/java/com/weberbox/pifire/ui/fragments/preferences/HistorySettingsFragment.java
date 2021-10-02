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
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.GrillControl;

import io.socket.client.Socket;

public class HistorySettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_history_settings, rootKey);

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

        EditTextPreference historyDisplay = (EditTextPreference) findPreference(getString(R.string.prefs_history_display));
        EditTextPreference historyPoints = (EditTextPreference) findPreference(getString(R.string.prefs_history_points));

        if (historyDisplay != null) {
            historyDisplay.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
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
            });
        }

        if (historyPoints != null) {
            historyPoints.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
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
                            } else if (Integer.parseInt(s.toString()) < 10) {
                                editText.setError(getString(R.string.settings_min_ten_error));
                            } else {
                                editText.setError(null);
                            }

                        }
                    });
                }
            });
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket = null;
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (mSocket != null) {
                if (preference instanceof EditTextPreference) {
                    if (preference.getContext().getString(R.string.prefs_history_display)
                            .equals(preference.getKey())) {
                        GrillControl.setHistoryMins(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_history_points)
                            .equals(preference.getKey())) {
                        GrillControl.setHistoryPoints(mSocket, ((EditTextPreference) preference).getText());
                    }
                }
                if (preference instanceof SwitchPreferenceCompat) {
                    if (preference.getContext().getString(R.string.prefs_history_auto)
                            .equals(preference.getKey())) {
                        GrillControl.setHistoryRefresh(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                    }
                    if (preference.getContext().getString(R.string.prefs_history_clear)
                            .equals(preference.getKey())) {
                        GrillControl.setHistoryClear(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
            }
        }
    }
}