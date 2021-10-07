package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentEventsBinding;
import com.weberbox.pifire.recycler.adapter.EventsListAdapter;
import com.weberbox.pifire.recycler.viewmodel.EventViewModel;
import com.weberbox.pifire.ui.model.DataModel;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.FadeTransition;
import com.weberbox.pifire.utils.FileUtils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding mBinding;
    private MainViewModel mMainViewModel;
    private FrameLayout mRootContainer;
    private EventsListAdapter mEventsListAdapter;
    private LinearLayout mEventsPlaceholder;
    private ProgressBar mLoadingBar;
    private SwipeRefreshLayout mSwipeRefresh;
    private List<EventViewModel> mEvents;
    private Socket mSocket;

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
        mBinding = FragmentEventsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEvents = new ArrayList<>();

        mRootContainer = mBinding.eventsContainer;
        mEventsPlaceholder = mBinding.eventsLayout.eventsPlaceholderContainer;
        mSwipeRefresh = mBinding.eventsPullRefresh;
        mLoadingBar = mBinding.eventsLayout.loadingProgressbar;

        mEventsListAdapter = new EventsListAdapter(mEvents);

        RecyclerView eventsList = mBinding.eventsLayout.eventsList;
        eventsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsList.setItemAnimator(new DefaultItemAnimator());
        eventsList.setAdapter(mEventsListAdapter);

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSocket != null && mSocket.connected()) {
                    forceRefreshData(mSocket);
                } else {
                    mSwipeRefresh.setRefreshing(false);
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getEventsData().observe(getViewLifecycleOwner(), new Observer<DataModel>() {
                @Override
                public void onChanged(@Nullable DataModel eventsData) {
                    mSwipeRefresh.setRefreshing(false);
                    if (eventsData != null && eventsData.getLiveData() != null) {
                        if (eventsData.getIsNewData()) {
                            FileUtils.saveJSONFile(getActivity(), Constants.JSON_EVENTS,
                                    eventsData.getLiveData());
                        }
                        updateUIWithData(eventsData.getLiveData());
                    }
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean enabled) {
                    if (enabled != null && enabled) {
                        if (!mIsLoading) {
                            mIsLoading = true;
                            if (mSocket != null && mSocket.connected()) {
                                toggleLoading(true);
                                requestDataUpdate();
                            }
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
        mBinding = null;
    }

    private void checkViewModelData() {
        if (mMainViewModel.getEventsData().getValue() == null) {
            toggleLoading(true);
            loadStoredDataRequestUpdate();
        }
    }

    private void requestDataUpdate() {
        if (mSocket != null && mSocket.connected()) {
            mIsLoading = true;
            mSocket.emit(ServerConstants.REQUEST_EVENT_DATA, new Ack() {
                @Override
                public void call(Object... args) {
                    if (mMainViewModel != null) {
                        mMainViewModel.setEventsData(args[0].toString(), true);
                        mIsLoading = false;
                    }
                }
            });
        }
    }

    private void loadStoredDataRequestUpdate() {
        String jsonData = FileUtils.loadJSONFile(getActivity(), Constants.JSON_EVENTS);
        if (jsonData != null && mMainViewModel != null) {
            mMainViewModel.setEventsData(jsonData, false);
        }
        if (!mIsLoading) {
            requestDataUpdate();
        }
    }

    private void forceRefreshData(Socket socket) {
        socket.emit(ServerConstants.REQUEST_EVENT_DATA, new Ack() {
            @Override
            public void call(Object... args) {
                if (mMainViewModel != null) {
                    mMainViewModel.setEventsData(args[0].toString(), true);
                }
            }
        });
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
        mEvents.clear();

        try {
            JSONObject rootObject = new JSONObject(response_data);
            JSONArray array = rootObject.getJSONArray(ServerConstants.EVENTS_LIST);

            int customAmount = Integer.parseInt(Prefs.getString(getString(R.string.prefs_event_amount),
                    getString(R.string.def_event_amounts_app)));
            int listAmount = Math.min(customAmount, array.length());

            for (int i = 0; i < listAmount; i++) {
                JSONArray innerArray = array.getJSONArray(i);

                EventViewModel events = new EventViewModel(
                        innerArray.getString(0),
                        innerArray.getString(1),
                        innerArray.getString(2)
                );
                mEvents.add(events);
            }

            mEventsListAdapter.notifyDataSetChanged();

            TransitionManager.beginDelayedTransition(mRootContainer, new FadeTransition());

            mEventsPlaceholder.setVisibility(View.GONE);

        } catch (JSONException| IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.e(e,"JSON Error");
            if (getActivity() != null) {
                showSnackBarMessage(getActivity());
            }
        }

        toggleLoading(false);
    }

    private void showSnackBarMessage(Activity activity) {
        Snackbar snack = Snackbar.make(mBinding.getRoot(), R.string.json_error_events, Snackbar.LENGTH_LONG);
        snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        snack.setTextColor(activity.getColor(R.color.colorWhite));
        snack.show();
    }
}
