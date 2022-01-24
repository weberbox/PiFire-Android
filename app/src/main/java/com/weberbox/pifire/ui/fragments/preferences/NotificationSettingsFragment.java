package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.OneSignalUtils;
import com.weberbox.pifire.utils.VersionUtils;

import io.socket.client.Socket;

public class NotificationSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_notification_settings, rootKey);
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PreferenceCategory oneSignalCat = findPreference(getString(R.string.prefs_notif_onesignal_cat));
        Preference oneSignalConsent = findPreference(getString(R.string.prefs_notif_onesignal_consent));
        SwitchPreferenceCompat influxDBEnable = findPreference(getString(R.string.prefs_notif_influxdb_enabled));

        if (oneSignalCat != null) {
            if (!AppConfig.USE_ONESIGNAL || PushConfig.ONESIGNAL_APP_ID.isEmpty()) {
                oneSignalCat.setVisible(false);
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
            if (!VersionUtils.isSupported("1.2.4")) {
                influxDBEnable.setEnabled(false);
                influxDBEnable.setSummary(getString(R.string.disabled_option_settings, "1.2.4"));
            }
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (AppConfig.USE_ONESIGNAL) {
            OneSignalUtils.checkOneSignalStatus(requireActivity(), mSocket);
        }
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
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_notifications);
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
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_notif_ifttt_api)
                        .equals(preference.getKey())) {
                    GrillControl.setIFTTTAPIKey(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_api)
                        .equals(preference.getKey())) {
                    GrillControl.setPushOverAPIKey(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_keys)
                        .equals(preference.getKey())) {
                    GrillControl.setPushOverUserKeys(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_url)
                        .equals(preference.getKey())) {
                    GrillControl.setPushOverURL(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_api)
                        .equals(preference.getKey())) {
                    GrillControl.setPushBulletAPIKey(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_channel)
                        .equals(preference.getKey())) {
                    GrillControl.setPushBulletChannel(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_url)
                        .equals(preference.getKey())) {
                    GrillControl.setPushBulletURL(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_url)
                        .equals(preference.getKey())) {
                    GrillControl.setInfluxDBUrl(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_token)
                        .equals(preference.getKey())) {
                    GrillControl.setInfluxDBToken(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_org)
                        .equals(preference.getKey())) {
                    GrillControl.setInfluxDBOrg(mSocket,
                            ((EditTextPreference) preference).getText());
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_bucket)
                        .equals(preference.getKey())) {
                    GrillControl.setInfluxDBBucket(mSocket,
                            ((EditTextPreference) preference).getText());
                }
            }
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_notif_ifttt_enabled)
                        .equals(preference.getKey())) {
                    GrillControl.setIFTTTEnabled(mSocket,
                            ((SwitchPreferenceCompat) preference).isChecked());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushover_enabled)
                        .equals(preference.getKey())) {
                    GrillControl.setPushOverEnabled(mSocket,
                            ((SwitchPreferenceCompat) preference).isChecked());
                }
                if (preference.getContext().getString(R.string.prefs_notif_pushbullet_enabled)
                        .equals(preference.getKey())) {
                    GrillControl.setPushBulletEnabled(mSocket,
                            ((SwitchPreferenceCompat) preference).isChecked());
                }
                if (preference.getContext().getString(R.string.prefs_notif_influxdb_enabled)
                        .equals(preference.getKey())) {
                    GrillControl.setInfluxDBEnabled(mSocket,
                            ((SwitchPreferenceCompat) preference).isChecked());
                }
                if (preference.getContext().getString(R.string.prefs_notif_onesignal_enabled)
                        .equals(preference.getKey())) {
                    GrillControl.setOneSignalEnabled(mSocket, ((SwitchPreferenceCompat)
                            preference).isChecked());
                }
            }
        }
    }
}
