package com.weberbox.pifire.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentEventsBinding;
import com.weberbox.pifire.model.local.EventsModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.EventsListAdapter;
import com.weberbox.pifire.utils.AlertUtils;
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
    private EventsListAdapter mEventsListAdapter;
    private VeilRecyclerFrameView mEventsRecycler;
    private ProgressBar mLoadingBar;
    private SwipeRefreshLayout mSwipeRefresh;
    private List<EventsModel> mEvents;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentEventsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEvents = new ArrayList<>();

        mSwipeRefresh = mBinding.eventsPullRefresh;
        mLoadingBar = mBinding.eventsLayout.loadingProgressbar;

        mEventsListAdapter = new EventsListAdapter();

        mEventsRecycler = mBinding.eventsLayout.eventsList;
        mEventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventsRecycler.addVeiledItems(15);
        mEventsRecycler.setAdapter(mEventsListAdapter);

        mSwipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                mSwipeRefresh.setRefreshing(false);
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getEventsData().observe(getViewLifecycleOwner(), eventsData -> {
                mSwipeRefresh.setRefreshing(false);
                if (eventsData != null && eventsData.getLiveData() != null) {
                    if (eventsData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_EVENTS,
                                eventsData.getLiveData());
                    }
                    updateUIWithData(eventsData.getLiveData());
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), enabled -> {
                toggleLoading(true);
                requestDataUpdate();
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void showOfflineAlert() {
        if (getActivity() != null) {
            AlertUtils.createOfflineAlert(getActivity());
        }
    }

    private boolean socketConnected() {
        if (mSocket != null && mSocket.connected()) {
            return true;
        } else {
            showOfflineAlert();
            return false;
        }
    }

    private void requestDataUpdate() {
        if (mSocket != null && mSocket.connected()) {
            forceRefreshData();
        } else {
            loadStoredData();
        }
    }

    private void loadStoredData() {
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_EVENTS, jsonString -> {
            if (jsonString != null && mMainViewModel != null) {
                mMainViewModel.setEventsData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        mSocket.emit(ServerConstants.REQUEST_EVENT_DATA, (Ack) args -> {
            if (mMainViewModel != null) {
                mMainViewModel.setEventsData(args[0].toString(), true);
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

                EventsModel events = new EventsModel(
                        innerArray.getString(0),
                        innerArray.getString(1),
                        innerArray.getString(2)
                );
                mEvents.add(events);
            }

            mEventsListAdapter.setEventsList(mEvents);

            mEventsRecycler.unVeil();

        } catch (JSONException | IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.e(e,"Events JSON Error");
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_events, false);
        }

        toggleLoading(false);
    }
}
