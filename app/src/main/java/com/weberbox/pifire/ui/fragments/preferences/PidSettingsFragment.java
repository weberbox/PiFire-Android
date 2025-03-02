package com.weberbox.pifire.ui.fragments.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.dialogs.PrefsEditDialog;
import com.weberbox.pifire.ui.dialogs.PrefsListDialog;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.ui.views.preferences.ListPreferenceSocket;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.NullUtils;
import com.weberbox.pifire.utils.VersionUtils;

import dev.chrisbanes.insetter.Insetter;
import io.socket.client.Socket;
import timber.log.Timber;

public class PidSettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;
    private ToolbarTitleCallback toolbarTitleCallback;
    private PreferenceCategory pid, pidAc, pidSp, noConfig;
    private Socket socket;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_pid_settings, rootKey);
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
        setDivider(new ColorDrawable(Color.TRANSPARENT));
        setDividerHeight(0);

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .applyToView(getListView());

        PreferenceCategory cntrlr = findPreference(getString(R.string.prefs_cntrlr_config));

        ListPreferenceSocket config = findPreference(getString(R.string.prefs_cntrlr_selected));

        EditTextPreference pidPb = findPreference(getString(R.string.prefs_pid_cntrlr_pb));
        EditTextPreference pidTd = findPreference(getString(R.string.prefs_pid_cntrlr_td));
        EditTextPreference pidTi = findPreference(getString(R.string.prefs_pid_cntrlr_ti));
        EditTextPreference pidCenter = findPreference(getString(R.string.prefs_pid_cntrlr_center));
        EditTextPreference pidAcPb = findPreference(getString(R.string.prefs_pid_ac_cntrlr_pb));
        EditTextPreference pidAcTd = findPreference(getString(R.string.prefs_pid_ac_cntrlr_td));
        EditTextPreference pidAcTi = findPreference(getString(R.string.prefs_pid_ac_cntrlr_ti));
        EditTextPreference pidAcCenter = findPreference(getString(R.string.prefs_pid_ac_cntrlr_center));
        EditTextPreference pidAcSp = findPreference(getString(R.string.prefs_pid_ac_cntrlr_sp));
        EditTextPreference pidSpPb = findPreference(getString(R.string.prefs_pid_sp_cntrlr_pb));
        EditTextPreference pidSpTd = findPreference(getString(R.string.prefs_pid_sp_cntrlr_td));
        EditTextPreference pidSpTi = findPreference(getString(R.string.prefs_pid_sp_cntrlr_ti));
        EditTextPreference pidSpCenter = findPreference(getString(R.string.prefs_pid_sp_cntrlr_center));
        EditTextPreference pidSpSp = findPreference(getString(R.string.prefs_pid_sp_cntrlr_sp));
        EditTextPreference pidSpTau = findPreference(getString(R.string.prefs_pid_sp_cntrlr_tau));
        EditTextPreference pidSpTheta = findPreference(getString(R.string.prefs_pid_sp_cntrlr_theta));
        EditTextPreference pidCycle = findPreference(getString(R.string.prefs_cycle_cntrlr_cycle));
        EditTextPreference pidUMin = findPreference(getString(R.string.prefs_cycle_cntrlr_u_min));
        EditTextPreference pidUMax = findPreference(getString(R.string.prefs_cycle_cntrlr_u_max));

        pid = findPreference(getString(R.string.prefs_cntrlr_config_pid));
        pidAc = findPreference(getString(R.string.prefs_cntrlr_config_pid_ac));
        pidSp = findPreference(getString(R.string.prefs_cntrlr_config_pid_sp));
        noConfig = findPreference(getString(R.string.prefs_cntrlr_no_config));

        if (VersionUtils.isSupported(ServerVersions.V_180)) {
            if (config != null) {
                showDependencies(config.getValue());
            }
        } else {
            if (NullUtils.checkObjectNotNull(pid, pidAc, pidSp, noConfig)) {
                if (cntrlr != null) {
                    cntrlr.setVisible(false);
                }
                pid.setVisible(false);
                pidAc.setVisible(false);
                pidSp.setVisible(false);
                noConfig.setVisible(false);
            }
        }

        if (pidPb != null) {
            pidPb.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidTd != null) {
            pidTd.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidTi != null) {
            pidTi.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidCenter != null) {
            pidCenter.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidAcPb != null) {
            pidAcPb.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidAcTd != null) {
            pidAcTd.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidAcTi != null) {
            pidAcTi.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidAcCenter != null) {
            pidAcCenter.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidAcSp != null) {
            pidAcSp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpPb != null) {
            pidSpPb.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpTd != null) {
            pidSpTd.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpTi != null) {
            pidSpTi.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpCenter != null) {
            pidSpCenter.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpSp != null) {
            pidSpSp.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpTau != null) {
            pidSpTau.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), null, null, editText));
            });
        }

        if (pidSpTheta != null) {
            pidSpTheta.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }


        if (pidCycle != null) {
            pidCycle.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 1.0, null, editText));
            });
        }

        if (pidUMin != null) {
            pidUMin.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.05, 0.99,
                                editText));
            });
        }

        if (pidUMax != null) {
            pidUMax.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editText.addTextChangedListener(
                        new EmptyTextListener(requireActivity(), 0.1, 1.0,
                                editText));
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
            toolbarTitleCallback.onTitleChange(getString(R.string.settings_cat_cntrlr_pid));
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
    public void onDestroy() {
        super.onDestroy();
        socket = null;
    }

    private void showDependencies(String selected) {
        if (NullUtils.checkObjectNotNull(pid, pidAc, pidSp, noConfig)) {
            switch (selected) {
                case "pid" -> {
                    pid.setVisible(true);
                    pidAc.setVisible(false);
                    pidSp.setVisible(false);
                    noConfig.setVisible(false);
                }
                case "pid_ac" -> {
                    pid.setVisible(false);
                    pidAc.setVisible(true);
                    pidSp.setVisible(false);
                    noConfig.setVisible(false);
                }
                case "pid_sp" -> {
                    pid.setVisible(false);
                    pidAc.setVisible(false);
                    pidSp.setVisible(true);
                    noConfig.setVisible(false);
                }
                default -> {
                    pid.setVisible(false);
                    pidAc.setVisible(false);
                    pidSp.setVisible(false);
                    noConfig.setVisible(true);
                }
            }
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
                if (preference.getContext().getString(R.string.prefs_cntrlr_selected)
                        .equals(preference.getKey())) {
                    showDependencies(((ListPreference) preference).getValue());
                    ServerControl.setCntrlrSelected(socket,
                            ((ListPreference) preference).getValue(), this::processPostResponse);
                }
            }
            if (preference instanceof EditTextPreference) {
                if (preference.getContext().getString(R.string.prefs_pid_cntrlr_pb)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidPb(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_cntrlr_td)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidTd(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_cntrlr_ti)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidTi(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_cntrlr_center)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidCenter(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_ac_cntrlr_pb)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidAcPb(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_ac_cntrlr_td)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidAcTd(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_ac_cntrlr_ti)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidAcTi(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_ac_cntrlr_center)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidAcCenter(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_ac_cntrlr_sp)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidAcSp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_pb)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpPb(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_td)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpTd(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_ti)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpTi(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_center)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpCenter(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_sp)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpSp(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_tau)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpTau(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_pid_sp_cntrlr_theta)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrPidSpTheta(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_cycle_cntrlr_cycle)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlrTime(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_cycle_cntrlr_u_max)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlruMax(socket,
                            ((EditTextPreference) preference).getText(), this::processPostResponse);
                }
                if (preference.getContext().getString(R.string.prefs_cycle_cntrlr_u_min)
                        .equals(preference.getKey())) {
                    ServerControl.setCntrlruMin(socket,
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
        if (preference instanceof ListPreference) {
            if (getContext() != null) {
                new PrefsListDialog(getContext(), preference).showDialog();
                return;
            }
        }
        super.onDisplayPreferenceDialog(preference);
    }

}
