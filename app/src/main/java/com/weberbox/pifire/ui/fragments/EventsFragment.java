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

import com.pixplicity.easyprefs.library.Prefs;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.databinding.FragmentEventsBinding;
import com.weberbox.pifire.model.remote.EventsModel;
import com.weberbox.pifire.model.remote.EventsModel.Events;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.recycler.adapter.EventsListAdapter;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;

import org.jetbrains.annotations.NotNull;

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
    private List<Events> events;
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
        eventsRecycler = binding.eventsList;

        eventsListAdapter = new EventsListAdapter(requireActivity());

        int padding = getResources().getDimensionPixelOffset(R.dimen.recycler_padding);

        eventsRecycler.getRecyclerView().setClipToPadding(false);
        eventsRecycler.getRecyclerView().setPadding(0, padding, 0, padding);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        eventsRecycler.setAdapter(eventsListAdapter);
        eventsRecycler.addVeiledItems(10);

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
    public void onResume() {
        super.onResume();
        socket = ((PiFireApplication) requireActivity().getApplication()).getSocket();
        requestDataUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        socket = null;
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
            loadingBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateUIWithData(String responseData) {
        events.clear();

        EventsModel eventsModel = EventsModel.parseJSON(responseData);

        int customAmount = Integer.parseInt(Prefs.getString(
                getString(R.string.prefs_event_amount),
                getString(R.string.def_event_amounts_app)));
        int listAmount = Math.min(customAmount, eventsModel.getEventsList().size());

        try {
            for (int i = 0; i < listAmount; i++) {
                List<String> event = eventsModel.getEventsList().get(i);
                Events events = new Events(event.get(0), event.get(1), event.get(2));
                this.events.add(events);
            }
        } catch (IndexOutOfBoundsException e) {
            Timber.e(e, "Events Index Exception");
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_events)), false);
        }

        eventsListAdapter.setEventsList(events);

        eventsRecycler.unVeil();
        eventsRecycler.getVeiledRecyclerView().setVisibility(View.GONE);

        toggleLoading(false);
    }
}
