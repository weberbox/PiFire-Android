package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.pixplicity.easyprefs.library.Prefs;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller.HandleStateListener;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.databinding.DialogTempPickerBinding;
import com.weberbox.pifire.interfaces.OnScrollStopListener;
import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;
import com.weberbox.pifire.model.local.TempPickerModel;
import com.weberbox.pifire.model.remote.DashDataModel.NotifyData;
import com.weberbox.pifire.recycler.adapter.TempPickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogDashboardCallback;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.ui.views.ProbeTypeCard;
import com.weberbox.pifire.utils.TempUtils;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TempPickerDialog {

    private MaterialSwitch shutdownLlSwitch, shutdownTSwitch, shutdownHlSwitch;
    private MaterialSwitch keepWarmSwitch, reigniteSwitch;
    private MaterialSwitch targetReqSwitch, highLimitReqSwitch, lowLimitReqSwitch;
    private String lowLimitTemp, targetTemp, highLimitTemp;
    private ConstraintLayout targetOptionsContainer, lowLimitOptionsContainer, highLimitOptionsContainer;
    private TempPickerAdapter targetPickerAdapter, lowLimitPickerAdapter, highLimitPickerAdapter;
    private int defaultGrillTemp;
    private int defaultProbeTemp;
    private final BottomSheetDialog pickerBottomSheet;
    private final LayoutInflater inflater;
    private final DialogDashboardCallback callback;
    private final Context context;
    private final String tempUnit;
    private final DashProbe probe;
    private final ArrayList<NotifyData> notifyData;
    private final boolean holdMode;
    private final boolean saveOnly;

    public TempPickerDialog(Context context, final DashProbe probe,
                             ArrayList<NotifyData> notifyData, boolean hold, boolean saveOnly,
                             DialogDashboardCallback callback) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.probe = probe;
        this.notifyData = notifyData;
        this.holdMode = hold;
        this.saveOnly = saveOnly;
        this.callback = callback;
        this.tempUnit = TempUtils.getTempUnit(context);
    }

    public BottomSheetDialog showDialog() {
        DialogTempPickerBinding binding = DialogTempPickerBinding.inflate(inflater);

        ConstraintLayout highLimitContainer = binding.highLimitContainer;
        ConstraintLayout lowLimitContainer = binding.lowLimitContainer;
        RelativeLayout targetOptions = binding.targetSwitchContainer;
        RelativeLayout highLimitOptions = binding.highLimitSwitchContainer;
        RelativeLayout lowLimitOptions = binding.lowLimitSwitchContainer;
        MaterialButton confirmButton = binding.setTempConfirm;
        MaterialButton clearButton = binding.setTempClear;
        MaterialButton optionButton = binding.probeOptions;
        ProbeTypeCard targetCard = binding.probeTypeTarget;
        ProbeTypeCard highLimitCard = binding.probeTypeHighLimit;
        ProbeTypeCard lowLimitCard = binding.probeTypeLowLimit;
        shutdownLlSwitch = binding.probeShutdownLlSwitch;
        shutdownTSwitch = binding.probeShutdownTSwitch;
        shutdownHlSwitch = binding.probeShutdownHlSwitch;
        keepWarmSwitch = binding.probeKeepWarmSwitch;
        reigniteSwitch = binding.probeReigniteSwitch;
        targetReqSwitch = binding.probeTypeTarget.getReqSwitch();
        highLimitReqSwitch = binding.probeTypeHighLimit.getReqSwitch();
        lowLimitReqSwitch = binding.probeTypeLowLimit.getReqSwitch();
        targetOptionsContainer = binding.targetOptionsContainer;
        highLimitOptionsContainer = binding.highLimitOptionsContainer;
        lowLimitOptionsContainer = binding.lowLimitOptionsContainer;

        PickerLayoutManager targetLayoutManager = createPickerLayoutManager();
        PickerLayoutManager highLimitLayoutManager = createPickerLayoutManager();
        PickerLayoutManager lowLimitLayoutManager = createPickerLayoutManager();

        RecyclerView targetRecyclerView = binding.targetTempList;
        RecyclerView highLimitRecyclerView = binding.highLimitTempList;
        RecyclerView lowLimitRecyclerView = binding.lowLimitTempList;

        SnapHelper targetSnapHelper = new LinearSnapHelper();
        SnapHelper highLimitSnapHelper = new LinearSnapHelper();
        SnapHelper lowLimitSnapHelper = new LinearSnapHelper();
        targetSnapHelper.attachToRecyclerView(targetRecyclerView);
        highLimitSnapHelper.attachToRecyclerView(highLimitRecyclerView);
        lowLimitSnapHelper.attachToRecyclerView(lowLimitRecyclerView);

        TempUtils tempUtils = new TempUtils(context);
        int maxGrillTemp = tempUtils.getMaxGrillTemp();
        int maxProbeTemp = tempUtils.getMaxProbeTemp();
        int minGrillTemp = tempUtils.getMinGrillTemp();
        int minProbeTemp = tempUtils.getMinProbeTemp();
        defaultGrillTemp = tempUtils.getDefaultGrillTemp();
        defaultProbeTemp = tempUtils.getDefaultProbeTemp();

        if (isPrimaryProbe()) {
            targetTemp = String.valueOf(defaultGrillTemp);
            lowLimitTemp = String.valueOf(defaultGrillTemp);
            highLimitTemp = String.valueOf(defaultGrillTemp);
            targetPickerAdapter = createTempPickerAdapter(minGrillTemp, maxGrillTemp);
            highLimitPickerAdapter = createTempPickerAdapter(minGrillTemp, maxGrillTemp);
            lowLimitPickerAdapter = createTempPickerAdapter(minGrillTemp, maxGrillTemp);
        } else {
            targetTemp = String.valueOf(defaultProbeTemp);
            lowLimitTemp = String.valueOf(defaultProbeTemp);
            highLimitTemp = String.valueOf(defaultProbeTemp);
            targetPickerAdapter = createTempPickerAdapter(minProbeTemp, maxProbeTemp);
            highLimitPickerAdapter = createTempPickerAdapter(minProbeTemp, maxProbeTemp);
            lowLimitPickerAdapter = createTempPickerAdapter(minProbeTemp, maxProbeTemp);
        }

        setSwitchVisibility();
        setProbeOptions();

        targetRecyclerView.setLayoutManager(targetLayoutManager);
        targetRecyclerView.setAdapter(targetPickerAdapter);
        highLimitRecyclerView.setLayoutManager(highLimitLayoutManager);
        highLimitRecyclerView.setAdapter(highLimitPickerAdapter);
        lowLimitRecyclerView.setLayoutManager(lowLimitLayoutManager);
        lowLimitRecyclerView.setAdapter(lowLimitPickerAdapter);

        targetLayoutManager.setOnScrollStopListener(onScrollStopListener);
        highLimitLayoutManager.setOnScrollStopListener(onScrollStopListener);
        lowLimitLayoutManager.setOnScrollStopListener(onScrollStopListener);

        RecyclerViewFastScroller targetFastScroll = binding.targetTempFastScroll;
        RecyclerViewFastScroller highLimitFastScroll = binding.highLimitTempFastScroll;
        RecyclerViewFastScroller lowLimitFastScroll = binding.lowLimitTempFastScroll;

        targetFastScroll.setHandleStateListener(handleStateListener);
        highLimitFastScroll.setHandleStateListener(handleStateListener);
        lowLimitFastScroll.setHandleStateListener(handleStateListener);

        shutdownLlSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                reigniteSwitch.setEnabled(!isChecked));

        shutdownTSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                keepWarmSwitch.setEnabled(!isChecked));

        keepWarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                shutdownTSwitch.setEnabled(!isChecked));

        reigniteSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                shutdownLlSwitch.setEnabled(!isChecked));

        targetCard.setOnClickListener(v -> {
            if (targetOptionsContainer.getVisibility() == View.GONE) {
                AnimUtils.slideOpen(targetOptionsContainer);
                if (highLimitOptionsContainer.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(highLimitOptionsContainer);
                }
                if (lowLimitOptionsContainer.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(lowLimitOptionsContainer);
                }
            }
        });

        highLimitCard.setOnClickListener(v -> {
            if (highLimitOptionsContainer.getVisibility() == View.GONE) {
                AnimUtils.slideOpen(highLimitOptionsContainer);
                if (targetOptionsContainer.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(targetOptionsContainer);
                }
                if (lowLimitOptionsContainer.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(lowLimitOptionsContainer);
                }
            } else {
                AnimUtils.slideClosed(highLimitOptionsContainer);
                if (targetOptionsContainer.getVisibility() == View.GONE) {
                    AnimUtils.slideOpen(targetOptionsContainer);
                }
            }
        });

        lowLimitCard.setOnClickListener(v -> {
            if (lowLimitOptionsContainer.getVisibility() == View.GONE) {
                AnimUtils.slideOpen(lowLimitOptionsContainer);
                if (targetOptionsContainer.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(targetOptionsContainer);
                }
                if (highLimitOptionsContainer.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(highLimitOptionsContainer);
                }
            } else {
                AnimUtils.slideClosed(lowLimitOptionsContainer);
                if (targetOptionsContainer.getVisibility() == View.GONE) {
                    AnimUtils.slideOpen(targetOptionsContainer);
                }
            }
        });

        confirmButton.setOnClickListener(v -> {
            for (NotifyData notify : notifyData) {
                if (notify.getLabel().equals(probe.getLabel())) {
                    switch (notify.getType()) {
                        case Constants.TYPE_TARGET -> {
                            if (targetReqSwitch.isChecked()) {
                                notify.setTarget(Integer.valueOf(targetTemp));
                                notify.setShutdown(shutdownTSwitch.isChecked());
                                notify.setKeepWarm(keepWarmSwitch.isChecked());
                                notify.setReq(targetReqSwitch.isChecked());
                            } else {
                                notify.setTarget(0);
                                notify.setShutdown(false);
                                notify.setKeepWarm(false);
                                notify.setEta(null);
                                notify.setReq(false);
                            }
                        }
                        case Constants.TYPE_LIMIT_HIGH -> {
                            if (highLimitReqSwitch.isChecked()) {
                                notify.setTarget(Integer.valueOf(highLimitTemp));
                                notify.setShutdown(shutdownHlSwitch.isChecked());
                                notify.setReq(highLimitReqSwitch.isChecked());
                            } else {
                                notify.setTarget(0);
                                notify.setTriggered(false);
                                notify.setShutdown(false);
                                notify.setReq(false);
                            }
                        }
                        case Constants.TYPE_LIMIT_LOW -> {
                            if (lowLimitReqSwitch.isChecked()) {
                                notify.setTarget(Integer.valueOf(lowLimitTemp));
                                notify.setTriggered(probe.getProbeTemp() <
                                        Double.parseDouble(lowLimitTemp));
                                notify.setShutdown(shutdownLlSwitch.isChecked());
                                notify.setReignite(reigniteSwitch.isChecked());
                                notify.setReq(lowLimitReqSwitch.isChecked());
                            } else {
                                notify.setTarget(0);
                                notify.setTriggered(false);
                                notify.setShutdown(false);
                                notify.setReignite(false);
                                notify.setReq(false);
                            }
                        }
                    }
                }
            }
            callback.onTempConfirmClicked(notifyData, targetTemp, holdMode);
            pickerBottomSheet.dismiss();
        });

        clearButton.setOnClickListener(v -> {
            for (NotifyData notify : notifyData) {
                if (notify.getLabel().equals(probe.getLabel())) {
                    switch (notify.getType()) {
                        case Constants.TYPE_TARGET -> {
                            notify.setTarget(0);
                            notify.setShutdown(false);
                            notify.setKeepWarm(false);
                            notify.setEta(null);
                            notify.setReq(false);
                        }
                        case Constants.TYPE_LIMIT_HIGH -> {
                            notify.setTarget(0);
                            notify.setTriggered(false);
                            notify.setShutdown(false);
                            notify.setReq(false);
                        }
                        case Constants.TYPE_LIMIT_LOW -> {
                            notify.setTarget(0);
                            notify.setTriggered(false);
                            notify.setShutdown(false);
                            notify.setReignite(false);
                            notify.setReq(false);
                        }
                    }
                }
            }
            callback.onTempClearClicked(notifyData);
            pickerBottomSheet.dismiss();
        });

        optionButton.setOnClickListener(v -> {
            if (targetOptionsContainer.getVisibility() == View.VISIBLE) {
                if (targetOptions.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(targetOptions);
                } else {
                    AnimUtils.slideOpen(targetOptions);
                }
            }
            if (highLimitOptionsContainer.getVisibility() == View.VISIBLE) {
                if (highLimitOptions.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(highLimitOptions);
                } else {
                    AnimUtils.slideOpen(highLimitOptions);
                }
            }
            if (lowLimitOptionsContainer.getVisibility() == View.VISIBLE) {
                if (lowLimitOptions.getVisibility() == View.VISIBLE) {
                    AnimUtils.slideClosed(lowLimitOptions);
                } else {
                    AnimUtils.slideOpen(lowLimitOptions);
                }
            }
        });

        pickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        pickerBottomSheet.setContentView(binding.getRoot());

        if (isPrimaryProbe()) {
            scrollToTemp(targetRecyclerView, getScrollTemp() - minGrillTemp);
            scrollToTemp(highLimitRecyclerView, Integer.parseInt(highLimitTemp) - minGrillTemp);
            scrollToTemp(lowLimitRecyclerView, Integer.parseInt(lowLimitTemp) - minGrillTemp);
        } else {
            scrollToTemp(targetRecyclerView, Integer.parseInt(targetTemp) - minProbeTemp);
            scrollToTemp(highLimitRecyclerView, Integer.parseInt(highLimitTemp) - minProbeTemp);
            scrollToTemp(lowLimitRecyclerView, Integer.parseInt(lowLimitTemp) - minProbeTemp);
        }

        if (saveOnly) {
            clearButton.setVisibility(View.GONE);
            optionButton.setVisibility(View.GONE);
            targetCard.setVisibility(View.GONE);
            highLimitContainer.setVisibility(View.GONE);
            lowLimitContainer.setVisibility(View.GONE);
        } else {
            clearButton.setVisibility(View.VISIBLE);
            optionButton.setVisibility(View.VISIBLE);
        }

        if (!VersionUtils.isSupportedBuild(ServerVersions.V_190, "5")) {
            highLimitContainer.setVisibility(View.GONE);
            lowLimitContainer.setVisibility(View.GONE);
            targetReqSwitch.setChecked(true);
        }

        pickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        pickerBottomSheet.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (pickerBottomSheet.getWindow() != null) {
                pickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return pickerBottomSheet;
    }

    private final OnScrollStopListener onScrollStopListener = view -> {
        LinearLayout parent = view.findViewById(R.id.temp_item_container);
        RelativeLayout parent_two = parent.findViewById(R.id.temp_item_container_two);
        TextView text = parent_two.findViewById(R.id.temp_item_text_view);
        saveSelectedTemp(text.getText().toString(), null);
    };

    private final HandleStateListener handleStateListener = new HandleStateListener() {
        @Override
        public void onEngaged() {

        }

        @Override
        public void onDragged(float v, int position) {
            saveSelectedTemp(null, position);
        }

        @Override
        public void onReleased() {

        }
    };

    private PickerLayoutManager createPickerLayoutManager() {
        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, true);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(1.9f);
        return pickerLayoutManager;
    }

    private TempPickerAdapter createTempPickerAdapter(int minTemp, int maxTemp) {
        return new TempPickerAdapter(generateTemperatureList(context, tempUnit,
                minTemp, (maxTemp + 1)));
    }

    private int getScrollTemp() {
        int scrollTemp;
        if (isPrimaryProbe()) {
            scrollTemp = defaultGrillTemp;
            boolean hold = probe.getSetTemp() > 0.0;
            if (hold) {
                scrollTemp = probe.getSetTemp().intValue();
            } else if (probe.getTarget() > 0) {
                scrollTemp = probe.getTarget();
            }
        } else {
            scrollTemp = defaultProbeTemp;
            if (probe.getTarget() > 0) {
                scrollTemp = probe.getTarget();
            }
        }
        targetTemp = String.valueOf(scrollTemp);
        return scrollTemp;
    }

    private boolean isPrimaryProbe() {
        return probe.getProbeType().equals(Constants.DASH_PROBE_PRIMARY);
    }

    private void setSwitchVisibility() {
        if (isPrimaryProbe()) {
            keepWarmSwitch.setVisibility(View.GONE);
            shutdownTSwitch.setVisibility(View.GONE);
            shutdownHlSwitch.setVisibility(View.VISIBLE);
            shutdownLlSwitch.setVisibility(View.VISIBLE);
            reigniteSwitch.setVisibility(View.VISIBLE);
        } else {
            keepWarmSwitch.setVisibility(View.VISIBLE);
            shutdownTSwitch.setVisibility(View.VISIBLE);
            shutdownHlSwitch.setVisibility(View.GONE);
            shutdownLlSwitch.setVisibility(View.GONE);
            reigniteSwitch.setVisibility(View.GONE);
        }
    }

    private void setProbeOptions() {
        for (NotifyData notify : notifyData) {
            if (notify.getLabel().equals(probe.getLabel())) {
                switch (notify.getType()) {
                    case Constants.TYPE_TARGET -> {
                        targetReqSwitch.setChecked(notify.getReq());
                        shutdownTSwitch.setChecked(notify.getShutdown());
                        shutdownTSwitch.setEnabled(!notify.getKeepWarm());
                        keepWarmSwitch.setChecked(notify.getKeepWarm());
                        keepWarmSwitch.setEnabled(!notify.getShutdown());
                        if (notify.getTarget() > 0) {
                            targetTemp = notify.getTarget().toString();
                        }
                    }
                    case Constants.TYPE_LIMIT_HIGH -> {
                        highLimitReqSwitch.setChecked(notify.getReq());
                        shutdownHlSwitch.setChecked(notify.getShutdown());
                        if (notify.getTarget() > 0) {
                            highLimitTemp = notify.getTarget().toString();
                        }
                    }
                    case Constants.TYPE_LIMIT_LOW -> {
                        lowLimitReqSwitch.setChecked(notify.getReq());
                        shutdownLlSwitch.setChecked(notify.getShutdown());
                        shutdownLlSwitch.setEnabled(!notify.getReignite());
                        reigniteSwitch.setChecked(notify.getReignite());
                        reigniteSwitch.setEnabled(!notify.getShutdown());
                        if (notify.getTarget() > 0) {
                            lowLimitTemp = notify.getTarget().toString();
                        }
                    }
                }
            }
        }
    }

    public void saveSelectedTemp(String temp, Integer position) {
        if (targetOptionsContainer.getVisibility() == View.VISIBLE) {
            targetTemp = Objects.requireNonNullElseGet(temp, () ->
                    String.valueOf(targetPickerAdapter.onChange(position)));
        }
        if (lowLimitOptionsContainer.getVisibility() == View.VISIBLE) {
            lowLimitTemp = Objects.requireNonNullElseGet(temp, () ->
                    String.valueOf(lowLimitPickerAdapter.onChange(position)));
        }
        if (highLimitOptionsContainer.getVisibility() == View.VISIBLE) {
            highLimitTemp = Objects.requireNonNullElseGet(temp, () ->
                    String.valueOf(highLimitPickerAdapter.onChange(position)));
        }
    }

    private void scrollToTemp(RecyclerView recyclerView, int position) {
        boolean increment = Prefs.getBoolean(context.getString(R.string.prefs_increment_temps),
                context.getResources().getBoolean(R.bool.def_increment_temps));
        recyclerView.scrollToPosition(increment ? position / 5 : position);
    }

    private static List<TempPickerModel> generateTemperatureList(Context context, String tempUnit,
                                                                 int start, int end) {
        List<TempPickerModel> tempPickerViewModelList;

        NumberFormat formatter = new DecimalFormat("00");
        if (Prefs.getBoolean(context.getString(R.string.prefs_increment_temps),
                context.getResources().getBoolean(R.bool.def_increment_temps))) {
            tempPickerViewModelList = IntStream.iterate(start, i -> i + 5)
                    .limit((end - start) / 5 + 1).mapToObj(i ->
                            new TempPickerModel(formatter.format(i), tempUnit))
                    .collect(Collectors.toList());
        } else {
            tempPickerViewModelList = IntStream.range(start, end).mapToObj(i ->
                            new TempPickerModel(formatter.format(i), tempUnit))
                    .collect(Collectors.toList());
        }

        return tempPickerViewModelList;
    }
}

