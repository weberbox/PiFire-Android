package com.weberbox.pifire.ui.fragments.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.Snackbar;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.interfaces.AdminCallbackInterface;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.AdminActionDialog;

import io.socket.client.Socket;

public class AdminSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, AdminCallbackInterface {

    private Socket mSocket;
    private Snackbar mErrorSnack;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_admin_settings, rootKey);
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

        mErrorSnack = Snackbar.make(view, R.string.prefs_not_connected, Snackbar.LENGTH_LONG);

        PreferenceCategory manualModeCat = findPreference(getString(R.string.prefs_manual_mode_cat));
        Preference manualMode = findPreference(getString(R.string.prefs_manual_mode_frag));
        Preference historyDelete = findPreference(getString(R.string.prefs_admin_delete_history));
        Preference eventsDelete = findPreference(getString(R.string.prefs_admin_delete_events));
        Preference pelletsDelete = findPreference(getString(R.string.prefs_admin_delete_pellets));
        Preference pelletsLogDelete = findPreference(getString(R.string.prefs_admin_delete_pellets_log));
        Preference factoryReset = findPreference(getString(R.string.prefs_admin_factory_reset));
        Preference rebootSystem = findPreference(getString(R.string.prefs_admin_reboot));
        Preference shutdownSystem = findPreference(getString(R.string.prefs_admin_shutdown));

        if (manualModeCat != null) {
            manualModeCat.setVisible(AppConfig.IS_DEV_BUILD);
        }

        if (manualMode != null) {
            manualMode.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    final FragmentManager fm = getActivity().getSupportFragmentManager();
                    final FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(android.R.id.content, new ManualSettingsFragment())
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            });
        }

        if (historyDelete != null) {
            historyDelete.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_HISTORY);
                return true;
            });
        }

        if (eventsDelete != null) {
            eventsDelete.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_EVENTS);
                return true;
            });
        }

        if (pelletsLogDelete != null) {
            pelletsLogDelete.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_PELLET_LOG);
                return true;
            });
        }

        if (pelletsDelete != null) {
            pelletsDelete.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_PELLET);
                return true;
            });
        }

        if (factoryReset != null) {
            factoryReset.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_RESET);
                return true;
            });
        }

        if (rebootSystem != null) {
            rebootSystem.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_REBOOT);
                return true;
            });
        }

        if (shutdownSystem != null) {
            shutdownSystem.setOnPreferenceClickListener(preference -> {
                showAdminDialog(Constants.ACTION_ADMIN_SHUTDOWN);
                return true;
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_admin);
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

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_admin_debug)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setDebugMode(mSocket,
                                ((SwitchPreferenceCompat) preference).isChecked());
                    }
                }
            }
        }
    }

    @Override
    public void onDialogPositive(int action) {
        if (mSocket != null && mSocket.connected()) {
            switch (action) {
                case Constants.ACTION_ADMIN_HISTORY:
                    GrillControl.setDeleteHistory(mSocket);
                    break;
                case Constants.ACTION_ADMIN_EVENTS:
                    GrillControl.setDeleteEvents(mSocket);
                    break;
                case Constants.ACTION_ADMIN_PELLET_LOG:
                    GrillControl.setDeletePelletsLog(mSocket);
                    break;
                case Constants.ACTION_ADMIN_PELLET:
                    GrillControl.setDeletePellets(mSocket);
                    break;
                case Constants.ACTION_ADMIN_RESET:
                    GrillControl.setFactoryReset(mSocket);
                    break;
                case Constants.ACTION_ADMIN_REBOOT:
                    GrillControl.setRebootSystem(mSocket);
                    break;
                case Constants.ACTION_ADMIN_SHUTDOWN:
                    GrillControl.setShutdownSystem(mSocket);
                    break;
            }
        }
    }

    private void showAdminDialog(int type) {
        if (getActivity() != null) {
            if (mSocket != null && mSocket.connected()) {
                AdminActionDialog adminDialog = new AdminActionDialog(getActivity(),
                        this, type);
                adminDialog.showDialog();
            } else {
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
