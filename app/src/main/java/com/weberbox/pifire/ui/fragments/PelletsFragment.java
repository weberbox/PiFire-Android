package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.androidveil.VeilLayout;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsCurrentBinding;
import com.weberbox.pifire.databinding.LayoutPelletsHopperBinding;
import com.weberbox.pifire.databinding.LayoutPelletsUsageBinding;
import com.weberbox.pifire.model.local.PelletItemModel;
import com.weberbox.pifire.model.local.PelletLogModel;
import com.weberbox.pifire.model.remote.PelletDataModel;
import com.weberbox.pifire.model.remote.PelletDataModel.Current;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.PelletItemsAdapter;
import com.weberbox.pifire.recycler.adapter.PelletProfileEditAdapter;
import com.weberbox.pifire.recycler.adapter.PelletsLogAdapter;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.PelletsAddDialog;
import com.weberbox.pifire.ui.dialogs.PelletsEditDialog;
import com.weberbox.pifire.ui.dialogs.ProfilePickerDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPelletsProfileCallback;
import com.weberbox.pifire.ui.views.CardViewHeaderButton;
import com.weberbox.pifire.ui.views.PelletsCardViewRecycler;
import com.weberbox.pifire.ui.views.PelletsEditorRecycler;
import com.weberbox.pifire.ui.views.PelletsLogsRecycler;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TimeUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.socket.client.Socket;
import timber.log.Timber;

public class PelletsFragment extends Fragment implements DialogPelletsProfileCallback {

    private MainViewModel mainViewModel;
    private FragmentPelletsBinding binding;
    private LayoutPelletsCurrentBinding pelletsCurrentBinding;
    private RelativeLayout rootContainer;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar loadingBar;
    private PelletItemsAdapter pelletBrandsAdapter, pelletWoodsAdapter;
    private PelletsLogAdapter pelletsLogAdapter;
    private PelletProfileEditAdapter profileEditAdapter;
    private List<PelletItemModel> brandsEditList, woodsEditList;
    private List<PelletLogModel> logsList;
    private List<PelletProfileModel> profileList, profileEditList;
    private List<String> brandsList, woodsList;
    private LinearProgressIndicator hopperLevel;
    private VeilLayout hopperPlaceholder, currentPlaceholder, usagePlaceholder;
    private VeilRecyclerFrameView brandsCardViewRecycler, woodsCardViewRecycler;
    private VeilRecyclerFrameView editorRecycler, logsRecycler;
    private TextView hopperLevelText, pelletUsageImperial, pelletUsageMetric;
    private String currentPelletId;
    private Socket socket;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPelletsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        brandsList = new ArrayList<>();
        woodsList = new ArrayList<>();
        brandsEditList = new ArrayList<>();
        woodsEditList = new ArrayList<>();
        logsList = new ArrayList<>();
        profileList = new ArrayList<>();
        profileEditList = new ArrayList<>();

        LayoutPelletsBinding pelletsBinding = binding.pelletsLayout;
        LayoutPelletsHopperBinding hopperBinding = pelletsBinding.pelletsHopperLevel;
        LayoutPelletsUsageBinding usageBinding = pelletsBinding.pelletsUsage;
        pelletsCurrentBinding = pelletsBinding.pelletsCurrent;

        rootContainer = binding.pelletsRootContainer;
        swipeRefresh = binding.pelletsPullRefresh;
        loadingBar = binding.loadingProgressbar;
        hopperLevel = hopperBinding.hopperLevel;
        hopperLevelText = hopperBinding.hopperLevelText;
        hopperPlaceholder = hopperBinding.hopperHolder;
        currentPlaceholder = pelletsCurrentBinding.currentHolder;
        usagePlaceholder = usageBinding.pelletsUsageHolder;

        CardViewHeaderButton hopperHeader = hopperBinding.hopperLevelHeader;

        CardViewHeaderButton currentHeader = pelletsCurrentBinding.loadOutHeader;
        PelletsCardViewRecycler brandsCardView = pelletsBinding.pelletsBrands;
        PelletsCardViewRecycler woodsCardView = pelletsBinding.pelletsWoods;
        PelletsLogsRecycler logsCardView = pelletsBinding.pelletsLogs;
        PelletsEditorRecycler editorCardView = pelletsBinding.pelletsEditor;

