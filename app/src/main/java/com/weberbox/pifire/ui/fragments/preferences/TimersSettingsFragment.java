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
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.ui.views.preferences.SwitchPreferenceCompatSocket;
import com.weberbox.pifire.utils.AlertUtils;

import java.util.Objects;

import io.socket.client.Socket;

public class TimersSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private EditTextPreference startToModeTemp, primeOnStartup, startupExitTemp;
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

        SwitchPreferenceCompatSocket startupExitTempEnabled = findPreference(getString(R.string.prefs_startup_exit_temp_enabled));
        SwitchPreferenceCompatSocket startupPrimeEnabled = findPreference(getString(R.string.prefs_prime_on_startup_enabled));
        EditTextPreference shutdownDuration = findPreference(getString(R.string.prefs_shutdown_duration));
        EditTextPreference startupDuration = findPreference(getString(R.string.prefs_startup_duration));
        EditTextPreference smartExitTemp = findPreference(getString(R.string.prefs_smart_start_exit_temp));
        ListPreference startToMode = findPreference(getString(R.string.prefs_startup_goto_mode));
        startToModeTemp = findPreference(getString(R.string.prefs_startup_goto_temp));
        primeOnStartup = findPreference(getString(R.string.prefs_prime_on_startup));
        startupExitTemp = findPreference(getString(R.string.prefs_startup_exit_temp));


        if (shutdownDuration != null) {
            shutdownDuration.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (startupDuration != null) {
            startupDuration.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (startupPrimeEnabled != null) {
            String prime = sharedPreferences.getString(getString(R.string.prefs_prime_on_startup),
                    getString(R.string.def_prime_on_startup));
            startupPrimeEnabled.setChecked(!prime.equals("0"));
        }

        if (primeOnStartup != null) {
            primeOnStartup.setVisible(sharedPreferences.getBoolean(
                    getString(R.string.prefs_prime_on_startup_enabled), false));
            primeOnStartup.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.0, null, editText));
            });
        }

        if (startupExitTempEnabled != null) {
            String extTemp = sharedPreferences.getString(getString(R.string.prefs_startup_exit_temp),
                    getString(R.string.def_startup_exit_temp));
            startupExitTempEnabled.setChecked(!extTemp.equals("0"));
        }

        if (startupExitTemp != null) {
            startupExitTemp.setVisible(sharedPreferences.getBoolean(
                    getString(R.string.prefs_startup_exit_temp_enabled), false));
            startupExitTemp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.0, null, editText));
            });
        }

        if (smartExitTemp != null) {
            smartExitTemp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.0, null, editText));
            });
        }

        if (startToMode != null && startToModeTemp != null) {
            Double minTemp = Double.parseDouble(
                    sharedPreferences.getString(getString(R.string.prefs_safety_max_start),
                            getString(R.string.def_safety_max_start)));
            Double maxTemp = Double.parseDouble(
                    sharedPreferences.getString(getString(R.string.prefs_safety_max_temp),
                            getString(R.string.def_safety_max_temp)));
            startToModeTemp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), minTemp, maxTemp, editText));
            });
            startToModeTemp.setVisible(
                    startToMode.getValue().equals(getString(R.string.grill_mode_hold)));
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
                if (preference.getContext().getString(R.string.prefs_shutdown_duration)
                        .equals(preference.getKey())) {
                    ServerControl.sendShutdownDuration(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_startup_duration)
                        .equals(preference.getKey())) {
                    ServerControl.sendStartupDuration(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_prime_on_startup)
                        .equals(preference.getKey())) {
                    ServerControl.sendPrimeOnStartup(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_startup_goto_temp)
                        .equals(preference.getKey())) {
                    ServerControl.setStartToModeTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_startup_exit_temp)
                        .equals(preference.getKey())) {
                    ServerControl.setStartExitTemp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_smart_start_exit_temp)
                        .equals(preference.getKey())) {
                    ServerControl.setSmartStartExitTemp(socket,
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
                if (preference.getContext().getString(R.string.prefs_prime_on_startup_enabled)
                        .equals(preference.getKey())) {
                    boolean isChecked = ((SwitchPreferenceCompat) preference).isChecked();
                    primeOnStartup.setVisible(isChecked);
                    if (isChecked) {
                        ServerControl.sendPrimeOnStartup(socket,
                                getString(R.string.def_prime_on_startup),
                                this::processPostResponse);
                        primeOnStartup.setText(getString(R.string.def_prime_on_startup));
                    } else {
                        ServerControl.sendPrimeOnStartup(socket, "0",
                                this::processPostResponse);
                    }
                }
                if (preference.getContext().getString(R.string.prefs_startup_exit_temp_enabled)
                        .equals(preference.getKey())) {
                    boolean isChecked = ((SwitchPreferenceCompat) preference).isChecked();
                    startupExitTemp.setVisible(isChecked);
                    if (isChecked) {
                        ServerControl.setStartExitTemp(socket,
                                getString(R.string.def_startup_exit_temp),
                                this::processPostResponse);
                        startupExitTemp.setText(getString(R.string.def_startup_exit_temp));
                    } else {
                        ServerControl.setStartExitTemp(socket, "0",
                                this::processPostResponse);
                    }
                }
            }
            if (preference instanceof ListPreference) {
                if (preference.getContext().getString(R.string.prefs_startup_goto_mode)
                        .equals(preference.getKey())) {
                    String startupMode = ((ListPreference) preference).getValue();
                    startToModeTemp.setVisible(Objects.equals(startupMode,
                            getString(R.string.grill_mode_hold)));
                    ServerControl.setStartToMode(socket, startupMode, this::processPostResponse);
                }
            }
        }
    }
}
