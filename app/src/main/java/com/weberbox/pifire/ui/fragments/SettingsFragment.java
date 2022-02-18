package com.weberbox.pifire.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSettingsBinding;
import com.weberbox.pifire.interfaces.SettingsBindingCallback;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.SettingsUtils;

import org.jetbrains.annotations.NotNull;

import io.socket.client.Socket;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SwipeRefreshLayout swipeRefresh;
    private SettingsUtils settingsUtils;
    private Socket socket;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            settingsUtils = new SettingsUtils(getActivity(), settingsSocketCallback);

            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.settingsLayout.setCallback(settingsBindingCallback);

        swipeRefresh = binding.settingsPullRefresh;

        swipeRefresh.setOnRefreshListener(() -> {
            if (socket != null && socket.connected()) {
                requestSettingsData();
            } else {
                swipeRefresh.setRefreshing(false);
                showOfflineAlert();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    public void startPreferenceActivity(int fragment) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PreferencesActivity.class);
            intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, fragment);
            startActivity(intent);
        }
    }

    private void requestSettingsData() {
        settingsUtils.requestSettingsData(socket);
    }

    private final SettingsBindingCallback settingsBindingCallback = fragment -> {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), PreferencesActivity.class);
            intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, fragment);
            startActivity(intent);
        }
    };

    private final SettingsSocketCallback settingsSocketCallback = new SettingsSocketCallback() {
        @Override
        public void onSettingsResult(boolean result) {
            swipeRefresh.setRefreshing(false);
            if (!result && getActivity() != null) {
                AlertUtils.createErrorAlert(getActivity(), R.string.json_error_settings, false);
            }
        }
    };
}
