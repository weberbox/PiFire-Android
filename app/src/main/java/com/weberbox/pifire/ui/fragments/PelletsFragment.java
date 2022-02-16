package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.JsonSyntaxException;
import com.skydoves.androidveil.VeilLayout;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsCurrentBinding;
import com.weberbox.pifire.databinding.LayoutPelletsEditCardBinding;
import com.weberbox.pifire.databinding.LayoutPelletsHopperBinding;
import com.weberbox.pifire.databinding.LayoutPelletsProfileAddBinding;
import com.weberbox.pifire.interfaces.PelletsProfileCallback;
import com.weberbox.pifire.model.local.PelletItemModel;
import com.weberbox.pifire.model.local.PelletLogModel;
import com.weberbox.pifire.model.remote.PelletDataModel;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.PelletItemsAdapter;
import com.weberbox.pifire.recycler.adapter.PelletProfileEditAdapter;
import com.weberbox.pifire.recycler.adapter.PelletsLogAdapter;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.PelletsAddDialog;
import com.weberbox.pifire.ui.dialogs.ProfilePickerDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.views.CardViewHeaderButton;
import com.weberbox.pifire.ui.views.PelletsCardViewRecycler;
import com.weberbox.pifire.ui.views.PelletsEditorRecycler;
import com.weberbox.pifire.ui.views.PelletsLogsRecycler;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import timber.log.Timber;

public class PelletsFragment extends Fragment implements PelletsProfileCallback {

    private MainViewModel mainViewModel;
    private FragmentPelletsBinding binding;
    private LayoutPelletsCurrentBinding pelletsCurrentBinding;
    private RelativeLayout rootContainer;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar loadingBar;
    private PelletItemsAdapter pelletBrandsAdapter, pelletWoodsAdapter;
    private PelletsLogAdapter pelletsLogAdapter;
    private PelletProfileEditAdapter pelletProfileEditAdapter;
    private List<PelletItemModel> brandsEditList, woodsEditList;
    private List<PelletLogModel> logsList;
    private List<PelletProfileModel> profileList, profileEditList;
    private List<String> brandsList, woodsList;
    private LinearProgressIndicator hopperLevel;
    private VeilLayout hopperPlaceholder, currentPlaceholder;
    private VeilRecyclerFrameView brandsCardViewRecycler, woodsCardViewRecycler;
    private VeilRecyclerFrameView editorRecycler, logsRecycler;
    private LinearLayout addProfileCard;
    private PowerSpinnerView pelletProfileBrand, pelletProfileWood, pelletProfileRating;
    private EditText profileAddComments;
    private TextView addNewProfile, hopperLevelText;
    private String currentPelletId;
    private Socket socket;
    private Handler handler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPelletsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
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

        handler = new Handler();

        LayoutPelletsBinding pelletsBinding = binding.pelletsLayout;
        LayoutPelletsCurrentBinding currentBinding = pelletsBinding.loadOutCardView;
        LayoutPelletsHopperBinding hopperBinding = pelletsBinding.pelletsHopperLevel;
        pelletsCurrentBinding = pelletsBinding.loadOutCardView;

        rootContainer = binding.pelletsRootContainer;
        swipeRefresh = binding.pelletsPullRefresh;
        loadingBar = binding.loadingProgressbar;
        hopperLevel = hopperBinding.hopperLevel;
        hopperLevelText = hopperBinding.hopperLevelText;
        hopperPlaceholder = hopperBinding.hopperHolder;
        currentPlaceholder = currentBinding.currentHolder;

        CardViewHeaderButton hopperHeader = hopperBinding.hopperLevelHeader;

        CardViewHeaderButton currentHeader = currentBinding.loadOutHeader;
        PelletsCardViewRecycler brandsCardView = pelletsBinding.brandsCardView;
        PelletsCardViewRecycler woodsCardView = pelletsBinding.woodsCardView;
        PelletsLogsRecycler logsCardView = pelletsBinding.logsCardView;
        PelletsEditorRecycler editorCardView = pelletsBinding.editorCardView;

        addNewProfile = editorCardView.getHeaderButton();
        TextView addNewBrand = brandsCardView.getHeaderButton();
        TextView addNewWood = woodsCardView.getHeaderButton();
        TextView brandsViewAll = brandsCardView.getViewAllButton();
        TextView woodsViewAll = woodsCardView.getViewAllButton();
        TextView logsViewAll = logsCardView.getViewAllButton();
        TextView profilesViewAll = editorCardView.getViewAllButton();

