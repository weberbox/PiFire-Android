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
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.interfaces.ManualCallbackInterface;
import com.weberbox.pifire.ui.activities.PreferencesActivity;

import io.socket.client.Socket;

public class ManualSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, ManualCallbackInterface {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_manual_settings, rootKey);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SwitchPreferenceCompat manualMode = findPreference(getString(R.string.prefs_manual_mode));
        SwitchPreferenceCompat manualFan = findPreference(getString(R.string.prefs_manual_mode_fan));
        SwitchPreferenceCompat manualAuger = findPreference(getString(R.string.prefs_manual_mode_auger));
        SwitchPreferenceCompat manualIgniter = findPreference(getString(R.string.prefs_manual_mode_igniter));
        SwitchPreferenceCompat manualPower = findPreference(getString(R.string.prefs_manual_mode_power));

        if (manualMode != null && manualFan != null && manualAuger != null && manualIgniter != null
                && manualPower != null) {
            manualMode.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!manualMode.isChecked()) {
                    manualFan.setChecked(false);
                    manualAuger.setChecked(false);
                    manualIgniter.setChecked(false);
                    manualPower.setChecked(false);
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
    public void onDialogPositive(boolean enabled) {
        if (mSocket != null && mSocket.connected()) {
            GrillControl.setManualMode(mSocket, enabled);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_manual_mode)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setManualMode(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_fan)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setManualFanOutput(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_auger)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setManualAugerOutput(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_igniter)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setManualIgniterOutput(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
                if (preference.getContext().getString(R.string.prefs_manual_mode_power)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setManualPowerOutput(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
            }
        }
    }
}
