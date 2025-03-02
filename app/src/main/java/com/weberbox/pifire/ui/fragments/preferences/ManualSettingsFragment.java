package com.weberbox.pifire.ui.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.model.remote.ManualDataModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.utils.AlertUtils;

import dev.chrisbanes.insetter.Insetter;
import io.socket.client.Socket;
import timber.log.Timber;

public class ManualSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ToolbarTitleCallback toolbarTitleCallback;
    private SwitchPreferenceCompat manualMode;
    private SwitchPreferenceCompat fanEnable;
    private SwitchPreferenceCompat augerEnable;
    private SwitchPreferenceCompat igniterEnable;
    private SwitchPreferenceCompat powerEnable;
    private SeekBarPreference pwmOutput;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_manual_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        getListView().setClipToPadding(false);
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .applyToView(getListView());

        manualMode = findPreference(getString(R.string.prefs_manual_mode));
        fanEnable = findPreference(getString(R.string.prefs_manual_mode_fan));
        augerEnable = findPreference(getString(R.string.prefs_manual_mode_auger));
        igniterEnable = findPreference(getString(R.string.prefs_manual_mode_igniter));
        powerEnable = findPreference(getString(R.string.prefs_manual_mode_power));
        pwmOutput = findPreference(getString(R.string.prefs_manual_mode_pwm));

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

        if (pwmOutput != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_dc_fan))) {
                pwmOutput.setUpdatesContinuously(false);
            } else {
                pwmOutput.setVisible(false);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            toolbarTitleCallback = (ToolbarTitleCallback) context;
        } catch (ClassCastException e) {
            Timber.e(e, "Activity does not implement callback");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (toolbarTitleCallback != null) {
            toolbarTitleCallback.onTitleChange(getString(R.string.settings_manual));
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
                        pwmOutput.setValue(100);
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
            if (preference instanceof SeekBarPreference) {
                if (preference.getContext().getString(R.string.prefs_manual_mode_pwm)
                        .equals(preference.getKey())) {
                    if (socketConnected()) {
                        ServerControl.setManualPWMOutput(socket,
                                ((SeekBarPreference) preference).getValue(),
                                this::processPostResponse);
                    }
                }
            }
        }
    }

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResponse() != null && result.getResponse().getResult().equals("error")) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        AlertUtils.createErrorAlert(getActivity(),
                                result.getResponse().getMessage(), false));
            }
        }
    }

    private boolean socketConnected() {
        if (socket != null && socket.connected()) {
            return true;
        } else {
            AlertUtils.createErrorAlert(requireActivity(), R.string.settings_error_offline, false);
            return false;
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
            Integer dutyCycle = manualDataModel.getManual().getPWM();

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

            if (pwmOutput != null && dutyCycle != null) {
                pwmOutput.setValue(dutyCycle);
            }

        } catch (NullPointerException e) {
            Timber.w(e, "Manual JSON Response Error");
            if (getActivity() != null) {
                AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                        getString(R.string.menu_setting)), false);
            }
        }
    }
}
