package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.utils.FirebaseUtils;

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        PreferenceCategory firebase = findPreference(getString(R.string.prefs_notif_firebase));

        if (firebase != null) {
            firebase.setVisible(AppConfig.USE_FIREBASE);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
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

        if (preference != null) {
            if (mSocket != null) {
                if (preference instanceof EditTextPreference) {
                    if (preference.getContext().getString(R.string.prefs_notif_ifttt_api)
                            .equals(preference.getKey())) {
                        GrillControl.setIFTTTAPIKey(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushover_api)
                            .equals(preference.getKey())) {
                        GrillControl.setPushOverAPIKey(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushover_keys)
                            .equals(preference.getKey())) {
                        GrillControl.setPushOverUserKeys(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushover_url)
                            .equals(preference.getKey())) {
                        GrillControl.setPushOverURL(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushbullet_api)
                            .equals(preference.getKey())) {
                        GrillControl.setPushBulletAPIKey(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushbullet_channel)
                            .equals(preference.getKey())) {
                        GrillControl.setPushBulletChannel(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushbullet_url)
                            .equals(preference.getKey())) {
                        GrillControl.setPushBulletURL(mSocket, ((EditTextPreference) preference).getText());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_firebase_serverkey)
                            .equals(preference.getKey())) {
                        GrillControl.setFirebaseServerKey(mSocket, ((EditTextPreference) preference).getText());
                    }
                }
                if (preference instanceof SwitchPreferenceCompat) {
                    if (preference.getContext().getString(R.string.prefs_notif_ifttt_enabled)
                            .equals(preference.getKey())) {
                        GrillControl.setIFTTTEnabled(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushover_enabled)
                            .equals(preference.getKey())) {
                        GrillControl.setPushOverEnabled(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_pushbullet_enabled)
                            .equals(preference.getKey())) {
                        GrillControl.setPushBulletEnabled(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                    }
                    if (preference.getContext().getString(R.string.prefs_notif_firebase_enabled)
                            .equals(preference.getKey())) {
                        GrillControl.setFirebaseEnabled(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
                        FirebaseUtils.toggleFirebaseSubscription(((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
            }
        }
    }
}
