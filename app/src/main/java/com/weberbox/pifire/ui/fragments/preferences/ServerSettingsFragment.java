package com.weberbox.pifire.ui.fragments.preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.ui.dialogs.CredentialsDialog;
import com.weberbox.pifire.ui.dialogs.CredentialsDialog.DialogAuthCallback;
import com.weberbox.pifire.utils.AlertUtils;

import dev.chrisbanes.insetter.Insetter;
import timber.log.Timber;

public class ServerSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ToolbarTitleCallback toolbarTitleCallback;
    private boolean reloadRequired = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackCallback);
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(messageReceiver,
                    new IntentFilter(Constants.INTENT_EXTRA_HEADERS));
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_server_settings, rootKey);
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

        Preference serverAddress = findPreference(getString(R.string.prefs_server_address));
        Preference credentials = findPreference(getString(R.string.prefs_server_credentials));

        if (serverAddress != null) {
            serverAddress.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(requireActivity(), ServerSetupActivity.class);
                intent.putExtra(Constants.INTENT_SETUP_BACK, true);
                startActivity(intent);
                return true;
            });

            serverAddress.setSummaryProvider((Preference.SummaryProvider<Preference>)
                    preference -> Prefs.getString(getString(R.string.prefs_server_address), ""));
        }

        if (credentials != null) {
            credentials.setOnPreferenceClickListener(preference -> {
                new CredentialsDialog(requireActivity(), callback).showDialog();
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
            toolbarTitleCallback.onTitleChange(getString(R.string.settings_server));
        }
        if (sharedPreferences != null) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(messageReceiver);
        }
        if (sharedPreferences != null) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reloadRequired = true;
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_server_basic_auth)
                        .equals(preference.getKey())) {
                    reloadRequired = true;
                }
                if (preference.getContext().getString(R.string.prefs_server_extra_headers)
                        .equals(preference.getKey())) {
                    reloadRequired = true;
                }
            }
        }
    }

    private final OnBackPressedCallback onBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            this.setEnabled(false);
            if (reloadRequired) {
                ((PiFireApplication) requireActivity().getApplication()).disconnectSocket();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(Constants.INTENT_SETUP_RESTART, true);
                startActivity(intent);
                requireActivity().finish();
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        }
    };

    private final DialogAuthCallback callback = new DialogAuthCallback() {
        @Override
        public void onAuthDialogSave(boolean success) {
            if (success) {
                reloadRequired = true;
            } else {
                AlertUtils.createErrorAlert(requireActivity(), R.string.settings_credentials_error, false);
            }
        }

        @Override
        public void onAuthDialogCancel() {

        }
    };
}

