package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.VersionUtils;

import io.socket.client.Socket;

public class AdminSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_admin_settings, rootKey);
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

        PreferenceCategory manualCat = findPreference(getString(R.string.prefs_manual_mode_cat));
        Preference serverUpdates = findPreference(getString(R.string.prefs_server_updates_frag));
        Preference manualMode = findPreference(getString(R.string.prefs_manual_mode_frag));
        Preference backupRestore = findPreference(getString(R.string.prefs_admin_backup_restore));
        Preference historyDelete = findPreference(getString(R.string.prefs_admin_delete_history));
        Preference eventsDelete = findPreference(getString(R.string.prefs_admin_delete_events));
        Preference pelletsDelete = findPreference(getString(R.string.prefs_admin_delete_pellets));
        Preference pelletsLogDelete = findPreference(getString(R.string.prefs_admin_delete_pellets_log));
        Preference factoryReset = findPreference(getString(R.string.prefs_admin_factory_reset));
        Preference rebootSystem = findPreference(getString(R.string.prefs_admin_reboot));
        Preference shutdownSystem = findPreference(getString(R.string.prefs_admin_shutdown));
        Preference restartSystem = findPreference(getString(R.string.prefs_admin_restart));

        if (serverUpdates != null) {
            if (VersionUtils.isSupported(ServerVersions.V_127)) {
                serverUpdates.setOnPreferenceClickListener(preference -> {
                    showFragment(new ServerUpdateFragment());
                    return true;
                });
            } else {
                serverUpdates.setEnabled(false);
                serverUpdates.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_127));
            }
        }

        if (backupRestore != null) {
            if (VersionUtils.isSupported(ServerVersions.V_122)) {
                backupRestore.setOnPreferenceClickListener(preference -> {
                    showFragment(new BackupRestoreFragment());
                    return true;
                });
            } else {
                backupRestore.setEnabled(false);
                backupRestore.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_122));
            }
        }

        if (manualCat != null && manualMode != null) {
            if (VersionUtils.isSupported(ServerVersions.V_121)) {
                manualMode.setOnPreferenceClickListener(preference -> {
                    showFragment(new ManualSettingsFragment());
                    return true;
                });
            } else {
                manualCat.setEnabled(false);
                manualMode.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_121));
            }
        }

        if (historyDelete != null) {
            historyDelete.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.history_delete_content))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.delete),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) ->
                                            ServerControl.sendDeleteHistory(socket,
                                                    this::processPostResponse))
                            .build();
                    dialog.show();
                }
                return false;
            });

        }

        if (eventsDelete != null) {
            eventsDelete.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_delete_events_text))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.delete),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) ->
                                            ServerControl.sendDeleteEvents(socket,
                                                    this::processPostResponse))
                            .build();
                    dialog.show();
                }
                return false;
            });
        }

        if (pelletsLogDelete != null) {
            pelletsLogDelete.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_delete_pellets_log_text))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.delete),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) ->
                                            ServerControl.sendDeletePelletsLog(socket,
                                                    this::processPostResponse))
                            .build();
                    dialog.show();
                }
                return false;
            });
        }

        if (pelletsDelete != null) {
            pelletsDelete.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_delete_pellets_text))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.delete),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) ->
                                            ServerControl.sendDeletePellets(socket,
                                                    this::processPostResponse))
                            .build();
                    dialog.show();
                }
                return false;
            });
        }

        if (factoryReset != null) {
            factoryReset.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_factory_reset_text))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.reset),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) -> {
                                        ServerControl.sendFactoryReset(socket,
                                                this::processPostResponse);
                                        dialogInterface.dismiss();
                                    })
                            .build();
                    dialog.show();

                }
                return false;
            });
        }

        if (restartSystem != null) {
            if (VersionUtils.isSupported(ServerVersions.V_129)) {
                restartSystem.setOnPreferenceClickListener(preference -> {
                    if (socketConnected()) {
                        BottomButtonDialog dialog = new BottomButtonDialog.Builder(
                                requireActivity())
                                .setTitle(getString(R.string.dialog_confirm_action))
                                .setMessage(getString(R.string.settings_admin_restart_text))
                                .setAutoDismiss(true)
                                .setNegativeButton(getString(R.string.cancel),
                                        (dialogInterface, which) -> {
                                        })
                                .setPositiveButtonWithColor(getString(R.string.restart),
                                        R.color.dialog_positive_button_color_red,
                                        (dialogInterface, which) -> {
                                            ServerControl.sendRestartSystem(socket,
                                                    this::processPostResponse);
                                            dialogInterface.dismiss();
                                        })
                                .build();
                        dialog.show();
                    }
                    return false;
                });
            } else {
                restartSystem.setEnabled(false);
                restartSystem.setSummary(getString(R.string.disabled_option_settings,
                        ServerVersions.V_129));
            }
        }

        if (rebootSystem != null) {
            rebootSystem.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_reboot_text))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.reboot),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) -> {
                                        ServerControl.sendRebootSystem(socket,
                                                this::processPostResponse);
                                        dialogInterface.dismiss();
                                    })
                            .build();
                    dialog.show();
                }
                return false;
            });
        }

        if (shutdownSystem != null) {
            shutdownSystem.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_shutdown_text))
                            .setAutoDismiss(true)
                            .setNegativeButton(getString(R.string.cancel),
                                    (dialogInterface, which) -> {
                                    })
                            .setPositiveButtonWithColor(getString(R.string.shutdown),
                                    R.color.dialog_positive_button_color_red,
                                    (dialogInterface, which) -> {
                                        ServerControl.sendShutdownSystem(socket,
                                                this::processPostResponse);
                                        dialogInterface.dismiss();
                                    })
                            .build();
                    dialog.show();
                }
                return false;
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_admin);
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

    private void showFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_enter, R.animator.fragment_fade_exit)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void processPostResponse(String response) {
        processPostResponse(response, true);
    }

    private void processPostResponse(String response, boolean showSuccess) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        requireActivity().runOnUiThread(() -> {
            if (result.getResult().equals("error")) {
                AlertUtils.createErrorAlert(requireActivity(), result.getMessage(), false);
            } else if (showSuccess) {
                AlertUtils.createAlert(requireActivity(), R.string.settings_action_success, 1000);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_admin_debug)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setDebugMode(socket,
                                ((SwitchPreferenceCompat) preference).isChecked(),
                                response -> processPostResponse(response, false));
                    }
                }
            }
        }
    }

    private boolean socketConnected() {
        if (socket != null && socket.connected()) {
            return true;
        } else {
            AlertUtils.createErrorAlert(getActivity(), R.string.settings_error_offline, false);
            return false;
        }
    }
}