        TextView addNewProfile = editorCardView.getHeaderButton();
        TextView addNewBrand = brandsCardView.getHeaderButton();
        TextView addNewWood = woodsCardView.getHeaderButton();
        TextView brandsViewAll = brandsCardView.getViewAllButton();
        TextView woodsViewAll = woodsCardView.getViewAllButton();
        TextView logsViewAll = logsCardView.getViewAllButton();
        TextView profilesViewAll = editorCardView.getViewAllButton();

        TextView refreshPellets = hopperHeader.getButton();
        TextView loadNewPellets = currentHeader.getButton();

        pelletUsageImperial = usageBinding.pelletsUsageImperial;
        pelletUsageMetric = usageBinding.pelletsUsageMetric;

        pelletBrandsAdapter = new PelletItemsAdapter(brandsEditList, this, true);

        brandsCardViewRecycler = brandsCardView.getRecycler();
        brandsCardViewRecycler.setAdapter(pelletBrandsAdapter);

        pelletWoodsAdapter = new PelletItemsAdapter(woodsEditList, this, true);

        woodsCardViewRecycler = woodsCardView.getRecycler();
        woodsCardViewRecycler.setAdapter(pelletWoodsAdapter);

        pelletsLogAdapter = new PelletsLogAdapter(logsList, this, true);

        logsRecycler = logsCardView.getRecycler();
        logsRecycler.setAdapter(pelletsLogAdapter);

        profileEditAdapter = new PelletProfileEditAdapter(profileEditList, this, true);

        editorRecycler = editorCardView.getRecycler();
        editorRecycler.setAdapter(profileEditAdapter);

        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        refreshPellets.setOnClickListener(v -> {
            if (socketConnected()) {
                ServerControl.sendCheckHopperLevel(socket, this::processPostResponse);
                toggleLoading(true);
            }
        });

        loadNewPellets.setOnClickListener(v -> {
            if (socketConnected() && getActivity() != null) {
                if (profileList != null && currentPelletId != null) {
                    ProfilePickerDialog profilePickerDialog = new ProfilePickerDialog(getActivity(),
                            profileList, currentPelletId, PelletsFragment.this);
                    profilePickerDialog.showDialog();
                }
            }
        });

        addNewBrand.setOnClickListener(v -> {
            if (socketConnected() && getActivity() != null) {
                PelletsAddDialog dialog = new PelletsAddDialog(getActivity(),
                        getString(R.string.pellets_add_brand), Constants.PELLET_BRAND, null, null,
                        this::onItemAdded);
                dialog.showDialog();
            }
        });

        addNewWood.setOnClickListener(v -> {
            if (socketConnected() && getActivity() != null) {
                PelletsAddDialog dialog = new PelletsAddDialog(getActivity(),
                        getString(R.string.pellets_add_woods), Constants.PELLET_WOOD, null, null,
                        this::onItemAdded);
                dialog.showDialog();
            }
        });

        addNewProfile.setOnClickListener(v -> {
            if (socketConnected() && getActivity() != null) {
                PelletsEditDialog dialog = new PelletsEditDialog(getActivity(), brandsList,
                        woodsList, null, -1, PelletsFragment.this);
                dialog.showDialog();
            }
        });

        brandsViewAll.setOnClickListener(v -> {
            setBrandsViewLimited(false);
            pelletBrandsAdapter.setLimitEnabled(false);
            pelletBrandsAdapter.notifyItemRangeChanged(AppConfig.RECYCLER_LIMIT, brandsList.size());
        });

        woodsViewAll.setOnClickListener(v -> {
            setWoodsViewLimited(false);
            pelletWoodsAdapter.setLimitEnabled(false);
            pelletWoodsAdapter.notifyItemRangeChanged(AppConfig.RECYCLER_LIMIT, woodsList.size());
        });

        logsViewAll.setOnClickListener(v -> {
            setLogsViewLimited(false);
            pelletsLogAdapter.setLimitEnabled(false);
            pelletsLogAdapter.notifyItemRangeChanged(AppConfig.RECYCLER_LIMIT, logsList.size());
        });

        profilesViewAll.setOnClickListener(v -> {
            setProfilesViewLimited(false);
            profileEditAdapter.setLimitEnabled(false);
            profileEditAdapter.notifyItemRangeChanged(AppConfig.RECYCLER_LIMIT, profileList.size());
        });

