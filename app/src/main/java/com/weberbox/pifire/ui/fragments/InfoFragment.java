package com.weberbox.pifire.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.androidveil.VeilLayout;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentInfoBinding;
import com.weberbox.pifire.model.local.LicensesModel;
import com.weberbox.pifire.model.remote.InfoDataModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.GPIODevicesAdapter;
import com.weberbox.pifire.recycler.adapter.GPIOInOutAdapter;
import com.weberbox.pifire.recycler.adapter.LicensesListAdapter;
import com.weberbox.pifire.recycler.manager.ScrollDisableLayoutManager;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;
import io.socket.client.Socket;
import timber.log.Timber;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;
    private MainViewModel mainViewModel;
    private Socket socket;
    private RelativeLayout rootContainer;
    private TextView version, build, cpuInfo, cpuTemp, networkInfo, upTime;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar loadingBar;
    private LicensesListAdapter licensesListAdapter;
    private GPIOInOutAdapter gpioOutputAdapter, gpioInputAdapter;
    private GPIODevicesAdapter gpioDevicesAdapter;
    private VeilRecyclerFrameView inputRecycler, outputRecycler, devicesRecycler;
    private VeilLayout systemVeil, modulesVeil, uptimeVeil, serverVeil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars(), Side.TOP)
                .applyToView(binding.infoToolbar);
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars(), Side.BOTTOM)
                .applyToView(binding.infoScrollView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requireActivity().getWindow().setNavigationBarContrastEnforced(true);
        } else {
            requireActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(
                    requireActivity(), R.color.colorNavBarAlpha));
        }

        MaterialToolbar toolbar = binding.infoToolbar;
        toolbar.setNavigationOnClickListener(view1 ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        rootContainer = binding.infoRootContainer;
        swipeRefresh = binding.infoPullRefresh;
        loadingBar = binding.loadingProgressbar;

        systemVeil = binding.systemCardView.systemVeilLayout;
        modulesVeil = binding.modulesCardView.modulesVeilLayout;
        uptimeVeil = binding.uptimeCardView.uptimeVeilLayout;
        serverVeil = binding.serverVersionCardView.serverVeilLayout;

        cpuInfo = binding.systemCardView.cpuInfoText;
        cpuTemp = binding.systemCardView.tempInfoText;
        networkInfo = binding.systemCardView.networkInfoText;

        upTime = binding.uptimeCardView.uptimeInfoText;

        inputRecycler = binding.gpioInOutCardView.gpioInputRecycler;
        outputRecycler = binding.gpioInOutCardView.gpioOutputRecycler;
        devicesRecycler = binding.gpioDevicesCardView.gpioDevicesRecycler;

        gpioOutputAdapter = new GPIOInOutAdapter();
        outputRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));
        outputRecycler.setNestedScrollingEnabled(false);
        outputRecycler.addVeiledItems(6);
        outputRecycler.setAdapter(gpioOutputAdapter);

        gpioInputAdapter = new GPIOInOutAdapter();
        inputRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));
        inputRecycler.setNestedScrollingEnabled(false);
        inputRecycler.addVeiledItems(1);
        inputRecycler.setAdapter(gpioInputAdapter);

        gpioDevicesAdapter = new GPIODevicesAdapter();
        devicesRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));
        devicesRecycler.setNestedScrollingEnabled(false);
        devicesRecycler.addVeiledItems(6);
        devicesRecycler.setAdapter(gpioDevicesAdapter);

        version = binding.serverVersionCardView.serverVersionText;
        build = binding.serverVersionCardView.serverBuildText;

        LinearLayout appGitContainer = binding.appVersionCardView.appBuildGitContainer;
        TextView appVersion = binding.appVersionCardView.appVersionText;
        TextView appVersionCode = binding.appVersionCardView.appVersionCodeText;
        TextView appBuildType = binding.appVersionCardView.appBuildTypeText;
        TextView appBuildFlavor = binding.appVersionCardView.appBuildFlavorText;
        TextView appBuildDate = binding.appVersionCardView.appBuildDate;
        TextView appGitBranch = binding.appVersionCardView.appBuildGitBranch;
        TextView appGitRev = binding.appVersionCardView.appBuildGitRev;

        appVersion.setText(BuildConfig.VERSION_NAME);
        appVersionCode.setText(String.valueOf(BuildConfig.VERSION_CODE));
        appBuildType.setText(BuildConfig.BUILD_TYPE);
        appBuildFlavor.setText(BuildConfig.FLAVOR);

        appBuildDate.setText(TimeUtils.getFormattedDate(
                BuildConfig.BUILD_TIME, "MM-dd-yy HH:mm"));

        if (AppConfig.IS_DEV_BUILD) {
            appGitContainer.setVisibility(View.VISIBLE);
            appGitBranch.setText(BuildConfig.GIT_BRANCH);
            appGitRev.setText(BuildConfig.GIT_REV);
        }

        TextView display = binding.modulesCardView.modulesDisplay;
        TextView distance = binding.modulesCardView.modulesDist;
        TextView platform = binding.modulesCardView.modulesPlatform;

        display.setText(Prefs.getString(getString(R.string.prefs_modules_display)));
        distance.setText(Prefs.getString(getString(R.string.prefs_modules_distance)));
        platform.setText(Prefs.getString(getString(R.string.prefs_modules_platform)));

        RecyclerView licenseInfo = binding.licensesCardView.infoLicensesRecycler;
        licensesListAdapter = new LicensesListAdapter(true);

        TextView licenseCredits = binding.licensesCardView.licenseCredits;
        licenseCredits.setText(getString(R.string.info_credits_text,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));
        licenseInfo.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));
        licenseInfo.setAdapter(licensesListAdapter);


        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });
        swipeRefresh.setEnabled(false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getInfoData().observe(getViewLifecycleOwner(), infoData -> {
            swipeRefresh.setEnabled(true);
            swipeRefresh.setRefreshing(false);
            if (infoData != null && infoData.getLiveData() != null) {
                if (infoData.getIsNewData()) {
                    FileUtils.executorSaveJSON(getActivity(), Constants.JSON_INFO,
                            infoData.getLiveData());
                }
                updateUIWithData(infoData.getLiveData());
            }
        });

        mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), connected -> {
            toggleLoading(true);
            if (connected) {
                requestDataUpdate();
            } else {
                showOfflineAlert();
            }
        });

        loadLicenses();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarContrastEnforced(false);
            }
        } else {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(
                        getActivity(), android.R.color.transparent));
            }
        }
    }

    private void showOfflineAlert() {
        if (getActivity() != null) {
            if (!Alerter.isShowing()) {
                AlertUtils.createOfflineAlert(getActivity());
            }
        }
    }

    private boolean socketConnected() {
        if (socket != null && socket.connected()) {
            return true;
        } else {
            showOfflineAlert();
            return false;
        }
    }

    private void requestDataUpdate() {
        if (socket != null && socket.connected()) {
            forceRefreshData();
        } else {
            loadStoredData();
        }
    }

    private void loadStoredData() {
        toggleLoading(true);
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_INFO, jsonString -> {
            if (jsonString != null && mainViewModel != null) {
                mainViewModel.setInfoData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        ServerControl.infoGetEmit(socket, response -> {
            if (mainViewModel != null) {
                mainViewModel.setInfoData(response, true);
            }
        });
    }

    private void toggleLoading(boolean show) {
        if (show && socket != null && socket.connected()) {
            loadingBar.setVisibility(View.VISIBLE);
        } else {
            loadingBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUIWithData(String responseData) {

        try {

            InfoDataModel infoDataModel = InfoDataModel.parseJSON(responseData);

            List<String> cpuInfo = infoDataModel.getCpuInfo();
            String cpuTemp = infoDataModel.getTemp();
            List<String> networkInfo = infoDataModel.getIfConfig();
            String upTime = infoDataModel.getUpTime();
            String version = infoDataModel.getServerVersion();
            String build = infoDataModel.getServerBuild();
            boolean dcFan = Prefs.getBoolean(getString(R.string.prefs_dc_fan));

            String cpuModel = "";
            String[] cpuSeparated;
            for (String cpu : cpuInfo) {
                if (cpu.contains("model name")) {
                    cpuSeparated = cpu.split(":");
                    cpuModel = cpuSeparated[1].trim();
                }
            }

            String[] inetSeparated;
            StringBuilder networkString = new StringBuilder();
            for (String network : networkInfo) {
                if (network.contains("netmask")) {
                    if (!network.contains("127.0.0.1")) {
                        inetSeparated = network.trim().split(" ");
                        networkString.append("Address:    ").append(inetSeparated[1]).append("\n\n");
                        networkString.append("Netmask:    ").append(inetSeparated[4]).append("\n\n");
                        networkString.append("Broadcast:    ").append(inetSeparated[7]).append("\n");
                    }
                }
            }

            TransitionManager.beginDelayedTransition(rootContainer, new Fade(Fade.IN));

            this.cpuInfo.setText(cpuModel);
            this.networkInfo.setText(networkString);
            this.upTime.setText(socket.connected() ? upTime : getString(R.string.offline));
            this.cpuTemp.setText(socket.connected() ? cpuTemp : getString(R.string.offline));
            this.gpioOutputAdapter.setInOutList(infoDataModel.getOutPins(), dcFan);
            this.gpioInputAdapter.setInOutList(infoDataModel.getInPins(), dcFan);
            this.gpioDevicesAdapter.setDevicesList(infoDataModel.getDevPins());
            this.version.setText(version);
            this.build.setText(build);

            systemVeil.unVeil();
            outputRecycler.unVeil();
            inputRecycler.unVeil();
            devicesRecycler.unVeil();
            modulesVeil.unVeil();
            uptimeVeil.unVeil();
            serverVeil.unVeil();

        } catch (JsonSyntaxException | IllegalStateException | NullPointerException e) {
            Timber.e(e, "Info Response Error");
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_info)), false);
        }

        toggleLoading(false);
    }

    private void loadLicenses() {
        if (getActivity() != null) {
            FileUtils.executorLoadRawJson(getActivity(), R.raw.licences, jsonString -> {
                try {
                    ArrayList<LicensesModel> licenses = LicensesModel.parseJSON(jsonString);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                licensesListAdapter.setLicenseList(licenses));
                    }
                } catch (Exception e) {
                    Timber.w(e, "Licences JSON Error");
                    if (getActivity() != null) {
                        AlertUtils.createErrorAlert(getActivity(),
                                getString(R.string.json_parsing_error,
                                        getString(R.string.info_credits)), false);
                    }
                }
            });
        }
    }
}
