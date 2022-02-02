package com.weberbox.pifire.ui.fragments.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;

public class ServerSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private boolean reloadRequired = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackCallback);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_server_settings, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        Preference serverAddress = findPreference(getString(R.string.prefs_server_address));
        EditTextPreference authPass = findPreference(getString(R.string.prefs_server_basic_auth_password));
        EditTextPreference authUser = findPreference(getString(R.string.prefs_server_basic_auth_user));

        if (serverAddress != null) {
            serverAddress.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), ServerSetupActivity.class);
                    startActivity(intent);
                }
                return true;
            });

            serverAddress.setSummaryProvider((Preference.SummaryProvider<Preference>)
                    preference -> Prefs.getString(getString(R.string.prefs_server_address), ""));
        }

        if (authUser != null) {
            authUser.setOnBindEditTextListener(editText -> editText.addTextChangedListener(
                    new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if (s.length() == 0) {
                                editText.setError(getString(R.string.settings_username_blank_error));
                            } else {
                                editText.setError(null);
                            }
                        }
                    }));

            authUser.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals("Error")) {
                    Toast.makeText(getActivity(), R.string.settings_username_encrypt_error,
                            Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    reloadRequired = true;
                    return true;
                }
            });
        }

        if (authPass != null) {
            authPass.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue.equals("Error")) {
                    Toast.makeText(getActivity(), R.string.settings_password_encrypt_error,
                            Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    reloadRequired = true;
                    return true;
                }
            });

            authPass.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 0) {
                            editText.setError(getString(R.string.settings_password_blank_error));
                        } else {
                            editText.setError(null);
                        }
                    }
                });
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_server);
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
                if (preference.getContext().getString(R.string.prefs_server_basic_auth)
                        .equals(preference.getKey())) {
                    reloadRequired = true;
                }
            }
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_server_basic_auth_password)
                        .equals(preference.getKey())) {
                    reloadRequired = true;
                }
                if (preference.getContext().getString(R.string.prefs_server_basic_auth_user)
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
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(Constants.INTENT_SETUP_RESTART, true);
                startActivity(intent);
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                requireActivity().onBackPressed();
            }
        }
    };
}

