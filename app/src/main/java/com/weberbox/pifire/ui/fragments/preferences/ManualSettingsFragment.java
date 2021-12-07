package com.weberbox.pifire.ui.fragments.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.Snackbar;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.model.ManualResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class ManualSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SwitchPreferenceCompat mManualMode;
    private SwitchPreferenceCompat mFanEnable;
    private SwitchPreferenceCompat mAugerEnable;
    private SwitchPreferenceCompat mIgniterEnable;
    private SwitchPreferenceCompat mPowerEnable;
    private Snackbar mErrorSnack;
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

        mErrorSnack = Snackbar.make(view, R.string.json_error_settings, Snackbar.LENGTH_LONG);

        mManualMode = findPreference(getString(R.string.prefs_manual_mode));
        mFanEnable = findPreference(getString(R.string.prefs_manual_mode_fan));
        mAugerEnable = findPreference(getString(R.string.prefs_manual_mode_auger));
        mIgniterEnable = findPreference(getString(R.string.prefs_manual_mode_igniter));
        mPowerEnable = findPreference(getString(R.string.prefs_manual_mode_power));

        if (mManualMode != null && mFanEnable != null && mAugerEnable != null &&
                mIgniterEnable != null && mPowerEnable != null) {
            mManualMode.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!mManualMode.isChecked()) {
                    mFanEnable.setChecked(false);
                    mAugerEnable.setChecked(false);
                    mIgniterEnable.setChecked(false);
                    mPowerEnable.setChecked(false);
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

        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_MANUAL_DATA, (Ack) args ->
                    getActivity().runOnUiThread(() ->
                            updateManualSettings(args[0].toString())));
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && mSocket != null) {
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

    private void updateManualSettings(String response_data) {

        try {

            ManualResponseModel manualResponseModel = ManualResponseModel.parseJSON(response_data);

            String currentMode = manualResponseModel.getMode();
            Boolean fanState = manualResponseModel.getManual().getFan();
            Boolean augerState = manualResponseModel.getManual().getAuger();
            Boolean igniterState = manualResponseModel.getManual().getIgniter();
            Boolean powerState = manualResponseModel.getManual().getPower();

            if (mManualMode != null && currentMode != null) {
                mManualMode.setEnabled(true);
                mManualMode.setChecked(currentMode.equalsIgnoreCase(ServerConstants.MODE_MANUAL));
            }

            if (mFanEnable != null && fanState != null) {
                mFanEnable.setChecked(fanState);
            }

            if (mAugerEnable != null && augerState != null) {
                mAugerEnable.setChecked(augerState);
            }

            if (mIgniterEnable != null && igniterState != null) {
                mIgniterEnable.setChecked(igniterState);
            }

            if (mPowerEnable != null && powerState != null) {
                mPowerEnable.setChecked(powerState);
            }

        } catch (NullPointerException e) {
            Timber.w(e, "Response Error");
            if (getActivity() != null) {
                showSnackBarMessage(getActivity());
            }
        }
    }

    private void showSnackBarMessage(Activity activity) {
        mErrorSnack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(
                R.color.colorAccentRed)));
        mErrorSnack.show();
    }
}
