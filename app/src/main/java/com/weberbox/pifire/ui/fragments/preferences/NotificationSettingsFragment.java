package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.config.PushConfig;
import com.weberbox.pifire.constants.Versions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.OneSignalUtils;
import com.weberbox.pifire.utils.VersionUtils;

import io.socket.client.Socket;

public class NotificationSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_notification_settings, rootKey);
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

        PreferenceCategory oneSignalCat = findPreference(getString(R.string.prefs_notif_onesignal_cat));
        SwitchPreferenceCompat oneSignal = findPreference(getString(R.string.prefs_notif_onesignal_enabled));
        Preference oneSignalConsent = findPreference(getString(R.string.prefs_notif_onesignal_consent));
        SwitchPreferenceCompat influxDBEnable = findPreference(getString(R.string.prefs_notif_influxdb_enabled));

        if (oneSignalCat != null) {
            if (!AppConfig.USE_ONESIGNAL || PushConfig.ONESIGNAL_APP_ID.isEmpty()) {
                oneSignalCat.setVisible(false);
            }
        }

        if (oneSignal != null) {
            if (!VersionUtils.isSupported(Versions.V_127)) {
                oneSignal.setEnabled(false);
                oneSignal.setSummary(getString(R.string.disabled_option_settings, Versions.V_127));
            }
        }

        if (oneSignalConsent != null) {
            oneSignalConsent.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    final FragmentManager fm = getActivity().getSupportFragmentManager();
                    final FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(android.R.id.content, new OneSignalRegisterFragment())
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            });
        }

        if (influxDBEnable != null && getActivity() != null) {
            if (!VersionUtils.isSupported(Versions.V_127)) {
                influxDBEnable.setEnabled(false);
                influxDBEnable.setSummary(getString(R.string.disabled_option_settings, Versions.V_127));
            }
        }

        if (AppConfig.USE_ONESIGNAL) {
            OneSignalUtils.checkOneSignalStatus(requireActivity(), socket);
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
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_notifications);
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
                if (preference.getContext().getString(R.string.prefs_notif_ifttt_api)
                        .equals(preference.getKey())) {
                    ServerControl.setIFTTTAPIKey(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_api)
                        .equals(preference.getKey())) {
                    ServerControl.setPushOverAPIKey(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_keys)
                        .equals(preference.getKey())) {
                    ServerControl.setPushOverUserKeys(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_url)
                        .equals(preference.getKey())) {
                    ServerControl.setPushOverURL(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_api)
                        .equals(preference.getKey())) {
                    ServerControl.setPushBulletAPIKey(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_url)
                        .equals(preference.getKey())) {
                    ServerControl.setPushBulletURL(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_url)
                        .equals(preference.getKey())) {
                    ServerControl.setInfluxDBUrl(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_token)
                        .equals(preference.getKey())) {
                    ServerControl.setInfluxDBToken(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_org)
                        .equals(preference.getKey())) {
                    ServerControl.setInfluxDBOrg(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_bucket)
                        .equals(preference.getKey())) {
                    ServerControl.setInfluxDBBucket(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_notif_ifttt_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setIFTTTEnabled(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setPushOverEnabled(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setPushBulletEnabled(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setInfluxDBEnabled(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_onesignal_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setOneSignalEnabled(socket, ((SwitchPreferenceCompat)
                            preference).isChecked(), this::processPostResponse);
                }
            }
        }
    }
}
