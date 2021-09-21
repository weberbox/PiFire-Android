package com.weberbox.pifire.ui.fragments;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;

public class EventsFragment extends Fragment {
    private static final String TAG = EventsFragment.class.getSimpleName();

    private FragmentEventsBinding mBinding;
    private MainViewModel mMainViewModel;
    private RecyclerView mEventsRecycler;
    private EventsListAdapter mEventsListAdapter;
    private LinearLayout mEventsPlaceholder;
    private ProgressBar mLoadingBar;
    private SwipeRefreshLayout mSwipeRefresh;
    private List<EventViewModel> mEvents;
    private Socket mSocket;

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

        mEventsPlaceholder = mBinding.eventsLayout.eventsPlaceholderContainer;
        mEventsRecycler = mBinding.eventsLayout.eventsList;
        mSwipeRefresh = mBinding.eventsPullRefresh;
        mLoadingBar = mBinding.eventsLayout.loadingProgressbar;

        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSocket != null && mSocket.connected()) {
                    requestListData();
                } else {
                    mSwipeRefresh.setRefreshing(false);
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getEventsData().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(@Nullable String response_data) {
                    mSwipeRefresh.setRefreshing(false);
                    if (response_data != null) {
                        if (getActivity() != null) {
                            FileUtils.saveJSONFile(getActivity(), Constants.JSON_EVENTS, response_data);
                        }
                        updateUIWithData(response_data);
                    }
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean enabled) {
                    checkStoredData();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void checkStoredData() {
        if (mMainViewModel.getEventsData().getValue() == null) {
            toggleLoading(true);
            requestListData();
        } else {
            toggleLoading(false);
        }
    }

    private void requestListData() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_EVENT_DATA, new Ack() {
                @Override
                public void call(Object... args) {
                    if (mMainViewModel != null) {
                        mMainViewModel.setEventsData(args[0].toString());
                    }
                }
            });
        } else {
            if (getActivity() != null) {
                String jsonData = FileUtils.loadJSONFile(getActivity(), Constants.JSON_EVENTS);
                if (jsonData != null && mMainViewModel != null) {
                    mMainViewModel.setEventsData(jsonData);
                }
            }
        }
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

            int customAmount = Integer.parseInt(Prefs.getString(getString(R.string.prefs_event_amount), getString(R.string.def_event_amounts_app)));
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

            mEventsListAdapter = new EventsListAdapter(mEvents);

            mEventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mEventsRecycler.setItemAnimator(new DefaultItemAnimator());
            mEventsRecycler.setAdapter(mEventsListAdapter);

            mEventsPlaceholder.setVisibility(View.GONE);

        } catch (JSONException| IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Log.e(TAG, "JSON Error: " + e.getMessage());
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
