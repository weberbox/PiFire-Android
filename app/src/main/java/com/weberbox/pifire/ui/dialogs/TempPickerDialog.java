package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.databinding.DialogTempPickerBinding;
import com.weberbox.pifire.model.local.TempPickerModel;
import com.weberbox.pifire.recycler.adapter.TempPickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogDashboardCallback;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.TempUtils;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TempPickerDialog {

    private RecyclerView recyclerView;
    private String selectedTemp;
    private final BottomSheetDialog pickerBottomSheet;
    private final LayoutInflater inflater;
    private final DialogDashboardCallback callback;
    private final Context context;
    private final String tempUnit;
    private final int tempType;
    private final int scrollTemp;
    private final boolean holdMode;
    private final boolean beginHold;

    public TempPickerDialog(Context context, int tempType, int defaultTemp, boolean hold,
                            boolean beginHold, DialogDashboardCallback callback) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.tempType = tempType;
        this.scrollTemp = defaultTemp;
        this.holdMode = hold;
        this.beginHold = beginHold;
        this.callback = callback;
        this.tempUnit = TempUtils.getTempUnit(context);
    }

    public BottomSheetDialog showDialog() {
        DialogTempPickerBinding binding = DialogTempPickerBinding.inflate(inflater);

        RelativeLayout warmContainer = binding.probeWarmContainer;
        ConstraintLayout probeOptions = binding.probeOptionsContainer;
        SwitchCompat shutdownSwitch = binding.probeShutdownSwitch;
        SwitchCompat keepWarmSwitch = binding.probeKeepWarmSwitch;
        Button confirmButton = binding.setTempConfirm;
        Button clearButton = binding.setTempClear;
        Button optionButton = binding.probeOptions;

        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, true);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(1.9f);

        recyclerView = binding.tempList;

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        TempPickerAdapter tempPickerAdapter;

        TempUtils tempUtils = new TempUtils(context);

        if (tempType == Constants.PICKER_TYPE_GRILL ||
                tempType == Constants.PICKER_TYPE_GRILL_NOTIFY) {
            selectedTemp = String.valueOf(tempUtils.getDefaultGrillTemp());
            tempPickerAdapter = new TempPickerAdapter(generateTemperatureList(context, tempUnit,
                    tempUtils.getMinGrillTemp(), (tempUtils.getMaxGrillTemp() + 1)));
            optionButton.setVisibility(View.GONE);
        } else {
            optionButton.setVisibility(View.VISIBLE);
            selectedTemp = String.valueOf(tempUtils.getDefaultProbeTemp());
            tempPickerAdapter = new TempPickerAdapter(generateTemperatureList(context, tempUnit,
                    tempUtils.getMinProbeTemp(), (tempUtils.getMaxProbeTemp() + 1)));
        }

        if (scrollTemp > 0) {
            selectedTemp = String.valueOf(scrollTemp);
        }

        recyclerView.setLayoutManager(pickerLayoutManager);
        recyclerView.setAdapter(tempPickerAdapter);

        pickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.temp_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.temp_item_container_two);
                    TextView text = parent_two.findViewById(R.id.temp_item_text_view);
                    selectedTemp = text.getText().toString();
                });

        RecyclerViewFastScroller fastScroll = binding.tempFastScroll;
        fastScroll.setHandleStateListener(new RecyclerViewFastScroller.HandleStateListener() {
            @Override
            public void onEngaged() {

            }

            @Override
            public void onDragged(float v, int position) {
                selectedTemp = String.valueOf(tempPickerAdapter.onChange(position));
            }

            @Override
            public void onReleased() {

            }
        });

        if (VersionUtils.isSupported(ServerVersions.V_127)) {
            warmContainer.setVisibility(View.VISIBLE);
        }

        shutdownSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                keepWarmSwitch.setEnabled(!isChecked));

        keepWarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                shutdownSwitch.setEnabled(!isChecked));

        optionButton.setOnClickListener(v -> {
            if (probeOptions.getVisibility() == View.GONE) {
                AnimUtils.slideOpen(probeOptions);
            } else {
                AnimUtils.slideClosed(probeOptions);
            }
        });

        confirmButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            callback.onTempConfirmClicked(tempType, selectedTemp, holdMode,
                    shutdownSwitch.isChecked(), keepWarmSwitch.isChecked());
        });

        clearButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            callback.onTempClearClicked(tempType);
        });


        pickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        pickerBottomSheet.setContentView(binding.getRoot());

        if (scrollTemp != 0) {
            if (tempType == Constants.PICKER_TYPE_GRILL ||
                    tempType == Constants.PICKER_TYPE_GRILL_NOTIFY) {
                setDefaultTemp(scrollTemp - tempUtils.getMinGrillTemp(), false);
            } else {
                setDefaultTemp(scrollTemp - tempUtils.getMinProbeTemp(), false);
            }
        }

        if (beginHold) {
            clearButton.setVisibility(View.GONE);
        } else {
            clearButton.setVisibility(View.VISIBLE);
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
            pickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return pickerBottomSheet;
    }

    @SuppressWarnings("SameParameterValue")
    private void setDefaultTemp(int position, boolean smooth) {
        boolean increment = Prefs.getBoolean(context.getString(R.string.prefs_increment_temps),
                context.getResources().getBoolean(R.bool.def_increment_temps));
        if (smooth) {
            recyclerView.smoothScrollToPosition(increment ? position / 5 : position);
        } else {
            recyclerView.scrollToPosition(increment ? position / 5 : position);
        }
    }

    private static List<TempPickerModel> generateTemperatureList(Context context,
            String tempUnit, int start, int end) {
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
