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
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogTimerPickerBinding;
import com.weberbox.pifire.interfaces.DashboardCallback;
import com.weberbox.pifire.interfaces.RecipeEditCallback;
import com.weberbox.pifire.recycler.adapter.TimePickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.model.local.TimePickerModel;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TimerPickerDialog {

    private final BottomSheetDialog pickerBottomSheet;
    private final DashboardCallback dashCallBack;
    private final RecipeEditCallback recipeCallback;
    private RecyclerView hoursList;
    private RecyclerView minutesList;
    private String hoursSelected = "00";
    private String minutesSelected = "00";
    private final LayoutInflater inflater;
    private final Context context;
    private final int scrollHours;
    private final int scrollMinutes;

    public TimerPickerDialog(Context context, DashboardCallback callback) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        dashCallBack = callback;
        recipeCallback = null;
        this.context = context;
        scrollHours = 0;
        scrollMinutes = 0;
    }

    public TimerPickerDialog(Context context, RecipeEditCallback callback) {
        pickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        recipeCallback = callback;
        dashCallBack = null;
        this.context = context;
        scrollHours = 0;
        scrollMinutes = 0;
    }

    public BottomSheetDialog showDialog() {
        DialogTimerPickerBinding binding = DialogTimerPickerBinding.inflate(inflater);

        RelativeLayout shutdownContainer = binding.timerShutdownContainer;
        SwitchCompat shutdownSwitch = binding.timerShutdownSwitch;
        Button confirmButton = binding.setTimerConfirm;

        PickerLayoutManager hoursPickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        hoursPickerLayoutManager.setChangeAlpha(true);
        hoursPickerLayoutManager.setScaleDownBy(0.99f);
        hoursPickerLayoutManager.setScaleDownDistance(1.9f);

        PickerLayoutManager minsPickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        minsPickerLayoutManager.setChangeAlpha(true);
        minsPickerLayoutManager.setScaleDownBy(0.96f);
        minsPickerLayoutManager.setScaleDownDistance(1.9f);

        hoursList = binding.hoursList;
        minutesList = binding.minutesList;

        SnapHelper hoursSnapHelper = new LinearSnapHelper();
        SnapHelper minutesSnapHelper = new LinearSnapHelper();
        hoursSnapHelper.attachToRecyclerView(hoursList);
        minutesSnapHelper.attachToRecyclerView(minutesList);

        TimePickerAdapter hoursAdapter = new TimePickerAdapter(generateTimeList(0, 24));
        TimePickerAdapter minsAdapter = new TimePickerAdapter(generateTimeList(0, 60));

        hoursList.setLayoutManager(hoursPickerLayoutManager);
        hoursList.setAdapter(hoursAdapter);

        minutesList.setLayoutManager(minsPickerLayoutManager);
        minutesList.setAdapter(minsAdapter);

        if (dashCallBack != null) {
            if (Prefs.getBoolean(context.getString(R.string.prefs_timer_shutdown),
                    context.getResources().getBoolean(R.bool.def_timer_shutdown))) {
                shutdownContainer.setVisibility(View.VISIBLE);
            }
        }

        hoursPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.timer_item_container);
                    TextView text = parent.findViewById(R.id.timer_item_text_view);
                    hoursSelected =  text.getText().toString();
                });

        minsPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.timer_item_container);
                    TextView text = parent.findViewById(R.id.timer_item_text_view);
                    minutesSelected =  text.getText().toString();
                });

        confirmButton.setOnClickListener(v -> {
            pickerBottomSheet.dismiss();
            if (dashCallBack != null) {
                dashCallBack.onTimerConfirmClicked(hoursSelected, minutesSelected,
                        shutdownSwitch.isChecked());
            } else {
                recipeCallback.onRecipeTime(hoursSelected, minutesSelected);
            }
        });

        pickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        pickerBottomSheet.setContentView(binding.getRoot());

        if(scrollHours != 0 && scrollMinutes != 0) {
            setCurrentHours(scrollHours, false);
            setCurrentMinutes(scrollMinutes, false);
        }

        pickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
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
    private void setCurrentHours(int position, boolean smooth){
        if(smooth) {
            hoursList.smoothScrollToPosition(position);
        } else {
            hoursList.scrollToPosition(position);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setCurrentMinutes(int position, boolean smooth){
        if(smooth) {
            minutesList.smoothScrollToPosition(position);
        } else {
            minutesList.scrollToPosition(position);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private List<TimePickerModel> generateTimeList(int start, int end) {
        List<TimePickerModel> timePickerViewModelList;

        NumberFormat formatter = new DecimalFormat("00");
        timePickerViewModelList = IntStream.range(start, end).mapToObj(i ->
                new TimePickerModel(formatter.format(i))).collect(Collectors.toList());

        return timePickerViewModelList;
    }
}
