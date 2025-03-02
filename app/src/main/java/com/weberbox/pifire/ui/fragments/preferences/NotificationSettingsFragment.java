package com.weberbox.pifire.ui.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.PushConfig;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.dialogs.PrefsEditDialog;
import com.weberbox.pifire.ui.views.preferences.AppriseLocationPreference;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.OneSignalUtils;

import dev.chrisbanes.insetter.Insetter;
import io.socket.client.Socket;
import timber.log.Timber;

public class NotificationSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ToolbarTitleCallback toolbarTitleCallback;
    private AppriseLocationPreference appriseLocations;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_notification_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        PreferenceCategory oneSignalCat = findPreference(getString(R.string.prefs_notif_onesignal_cat));

        if (oneSignalCat != null) {
            if (PushConfig.ONESIGNAL_APP_ID.isEmpty()) {
                oneSignalCat.setVisible(false);
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState);
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

        Preference oneSignalConsent = findPreference(getString(R.string.prefs_notif_onesignal_consent));
        SwitchPreferenceCompat appriseEnabled = findPreference(getString(R.string.prefs_notif_apprise_enabled));
        appriseLocations = findPreference(getString(R.string.prefs_notif_apprise_locations));

        if (oneSignalConsent != null) {
            oneSignalConsent.setOnPreferenceClickListener(preference -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right,
                                R.anim.slide_out_right,
                                R.anim.slide_in_left,
                                R.anim.slide_out_left)
                        .replace(R.id.fragment_container, new OneSignalConsentFragment())
                        .addToBackStack(OneSignalConsentFragment.class.getName())
                        .commit();
                return true;
            });
        }

        if (appriseEnabled != null && appriseLocations != null) {
            appriseLocations.setVisible(appriseEnabled.isChecked());
        }

        OneSignalUtils.checkOneSignalStatus(requireActivity(), socket);
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
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (toolbarTitleCallback != null) {
            toolbarTitleCallback.onTitleChange(getString(R.string.settings_notifications));
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
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        AlertUtils.createErrorAlert(getActivity(),
                                result.getMessage(), false));
            }
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
                if (preference.getContext().getString(R.string.prefs_notif_apprise_enabled)
                        .equals(preference.getKey())) {
                    boolean enabled = ((SwitchPreferenceCompat) preference).isChecked();
                    appriseLocations.setVisible(enabled);
                    ServerControl.setAppriseEnabled(socket, enabled, this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_notif_onesignal_enabled)
                        .equals(preference.getKey())) {
                    boolean checked = ((SwitchPreferenceCompat) preference).isChecked();
                    if (checked) {
                        OneSignalUtils.promptForPushNotifications();
                    }
                    ServerControl.setOneSignalEnabled(socket, checked, this::processPostResponse);
                }
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof EditTextPreference) {
            if (getContext() != null) {
                new PrefsEditDialog(getContext(), preference).showDialog();
                return;
            }
        }
        super.onDisplayPreferenceDialog(preference);
    }
}
