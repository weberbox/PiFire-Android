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
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.dialogs.PrefsEditDialog;
import com.weberbox.pifire.utils.AlertUtils;

import dev.chrisbanes.insetter.Insetter;
import io.socket.client.Socket;
import timber.log.Timber;

public class NameSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ToolbarTitleCallback toolbarTitleCallback;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_grill_name_settings, rootKey);
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

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .applyToView(getListView());

        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);
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
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (toolbarTitleCallback != null) {
            toolbarTitleCallback.onTitleChange(getString(R.string.settings_grill_name));
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
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        AlertUtils.createErrorAlert(getActivity(),
                                result.getMessage(), false));
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && socket != null) {
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_grill_name)
                        .equals(preference.getKey())) {
                    ServerControl.setGrillName(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(@NonNull Preference preference) {
        if (preference instanceof EditTextPreference) {
            if (getContext() != null) {
                new PrefsEditDialog(getContext(), preference).showDialog();
                return;
            }
        }
        super.onDisplayPreferenceDialog(preference);
    }
}