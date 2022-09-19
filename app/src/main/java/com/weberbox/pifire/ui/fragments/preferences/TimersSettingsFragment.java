package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.VersionUtils;

import io.socket.client.Socket;

public class TimersSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_timers_settings, rootKey);
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

        EditTextPreference shutdownTime = findPreference(getString(R.string.prefs_shutdown_time));
        EditTextPreference startupTime = findPreference(getString(R.string.prefs_startup_time));
        SwitchPreferenceCompat smartStart = findPreference(getString(R.string.prefs_smart_start_enabled));
        Preference smartStartTable = findPreference(getString(R.string.prefs_smart_start_table));
        SwitchPreferenceCompat autoPowerOff = findPreference(getString(R.string.prefs_auto_power_off));


        if (shutdownTime != null) {
            shutdownTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (startupTime != null) {
            if (VersionUtils.isSupported(ServerVersions.V_127)) {
                startupTime.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(requireActivity(), 1.0, null, editText));
                });
            } else {
                startupTime.setEnabled(false);
                startupTime.setSummaryProvider(null);
                startupTime.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_127));
            }
        }

        if (smartStart != null) {
            if (!VersionUtils.isSupported(ServerVersions.V_131)) {
                smartStart.setChecked(false);
                smartStart.setEnabled(false);
                smartStart.setSummaryProvider(null);
                smartStart.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_131));
            }
        }

        if (smartStartTable != null) {
            if (!VersionUtils.isSupported(ServerVersions.V_131)) {
                smartStartTable.setEnabled(false);
                smartStartTable.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_131));
            }
        }

        if (autoPowerOff != null) {
            if (!VersionUtils.isSupported(ServerVersions.V_129)) {
                autoPowerOff.setEnabled(false);
                autoPowerOff.setSummaryProvider(null);
                autoPowerOff.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_129));
            }
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
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_timers);
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
                if (preference.getContext().getString(R.string.prefs_shutdown_time)
                        .equals(preference.getKey())) {
                    ServerControl.sendShutdownTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_startup_time)
                        .equals(preference.getKey())) {
                    ServerControl.sendStartupTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_auto_power_off)
                        .equals(preference.getKey())) {
                    ServerControl.sendAutoPowerOff(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_smart_start_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setSmartStartEnabled(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
            }
        }
    }
}
