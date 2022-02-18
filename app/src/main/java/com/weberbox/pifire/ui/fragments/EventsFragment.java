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
import com.weberbox.pifire.control.ServerControl;
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

import io.socket.client.Socket;
import timber.log.Timber;

public class EventsFragment extends Fragment {

    private FragmentEventsBinding binding;
    private MainViewModel mainViewModel;
    private EventsListAdapter eventsListAdapter;
    private VeilRecyclerFrameView eventsRecycler;
    private ProgressBar loadingBar;
    private SwipeRefreshLayout swipeRefresh;
    private List<EventsModel> events;
    private Socket socket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        events = new ArrayList<>();

        swipeRefresh = binding.eventsPullRefresh;
        loadingBar = binding.loadingProgressbar;

        eventsListAdapter = new EventsListAdapter();

        int padding = getResources().getDimensionPixelOffset(R.dimen.recycler_padding);

        eventsRecycler = binding.eventsLayout.eventsList;
        eventsRecycler.getRecyclerView().setClipToPadding(false);
        eventsRecycler.getRecyclerView().setPadding(0,padding,0,padding);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsRecycler.setAdapter(eventsListAdapter);
        eventsRecycler.addVeiledItems(15);

        swipeRefresh.setOnRefreshListener(() -> {
            if (socketConnected()) {
                forceRefreshData();
            } else {
                swipeRefresh.setRefreshing(false);
            }
        });

        if (getActivity() != null) {
            mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mainViewModel.getEventsData().observe(getViewLifecycleOwner(), eventsData -> {
                swipeRefresh.setRefreshing(false);
                if (eventsData != null && eventsData.getLiveData() != null) {
                    if (eventsData.getIsNewData()) {
                        FileUtils.executorSaveJSON(getActivity(), Constants.JSON_EVENTS,
                                eventsData.getLiveData());
                    }
                    updateUIWithData(eventsData.getLiveData());
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
        FileUtils.executorLoadJSON(getActivity(), Constants.JSON_EVENTS, jsonString -> {
            if (jsonString != null && mainViewModel != null) {
                mainViewModel.setEventsData(jsonString, false);
            } else {
                toggleLoading(false);
            }
        });
    }

    private void forceRefreshData() {
        ServerControl.eventsGetEmit(socket, response -> {
            if (mainViewModel != null) {
                mainViewModel.setEventsData(response, true);
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

    private void updateUIWithData(String responseData) {
        events.clear();

        try {
            JSONObject rootObject = new JSONObject(responseData);
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
                this.events.add(events);
            }

            eventsListAdapter.setEventsList(events);

            eventsRecycler.unVeil();

        } catch (JSONException | IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.e(e,"Events JSON Error");
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_events, false);
        }

        toggleLoading(false);
    }
}
