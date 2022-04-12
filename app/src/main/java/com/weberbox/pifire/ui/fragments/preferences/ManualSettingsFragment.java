package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ManualDataModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;
import timber.log.Timber;

public class ManualSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private SwitchPreferenceCompat manualMode;
    private SwitchPreferenceCompat fanEnable;
    private SwitchPreferenceCompat augerEnable;
    private SwitchPreferenceCompat igniterEnable;
    private SwitchPreferenceCompat powerEnable;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_manual_settings, rootKey);
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

        manualMode = findPreference(getString(R.string.prefs_manual_mode));
        fanEnable = findPreference(getString(R.string.prefs_manual_mode_fan));
        augerEnable = findPreference(getString(R.string.prefs_manual_mode_auger));
        igniterEnable = findPreference(getString(R.string.prefs_manual_mode_igniter));
        powerEnable = findPreference(getString(R.string.prefs_manual_mode_power));

        if (manualMode != null && fanEnable != null && augerEnable != null &&
                igniterEnable != null && powerEnable != null) {
            manualMode.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!manualMode.isChecked()) {
                    fanEnable.setChecked(false);
                    augerEnable.setChecked(false);
                    igniterEnable.setChecked(false);
                    powerEnable.setChecked(false);
                }
                return true;
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_manual);
        }

        if (socket != null && socket.connected()) {
            ServerControl.manualGetEmit(socket, response -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateManualSettings(response));
                }
            });
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && socket != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_manual_mode)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setManualMode(socket,
                                ((SwitchPreferenceCompat) preference).isChecked(),
                                this::processPostResponse);
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_fan)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setManualFanOutput(socket,
                                ((SwitchPreferenceCompat) preference).isChecked(),
                                this::processPostResponse);
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_auger)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setManualAugerOutput(socket,
                                ((SwitchPreferenceCompat) preference).isChecked(),
                                this::processPostResponse);
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_igniter)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setManualIgniterOutput(socket,
                                ((SwitchPreferenceCompat) preference).isChecked(),
                                this::processPostResponse);
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_power)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setManualPowerOutput(socket,
                                ((SwitchPreferenceCompat) preference).isChecked(),
                                this::processPostResponse);
                    }
                }
            }
        }
    }

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResponse() != null && result.getResponse().getResult().equals("error")) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(requireActivity(),
                            result.getResponse().getMessage(), false));
        }
    }

    private void updateManualSettings(String response_data) {

        try {

            ManualDataModel manualDataModel = ManualDataModel.parseJSON(response_data);

            String currentMode = manualDataModel.getMode();
            Boolean fanState = manualDataModel.getManual().getFan();
            Boolean augerState = manualDataModel.getManual().getAuger();
            Boolean igniterState = manualDataModel.getManual().getIgniter();
            Boolean powerState = manualDataModel.getManual().getPower();

            if (manualMode != null && currentMode != null) {
                manualMode.setChecked(currentMode.equalsIgnoreCase(ServerConstants.MODE_MANUAL));
            }

            if (fanEnable != null && fanState != null) {
                fanEnable.setChecked(fanState);
            }

            if (augerEnable != null && augerState != null) {
                augerEnable.setChecked(augerState);
            }

            if (igniterEnable != null && igniterState != null) {
                igniterEnable.setChecked(igniterState);
            }

            if (powerEnable != null && powerState != null) {
                powerEnable.setChecked(powerState);
            }

        } catch (NullPointerException e) {
            Timber.w(e, "Manual JSON Response Error");
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_setting)), false);
        }
    }
}
