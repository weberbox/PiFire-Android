package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.VersionUtils;

import io.socket.client.Socket;

public class TimersSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_timers_settings, rootKey);
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

        EditTextPreference shutdownTime = findPreference(getString(R.string.prefs_shutdown_time));
        EditTextPreference startupTime = findPreference(getString(R.string.prefs_startup_time));

        if (shutdownTime != null && getActivity() != null) {
            shutdownTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(getActivity(), editText));
            });
        }

        if (startupTime != null && getActivity() != null) {
            if (VersionUtils.isSupported("1.2.3")) {
                startupTime.setOnBindEditTextListener(editText -> {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.addTextChangedListener(
                            new EmptyTextListener(getActivity(), editText));
                });
            } else {
                startupTime.setEnabled(false);
                startupTime.setSummaryProvider(null);
                startupTime.setSummary(getString(R.string.disabled_option_settings, "1.2.3"));
            }
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_timers);
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

        if (preference != null && mSocket != null) {
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_shutdown_time)
                        .equals(preference.getKey())) {
                    if (VersionUtils.isSupported("1.2.2")) {
                        GrillControl.setShutdownTime(mSocket,
                                ((EditTextPreference) preference).getText());
                    } else {
                        GrillControl.setShutdownTimeDep(mSocket,
                                ((EditTextPreference) preference).getText());
                    }
                }
                if (preference.getContext().getString(R.string.prefs_startup_time)
                        .equals(preference.getKey())) {
                    GrillControl.setStartupTime(mSocket,
                            ((EditTextPreference) preference).getText());
                }
            }
        }
    }
}
