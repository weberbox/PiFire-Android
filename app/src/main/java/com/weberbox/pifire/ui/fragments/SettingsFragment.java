package com.weberbox.pifire.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSettingsBinding;
import com.weberbox.pifire.interfaces.SettingsCallback;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.SettingsUtils;

import org.jetbrains.annotations.NotNull;

import io.socket.client.Socket;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding mBinding;
    private SwipeRefreshLayout mSwipeRefresh;
    private SettingsUtils mSettingsUtils;
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
            mSettingsUtils = new SettingsUtils(getActivity(), settingsCallback);

            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.setCallback(this);

        mSwipeRefresh = mBinding.settingsPullRefresh;

        mSwipeRefresh.setOnRefreshListener(() -> {
            if (mSocket != null && mSocket.connected()) {
                requestSettingsData();
            } else {
                mSwipeRefresh.setRefreshing(false);
                showOfflineAlert();
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

    private void showOfflineAlert() {
        if (getActivity() != null) {
            AlertUtils.createOfflineAlert(getActivity());
        }
    }

    public void onClickAppSettings() {
        startPreferenceActivity(Constants.FRAG_APP_SETTINGS);
    }

    public void onClickProbeSettings() {
        startPreferenceActivity(Constants.FRAG_PROBE_SETTINGS);
    }

    public void onClickNameSettings() {
        startPreferenceActivity(Constants.FRAG_NAME_SETTINGS);
    }

    public void onClickWorkSettings() {
        startPreferenceActivity(Constants.FRAG_WORK_SETTINGS);
    }

    public void onClickPelletSettings() {
        startPreferenceActivity(Constants.FRAG_PELLET_SETTINGS);
    }

    public void onClickTimersSettings() {
        startPreferenceActivity(Constants.FRAG_SHUTDOWN_SETTINGS);
    }

    public void onClickHistorySettings() {
        startPreferenceActivity(Constants.FRAG_HISTORY_SETTINGS);
    }

    public void onClickSafetySettings() {
        startPreferenceActivity(Constants.FRAG_SAFETY_SETTINGS);
    }

    public void onClickNotificationsSettings() {
        startPreferenceActivity(Constants.FRAG_NOTIF_SETTINGS);
    }

    private void startPreferenceActivity(int fragment) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PreferencesActivity.class);
            intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, fragment);
            startActivity(intent);
        }
    }

    private void requestSettingsData() {
        mSettingsUtils.requestSettingsData(mSocket);
    }

    private final SettingsCallback settingsCallback = new SettingsCallback() {
        @Override
        public void onSettingsResult(boolean result) {
            mSwipeRefresh.setRefreshing(false);
            if (!result && getActivity() != null) {
                AlertUtils.createErrorAlert(getActivity(), R.string.json_error_settings, false);
            }
        }
    };
}
