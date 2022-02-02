package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Versions;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.OneSignalUtils;
import com.weberbox.pifire.utils.VersionUtils;

public class OneSignalRegisterFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_onesignal_consent, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        if (!VersionUtils.isSupported(Versions.V_127)) {
            AlertUtils.createErrorAlert(requireActivity(),
                    getString(R.string.disabled_option_dialog_message, Versions.V_127), 5000);
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_notif_onesignal_accepted)
                        .equals(preference.getKey())) {
                    boolean accepted = ((SwitchPreferenceCompat) preference).isChecked();
                    OneSignalUtils.provideUserConsent(accepted);
                    Prefs.putBoolean(preference.getContext().getString(
                            R.string.prefs_onesignal_consent_dismiss), accepted);
                }
            }
        }
    }
}
