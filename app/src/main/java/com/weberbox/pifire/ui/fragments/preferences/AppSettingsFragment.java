package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.PrefsEditDialog;
import com.weberbox.pifire.ui.dialogs.PrefsListDialog;
import com.weberbox.pifire.update.UpdateUtils;
import com.weberbox.pifire.utils.CrashUtils;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;
import io.sentry.Sentry;

public class AppSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private UpdateUtils updateUtils;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_app_settings, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        updateUtils = new UpdateUtils(requireActivity(), activityResultLauncher);

        Preference updateCheck = findPreference(getString(R.string.prefs_app_updater_check_now));
        PreferenceCategory crashCat = findPreference(getString(R.string.prefs_crash_cat));
        SwitchPreferenceCompat crashEnabled = findPreference(getString(R.string.prefs_crash_enable));
        SwitchPreferenceCompat devCrashEnabled = findPreference(getString(R.string.prefs_dev_crash_enable));
        Preference serverSettings = findPreference(getString(R.string.prefs_server_settings));
        PreferenceCategory updaterCat = findPreference(getString(R.string.prefs_app_updater_cat));

        getListView().setClipToPadding(false);

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .margin(WindowInsetsCompat.Type.systemBars(), Side.BOTTOM)
                .applyToView(getListView());

        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);

        if (serverSettings != null) {
            serverSettings.setOnPreferenceClickListener(preference -> {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fragment_fade_enter,
                                R.animator.fragment_fade_exit)
                        .replace(R.id.fragment_container, new ServerSettingsFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            });
        }

        if (crashCat != null && crashEnabled != null) {
            if (getString(R.string.def_sentry_io_dsn).isEmpty()) {
                crashCat.setEnabled(false);
                crashEnabled.setChecked(false);
                crashEnabled.setSummary(getString(R.string.settings_enable_crash_disabled));
            }
        }

        if (devCrashEnabled != null) {
            devCrashEnabled.setVisible(AppConfig.IS_DEV_BUILD && BuildConfig.DEBUG);
        }

        if (updaterCat != null) {
            updaterCat.setVisible(AppConfig.IS_PLAY_BUILD);
        }

        if (updateCheck != null) {
            updateCheck.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    updateUtils.checkForUpdate(true, false);
                }
                return true;
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_app);
        }
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (updateUtils != null) {
            updateUtils.stopAppUpdater();
        }
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    private final ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        if (result != null && updateUtils != null) {
                            updateUtils.handleUpdateRequest(result.getResultCode());
                        }
                    });

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_crash_enable)
                        .equals(preference.getKey())) {
                    if (((SwitchPreferenceCompat) preference).isChecked() && !Sentry.isEnabled()) {
                        CrashUtils.initCrashReporting(requireActivity().getApplicationContext());
                    }
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
        if (preference instanceof ListPreference) {
            if (getContext() != null) {
                new PrefsListDialog(getContext(), preference).showDialog();
                return;
            }
        }
        super.onDisplayPreferenceDialog(preference);
    }
}
