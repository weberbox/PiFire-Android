package com.weberbox.pifire.ui.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.utils.AlertUtils;

import dev.chrisbanes.insetter.Insetter;
import io.socket.client.Socket;
import timber.log.Timber;

public class AdminSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ToolbarTitleCallback toolbarTitleCallback;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_admin_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
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

        PreferenceCategory manualCat = findPreference(getString(R.string.prefs_manual_mode_cat));
        Preference manualMode = findPreference(getString(R.string.prefs_manual_mode_frag));
        Preference historyDelete = findPreference(getString(R.string.prefs_admin_delete_history));
        Preference eventsDelete = findPreference(getString(R.string.prefs_admin_delete_events));
        Preference pelletsDelete = findPreference(getString(R.string.prefs_admin_delete_pellets));
        Preference pelletsLogDelete = findPreference(getString(R.string.prefs_admin_delete_pellets_log));
        Preference factoryReset = findPreference(getString(R.string.prefs_admin_factory_reset));
        Preference rebootSystem = findPreference(getString(R.string.prefs_admin_reboot));
        Preference shutdownSystem = findPreference(getString(R.string.prefs_admin_shutdown));
        Preference restartSystem = findPreference(getString(R.string.prefs_admin_restart));

        if (manualCat != null && manualMode != null) {
            manualMode.setOnPreferenceClickListener(preference -> {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left,
                                R.anim.slide_out_left,
                                R.anim.slide_in_right,
                                R.anim.slide_out_right)
                        .replace(R.id.fragment_container, new ManualSettingsFragment())
                        .addToBackStack(ManualSettingsFragment.class.getName())
                        .commit();
                return false;
            });
        }

        if (historyDelete != null) {
            historyDelete.setOnPreferenceClickListener(preference -> {
                if (socketConnected()) {
                    BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                            .setTitle(getString(R.string.dialog_confirm_action))
                            .setMessage(getString(R.string.settings_admin_delete_history_text))
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            toolbarTitleCallback = (ToolbarTitleCallback) context;
        } catch (ClassCastException e) {
            Timber.e(e, "Activity does not implement callback");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (toolbarTitleCallback != null) {
            toolbarTitleCallback.onTitleChange(getString(R.string.settings_admin));
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
        processPostResponse(response, true);
    }

    private void processPostResponse(String response, boolean showSuccess) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                if (result.getResult().equals("error")) {
                    AlertUtils.createErrorAlert(getActivity(), result.getMessage(), false);
                } else if (showSuccess) {
                    AlertUtils.createAlert(getActivity(), R.string.settings_action_success, 1000);
                }
            });
        }
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
                if (preference.getContext().getString(R.string.prefs_admin_boot_to_monitor)
                        .equals(preference.getKey())) {
                    if (socket != null && socket.connected()) {
                        ServerControl.setBootToMonitor(socket,
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
            AlertUtils.createErrorAlert(requireActivity(), R.string.settings_error_offline, false);
            return false;
        }
    }
}
