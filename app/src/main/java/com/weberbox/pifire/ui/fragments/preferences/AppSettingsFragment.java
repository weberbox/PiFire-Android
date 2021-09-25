package com.weberbox.pifire.ui.fragments.preferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.updater.AppUpdater;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.utils.FirebaseUtils;

public class AppSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = AppSettingsFragment.class.getSimpleName();

    private boolean mBasicAuthChanged = false;
    private AppUpdater mAppUpdater;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_app_settings, rootKey);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Preference serverAddress = findPreference(getString(R.string.prefs_server_address));
        EditTextPreference authPass = findPreference(getString(R.string.prefs_server_basic_auth_password));
        EditTextPreference authUser = findPreference(getString(R.string.prefs_server_basic_auth_user));
        PreferenceCategory debugCat = findPreference(getString(R.string.prefs_debug_cat));
        Preference firebaseToken = findPreference(getString(R.string.prefs_firebase_token));
        Preference updateCheck = findPreference(getString(R.string.prefs_app_updater_check_now));

        if (serverAddress != null) {
            serverAddress.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), ServerSetupActivity.class);
                        startActivity(intent);
                    }
                    return true;
                }
            });

            serverAddress.setSummaryProvider(new Preference.SummaryProvider<Preference>() {
                @Override
                public CharSequence provideSummary(Preference preference) {
                    return Prefs.getString(getString(R.string.prefs_server_address), "");
                }
            });
        }

        if (authUser != null) {
            authUser.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
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
                                editText.setError(getString(R.string.settings_username_blank_error));
                            } else {
                                editText.setError(null);
                            }
                        }
                    });
                }
            });

            authUser.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.equals("Error")) {
                        Toast.makeText(getActivity(), R.string.settings_username_encrypt_error,
                                Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        mBasicAuthChanged = true;
                        return true;
                    }
                }
            });

            authUser.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                    if (preference.getText().equals("Error")) {
                        return getString(R.string.settings_username_encrypt_error);
                    } else if (preference.getText().equals("")) {
                        return getString(R.string.not_set);
                    } else {
                        return preference.getText();
                    }
                }
            });
        }

        if (authPass != null) {
            authPass.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.equals("Error")) {
                        Toast.makeText(getActivity(), R.string.settings_password_encrypt_error,
                                Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        mBasicAuthChanged = true;
                        return true;
                    }
                }
            });

            authPass.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                    if (preference.getText().equals("Error")) {
                        return getString(R.string.settings_password_encrypt_error);
                    } else if (preference.getText().equals("")) {
                        return getString(R.string.not_set);
                    } else {
                        return preference.getText().replaceAll(".", "*");
                    }
                }
            });

            authPass.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
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
                }
            });
        }

        if (debugCat != null) {
            debugCat.setVisible(AppConfig.DEBUG);
        }

        if (firebaseToken != null) {
            firebaseToken.setVisible(AppConfig.USE_FIREBASE);
            firebaseToken.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (getActivity() != null) {
                        FirebaseUtils.getFirebaseToken(getActivity());
                    }
                    return true;
                }
            });
        }

        if (updateCheck != null) {
            updateCheck.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (getActivity() != null) {
                        mAppUpdater = new AppUpdater(getActivity())
                                .setDisplay(Display.DIALOG)
                                .setButtonDoNotShowAgain(false)
                                .showAppUpToDate(true)
                                .showAppUpdateError(true)
                                .setView(view)
                                .setUpdateFrom(UpdateFrom.JSON)
                                .setUpdateJSON(getString(R.string.def_app_update_check_url));
                        mAppUpdater.start();
                    }
                    return true;
                }
            });
        }

        return view;
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
        if (mAppUpdater != null) {
            mAppUpdater.stop();
        }
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                this.setEnabled(false);
                if (mBasicAuthChanged) {
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

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_server_basic_auth).equals(preference.getKey())) {
                    mBasicAuthChanged = true;
                }
            }
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_server_basic_auth_password).equals(preference.getKey())) {
                    mBasicAuthChanged = true;
                }
                if (preference.getContext().getString(R.string.prefs_server_basic_auth_user).equals(preference.getKey())) {
                    mBasicAuthChanged = true;
                }
            }
        }
    }
}
