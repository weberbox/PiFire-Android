package com.weberbox.pifire.ui.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.androidveil.VeilLayout;
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
import com.weberbox.pifire.recycler.adapter.LicensesListAdapter;
import com.weberbox.pifire.recycler.manager.ScrollDisableLayoutManager;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import timber.log.Timber;

public class InfoFragment extends Fragment {

    private FragmentInfoBinding binding;
    private MainViewModel mainViewModel;
    private Socket socket;
    private RelativeLayout rootContainer;
    private TextView version, cpuInfo, cpuTemp, networkInfo, upTime, auger, fan, igniter, power;
    private TextView selector;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar loadingBar;
    private LicensesListAdapter licensesListAdapter;
    private ArrayList<LicensesModel> licenses;
    private View gradient;
    private TextView viewAllButton;
    private VeilLayout systemVeil, GPIOVeil, modulesVeil, uptimeVeil, serverVeil;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();

        TextView actionBarText = binding.infoToolbar.actionBarText;
        ImageView navButton = binding.infoToolbar.actionBarNavButton;
        ImageView configButton = binding.infoToolbar.actionBarConfigButton;

        actionBarText.setText(R.string.menu_info);
        navButton.setImageResource(R.drawable.ic_nav_back);
        navButton.setOnClickListener(v -> requireActivity().onBackPressed());
        configButton.setVisibility(View.GONE);

        licenses = new ArrayList<>();

        rootContainer = binding.infoRootContainer;
        swipeRefresh = binding.infoPullRefresh;
        loadingBar = binding.loadingProgressbar;

        systemVeil = binding.systemCardView.systemVeilLayout;
        GPIOVeil = binding.gpioCardView.gpioVeilLayout;
        modulesVeil = binding.modulesCardView.modulesVeilLayout;
        uptimeVeil = binding.uptimeCardView.uptimeVeilLayout;
        serverVeil = binding.serverVersionCardView.serverVeilLayout;

        cpuInfo = binding.systemCardView.cpuInfoText;
        cpuTemp = binding.systemCardView.tempInfoText;
        networkInfo = binding.systemCardView.networkInfoText;

        upTime = binding.uptimeCardView.uptimeInfoText;

        auger = binding.gpioCardView.gpioOutputAuger;
        fan = binding.gpioCardView.gpioOutputFan;
        igniter = binding.gpioCardView.gpioOutputIgniter;
        power = binding.gpioCardView.gpioOutputPower;
        selector = binding.gpioCardView.gpioInputSelector;

        version = binding.serverVersionCardView.serverVersionText;

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

        TextView adc = binding.modulesCardView.modulesAdc;
        TextView display = binding.modulesCardView.modulesDisplay;
        TextView distance = binding.modulesCardView.modulesDist;
        TextView platform = binding.modulesCardView.modulesPlatform;

        adc.setText(Prefs.getString(getString(R.string.prefs_modules_adc), ""));
        display.setText(Prefs.getString(getString(R.string.prefs_modules_display), ""));
        distance.setText(Prefs.getString(getString(R.string.prefs_modules_distance), ""));
        platform.setText(Prefs.getString(getString(R.string.prefs_modules_platform), ""));

        RecyclerView licenseInfo = binding.licensesCardView.infoLicensesRecycler;
        licensesListAdapter = new LicensesListAdapter(true);

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

            InfoDataModel infoDataModel = InfoDataModel.parseJSON(responseData);

            List<String> cpuInfo = infoDataModel.getCpuInfo();
            String cpuTemp = infoDataModel.getTemp();
            List<String> networkInfo = infoDataModel.getIfConfig();
            String upTime = infoDataModel.getUpTime();
            String auger = infoDataModel.getOutPins().getAuger();
            String fan = infoDataModel.getOutPins().getFan();
            String igniter = infoDataModel.getOutPins().getIgniter();
            String power = infoDataModel.getOutPins().getPower();
            String selector = infoDataModel.getInPins().getSelector();
            String version = infoDataModel.getServerVersion();

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
            this.auger.setText(auger);
            this.fan.setText(fan);
            this.igniter.setText(igniter);
            this.power.setText(power);
            this.selector.setText(selector);
            this.version.setText(version);

            systemVeil.unVeil();
            GPIOVeil.unVeil();
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
