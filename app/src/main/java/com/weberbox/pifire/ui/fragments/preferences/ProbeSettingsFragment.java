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
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerVersions;
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

    private SharedPreferences sharedPreferences;
    private ListPreference adcGrillProbeOne, adcGrillProbeTwo, adcProbeOne, adcProbeTwo;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sharedPreferences = getPreferenceScreen().getSharedPreferences();

        ListPreference tempUnits = findPreference(getString(R.string.prefs_grill_units));
        ListPreference grillProbe = findPreference(getString(R.string.prefs_grill_probe));
        ListPreference grillProbe1Type = findPreference(getString(R.string.prefs_grill_probe_one_type));
        ListPreference grillProbe2Type = findPreference(getString(R.string.prefs_grill_probe_two_type));
        ListPreference grillProbeType = findPreference(getString(R.string.prefs_grill_probe_type));
        ListPreference probeOneType = findPreference(getString(R.string.prefs_probe_one_type));
        ListPreference probeTwoType = findPreference(getString(R.string.prefs_probe_two_type));
        PreferenceCategory adcProbeAssignmentCat = findPreference(getString(R.string.prefs_adc_assignment_cat));
        adcGrillProbeOne = findPreference(getString(R.string.prefs_adc_grill_probe_one));
        adcGrillProbeTwo = findPreference(getString(R.string.prefs_adc_grill_probe_two));
        adcProbeOne = findPreference(getString(R.string.prefs_adc_probe_one));
        adcProbeTwo = findPreference(getString(R.string.prefs_adc_probe_two));

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

        if (adcProbeAssignmentCat != null) {
            if (!VersionUtils.isSupported(ServerVersions.V_135)) {
                adcProbeAssignmentCat.setEnabled(false);
                adcProbeAssignmentCat.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_135));
            }
        }

        ArrayList<String> probeOptions = new ArrayList<>(getADCAssignmentOptions());
        ArrayList<String> probeSources = new ArrayList<>(getADCAssignmentSources());

        if (adcGrillProbeOne != null) {
            adcGrillProbeOne.setEntries(probeOptions.toArray(new String[0]));
            adcGrillProbeOne.setEntryValues(probeOptions.toArray(new String[0]));
            adcGrillProbeOne.setValue(probeSources.get(0));
            if (Prefs.getBoolean(getString(R.string.prefs_four_probe))) {
                adcGrillProbeOne.setTitle(getString(R.string.settings_adc_grill_probe_one));
            }
        } else {
            if (adcProbeAssignmentCat != null) {
                adcProbeAssignmentCat.setEnabled(false);
            }
        }

        if (adcGrillProbeTwo != null) {
            if (!Prefs.getBoolean(getString(R.string.prefs_four_probe))) {
                adcGrillProbeTwo.setVisible(false);
            } else {
                adcGrillProbeTwo.setEntries(probeOptions.toArray(new String[0]));
                adcGrillProbeTwo.setEntryValues(probeOptions.toArray(new String[0]));
                adcGrillProbeTwo.setValue(probeSources.get(3));
            }
        } else {
            if (adcProbeAssignmentCat != null) {
                adcProbeAssignmentCat.setEnabled(false);
            }
        }

        if (adcProbeOne != null) {
            adcProbeOne.setEntries(probeOptions.toArray(new String[0]));
            adcProbeOne.setEntryValues(probeOptions.toArray(new String[0]));
            adcProbeOne.setValue(probeSources.get(1));
        } else {
            if (adcProbeAssignmentCat != null) {
                adcProbeAssignmentCat.setEnabled(false);
            }
        }

        if (adcProbeTwo != null) {
            adcProbeTwo.setEntries(probeOptions.toArray(new String[0]));
            adcProbeTwo.setEntryValues(probeOptions.toArray(new String[0]));
            adcProbeTwo.setValue(probeSources.get(2));
        } else {
            if (adcProbeAssignmentCat != null) {
                adcProbeAssignmentCat.setEnabled(false);
            }
        }

        if (tempUnits != null) {
            if (!VersionUtils.isSupported(ServerVersions.V_122)) {
                tempUnits.setEnabled(false);
                tempUnits.setSummaryProvider(null);
                tempUnits.setSummary(getString(R.string.disabled_option_settings, ServerVersions.V_122));
            }
        }

        return super.onCreateView(inflater, container, savedInstanceState);
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
                    if (VersionUtils.isSupported(ServerVersions.V_129)) {
                        ServerControl.setGrillProbe1Type(socket,
                                ((ListPreference) preference).getValue(), this::processPostResponse);
                    } else {
                        ServerControl.setGrillProbe0Type(socket,
                                ((ListPreference) preference).getValue(), this::processPostResponse);
                    }
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
                if (preference.getContext().getString(R.string.prefs_adc_grill_probe_one)
                        .equals(preference.getKey())) {
                    ServerControl.setADCProbeSources(socket,
                            buildProbeSourcesList(0, ((ListPreference) preference).getValue()),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_adc_grill_probe_two)
                        .equals(preference.getKey())) {
                    ServerControl.setADCProbeSources(socket,
                            buildProbeSourcesList(3, ((ListPreference) preference).getValue()),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_adc_probe_one)
                        .equals(preference.getKey())) {
                    ServerControl.setADCProbeSources(socket,
                            buildProbeSourcesList(1, ((ListPreference) preference).getValue()),
                            this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_adc_probe_two)
                        .equals(preference.getKey())) {
                    ServerControl.setADCProbeSources(socket,
                            buildProbeSourcesList(2, ((ListPreference) preference).getValue()),
                            this::processPostResponse);
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

    private List<String> getADCAssignmentOptions() {
        String[] myResArray = getResources().getStringArray(R.array.adc_probe_options);
        List<String> adcOptions = Arrays.asList(myResArray);
        String defValue = new Gson().toJson(adcOptions);
        String jsonOptions = Prefs.getString(getString(R.string.prefs_adc_probe_options), defValue);
        TypeToken<ArrayList<String>> token = new TypeToken<>() {
        };
        return new Gson().fromJson(jsonOptions, token.getType());
    }

    private List<String> getADCAssignmentSources() {
        String[] myResArray = getResources().getStringArray(R.array.adc_probe_options);
        List<String> adcOptions = Arrays.asList(myResArray);
        String defValue = new Gson().toJson(adcOptions);
        String jsonSources = Prefs.getString(getString(R.string.prefs_adc_probe_sources), defValue);
        TypeToken<ArrayList<String>> token = new TypeToken<>() {
        };
        return new Gson().fromJson(jsonSources, token.getType());
    }

    private List<String> buildProbeSourcesList(int probe, String assignment) {
        List<String> currentSources = new ArrayList<>();
        currentSources.add(adcGrillProbeOne.getValue());
        currentSources.add(adcProbeOne.getValue());
        currentSources.add(adcProbeTwo.getValue());
        currentSources.add(adcGrillProbeTwo.getValue());
        currentSources.set(probe, assignment);
        return currentSources;
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
