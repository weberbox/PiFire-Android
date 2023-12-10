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
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;

public class PWMSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_pwm_settings, rootKey);
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

        EditTextPreference tempFanUpdateTime = findPreference(getString(R.string.prefs_pwm_fan_update_time));
        EditTextPreference pwmFrequency = findPreference(getString(R.string.prefs_pwm_frequency));
        EditTextPreference pwmMinDutyCycle = findPreference(getString(R.string.prefs_pwm_min_duty_cycle));
        EditTextPreference pwmMaxDutyCycle = findPreference(getString(R.string.prefs_pwm_max_duty_cycle));

        if (tempFanUpdateTime != null) {
            tempFanUpdateTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (pwmFrequency != null) {
            pwmFrequency.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (pwmMinDutyCycle != null) {
            pwmMinDutyCycle.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, 100.0, editText));
            });
        }

        if (pwmMaxDutyCycle != null) {
            pwmMaxDutyCycle.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, 100.0, editText));
            });
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
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_pwm);
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
                if (preference.getContext().getString(R.string.prefs_pwm_fan_update_time)
                        .equals(preference.getKey())) {
                    ServerControl.setPWMTempUpdateTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pwm_frequency)
                        .equals(preference.getKey())) {
                    ServerControl.setPWMFanFrequency(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pwm_min_duty_cycle)
                        .equals(preference.getKey())) {
                    ServerControl.setPWMMinDutyCycle(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pwm_max_duty_cycle)
                        .equals(preference.getKey())) {
                    ServerControl.setPWMMaxDutyCycle(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_pwm_fan_control)
                        .equals(preference.getKey())) {
                    ServerControl.setPWMControlDefault(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
            }
        }
    }
}