        if (getActivity() != null) {
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mainViewModel.getPelletData().observe(getViewLifecycleOwner(), pelletData -> {
                swipeRefresh.setRefreshing(false);
                if (pelletData != null && pelletData.getLiveData() != null) {
                    if (pelletData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_PELLETS,
                                pelletData.getLiveData());
                    }
                    updateUIWithData(pelletData.getLiveData());
                }
            });

            mainViewModel.getDashData().observe(getViewLifecycleOwner(), dashData -> {
                if (dashData != null && dashData.getHopperLevel() != null) {
                    toggleLoading(false);
                    int pelletLevel = dashData.getHopperLevel();
                    hopperLevel.setProgress(pelletLevel);
                    hopperLevelText.setText(StringUtils.formatPercentage(pelletLevel));
                    if (getActivity() != null) {
                        if (pelletLevel < AppConfig.LOW_PELLET_WARNING) {
                            hopperLevel.setIndicatorColor(ContextCompat.getColor(getActivity(),
                                    R.color.colorPelletDanger));
                            hopperLevel.setTrackColor(ContextCompat.getColor(getActivity(),
                                    R.color.colorPelletDangerBG));
                        } else {
                            hopperLevel.setIndicatorColor(ContextCompat.getColor(getActivity(),
                                    R.color.colorAccent));
                            hopperLevel.setTrackColor(ContextCompat.getColor(getActivity(),
                                    R.color.colorAccentDisabled));
                        }
                    }
                }
            });

            mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                toggleLoading(true);
                requestDataUpdate();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
        requestDataUpdate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        socket = null;
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
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_PELLETS, jsonString -> {
            if (jsonString != null && mainViewModel != null) {
                mainViewModel.setPelletData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        ServerControl.pelletsGetEmit(socket, response -> {
            if (mainViewModel != null) {
                mainViewModel.setPelletData(response, true);
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

    private void processPostResponse(String response) {
        ServerResponseModel result = ServerResponseModel.parseJSON(response);
        if (result.getResult().equals("error")) {
            requireActivity().runOnUiThread(() ->
                    AlertUtils.createErrorAlert(requireActivity(),
                            result.getMessage(), false));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUIWithData(String responseData) {
        brandsList.clear();
        woodsList.clear();
        brandsEditList.clear();
        woodsEditList.clear();
        logsList.clear();
        profileList.clear();
        profileEditList.clear();

        try {
            PelletDataModel pelletDataModel = PelletDataModel.parseJSON(responseData);

            Current current = pelletDataModel.getCurrent();

            brandsList.addAll(pelletDataModel.getBrands());
            woodsList.addAll(pelletDataModel.getWoods());

            Map<String, String> logs = pelletDataModel.getLogs();
            Map<String, PelletProfileModel> profiles = pelletDataModel.getProfiles();

            TransitionManager.beginDelayedTransition(rootContainer, new Fade(Fade.IN));

            profileList.addAll(profiles.values());

            if (current.getDateLoaded() != null) {
                if (Prefs.getString(getString(R.string.prefs_grill_units)).equals("F")) {
                    String dateLoaded;
                    try {
                        dateLoaded = TimeUtils.parseDate(current.getDateLoaded(),
                                "yyyy-MM-dd hh:mm:ss", "MM/dd h:mm a");
                    } catch (ParseException e) {
                        Timber.e(e, "Failed parsing pellets load date");
                        dateLoaded = current.getDateLoaded();
                    }
                    pelletsCurrentBinding.currentDateLoadedText.setText(dateLoaded);
                } else {
                    pelletsCurrentBinding.currentDateLoadedText.setText(current.getDateLoaded());
                }
            }

            if (current.getEstimatedUsage() != null) {
                double grams = current.getEstimatedUsage();
                double pounds = grams * 0.00220462;
                double ounces = grams * 0.03527392;
                if (pounds > 1) {
                    pelletUsageImperial.setText(String.format(Locale.US, "%.2f lbs", pounds));
                } else {
                    pelletUsageImperial.setText(String.format(Locale.US, "%.2f oz", ounces));
                }
                if (grams < 1000) {
                    pelletUsageMetric.setText(String.format(Locale.US, "%.2f g", grams));
                } else {
                    pelletUsageMetric.setText(String.format(Locale.US, "%.2f kg", grams / 1000));
                }
            } else {
                pelletUsageImperial.setText(R.string.placeholder_none);
                pelletUsageMetric.setText(R.string.placeholder_none);
            }

            if (current.getPelletId() != null) {
                currentPelletId = current.getPelletId();
            }

            if (current.getPelletId() != null) {
                PelletProfileModel currentProfile = profiles.get(current.getPelletId());
                if (currentProfile != null) {
                    pelletsCurrentBinding.currentBrandText.setText(currentProfile.getBrand());
                    pelletsCurrentBinding.currentWoodText.setText(currentProfile.getWood());
                    pelletsCurrentBinding.currentCommentsText.setText(currentProfile.getComments());
                    pelletsCurrentBinding.currentRatingText.setRating(currentProfile.getRating());
                }
            }

            for (int i = 0; i < brandsList.size(); i++) {
                PelletItemModel brandsList = new PelletItemModel(
                        this.brandsList.get(i),
                        Constants.PELLET_BRAND
                );
                brandsEditList.add(brandsList);
            }

            pelletBrandsAdapter.notifyDataSetChanged();

            for (int i = 0; i < woodsList.size(); i++) {
                PelletItemModel woodsList = new PelletItemModel(
                        this.woodsList.get(i),
                        Constants.PELLET_WOOD
                );
                woodsEditList.add(woodsList);
            }

            pelletWoodsAdapter.notifyDataSetChanged();

            for (PelletProfileModel profile : profiles.values()) {

                if (profile != null) {
                    PelletProfileModel profileEditList = new PelletProfileModel(
                            profile.getBrand(),
                            profile.getWood(),
                            profile.getRating(),
                            profile.getComments(),
                            profile.getId()
                    );
                    this.profileEditList.add(profileEditList);
                }
            }

            profileEditAdapter.notifyDataSetChanged();

            for (String date : logs.keySet()) {
                String logPelletId = logs.get(date);

                PelletProfileModel pelletProfile = profiles.get(logPelletId);

                if (pelletProfile != null) {
                    PelletLogModel logList = new PelletLogModel(
                            date,
                            pelletProfile.getBrand() + " " + pelletProfile.getWood(),
                            pelletProfile.getRating()
                    );
                    logsList.add(logList);
                }
            }

            pelletsLogAdapter.notifyDataSetChanged();

            setBrandsViewLimited(true);
            setWoodsViewLimited(true);
            setLogsViewLimited(true);
            setProfilesViewLimited(true);

            hopperPlaceholder.unVeil();
            usagePlaceholder.unVeil();
            currentPlaceholder.unVeil();
            brandsCardViewRecycler.unVeil();
            woodsCardViewRecycler.unVeil();
            editorRecycler.unVeil();
            logsRecycler.unVeil();
            brandsCardViewRecycler.getVeiledRecyclerView().setVisibility(View.GONE);
            woodsCardViewRecycler.getVeiledRecyclerView().setVisibility(View.GONE);
            editorRecycler.getVeiledRecyclerView().setVisibility(View.GONE);
            logsRecycler.getVeiledRecyclerView().setVisibility(View.GONE);

            toggleLoading(false);

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.e(e, "Pellets JSON Error");
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_pellet_manager)), false);
        }
    }

    private void setWoodsViewLimited(boolean limited) {
        if (pelletWoodsAdapter.getLimitEnabled()) {
            binding.pelletsLayout.pelletsWoods.setViewAll(limited && woodsList.size() > 3);
        }
    }

    private void setBrandsViewLimited(boolean limited) {
        if (pelletBrandsAdapter.getLimitEnabled()) {
            binding.pelletsLayout.pelletsBrands.setViewAll(limited && brandsList.size() > 3);
        }
    }

    private void setLogsViewLimited(boolean limited) {
        if (pelletsLogAdapter.getLimitEnabled()) {
            binding.pelletsLayout.pelletsLogs.setViewAll(limited && logsList.size() > 3);
        }
    }

    private void setProfilesViewLimited(boolean limited) {
        if (profileEditAdapter.getLimitEnabled()) {
            binding.pelletsLayout.pelletsEditor.setViewAll(limited && profileList.size() > 3);
        }
    }

    @Override
    public void onItemDelete(String type, String item, int position) {
        if (socketConnected()) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                    .setAutoDismiss(true)
                    .setTitle(getString(R.string.dialog_confirm_action))
                    .setMessage(getString(R.string.pellets_delete_content))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, which) -> {

                    })
                    .setPositiveButtonWithColor(getString(R.string.delete),
                            R.color.dialog_positive_button_color_red, (dialogInterface, which) ->
                                    onDeleteConfirmed(type, item, position))
                    .build();
            dialog.show();
        }
    }

    @Override
    public void onLogLongClick(String logDate, int position) {
        if (socketConnected()) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                    .setAutoDismiss(true)
                    .setTitle(getString(R.string.dialog_confirm_action))
                    .setMessage(getString(R.string.pellets_delete_content))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, which) -> {
                    })
                    .setPositiveButtonWithColor(getString(R.string.delete),
                            R.color.dialog_positive_button_color_red, (dialogInterface, which) ->
                                    onDeleteConfirmed(Constants.PELLET_LOG, logDate, position))
                    .build();
            dialog.show();
        }
    }

    @Override
    public void onDeleteConfirmed(String type, String item, int position) {
        if (socketConnected()) {
            switch (type) {
                case Constants.PELLET_WOOD -> {
                    ServerControl.sendDeletePelletWood(socket, item, this::processPostResponse);
                    woodsEditList.remove(position);
                    pelletWoodsAdapter.notifyItemRemoved(position);
                    pelletWoodsAdapter.notifyItemRangeChanged(position, woodsEditList.size());
                }
                case Constants.PELLET_BRAND -> {
                    ServerControl.sendDeletePelletBrand(socket, item, this::processPostResponse);
                    brandsEditList.remove(position);
                    pelletBrandsAdapter.notifyItemRemoved(position);
                    pelletBrandsAdapter.notifyItemRangeChanged(position, brandsEditList.size());
                }
                case Constants.PELLET_PROFILE -> deletePelletProfile(item, position);
                case Constants.PELLET_LOG -> {
                    ServerControl.sendDeletePelletLog(socket, item, this::processPostResponse);
                    logsList.remove(position);
                    pelletsLogAdapter.notifyItemRemoved(position);
                    pelletsLogAdapter.notifyItemRangeChanged(position, logsList.size());
                }
            }
        }
    }

    @Override
    public void onItemAdded(String type, String item) {
        if (socketConnected()) {
            switch (type) {
                case Constants.PELLET_WOOD -> {
                    ServerControl.sendAddPelletWood(socket, item, this::processPostResponse);
                    forceRefreshData();
                }
                case Constants.PELLET_BRAND -> {
                    ServerControl.sendAddPelletBrand(socket, item, this::processPostResponse);
                    forceRefreshData();
                }
            }
        }
    }

    @Override
    public void onProfileOpen(PelletProfileModel profile, int position) {
        if (socketConnected()) {
            PelletsEditDialog dialog = new PelletsEditDialog(requireActivity(), brandsList,
                    woodsList, profile, position, PelletsFragment.this);
            dialog.showDialog();
        }
    }

    @Override
    public void onProfileSelected(String profileName, String profileId) {
        if (profileId != null && socket != null) {
            ServerControl.sendLoadPelletProfile(socket, profileId, this::processPostResponse);
            forceRefreshData();
        }
    }

    @Override
    public void onProfileAdd(PelletProfileModel profile) {
        if (socketConnected()) {
            ServerControl.sendAddPelletProfile(socket, profile, this::processPostResponse);
            forceRefreshData();
        }
    }

    @Override
    public void onProfileEdit(PelletProfileModel profile, boolean load) {
        if (socketConnected()) {
            if (load) {
                ServerControl.sendAddPelletProfileLoad(socket, profile, this::processPostResponse);
            } else {
                ServerControl.sendEditPelletProfile(socket, profile, this::processPostResponse);
            }
            forceRefreshData();
        }
    }

    @Override
    public void onProfileDelete(String profileId, int position) {
        deletePelletProfile(profileId, position);
    }

    private void deletePelletProfile(String profile, int position) {
        if (socketConnected()) {
            if (currentPelletId != null && !currentPelletId.equals(profile)) {
                ServerControl.sendDeletePelletProfile(socket, profile, this::processPostResponse);
                profileEditList.remove(position);
                profileEditAdapter.notifyItemRemoved(position);
                profileEditAdapter.notifyItemRangeChanged(position,
                        profileEditList.size());
            } else {
                AlertUtils.createErrorAlert(getActivity(), R.string.pellets_cannot_delete_profile,
                        false);
            }
        }
    }
}