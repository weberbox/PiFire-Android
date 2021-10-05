package com.weberbox.pifire.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentSettingsBinding;
import com.weberbox.pifire.model.GrillProbeModel;
import com.weberbox.pifire.model.ProbeProfileModel;
import com.weberbox.pifire.model.SettingsResponseModel;
import com.weberbox.pifire.model.SettingsResponseModel.CycleData;
import com.weberbox.pifire.model.SettingsResponseModel.Firebase;
import com.weberbox.pifire.model.SettingsResponseModel.Globals;
import com.weberbox.pifire.model.SettingsResponseModel.GrillProbeSettings;
import com.weberbox.pifire.model.SettingsResponseModel.HistoryPage;
import com.weberbox.pifire.model.SettingsResponseModel.Ifttt;
import com.weberbox.pifire.model.SettingsResponseModel.PelletLevel;
import com.weberbox.pifire.model.SettingsResponseModel.ProbeSettings;
import com.weberbox.pifire.model.SettingsResponseModel.ProbeTypes;
import com.weberbox.pifire.model.SettingsResponseModel.PushBullet;
import com.weberbox.pifire.model.SettingsResponseModel.Pushover;
import com.weberbox.pifire.model.SettingsResponseModel.Safety;
import com.weberbox.pifire.model.SettingsResponseModel.SmokePlus;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.AnimUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class SettingsFragment extends Fragment {

    // TODO allow offline changes and sync to server once online. Use settings with newer timestamp.
    //  Timestamp added to server settings

    private FragmentSettingsBinding mBinding;
    private SwipeRefreshLayout mSwipeRefresh;
    private Socket mSocket;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSettingsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
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
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.setCallback(this);

        mSwipeRefresh = mBinding.settingsPullRefresh;

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSocket != null && mSocket.connected()) {
                    requestSettingsData();
                } else {
                    mSwipeRefresh.setRefreshing(false);
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        requestSettingsData();
    }

    public void settingsOnClick(String settings) {
        switch (settings) {
            case Constants.DB_SET_APP:
                startPreferenceActivity(Constants.FRAG_APP_SETTINGS);
                break;
            case Constants.DB_SET_PROBE:
                startPreferenceActivity(Constants.FRAG_PROBE_SETTINGS);
                break;
            case Constants.DB_SET_NAME:
                startPreferenceActivity(Constants.FRAG_NAME_SETTINGS);
                break;
            case Constants.DB_SET_WORK:
                startPreferenceActivity(Constants.FRAG_WORK_SETTINGS);
                break;
            case Constants.DB_SET_PELLETS:
                startPreferenceActivity(Constants.FRAG_PELLET_SETTINGS);
                break;
            case Constants.DB_SET_SHUTDOWN:
                startPreferenceActivity(Constants.FRAG_SHUTDOWN_SETTINGS);
                break;
            case Constants.DB_SET_HISTORY:
                startPreferenceActivity(Constants.FRAG_HISTORY_SETTINGS);
                break;
            case Constants.DB_SET_SAFETY:
                startPreferenceActivity(Constants.FRAG_SAFETY_SETTINGS);
                break;
            case Constants.DB_SET_NOTIF:
                startPreferenceActivity(Constants.FRAG_NOTIF_SETTINGS);
                break;
        }
    }

    private void startPreferenceActivity(int fragment) {
        if(getActivity() != null) {
            Intent intent = new Intent(getActivity(), PreferencesActivity.class);
            intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, fragment);
            startActivity(intent);
        }
    }

    private void requestSettingsData() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_SETTINGS_DATA, (Ack) args -> {
                updateSettingsData(args[0].toString());
            });
        }
    }

    private void updateSettingsData(String response_data) {
        mSwipeRefresh.setRefreshing(false);

        try {

            SettingsResponseModel settingsResponse = SettingsResponseModel.parseJSON(response_data);

            HistoryPage historyPage = settingsResponse.getHistoryPage();
            ProbeSettings probesSettings = settingsResponse.getProbeSettings();
            Globals globals = settingsResponse.getGlobals();
            Ifttt ifttt = settingsResponse.getIfttt();
            PushBullet pushBullet = settingsResponse.getPushBullet();
            Pushover pushOver = settingsResponse.getPushover();
            Firebase fireBase = settingsResponse.getFirebase();
            ProbeTypes probeTypes = settingsResponse.getProbeTypes();
            CycleData cycleData = settingsResponse.getCycleData();
            SmokePlus smokePlus = settingsResponse.getSmokePlus();
            Safety safety  = settingsResponse.getSafety();
            PelletLevel pellets  = settingsResponse.getPellets();

            Map<String, ProbeProfileModel> probes = probesSettings.getProbeProfiles();

            Prefs.putString(getString(R.string.prefs_probe_profiles), new Gson().toJson(probes));

            Prefs.putString(getString(R.string.prefs_history_display), historyPage.getMinutes());
            Prefs.putBoolean(getString(R.string.prefs_history_clear), historyPage.getClearHistoryOnStart());
            Prefs.putBoolean(getString(R.string.prefs_history_auto), historyPage.getAutoRefresh().equals("on"));
            Prefs.putString(getString(R.string.prefs_history_points), historyPage.getDataPoints());

            Prefs.putString(getString(R.string.prefs_grill_name), globals.getGrillName());
            Prefs.putBoolean(getString(R.string.prefs_admin_debug), globals.getDebugMode());
            Prefs.putString(getString(R.string.prefs_shutdown_time), globals.getShutdownTimer());
            Prefs.putBoolean(getString(R.string.prefs_four_probe), globals.getFourProbes());
            Prefs.putString(getString(R.string.prefs_grill_units), globals.getUnits());

            Prefs.putBoolean(getString(R.string.prefs_notif_ifttt_enabled), ifttt.getEnabled());
            Prefs.putString(getString(R.string.prefs_notif_ifttt_api), ifttt.getAPIKey());

            Prefs.putBoolean(getString(R.string.prefs_notif_pushbullet_enabled), pushBullet.getEnabled());
            Prefs.putString(getString(R.string.prefs_notif_pushbullet_api), pushBullet.getAPIKey());
            Prefs.putString(getString(R.string.prefs_notif_pushbullet_url), pushBullet.getPublicURL());

            Prefs.putBoolean(getString(R.string.prefs_notif_pushover_enabled), pushOver.getEnabled());
            Prefs.putString(getString(R.string.prefs_notif_pushover_api), pushOver.getAPIKey());
            Prefs.putString(getString(R.string.prefs_notif_pushover_keys), pushOver.getUserKeys());
            Prefs.putString(getString(R.string.prefs_notif_pushover_url), pushOver.getPublicURL());

            Prefs.putBoolean(getString(R.string.prefs_notif_firebase_enabled), fireBase.getEnabled());
            Prefs.putString(getString(R.string.prefs_notif_firebase_serverkey), fireBase.getServerKey());

            if (globals.getFourProbes() != null && globals.getFourProbes()) {
                GrillProbeSettings grillProbeSettings = settingsResponse.getGrillProbeSettings();

                Map<String, GrillProbeModel> grillProbes = grillProbeSettings.getGrillProbes();

                Prefs.putString(getString(R.string.prefs_grill_probes), new Gson().toJson(grillProbes));
                Prefs.putString(getString(R.string.prefs_grill_probe), grillProbeSettings.getGrillProbe());

                Prefs.putString(getString(R.string.prefs_grill_probe_one_type), probeTypes.getGrill1type());
                Prefs.putString(getString(R.string.prefs_grill_probe_two_type), probeTypes.getGrill2type());
            } else {
                Prefs.putString(getString(R.string.prefs_grill_probe_type), probeTypes.getGrill0type());
            }

            Prefs.putString(getString(R.string.prefs_probe_one_type), probeTypes.getProbe1type());
            Prefs.putString(getString(R.string.prefs_probe_two_type), probeTypes.getProbe2type());

            Prefs.putString(getString(R.string.prefs_work_pid_pb), cycleData.getPb());
            Prefs.putString(getString(R.string.prefs_work_pid_ti), cycleData.getTi());
            Prefs.putString(getString(R.string.prefs_work_pid_td), cycleData.getTd());
            Prefs.putString(getString(R.string.prefs_work_pid_cycle), cycleData.getHoldCycleTime());
            Prefs.putString(getString(R.string.prefs_work_auger_on), cycleData.getSmokeCycleTime());
            Prefs.putString(getString(R.string.prefs_work_pmode_mode), cycleData.getPMode());

            Prefs.putBoolean(getString(R.string.prefs_work_splus_enabled), smokePlus.getEnabled());
            Prefs.putString(getString(R.string.prefs_work_splus_min), smokePlus.getMinTemp());
            Prefs.putString(getString(R.string.prefs_work_splus_max), smokePlus.getMaxTemp());
            Prefs.putString(getString(R.string.prefs_work_splus_fan), smokePlus.getCycle());

            Prefs.putString(getString(R.string.prefs_safety_min_start), safety.getMinStartupTemp());
            Prefs.putString(getString(R.string.prefs_safety_max_start), safety.getMaxStartupTemp());
            Prefs.putString(getString(R.string.prefs_safety_max_temp), safety.getMaxTemp());
            Prefs.putString(getString(R.string.prefs_safety_retries), safety.getReigniteRetries());

            Prefs.putString(getString(R.string.prefs_pellet_empty), pellets.getEmpty());
            Prefs.putString(getString(R.string.prefs_pellet_full), pellets.getFull());

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e,"JSON Error");
            if (getActivity() != null) {
                showSnackBarMessage(getActivity());
            }
        }
    }

    private void showSnackBarMessage(Activity activity) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), R.string.json_error_settings, Snackbar.LENGTH_LONG);
        snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        snack.setTextColor(activity.getColor(R.color.colorWhite));
        snack.show();
    }
}
