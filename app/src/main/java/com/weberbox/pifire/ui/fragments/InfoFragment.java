package com.weberbox.pifire.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.androidveil.VeilLayout;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentInfoBinding;
import com.weberbox.pifire.model.local.GPIODevicesModel;
import com.weberbox.pifire.model.local.GPIOInOutModel;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
    private ArrayList<LicensesModel> licenses;
    private ArrayList<GPIOInOutModel> inputs, outputs;
    private ArrayList<GPIODevicesModel> devices;
    private GPIOInOutAdapter gpioOutputAdapter, gpioInputAdapter;
    private GPIODevicesAdapter gpioDevicesAdapter;
    private VeilRecyclerFrameView inputRecycler, outputRecycler, devicesRecycler;
    private View gradient;
    private TextView viewAllButton;
    private VeilLayout systemVeil, modulesVeil, uptimeVeil, serverVeil;


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
                .applyToView(binding.infoToolbar.getRoot());
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars(), Side.BOTTOM)
                .applyToView(binding.infoScrollView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (getActivity() != null) {
                getActivity().getWindow().setNavigationBarContrastEnforced(true);
            }
        }

        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();

        TextView actionBarText = binding.infoToolbar.actionBarText;
        ImageView navButton = binding.infoToolbar.actionBarNavButton;
        ImageView configButton = binding.infoToolbar.actionBarConfigButton;

        actionBarText.setText(R.string.menu_info);
        navButton.setImageResource(R.drawable.ic_nav_back);
        navButton.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        configButton.setVisibility(View.GONE);

        licenses = new ArrayList<>();
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        devices = new ArrayList<>();

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

        gpioOutputAdapter = new GPIOInOutAdapter(outputs);
        outputRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));
        outputRecycler.setNestedScrollingEnabled(false);
        outputRecycler.addVeiledItems(6);
        outputRecycler.setAdapter(gpioOutputAdapter);

        gpioInputAdapter = new GPIOInOutAdapter(inputs);
        inputRecycler.setLayoutManager(new ScrollDisableLayoutManager(requireActivity()));
        inputRecycler.setNestedScrollingEnabled(false);
        inputRecycler.addVeiledItems(1);
        inputRecycler.setAdapter(gpioInputAdapter);

        gpioDevicesAdapter = new GPIODevicesAdapter(devices);
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

        gradient = binding.licensesCardView.licensesViewAllShadow;
        viewAllButton = binding.licensesCardView.licensesViewAll;

        viewAllButton.setOnClickListener(v -> {
            licensesListAdapter.setLimitEnabled(false);
            licensesListAdapter.notifyItemRangeChanged(3, licenses.size());
            setLicensesViewLimited(false);
        });

        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        if (getActivity() != null) {
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mainViewModel.getInfoData().observe(getViewLifecycleOwner(), infoData -> {
                swipeRefresh.setRefreshing(false);
                if (infoData != null && infoData.getLiveData() != null) {
                    if (infoData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_INFO,
                                infoData.getLiveData());
                    }
                    updateUIWithData(infoData.getLiveData());
                }
            });
        }

        mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
            toggleLoading(true);
            requestDataUpdate();
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
        }
    }

    private void showOfflineAlert() {
        if (getActivity() != null) {
            AlertUtils.createOfflineAlert(getActivity());
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

    private void setLicensesViewLimited(boolean limited) {
        gradient.setVisibility(limited ? View.VISIBLE : View.GONE);
        viewAllButton.setVisibility(limited ? View.VISIBLE : View.GONE);
    }

    private void updateUIWithData(String responseData) {

        try {

            inputs.clear();
            outputs.clear();
            devices.clear();

            InfoDataModel infoDataModel = InfoDataModel.parseJSON(responseData);

            List<String> cpuInfo = infoDataModel.getCpuInfo();
            String cpuTemp = infoDataModel.getTemp();
            List<String> networkInfo = infoDataModel.getIfConfig();
            String upTime = infoDataModel.getUpTime();
            String version = infoDataModel.getServerVersion();
            String build = infoDataModel.getServerBuild();

            boolean dcFan = Prefs.getBoolean(getString(R.string.prefs_dc_fan));
            infoDataModel.getOutPins().forEach((key, value) -> {
                if (key.contains("dc_fan") || key.contains("pwm")) {
                    if (dcFan) {
                        outputs.add(new GPIOInOutModel(key, value));
                    }
                } else {
                    outputs.add(new GPIOInOutModel(key, value));
                }
            });

            infoDataModel.getInPins().forEach((key, value) ->
                    inputs.add(new GPIOInOutModel(key, value)));

            AtomicInteger cycle = new AtomicInteger();
            infoDataModel.getDevPins().forEach((key, value) -> {
                cycle.getAndSet(1);
                value.forEach((function, pin) -> {
                    if (cycle.intValue() == 1) {
                        cycle.getAndIncrement();
                        devices.add(new GPIODevicesModel(key, function, pin));
                    } else {
                        devices.add(new GPIODevicesModel("", function, pin));
                    }
                    cycle.getAndSet(0);
                });
            });
            this.gpioDevicesAdapter.setList(devices);

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
            this.gpioOutputAdapter.setList(outputs);
            this.gpioInputAdapter.setList(inputs);
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
        licenses.clear();

        if (getActivity() != null) {
            FileUtils.executorLoadRawJson(getActivity(), R.raw.licences, jsonString -> {
                try {

                    if (jsonString != null) {
                        JSONObject rootObject = new JSONObject(jsonString);
                        JSONArray array = rootObject.getJSONArray(Constants.LICENSES_LIST);

                        for (int i = 0; i < array.length(); i++) {
                            JSONArray innerArray = array.getJSONArray(i);

                            LicensesModel project = new LicensesModel(
                                    innerArray.getString(0),
                                    innerArray.getString(1)
                            );
                            licenses.add(project);
                        }

                        requireActivity().runOnUiThread(() -> {
                            setLicensesViewLimited(licenses.size() > 3);
                            licensesListAdapter.setList(licenses);
                        });

                    }

                } catch (Exception e) {
                    Timber.w(e, "Licences JSON Error");
                    AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                            getString(R.string.info_credits)), false);
                }
            });
        }
    }
}
