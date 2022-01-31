package com.weberbox.pifire.ui.fragments.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.GrillProbeModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeProfileModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;

public class ProbeSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_probe_settings, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        ListPreference tempUnits = findPreference(getString(R.string.prefs_grill_units));
        ListPreference grillProbe = findPreference(getString(R.string.prefs_grill_probe));
        ListPreference grillProbe1Type = findPreference(getString(R.string.prefs_grill_probe_one_type));
        ListPreference grillProbe2Type = findPreference(getString(R.string.prefs_grill_probe_two_type));
        ListPreference grillProbeType = findPreference(getString(R.string.prefs_grill_probe_type));
        ListPreference probeOneType = findPreference(getString(R.string.prefs_probe_one_type));
        ListPreference probeTwoType = findPreference(getString(R.string.prefs_probe_two_type));

        Map<String, ProbeProfileModel> profilesHash = getProbeProfilesHash();
        String[] probeEntryValues = profilesHash.keySet().toArray(new String[0]);
        ArrayList<String> profileEntryNames = new ArrayList<>();

        for (ProbeProfileModel p : profilesHash.values()) {
            profileEntryNames.add(p.getName());
        }

        String[] probeEntryNames = profileEntryNames.toArray(new String[0]);

        if (grillProbe != null && grillProbe1Type != null && grillProbe2Type != null
                && grillProbeType != null) {
            if (Prefs.getBoolean(getString(R.string.prefs_four_probe))) {
                grillProbeType.setVisible(false);

                if (!profilesHash.isEmpty()) {
                    grillProbe1Type.setEntries(probeEntryNames);
                    grillProbe1Type.setEntryValues(probeEntryValues);
                    grillProbe2Type.setEntries(probeEntryNames);
                    grillProbe2Type.setEntryValues(probeEntryValues);
                }

                Map<String, GrillProbeModel> grillProbeHash = getGrillProbeHash();
                String[] grillProbeEntryValues = grillProbeHash.keySet().toArray(new String[0]);
                ArrayList<String> grillProbeEntryNames = new ArrayList<>();

                for (GrillProbeModel gp : grillProbeHash.values()) {
                    grillProbeEntryNames.add(gp.getName());
                }

                String[] grillEntries = grillProbeEntryNames.toArray(new String[0]);

                if (!grillProbeHash.isEmpty()) {
                    grillProbe.setEntries(grillEntries);
                    grillProbe.setEntryValues(grillProbeEntryValues);
                }

            } else {
                grillProbe.setVisible(false);
                grillProbe1Type.setVisible(false);
                grillProbe2Type.setVisible(false);

                if (!profilesHash.isEmpty()) {
                    grillProbeType.setEntries(probeEntryNames);
                    grillProbeType.setEntryValues(probeEntryValues);
                }
            }
        }

        if (probeOneType != null && probeTwoType != null) {
            if (!profilesHash.isEmpty()) {
                probeOneType.setEntries(probeEntryNames);
                probeOneType.setEntryValues(probeEntryValues);
                probeTwoType.setEntries(probeEntryNames);
                probeTwoType.setEntryValues(probeEntryValues);
            }
        }

        if (tempUnits != null) {
            if (!VersionUtils.isSupported("1.2.2")) {
                tempUnits.setEnabled(false);
                tempUnits.setSummaryProvider(null);
                tempUnits.setSummary(getString(R.string.disabled_option_settings, "1.2.2"));
            }
        }

        return view;
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
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_probe);
        }
        if (getPreferenceScreen().getSharedPreferences() != null) {
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getPreferenceScreen().getSharedPreferences() != null) {
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
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
            if (preference instanceof ListPreference) {
                if (preference.getContext().getString(R.string.prefs_grill_units)
                        .equals(preference.getKey())) {
                    ServerControl.setTempUnits(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe)
                        .equals(preference.getKey())) {
                    String probe = ((ListPreference) preference).getValue();
                    ServerControl.setGrillProbe(socket, getGrillProbesEnabled(probe), probe,
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe_type)
                        .equals(preference.getKey())) {
                    ServerControl.setGrillProbeType(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe_one_type)
                        .equals(preference.getKey())) {
                    ServerControl.setGrillProbe1Type(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe_two_type)
                        .equals(preference.getKey())) {
                    ServerControl.setGrillProbe2Type(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_probe_one_type)
                        .equals(preference.getKey())) {
                    ServerControl.setProbe1Type(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_probe_two_type)
                        .equals(preference.getKey())) {
                    ServerControl.setProbe2Type(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
            }
        }
    }

    private HashMap<String, ProbeProfileModel> getProbeProfilesHash() {
        String defValue = new Gson().toJson(new HashMap<String, ProbeProfileModel>());
        String jsonProfiles = Prefs.getString(getString(R.string.prefs_probe_profiles), defValue);
        TypeToken<HashMap<String, ProbeProfileModel>> token = new TypeToken<>() {
        };
        return new Gson().fromJson(jsonProfiles, token.getType());
    }

    private HashMap<String, GrillProbeModel> getGrillProbeHash() {
        String defValue = new Gson().toJson(new HashMap<String, GrillProbeModel>());
        String jsonProfiles = Prefs.getString(getString(R.string.prefs_grill_probes), defValue);
        TypeToken<HashMap<String, GrillProbeModel>> token = new TypeToken<>() {
        };
        return new Gson().fromJson(jsonProfiles, token.getType());
    }

    private List<Integer> getGrillProbesEnabled(String probe) {
        List<Integer> probes = Arrays.asList(1, 0, 0);
        switch (probe) {
            case "grill_probe1":
                probes = Arrays.asList(1, 0, 0);
                break;
            case "grill_probe2":
                probes = Arrays.asList(0, 1, 0);
                break;
            case "grill_probe3":
                probes = Arrays.asList(0, 0, 1);
                break;
        }
        return probes;
    }
}
