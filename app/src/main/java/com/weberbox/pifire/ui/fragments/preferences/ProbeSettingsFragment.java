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
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.model.GrillProbeModel;
import com.weberbox.pifire.model.ProbeProfileModel;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;

public class ProbeSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Socket mSocket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_probe_settings, rootKey);

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
        mSocket = null;
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
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        if (preference != null && mSocket != null) {
            if (preference instanceof ListPreference) {
                if (preference.getContext().getString(R.string.prefs_grill_units)
                        .equals(preference.getKey())) {
                    GrillControl.setTempUnits(mSocket,
                            ((ListPreference) preference).getValue());
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe)
                        .equals(preference.getKey())) {
                    GrillControl.setGrillProbe(mSocket,
                            ((ListPreference) preference).getValue());
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe_type)
                        .equals(preference.getKey())) {
                    GrillControl.setGrillProbeType(mSocket,
                            ((ListPreference) preference).getValue());
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe_one_type)
                        .equals(preference.getKey())) {
                    GrillControl.setGrillProbe1Type(mSocket,
                            ((ListPreference) preference).getValue());
                }
                if (preference.getContext().getString(R.string.prefs_grill_probe_two_type)
                        .equals(preference.getKey())) {
                    GrillControl.setGrillProbe2Type(mSocket,
                            ((ListPreference) preference).getValue());
                }
                if (preference.getContext().getString(R.string.prefs_probe_one_type)
                        .equals(preference.getKey())) {
                    GrillControl.setProbe1Type(mSocket,
                            ((ListPreference) preference).getValue());
                }
                if (preference.getContext().getString(R.string.prefs_probe_two_type)
                        .equals(preference.getKey())) {
                    GrillControl.setProbe2Type(mSocket,
                            ((ListPreference) preference).getValue());
                }
            }
        }
    }

    private HashMap<String, ProbeProfileModel> getProbeProfilesHash() {
        String defValue = new Gson().toJson(new HashMap<String, ProbeProfileModel>());
        String jsonProfiles = Prefs.getString(getString(R.string.prefs_probe_profiles), defValue);
        TypeToken<HashMap<String, ProbeProfileModel>> token = new TypeToken<HashMap<String,
                ProbeProfileModel>>() {
        };
        return new Gson().fromJson(jsonProfiles, token.getType());
    }

    private HashMap<String, GrillProbeModel> getGrillProbeHash() {
        String defValue = new Gson().toJson(new HashMap<String, GrillProbeModel>());
        String jsonProfiles = Prefs.getString(getString(R.string.prefs_grill_probes), defValue);
        TypeToken<HashMap<String, GrillProbeModel>> token = new TypeToken<HashMap<String,
                GrillProbeModel>>() {
        };
        return new Gson().fromJson(jsonProfiles, token.getType());
    }
}
