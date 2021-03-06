package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;

public class HistorySettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_history_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        EditTextPreference historyDisplay = findPreference(getString(R.string.prefs_history_display));
        EditTextPreference historyPoints = findPreference(getString(R.string.prefs_history_points));

        if (historyDisplay != null && getActivity() != null) {
            historyDisplay.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(getActivity(), 1.0, null, editText));
            });
        }

        if (historyPoints != null) {
            historyPoints.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(getActivity(), 10.0, null, editText));
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
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_history);
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
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_history_display)
                        .equals(preference.getKey())) {
                    ServerControl.setHistoryMins(socket,
                            ((EditTextPreference) preference).getText(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_history_points)
                        .equals(preference.getKey())) {
                    ServerControl.setHistoryPoints(socket,
                            ((EditTextPreference) preference).getText(),
                            this::processPostResponse);
                }
            }
            if (preference instanceof SwitchPreferenceCompat) {
                if (preference.getContext().getString(R.string.prefs_history_auto)
                        .equals(preference.getKey())) {
                    ServerControl.setHistoryRefresh(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_history_clear)
                        .equals(preference.getKey())) {
                    ServerControl.sendHistoryClear(socket,
                            ((SwitchPreferenceCompat) preference).isChecked(),
                            this::processPostResponse);
                }
            }
        }
    }
}