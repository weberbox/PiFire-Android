package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;

public class PelletSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_pellet_settings, rootKey);
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

        PreferenceCategory pelletWarnings = findPreference(getString(R.string.prefs_pellet_warning_cat));
        EditTextPreference pelletWarningTime = findPreference(getString(R.string.prefs_pellet_warning_time));
        EditTextPreference pelletWarningLevel = findPreference(getString(R.string.prefs_pellet_warning_level));
        EditTextPreference pelletsFull = findPreference(getString(R.string.prefs_pellet_full));
        EditTextPreference pelletsEmpty = findPreference(getString(R.string.prefs_pellet_empty));
        EditTextPreference augerRate = findPreference(getString(R.string.prefs_pellet_auger_rate));

        if (pelletWarnings != null && Prefs.getString(getString(R.string.prefs_pellet_warning_level),
                "").isEmpty()) {
            pelletWarnings.setVisible(false);
        }

        if (pelletWarningTime != null) {
            pelletWarningTime.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (pelletWarningLevel != null) {
            pelletWarningLevel.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, 100.0, editText));
            });
        }

        if (pelletsFull != null) {
            pelletsFull.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, 100.0, editText));
            });
        }

        if (pelletsEmpty != null) {
            pelletsEmpty.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, 100.0, editText));
            });
        }

        if (augerRate != null) {
            augerRate.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.0, null, editText));
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_pellets);
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
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(requireActivity(),
                            result.getMessage(), false));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && socket != null) {
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_pellet_warning_enabled)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletWarningEnabled(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pellet_prime_igniter)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletPrimeIgnition(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
            }
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_pellet_warning_time)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletWarningTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pellet_warning_level)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletWarningLevel(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pellet_empty)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletsEmpty(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pellet_full)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletsFull(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pellet_auger_rate)
                        .equals(preference.getKey())) {
                    ServerControl.setPelletsAugerRate(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
        }
    }
}