        TextView refreshPellets = hopperHeader.getButton();
        TextView loadNewPellets = currentHeader.getButton();

        LayoutPelletsProfileAddBinding pelletsProfileAddBinding =
                editorCardView.getAddProfileContainer();
        LayoutPelletsEditCardBinding editCardBinding =
                pelletsProfileAddBinding.pelletEditContainer;

        AppCompatButton pelletAddSave = pelletsProfileAddBinding.pelletAddSave;
        AppCompatButton pelletAddLoad = pelletsProfileAddBinding.pelletAddLoad;
        pelletProfileBrand = editCardBinding.pelletEditBrandText;
        pelletProfileWood = editCardBinding.pelletEditWoodText;
        pelletProfileRating = editCardBinding.pelletEditRatingText;
        profileAddComments = editCardBinding.pelletEditCommentsText;
        PowerSpinnerView pelletsRating = editCardBinding.pelletEditRatingText;
        pelletsRating.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);

        addProfileCard = editorCardView.getAddProfileView();

        pelletBrandsAdapter = new PelletItemsAdapter(brandsEditList, this, true);

        brandsCardViewRecycler = brandsCardView.getRecycler();
        brandsCardViewRecycler.setAdapter(pelletBrandsAdapter);

        pelletWoodsAdapter = new PelletItemsAdapter(woodsEditList, this, true);

        woodsCardViewRecycler = woodsCardView.getRecycler();
        woodsCardViewRecycler.setAdapter(pelletWoodsAdapter);

        pelletsLogAdapter = new PelletsLogAdapter(logsList, this, true);

        logsRecycler = logsCardView.getRecycler();
        logsRecycler.setAdapter(pelletsLogAdapter);

        pelletProfileEditAdapter = new PelletProfileEditAdapter(requireActivity(), brandsList,
                woodsList, profileEditList, this, true);

        editorRecycler = editorCardView.getRecycler();
        editorRecycler.setAdapter(pelletProfileEditAdapter);

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
                startDelayedRefresh();
            }
        });

        loadNewPellets.setOnClickListener(v -> {
            if (socketConnected()) {
                if (profileList != null && currentPelletId != null) {
                    ProfilePickerDialog profilePickerDialog = new ProfilePickerDialog(getActivity(),
                            PelletsFragment.this, profileList, currentPelletId);
                    profilePickerDialog.showDialog();
                }
            }
        });

        addNewBrand.setOnClickListener(v -> {
            if (socketConnected()) {
                PelletsAddDialog pelletsAddDialog = new PelletsAddDialog(getActivity(),
                        PelletsFragment.this, Constants.PELLET_BRAND,
                        getString(R.string.pellets_add_brand));
                pelletsAddDialog.showDialog();
            }
        });

        addNewWood.setOnClickListener(v -> {
            if (socketConnected()) {
                PelletsAddDialog pelletsAddDialog = new PelletsAddDialog(getActivity(),
                        PelletsFragment.this, Constants.PELLET_WOOD,
                        getString(R.string.pellets_add_woods));
                pelletsAddDialog.showDialog();
            }
        });

        addNewProfile.setOnClickListener(v -> {
            if (socketConnected()) {
                toggleCardView();
            }
        });

        pelletAddSave.setOnClickListener(v -> {
            if (pelletProfileBrand.getText().length() == 0) {
                pelletProfileBrand.setError(getString(R.string.settings_blank_error));
            } else if (pelletProfileWood.getText().length() == 0) {
                pelletProfileWood.setError(getString(R.string.settings_blank_error));
            } else if (pelletProfileRating.getText().length() == 0) {
                pelletProfileRating.setError(getString(R.string.settings_blank_error));
            } else {
                addProfileCard.setVisibility(View.GONE);
                addNewProfile.setText(R.string.pellets_add);
                if (socketConnected()) {
                    ServerControl.sendAddPelletProfile(socket,
                            new PelletProfileModel(
                                    pelletProfileBrand.getText().toString(),
                                    pelletProfileWood.getText().toString(),
                                    StringUtils.getRatingInt(pelletProfileRating.getText().toString()),
                                    profileAddComments.getText().toString(),
                                    "None"
                            ), this::processPostResponse);
                    forceRefreshData();
                }
            }
        });

        pelletAddLoad.setOnClickListener(v -> {
            if (pelletProfileBrand.getText().length() == 0) {
                pelletProfileBrand.setError(getString(R.string.settings_blank_error));
            } else if (pelletProfileWood.getText().length() == 0) {
                pelletProfileWood.setError(getString(R.string.settings_blank_error));
            } else if (pelletProfileRating.getText().length() == 0) {
                pelletProfileRating.setError(getString(R.string.settings_blank_error));
            } else {
                addProfileCard.setVisibility(View.GONE);
                addNewProfile.setText(R.string.pellets_add);
                if (socketConnected()) {
                    ServerControl.sendAddPelletProfileLoad(socket,
                            new PelletProfileModel(
                                    pelletProfileBrand.getText().toString(),
                                    pelletProfileWood.getText().toString(),
                                    StringUtils.getRatingInt(pelletProfileRating.getText().toString()),
                                    profileAddComments.getText().toString(),
                                    "None"
                            ), this::processPostResponse);
                    forceRefreshData();
                }
            }
        });

        brandsViewAll.setOnClickListener(v -> {
            setBrandsViewLimited(false);
            pelletBrandsAdapter.setLimitEnabled(false);
            pelletBrandsAdapter.notifyDataSetChanged();
        });

        woodsViewAll.setOnClickListener(v -> {
            setWoodsViewLimited(false);
            pelletWoodsAdapter.setLimitEnabled(false);
            pelletWoodsAdapter.notifyDataSetChanged();
        });

        logsViewAll.setOnClickListener(v -> {
            setLogsViewLimited(false);
            pelletsLogAdapter.setLimitEnabled(false);
            pelletsLogAdapter.notifyDataSetChanged();
        });

        profilesViewAll.setOnClickListener(v -> {
            setProfilesViewLimited(false);
            pelletProfileEditAdapter.setLimitEnabled(false);
            pelletProfileEditAdapter.notifyDataSetChanged();
        });

        pelletProfileBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    pelletProfileBrand.setError(getString(R.string.settings_blank_error));
                } else {
                    pelletProfileBrand.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        pelletProfileWood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    pelletProfileWood.setError(getString(R.string.settings_blank_error));
                } else {
                    pelletProfileWood.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        pelletProfileRating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    pelletProfileRating.setError(getString(R.string.settings_blank_error));
                } else {
                    pelletProfileRating.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        pelletProfileBrand.setSpinnerOutsideTouchListener((v, motionEvent) ->
                pelletProfileBrand.dismiss());

        pelletProfileWood.setSpinnerOutsideTouchListener((v, motionEvent) ->
                pelletProfileWood.dismiss());

        pelletProfileRating.setSpinnerOutsideTouchListener((v, motionEvent) ->
                pelletProfileRating.dismiss());

        DefaultSpinnerAdapter brandsSpinnerAdapter = new DefaultSpinnerAdapter(pelletProfileBrand);
        pelletProfileBrand.setSpinnerAdapter(brandsSpinnerAdapter);
        pelletProfileBrand.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
        setPowerSpinnerMaxHeight(brandsSpinnerAdapter, pelletProfileBrand.getSpinnerRecyclerView());

        DefaultSpinnerAdapter woodsSpinnerAdapter = new DefaultSpinnerAdapter(pelletProfileWood);
        pelletProfileWood.setSpinnerAdapter(woodsSpinnerAdapter);
        pelletProfileWood.setItems(woodsList);
        pelletProfileWood.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
        setPowerSpinnerMaxHeight(woodsSpinnerAdapter, pelletProfileWood.getSpinnerRecyclerView());

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

            mainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                toggleLoading(true);
                requestDataUpdate();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopDelayedRefresh();
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

    private void stopDelayedRefresh() {
        handler.removeCallbacks(runnable);
    }

    private void startDelayedRefresh() {
        handler.postDelayed(runnable, 1500);
    }

    private final Runnable runnable = this::forceRefreshData;

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
            loadingBar.setVisibility(View.GONE);
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

            Integer pelletLevel = pelletDataModel.getCurrent().getHopperLevel();

            if (pelletLevel != null) {
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
                                R.color.colorAccent_disabled));
                    }
                }
            }

            PelletDataModel.Current current = pelletDataModel.getCurrent();

            brandsList.addAll(pelletDataModel.getBrands());
            woodsList.addAll(pelletDataModel.getWoods());

            Map<String, String> logs = pelletDataModel.getLogs();
            Map<String, PelletProfileModel> profiles = pelletDataModel.getProfiles();

            TransitionManager.beginDelayedTransition(rootContainer, new Fade(Fade.IN));

            profileList.addAll(profiles.values());

            if (current.getDateLoaded() != null) {
                pelletsCurrentBinding.currentDateLoadedText.setText(current.getDateLoaded());
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

            pelletProfileEditAdapter.notifyDataSetChanged();

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

            pelletProfileBrand.setItems(brandsList);
            pelletProfileWood.setItems(woodsList);

            hopperPlaceholder.unVeil();
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
            Timber.w(e, "Pellets JSON Error");
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_pellets, false);
        }
    }

    private void setWoodsViewLimited(boolean limited) {
        if (pelletWoodsAdapter.getLimitEnabled()) {
            binding.pelletsLayout.woodsCardView.setViewAll(limited && woodsList.size() > 3);
        }
    }

    private void setBrandsViewLimited(boolean limited) {
        if (pelletBrandsAdapter.getLimitEnabled()) {
            binding.pelletsLayout.brandsCardView.setViewAll(limited && brandsList.size() > 3);
        }
    }

    private void setLogsViewLimited(boolean limited) {
        if (pelletsLogAdapter.getLimitEnabled()) {
            binding.pelletsLayout.logsCardView.setViewAll(limited && logsList.size() > 3);
        }
    }

    private void setProfilesViewLimited(boolean limited) {
        if (pelletProfileEditAdapter.getLimitEnabled()) {
            binding.pelletsLayout.editorCardView.setViewAll(limited && profileList.size() > 3);
        }
    }

    private void setPowerSpinnerMaxHeight(DefaultSpinnerAdapter adapter, RecyclerView recyclerView) {
        if (adapter != null) {
            if (adapter.getItemCount() > 6 && getActivity() != null) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = size.y / 4;
                recyclerView.setLayoutParams(params);
            }
        }
    }

    private void toggleCardView() {
        boolean visibility = addProfileCard.getVisibility() == View.VISIBLE;
        if (visibility) {
            AnimUtils.slideClosed(addProfileCard);
        } else {
            AnimUtils.slideOpen(addProfileCard);
        }
        addNewProfile.setText(visibility ? R.string.pellets_add : R.string.close);
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
                case Constants.PELLET_WOOD:
                    ServerControl.sendDeletePelletWood(socket, item, this::processPostResponse);
                    woodsEditList.remove(position);
                    pelletWoodsAdapter.notifyItemRemoved(position);
                    pelletWoodsAdapter.notifyItemRangeChanged(position, woodsEditList.size());
                    break;
                case Constants.PELLET_BRAND:
                    ServerControl.sendDeletePelletBrand(socket, item, this::processPostResponse);
                    brandsEditList.remove(position);
                    pelletBrandsAdapter.notifyItemRemoved(position);
                    pelletBrandsAdapter.notifyItemRangeChanged(position, brandsEditList.size());
                    break;
                case Constants.PELLET_PROFILE:
                    deletePelletProfile(item, position);
                    break;
                case Constants.PELLET_LOG:
                    ServerControl.sendDeletePelletLog(socket, item, this::processPostResponse);
                    logsList.remove(position);
                    pelletsLogAdapter.notifyItemRemoved(position);
                    pelletsLogAdapter.notifyItemRangeChanged(position, logsList.size());
                    break;
            }
        }
    }

    @Override
    public void onItemAdded(String type, String item) {
        if (socketConnected()) {
            switch (type) {
                case Constants.PELLET_WOOD:
                    ServerControl.sendAddPelletWood(socket, item, this::processPostResponse);
                    forceRefreshData();
                    break;
                case Constants.PELLET_BRAND:
                    ServerControl.sendAddPelletBrand(socket, item, this::processPostResponse);
                    forceRefreshData();
                    break;
            }
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
    public void onProfileEdit(PelletProfileModel model) {
        if (socketConnected()) {
            ServerControl.sendEditPelletProfile(socket, model, this::processPostResponse);
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
                pelletProfileEditAdapter.notifyItemRemoved(position);
                pelletProfileEditAdapter.notifyItemRangeChanged(position,
                        profileEditList.size());
            } else {
                AlertUtils.createErrorAlert(getActivity(), R.string.pellets_cannot_delete_profile,
                        false);
            }
        }
    }
}