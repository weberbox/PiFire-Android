package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.interfaces.AdminCallbackInterface;
import com.weberbox.pifire.ui.dialogs.AdminActionDialog;

import io.socket.client.Socket;

public class AdminSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, AdminCallbackInterface {
    private static final String TAG = AdminSettingsFragment.class.getSimpleName();

    private Socket mSocket;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Preference historyDelete = (Preference) findPreference(getString(R.string.prefs_admin_delete_history));
        Preference eventsDelete = (Preference) findPreference(getString(R.string.prefs_admin_delete_events));
        Preference pelletsDelete = (Preference) findPreference(getString(R.string.prefs_admin_delete_pellets));
        Preference pelletsLogDelete = (Preference) findPreference(getString(R.string.prefs_admin_delete_pellets_log));
        Preference factoryReset = (Preference) findPreference(getString(R.string.prefs_admin_factory_reset));
        Preference rebootSystem = (Preference) findPreference(getString(R.string.prefs_admin_reboot));
        Preference shutdownSystem = (Preference) findPreference(getString(R.string.prefs_admin_shutdown));

        if (historyDelete != null) {
            historyDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_HISTORY);
                    return true;
                }
            });
        }

        if (eventsDelete != null) {
            eventsDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_EVENTS);
                    return true;
                }
            });
        }

        if (pelletsLogDelete != null) {
            pelletsLogDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_PELLET_LOG);
                    return true;
                }
            });
        }

        if (pelletsDelete != null) {
            pelletsDelete.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_PELLET);
                    return true;
                }
            });
        }

        if (factoryReset != null) {
            factoryReset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_RESET);
                    return true;
                }
            });
        }

        if (rebootSystem != null) {
            rebootSystem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_REBOOT);
                    return true;
                }
            });
        }

        if (shutdownSystem != null) {
            shutdownSystem.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showAdminDialog(Constants.ACTION_ADMIN_SHUTDOWN);
                    return true;
                }
            });
        }

        return view;
    }

    private void showAdminDialog(int type) {
        if (getActivity() != null) {
            AdminActionDialog adminDialog = new AdminActionDialog(getActivity(), this, type);
            adminDialog.showDialog();
        }
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
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_admin_debug)
                        .equals(preference.getKey())) {
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setDebugMode(mSocket, ((SwitchPreferenceCompat) preference).isChecked());
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
}
