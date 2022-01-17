package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.preferences.EmptyTextListener;

import io.socket.client.Socket;

public class SafetySettingsFragment extends PreferenceFragmentCompat implements
        EditTextPreference.OnBindEditTextListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_safety_settings, rootKey);
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

        EditTextPreference minStartTemp = findPreference(getString(R.string.prefs_safety_min_start));
        EditTextPreference maxStartTemp = findPreference(getString(R.string.prefs_safety_max_start));
        EditTextPreference maxGrillTemp = findPreference(getString(R.string.prefs_safety_max_temp));

        if (minStartTemp != null) {
            minStartTemp.setOnBindEditTextListener(this);
        }
        if (maxStartTemp != null) {
            maxStartTemp.setOnBindEditTextListener(this);
        }
        if (maxGrillTemp != null) {
            maxGrillTemp.setOnBindEditTextListener(this);
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
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_safety);
        }
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
    public void onBindEditText(@NonNull EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(
                new EmptyTextListener(getActivity(), editText));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && mSocket != null) {
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_safety_min_start)
                        .equals(preference.getKey())) {
                    GrillControl.setMinStartTemp(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_safety_max_start)
                        .equals(preference.getKey())) {
                    GrillControl.setMaxStartTemp(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_safety_max_temp)
                        .equals(preference.getKey())) {
                    GrillControl.setMaxGrillTemp(mSocket,
                            ((EditTextPreference) preference).getText());
                }
            }
            if (preference instanceof ListPreference) {
                if (preference.getContext().getString(R.string.prefs_safety_retries)
                        .equals(preference.getKey())) {
                    GrillControl.setReigniteRetries(mSocket,
                            ((ListPreference) preference).getValue());
                }
            }
        }
    }
}
