package com.weberbox.pifire.ui.fragments.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.secure.SecureCore;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.updater.AppUpdater;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.utils.FirebaseUtils;

public class AppSettingsFragment extends PreferenceFragmentCompat {

    private AppUpdater mAppUpdater;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_app_settings, rootKey);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Preference firebaseToken = findPreference(getString(R.string.prefs_firebase_token));
        Preference updateCheck = findPreference(getString(R.string.prefs_app_updater_check_now));
        PreferenceCategory acraCat = findPreference(getString(R.string.prefs_acra_cat));
        SwitchPreferenceCompat acraEnabled = findPreference(getString(R.string.prefs_acra_enable));
        Preference serverSettings = findPreference(getString(R.string.prefs_server_settings));

        if (serverSettings != null) {
            serverSettings.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    final FragmentManager fm = getActivity().getSupportFragmentManager();
                    final FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(android.R.id.content, new ServerSettingsFragment())
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            });
        }

        if (firebaseToken != null) {
            firebaseToken.setVisible(AppConfig.USE_FIREBASE && AppConfig.DEBUG);
            firebaseToken.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    FirebaseUtils.getFirebaseToken(getActivity());
                }
                return true;
            });
        }

        if (acraCat != null && acraEnabled != null) {
            if (SecureCore.getAcraUrl().isEmpty()) {
                acraCat.setEnabled(false);
                acraEnabled.setSummary(getString(R.string.settings_enable_acra_disabled));
            }
        }

        if (updateCheck != null) {
            updateCheck.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    mAppUpdater = new AppUpdater(getActivity())
                            .setDisplay(Display.DIALOG)
                            .setButtonDoNotShowAgain(false)
                            .showAppUpToDate(true)
                            .showAppUpdateError(true)
                            .setForceCheck(true)
                            .setView(view)
                            .setUpdateFrom(UpdateFrom.JSON)
                            .setUpdateJSON(getString(R.string.def_app_update_check_url));
                    mAppUpdater.start();
                }
                return true;
            });
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_app);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAppUpdater != null) {
            mAppUpdater.stop();
        }
    }
}
