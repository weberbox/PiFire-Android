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
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSettingsBinding;
import com.weberbox.pifire.interfaces.SettingsCallback;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.SettingsUtils;

import org.jetbrains.annotations.NotNull;

import io.socket.client.Socket;

public class SettingsFragment extends Fragment {

    // TODO allow offline changes and sync to server once online.

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
                AnimUtils.shakeOfflineBanner(getActivity());
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
        mSettingsUtils.requestSettingsData(mSocket);
    }

    private final SettingsCallback settingsCallback = new SettingsCallback() {
        @Override
        public void onSettingsResult(boolean result) {
            mSwipeRefresh.setRefreshing(false);
            if (!result && getActivity() != null) {
                showSnackBarMessage(getActivity());
            }
        }
    };

    private void showSnackBarMessage(Activity activity) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), R.string.json_error_settings, Snackbar.LENGTH_LONG);
        snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        snack.setTextColor(activity.getColor(R.color.colorWhite));
        snack.show();
    }
}
