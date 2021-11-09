package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.databinding.FragmentPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsCurrentBinding;
import com.weberbox.pifire.databinding.LayoutPelletsEditCardBinding;
import com.weberbox.pifire.databinding.LayoutPelletsEditorBinding;
import com.weberbox.pifire.databinding.LayoutPelletsHopperBinding;
import com.weberbox.pifire.databinding.LayoutPelletsLogsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsProfileAddBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.model.PelletResponseModel;
import com.weberbox.pifire.recycler.adapter.PelletItemsAdapter;
import com.weberbox.pifire.recycler.adapter.PelletProfileEditAdapter;
import com.weberbox.pifire.recycler.adapter.PelletsLogAdapter;
import com.weberbox.pifire.recycler.viewmodel.PelletItemViewModel;
import com.weberbox.pifire.recycler.viewmodel.PelletLogViewModel;
import com.weberbox.pifire.ui.dialogs.PelletPickerDialog;
import com.weberbox.pifire.ui.dialogs.PelletsAddDialog;
import com.weberbox.pifire.ui.dialogs.PelletsDeleteDialog;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.views.CardViewHeaderButton;
import com.weberbox.pifire.ui.views.PelletsCardViewRecycler;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class PelletsFragment extends Fragment implements PelletsCallbackInterface {

    private MainViewModel mMainViewModel;
    private FragmentPelletsBinding mBinding;
    private LayoutPelletsCurrentBinding mPelletsCurrentBinding;
    private RelativeLayout mRootContainer;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mLoadingBar;
    private PelletItemsAdapter mPelletBrandsAdapter;
    private PelletItemsAdapter mPelletWoodsAdapter;
    private PelletsLogAdapter mPelletsLogAdapter;
    private PelletProfileEditAdapter mPelletProfileEditAdapter;
    private List<PelletItemViewModel> mBrandsEditList;
    private List<PelletItemViewModel> mWoodsEditList;
    private List<PelletLogViewModel> mLogsList;
    private List<PelletProfileModel> mProfileList;
    private List<PelletProfileModel> mProfileEditList;
    private List<String> mBrandsList;
    private List<String> mWoodsList;
    private LinearProgressIndicator mHopperLevel;
    private ConstraintLayout mHopperView;
    private ConstraintLayout mHopperPlaceholder;
    private ConstraintLayout mCurrentPlaceholder;
    private ConstraintLayout mCurrentView;
    private LinearLayout mAddProfileCard;
    private LinearLayout mBrandsPlaceholder;
    private LinearLayout mWoodsPlaceholder;
    private LinearLayout mProfilePlaceholder;
    private LinearLayout mLogsPlaceholder;
    private PowerSpinnerView mPelletProfileBrand;
    private PowerSpinnerView mPelletProfileWood;
    private PowerSpinnerView mPelletProfileRating;
    private EditText mProfileAddComments;
    private TextView mAddNewProfile;
    private TextView mHopperLevelText;
    private String mCurrentPelletId;
    private Socket mSocket;
    private Handler mHandler;

    private boolean mIsLoading = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPelletsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBrandsList = new ArrayList<>();
        mWoodsList = new ArrayList<>();
        mBrandsEditList = new ArrayList<>();
        mWoodsEditList = new ArrayList<>();
        mLogsList = new ArrayList<>();
        mProfileList = new ArrayList<>();
        mProfileEditList = new ArrayList<>();

        mHandler = new Handler();

        LayoutPelletsBinding pelletsBinding = mBinding.pelletsLayout;
        LayoutPelletsEditorBinding editorBinding = pelletsBinding.editorCardView;
        LayoutPelletsCurrentBinding currentBinding = pelletsBinding.loadOutCardView;
        LayoutPelletsLogsBinding logsBinding = pelletsBinding.logsCardView;
        LayoutPelletsHopperBinding hopperBinding = pelletsBinding.pelletsHopperLevel;
        mPelletsCurrentBinding = pelletsBinding.loadOutCardView;

        mRootContainer = mBinding.pelletsRootContainer;
        mSwipeRefresh = mBinding.pelletsPullRefresh;
        mLoadingBar = mBinding.loadingProgressbar;
        mHopperLevel = hopperBinding.hopperLevel;
        mHopperLevelText = hopperBinding.hopperLevelText;
        mHopperView = hopperBinding.hopperView;
        mHopperPlaceholder = hopperBinding.hopperHolder;
        mCurrentView = currentBinding.currentView;
        mCurrentPlaceholder = currentBinding.currentHolder;
        mLogsPlaceholder = logsBinding.logsHolder;
        mProfilePlaceholder = editorBinding.profileHolder;
        mAddNewProfile = editorBinding.addProfileButton;

        CardViewHeaderButton hopperHeader = hopperBinding.hopperLevelHeader;

        CardViewHeaderButton currentHeader = currentBinding.loadOutHeader;
        PelletsCardViewRecycler brandsCardView = pelletsBinding.brandsCardView;
        PelletsCardViewRecycler woodsCardView = pelletsBinding.woodsCardView;

        mBrandsPlaceholder = brandsCardView.getHolderView();
        mWoodsPlaceholder = woodsCardView.getHolderView();
        TextView addNewBrand = brandsCardView.getHeaderButton();
        TextView addNewWood = woodsCardView.getHeaderButton();

        TextView refreshPellets = hopperHeader.getButton();
        TextView loadNewPellets = currentHeader.getButton();

        LayoutPelletsProfileAddBinding pelletsProfileAddBinding =
                editorBinding.pelletsAddProfileContainer;
        LayoutPelletsEditCardBinding editCardBinding =
                pelletsProfileAddBinding.pelletEditContainer;

        AppCompatButton pelletAddSave = pelletsProfileAddBinding.pelletAddSave;
        AppCompatButton pelletAddLoad = pelletsProfileAddBinding.pelletAddLoad;
        mPelletProfileBrand = editCardBinding.pelletEditBrandText;
        mPelletProfileWood = editCardBinding.pelletEditWoodText;
        mPelletProfileRating = editCardBinding.pelletEditRatingText;
        mProfileAddComments = editCardBinding.pelletEditCommentsText;
        PowerSpinnerView pelletsRating = editCardBinding.pelletEditRatingText;
        pelletsRating.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);


        RecyclerView brandsCardViewRecycler = brandsCardView.getRecycler();
        RecyclerView woodsCardViewRecycler = woodsCardView.getRecycler();
        RecyclerView logsRecycler = logsBinding.logsRecycler;
        RecyclerView editorRecycler = editorBinding.editorRecycler;
        mAddProfileCard = editorBinding.pelletsAddProfile;

        mPelletBrandsAdapter = new PelletItemsAdapter(mBrandsEditList, this);

        brandsCardViewRecycler.setAdapter(mPelletBrandsAdapter);

        mPelletWoodsAdapter = new PelletItemsAdapter(mWoodsEditList, this);

        woodsCardViewRecycler.setAdapter(mPelletWoodsAdapter);

        mPelletsLogAdapter = new PelletsLogAdapter(mLogsList, this);

        logsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        logsRecycler.setItemAnimator(new DefaultItemAnimator());
        logsRecycler.setAdapter(mPelletsLogAdapter);

        mPelletProfileEditAdapter = new PelletProfileEditAdapter(requireActivity(), mBrandsList,
                mWoodsList, mProfileEditList, this);

        editorRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        editorRecycler.setItemAnimator(new DefaultItemAnimator());
        editorRecycler.setAdapter(mPelletProfileEditAdapter);

        editorRecycler.setNestedScrollingEnabled(false);
        logsRecycler.setNestedScrollingEnabled(false);


        mSwipeRefresh.setOnRefreshListener(() -> {
            if (mSocket != null && mSocket.connected()) {
                forceRefreshData();
            } else {
                mSwipeRefresh.setRefreshing(false);
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        refreshPellets.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                GrillControl.setCheckHopperLevel(mSocket);
                toggleLoading(true);
                startDelayedRefresh();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        loadNewPellets.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                if (mProfileList != null && mCurrentPelletId != null) {
                    PelletPickerDialog pelletPickerDialog = new PelletPickerDialog(getActivity(),
                            PelletsFragment.this, mProfileList, mCurrentPelletId);
                    pelletPickerDialog.showDialog();
                }
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        addNewBrand.setOnClickListener(v -> {
            if (getActivity() != null) {
                if (mSocket != null && mSocket.connected()) {
                    PelletsAddDialog pelletsAddDialog = new PelletsAddDialog(getActivity(),
                            PelletsFragment.this, Constants.PELLET_BRAND,
                            getString(R.string.pellets_add_brand));
                    pelletsAddDialog.showDialog();
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        addNewWood.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                PelletsAddDialog pelletsAddDialog = new PelletsAddDialog(getActivity(),
                        PelletsFragment.this, Constants.PELLET_WOOD,
                        getString(R.string.pellets_add_woods));
                pelletsAddDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        mAddNewProfile.setOnClickListener(v -> {
            if (mSocket != null && mSocket.connected()) {
                toggleCardView();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        });

        pelletAddSave.setOnClickListener(v -> {
            if (mPelletProfileBrand.getText().length() == 0) {
                mPelletProfileBrand.setError(getString(R.string.settings_blank_error));
            } else if (mPelletProfileWood.getText().length() == 0) {
                mPelletProfileWood.setError(getString(R.string.settings_blank_error));
            } else if (mPelletProfileRating.getText().length() == 0) {
                mPelletProfileRating.setError(getString(R.string.settings_blank_error));
            } else {
                mAddProfileCard.setVisibility(View.GONE);
                mAddNewProfile.setText(R.string.pellets_add);
                if (mSocket != null && mSocket.connected()) {
                    GrillControl.setAddPelletProfile(mSocket,
                            new PelletProfileModel(
                                    mPelletProfileBrand.getText().toString(),
                                    mPelletProfileWood.getText().toString(),
                                    StringUtils.getRatingInt(mPelletProfileRating.getText().toString()),
                                    mProfileAddComments.getText().toString(),
                                    "None"
                            ));
                    forceRefreshData();
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        pelletAddLoad.setOnClickListener(v -> {
            if (mPelletProfileBrand.getText().length() == 0) {
                mPelletProfileBrand.setError(getString(R.string.settings_blank_error));
            } else if (mPelletProfileWood.getText().length() == 0) {
                mPelletProfileWood.setError(getString(R.string.settings_blank_error));
            } else if (mPelletProfileRating.getText().length() == 0) {
                mPelletProfileRating.setError(getString(R.string.settings_blank_error));
            } else {
                mAddProfileCard.setVisibility(View.GONE);
                mAddNewProfile.setText(R.string.pellets_add);
                if (mSocket != null && mSocket.connected()) {
                    GrillControl.setAddPelletProfileLoad(mSocket,
                            new PelletProfileModel(
                                    mPelletProfileBrand.getText().toString(),
                                    mPelletProfileWood.getText().toString(),
                                    StringUtils.getRatingInt(mPelletProfileRating.getText().toString()),
                                    mProfileAddComments.getText().toString(),
                                    "None"
                            ));
                    forceRefreshData();
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        mPelletProfileBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPelletProfileBrand.setError(getString(R.string.settings_blank_error));
                } else {
                    mPelletProfileBrand.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPelletProfileWood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPelletProfileWood.setError(getString(R.string.settings_blank_error));
                } else {
                    mPelletProfileWood.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPelletProfileRating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPelletProfileRating.setError(getString(R.string.settings_blank_error));
                } else {
                    mPelletProfileRating.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPelletProfileBrand.setSpinnerOutsideTouchListener((v, motionEvent) ->
                mPelletProfileBrand.dismiss());


        mPelletProfileWood.setSpinnerOutsideTouchListener((v, motionEvent) ->
                mPelletProfileWood.dismiss());


        mPelletProfileRating.setSpinnerOutsideTouchListener((v, motionEvent) ->
                mPelletProfileRating.dismiss());

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getPelletData().observe(getViewLifecycleOwner(), pelletData -> {
                mSwipeRefresh.setRefreshing(false);
                if (pelletData != null && pelletData.getLiveData() != null) {
                    if (pelletData.getIsNewData()) {
                        FileUtils.saveJSONFile(getActivity(), Constants.JSON_PELLETS,
                                pelletData.getLiveData());
                    }
                    updateUIWithData(pelletData.getLiveData());
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                if (enabled != null && enabled) {
                    if (!mIsLoading) {
                        mIsLoading = true;
                        if (mSocket != null && mSocket.connected()) {
                            toggleLoading(true);
                            requestDataUpdate();
                        }
                    }
                }
            });
        }

        checkViewModelData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopDelayedRefresh();
        mBinding = null;
    }

    private void stopDelayedRefresh() {
        mHandler.removeCallbacks(runnable);
    }

    private void startDelayedRefresh() {
        mHandler.postDelayed(runnable, 1500);
    }

    private final Runnable runnable = this::forceRefreshData;

    private void checkViewModelData() {
        if (mMainViewModel.getEventsData().getValue() == null) {
            toggleLoading(true);
            loadStoredDataRequestUpdate();
        }
    }

    private void requestDataUpdate() {
        if (mSocket != null && mSocket.connected()) {
            mIsLoading = true;
            mSocket.emit(ServerConstants.REQUEST_PELLET_DATA, (Ack) args -> {
                if (mMainViewModel != null) {
                    mMainViewModel.setPelletData(args[0].toString(), true);
                    mIsLoading = false;
                }
            });
        }
    }

    private void loadStoredDataRequestUpdate() {
        String jsonData = FileUtils.loadJSONFile(getActivity(), Constants.JSON_PELLETS);
        if (jsonData != null && mMainViewModel != null) {
            mMainViewModel.setPelletData(jsonData, false);
        }
        if (!mIsLoading) {
            requestDataUpdate();
        }
    }

    private void forceRefreshData() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_PELLET_DATA, (Ack) args -> {
                if (mMainViewModel != null) {
                    mMainViewModel.setPelletData(args[0].toString(), true);
                }
            });
        }
    }

    private void toggleLoading(boolean show) {
        if (show && mSocket != null && mSocket.connected()) {
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUIWithData(String response_data) {
        mBrandsList.clear();
        mWoodsList.clear();
        mBrandsEditList.clear();
        mWoodsEditList.clear();
        mLogsList.clear();
        mProfileList.clear();
        mProfileEditList.clear();

        try {
            PelletResponseModel pelletResponseModel = PelletResponseModel.parseJSON(response_data);

            Integer pelletLevel = pelletResponseModel.getCurrent().getHopperLevel();

            if (pelletLevel != null) {
                mHopperLevel.setProgress(pelletLevel);
                mHopperLevelText.setText(StringUtils.formatPercentage(pelletLevel));
                if (getActivity() != null) {
                    if (pelletLevel < AppConfig.LOW_PELLET_WARNING) {
                        mHopperLevel.setIndicatorColor(ContextCompat.getColor(getActivity(),
                                R.color.colorPelletDanger));
                        mHopperLevel.setTrackColor(ContextCompat.getColor(getActivity(),
                                R.color.colorPelletDangerBG));
                    } else {
                        mHopperLevel.setIndicatorColor(ContextCompat.getColor(getActivity(),
                                R.color.colorAccent));
                        mHopperLevel.setTrackColor(ContextCompat.getColor(getActivity(),
                                R.color.colorAccent_disabled));
                    }
                }
            }

            PelletResponseModel.Current current =  pelletResponseModel.getCurrent();

            mBrandsList.addAll(pelletResponseModel.getBrands());
            mWoodsList.addAll(pelletResponseModel.getWoods());

            Map<String, String> logs = pelletResponseModel.getLogs();
            Map<String, PelletProfileModel> profiles = pelletResponseModel.getProfiles();

            TransitionManager.beginDelayedTransition(mRootContainer, new Fade(Fade.IN));

            mProfileList.addAll(profiles.values());

            if (current.getDateLoaded() != null) {
                mPelletsCurrentBinding.currentDateLoadedText.setText(current.getDateLoaded());
            }

            if (current.getPelletId() != null) {
                mCurrentPelletId = current.getPelletId();
            }

            if (current.getPelletId() != null) {
                PelletProfileModel currentProfile = profiles.get(current.getPelletId());
                if (currentProfile != null) {
                    mPelletsCurrentBinding.currentBrandText.setText(currentProfile.getBrand());
                    mPelletsCurrentBinding.currentWoodText.setText(currentProfile.getWood());
                    mPelletsCurrentBinding.currentCommentsText.setText(currentProfile.getComments());
                    mPelletsCurrentBinding.currentRatingText.setText(StringUtils.getRatingText(
                            currentProfile.getRating()));
                }
            }

            for (int i = 0; i < mBrandsList.size(); i++) {
                PelletItemViewModel brandsList = new PelletItemViewModel(
                        mBrandsList.get(i),
                        Constants.PELLET_BRAND
                );
                mBrandsEditList.add(brandsList);
            }

            mPelletBrandsAdapter.notifyDataSetChanged();


            for (int i = 0; i < mWoodsList.size(); i++) {
                PelletItemViewModel woodsList = new PelletItemViewModel(
                        mWoodsList.get(i),
                        Constants.PELLET_WOOD
                );
                mWoodsEditList.add(woodsList);
            }

            mPelletWoodsAdapter.notifyDataSetChanged();

            for (PelletProfileModel profile:profiles.values()) {

                if (profile != null) {
                    PelletProfileModel profileEditList = new PelletProfileModel(
                            profile.getBrand(),
                            profile.getWood(),
                            profile.getRating(),
                            profile.getComments(),
                            profile.getId()
                    );
                    mProfileEditList.add(profileEditList);
                }
            }

            mPelletProfileEditAdapter.notifyDataSetChanged();

            for (String date:logs.keySet()) {
                String logPelletId = logs.get(date);

                PelletProfileModel pelletProfile = profiles.get(logPelletId);

                if (pelletProfile != null) {
                    PelletLogViewModel logList = new PelletLogViewModel(
                            date,
                            pelletProfile.getBrand() + " " + pelletProfile.getWood(),
                            pelletProfile.getRating()
                    );
                    mLogsList.add(logList);
                }
            }

            mPelletsLogAdapter.notifyDataSetChanged();

            DefaultSpinnerAdapter brandsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileBrand);
            mPelletProfileBrand.setSpinnerAdapter(brandsSpinnerAdapter);
            mPelletProfileBrand.setItems(mBrandsList);
            mPelletProfileBrand.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
            setPowerSpinnerMaxHeight(brandsSpinnerAdapter, mPelletProfileBrand.getSpinnerRecyclerView());

            DefaultSpinnerAdapter woodsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileWood);
            mPelletProfileWood.setSpinnerAdapter(woodsSpinnerAdapter);
            mPelletProfileWood.setItems(mWoodsList);
            mPelletProfileWood.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
            setPowerSpinnerMaxHeight(woodsSpinnerAdapter, mPelletProfileWood.getSpinnerRecyclerView());


        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e,"JSON Error");
            showSnackBarMessage(getActivity(), R.string.json_error_pellets);
        }

        mHopperPlaceholder.setVisibility(View.GONE);
        mCurrentPlaceholder.setVisibility(View.GONE);
        mBrandsPlaceholder.setVisibility(View.GONE);
        mWoodsPlaceholder.setVisibility(View.GONE);
        mProfilePlaceholder.setVisibility(View.GONE);
        mLogsPlaceholder.setVisibility(View.GONE);
        mHopperView.setVisibility(View.VISIBLE);
        mCurrentView.setVisibility(View.VISIBLE);

        toggleLoading(false);
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
        boolean visibility = mAddProfileCard.getVisibility() == View.VISIBLE;
        if (visibility) {
            AnimUtils.slideUp(mAddProfileCard);
        } else {
            AnimUtils.slideDown(mAddProfileCard);
        }
        mAddNewProfile.setText(visibility ? R.string.pellets_add : R.string.close);
    }

    @Override
    public void onItemDelete(String type, String item, int position) {
        if (getActivity() != null) {
            if (mSocket != null && mSocket.connected()) {
                PelletsDeleteDialog pelletsDialog = new PelletsDeleteDialog(getActivity(),
                        this, type, item, position);
                pelletsDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        }
    }

    @Override
    public void onLogLongClick(String logDate, int position) {
        if (getActivity() != null) {
            if (mSocket != null && mSocket.connected()) {
                PelletsDeleteDialog pelletsDialog = new PelletsDeleteDialog(getActivity(),
                        this, Constants.PELLET_LOG, logDate, position);
                pelletsDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        }
    }

    @Override
    public void onDeleteConfirmed(String type, String item, int position) {
        if (mSocket != null && mSocket.connected()) {
            switch (type) {
                case Constants.PELLET_WOOD:
                    GrillControl.setDeletePelletWood(mSocket, item);
                    mWoodsEditList.remove(position);
                    mPelletWoodsAdapter.notifyItemRemoved(position);
                    mPelletWoodsAdapter.notifyItemRangeChanged(position, mWoodsEditList.size());
                    break;
                case Constants.PELLET_BRAND:
                    GrillControl.setDeletePelletBrand(mSocket, item);
                    mBrandsEditList.remove(position);
                    mPelletBrandsAdapter.notifyItemRemoved(position);
                    mPelletBrandsAdapter.notifyItemRangeChanged(position, mBrandsEditList.size());
                    break;
                case Constants.PELLET_PROFILE:
                    deletePelletProfile(item, position);
                    break;
                case Constants.PELLET_LOG:
                    GrillControl.setDeletePelletLog(mSocket, item);
                    mLogsList.remove(position);
                    mPelletsLogAdapter.notifyItemRemoved(position);
                    mPelletsLogAdapter.notifyItemRangeChanged(position, mLogsList.size());
                    break;
            }
        } else {
            if (getActivity() != null) AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    @Override
    public void onItemAdded(String type, String item) {
        if (mSocket != null && mSocket.connected()) {
            switch (type) {
                case Constants.PELLET_WOOD:
                    GrillControl.setAddPelletWood(mSocket, item);
                    forceRefreshData();
                    break;
                case Constants.PELLET_BRAND:
                    GrillControl.setAddPelletBrand(mSocket, item);
                    forceRefreshData();
                    break;
            }
        } else {
            AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    @Override
    public void onProfileSelected(String profileName, String profileId) {
        if (profileId != null && mSocket != null) {
            GrillControl.setLoadPelletProfile(mSocket, profileId);
            forceRefreshData();
        }
    }

    @Override
    public void onProfileEdit(PelletProfileModel model) {
        if (mSocket != null && mSocket.connected()) {
            GrillControl.setEditPelletProfile(mSocket, model);
            forceRefreshData();
        } else {
            AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    @Override
    public void onProfileDelete(String profileId, int position) {
        deletePelletProfile(profileId, position);
    }

    private void deletePelletProfile(String profile, int position) {
        if (mSocket != null && mSocket.connected()) {
            if (mCurrentPelletId != null && !mCurrentPelletId.equals(profile)) {
                GrillControl.setDeletePelletProfile(mSocket, profile);
                mProfileEditList.remove(position);
                mPelletProfileEditAdapter.notifyItemRemoved(position);
                mPelletProfileEditAdapter.notifyItemRangeChanged(position,
                        mProfileEditList.size());
            } else {
                showSnackBarMessage(getActivity(), R.string.pellets_cannot_delete_profile);
            }
        } else {
            AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    private void showSnackBarMessage(Activity activity, int message) {
        if (activity != null) {
            Snackbar snack = Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_LONG);
            snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
            snack.setTextColor(activity.getColor(R.color.colorWhite));
            snack.show();
        }
    }
}