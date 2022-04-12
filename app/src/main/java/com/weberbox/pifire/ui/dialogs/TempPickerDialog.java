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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.Versions;
import com.weberbox.pifire.databinding.DialogTempPickerBinding;
import com.weberbox.pifire.interfaces.DashboardCallback;
import com.weberbox.pifire.model.local.TempPickerModel;
import com.weberbox.pifire.recycler.adapter.TempPickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.TempUtils;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TempPickerDialog {

    private RecyclerView tempListRecycler;
    private String selectedTemp;
    private final BottomSheetDialog pickerBottomSheet;
    private final LayoutInflater inflater;
    private final DashboardCallback callBack;
    private final Context context;
    private final String tempUnit;
    private final int tempType;
    private final int scrollTemp;
    private final boolean holdMode;

    public TempPickerDialog(Context context, Fragment fragment, int tempType, int defaultTemp,
                            boolean hold) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        callBack = (DashboardCallback) fragment;
        this.context = context;
        this.tempType = tempType;
        scrollTemp = defaultTemp;
        holdMode = hold;
        tempUnit = TempUtils.getTempUnit(context);
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

        PickerLayoutManager tempPickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, true);
        tempPickerLayoutManager.setChangeAlpha(true);
        tempPickerLayoutManager.setScaleDownBy(0.99f);
        tempPickerLayoutManager.setScaleDownDistance(1.9f);

        tempListRecycler = binding.tempList;

        SnapHelper tempSnapHelper = new LinearSnapHelper();
        tempSnapHelper.attachToRecyclerView(tempListRecycler);

        TempPickerAdapter tempPickerAdapter;

        TempUtils tempUtils = new TempUtils(context);

        if (tempType == Constants.PICKER_TYPE_GRILL) {
            selectedTemp = String.valueOf(tempUtils.getDefaultGrillTemp());
            tempPickerAdapter = new TempPickerAdapter(generateTemperatureList(tempUnit,
                    tempUtils.getMinGrillTemp(), (tempUtils.getMaxGrillTemp() + 1)));
            optionButton.setVisibility(View.GONE);
        } else {
            optionButton.setVisibility(View.VISIBLE);
            selectedTemp = String.valueOf(tempUtils.getDefaultProbeTemp());
            tempPickerAdapter = new TempPickerAdapter(generateTemperatureList(tempUnit,
                    tempUtils.getMinProbeTemp(), (tempUtils.getMaxProbeTemp() + 1)));
        }

        if (scrollTemp > 0) {
            selectedTemp = String.valueOf(scrollTemp);
        }

        tempListRecycler.setLayoutManager(tempPickerLayoutManager);
        tempListRecycler.setAdapter(tempPickerAdapter);

        tempPickerLayoutManager.setOnScrollStopListener(
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

        if (VersionUtils.isSupported(Versions.V_127)) {
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
            callBack.onTempConfirmClicked(tempType, selectedTemp, holdMode,
                    shutdownSwitch.isChecked(), keepWarmSwitch.isChecked());
        });

        clearButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            callBack.onTempClearClicked(tempType);
        });


        pickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        pickerBottomSheet.setContentView(binding.getRoot());

        if (scrollTemp != 0) {
            if (tempType == Constants.PICKER_TYPE_GRILL) {
                setDefaultTemp(scrollTemp - tempUtils.getMinGrillTemp(), false);
            } else {
                setDefaultTemp(scrollTemp - tempUtils.getMinProbeTemp(), false);
            }
        }

        if (holdMode) {
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
        if (smooth) {
            tempListRecycler.smoothScrollToPosition(position);
        } else {
            tempListRecycler.scrollToPosition(position);
        }
    }

    private static List<TempPickerModel> generateTemperatureList(
            String tempUnit, int start, int end) {
        List<TempPickerModel> tempPickerViewModelList;

        NumberFormat formatter = new DecimalFormat("00");
        tempPickerViewModelList = IntStream.range(start, end).mapToObj(i ->
                new TempPickerModel(formatter.format(i), tempUnit)).collect(Collectors.toList());

        return tempPickerViewModelList;
    }
}
