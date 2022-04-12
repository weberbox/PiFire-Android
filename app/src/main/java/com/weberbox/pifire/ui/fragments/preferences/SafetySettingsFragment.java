package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;

public class SafetySettingsFragment extends PreferenceFragmentCompat implements
        EditTextPreference.OnBindEditTextListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_safety_settings, rootKey);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_safety);
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
    public void onBindEditText(@NonNull EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(
                new EmptyTextListener(getActivity(), 1.0, null, editText));
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
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_safety_min_start)
                        .equals(preference.getKey())) {
                    ServerControl.setMinStartTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_safety_max_start)
                        .equals(preference.getKey())) {
                    ServerControl.setMaxStartTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_safety_max_temp)
                        .equals(preference.getKey())) {
                    ServerControl.setMaxGrillTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
            if (preference instanceof ListPreference) {
                if (preference.getContext().getString(R.string.prefs_safety_retries)
                        .equals(preference.getKey())) {
                    ServerControl.setReigniteRetries(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
            }
        }
    }
}
