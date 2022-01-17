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

    private final BottomSheetDialog mTimePickerBottomSheet;
    private final DashboardCallback mDashCallBack;
    private final RecipeEditCallback mRecipeCallback;
    private RecyclerView mHoursList;
    private RecyclerView mMinutesList;
    private String mHoursSelected = "00";
    private String mMinutesSelected = "00";
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final int mScrollHours;
    private final int mScrollMinutes;

    public TimerPickerDialog(Context context, DashboardCallback callback) {
        mTimePickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mDashCallBack = callback;
        mRecipeCallback = null;
        mContext = context;
        mScrollHours = 0;
        mScrollMinutes = 0;
    }

    public TimerPickerDialog(Context context, RecipeEditCallback callback) {
        mTimePickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mRecipeCallback = callback;
        mDashCallBack = null;
        mContext = context;
        mScrollHours = 0;
        mScrollMinutes = 0;
    }

    public BottomSheetDialog showDialog() {
        DialogTimerPickerBinding binding = DialogTimerPickerBinding.inflate(mInflater);

        RelativeLayout shutdownContainer = binding.timerShutdownContainer;
        SwitchCompat shutdownSwitch = binding.timerShutdownSwitch;
        Button confirmButton = binding.setTimerConfirm;

        PickerLayoutManager hoursPickerLayoutManager = new PickerLayoutManager(mContext,
                PickerLayoutManager.VERTICAL, false);
        hoursPickerLayoutManager.setChangeAlpha(true);
        hoursPickerLayoutManager.setScaleDownBy(0.99f);
        hoursPickerLayoutManager.setScaleDownDistance(1.9f);

        PickerLayoutManager minsPickerLayoutManager = new PickerLayoutManager(mContext,
                PickerLayoutManager.VERTICAL, false);
        minsPickerLayoutManager.setChangeAlpha(true);
        minsPickerLayoutManager.setScaleDownBy(0.96f);
        minsPickerLayoutManager.setScaleDownDistance(1.9f);

        mHoursList = binding.hoursList;
        mMinutesList = binding.minutesList;

        SnapHelper hoursSnapHelper = new LinearSnapHelper();
        SnapHelper minutesSnapHelper = new LinearSnapHelper();
        hoursSnapHelper.attachToRecyclerView(mHoursList);
        minutesSnapHelper.attachToRecyclerView(mMinutesList);

        TimePickerAdapter hoursAdapter = new TimePickerAdapter(generateTimeList(0, 24));
        TimePickerAdapter minsAdapter = new TimePickerAdapter(generateTimeList(0, 60));

        mHoursList.setLayoutManager(hoursPickerLayoutManager);
        mHoursList.setAdapter(hoursAdapter);

        mMinutesList.setLayoutManager(minsPickerLayoutManager);
        mMinutesList.setAdapter(minsAdapter);

        if (mDashCallBack != null) {
            if (Prefs.getBoolean(mContext.getString(R.string.prefs_timer_shutdown),
                    mContext.getResources().getBoolean(R.bool.def_timer_shutdown))) {
                shutdownContainer.setVisibility(View.VISIBLE);
            }
        }

        hoursPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.timer_item_container);
                    TextView text = parent.findViewById(R.id.timer_item_text_view);
                    mHoursSelected =  text.getText().toString();
                });

        minsPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.timer_item_container);
                    TextView text = parent.findViewById(R.id.timer_item_text_view);
                    mMinutesSelected =  text.getText().toString();
                });

        confirmButton.setOnClickListener(v -> {
            mTimePickerBottomSheet.dismiss();
            if (mDashCallBack != null) {
                mDashCallBack.onTimerConfirmClicked(mHoursSelected, mMinutesSelected,
                        shutdownSwitch.isChecked());
            } else {
                mRecipeCallback.onRecipeTime(mHoursSelected, mMinutesSelected);
            }
        });

        mTimePickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        mTimePickerBottomSheet.setContentView(binding.getRoot());

        if(mScrollHours != 0 && mScrollMinutes != 0) {
            setCurrentHours(mScrollHours, false);
            setCurrentMinutes(mScrollMinutes, false);
        }

        mTimePickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mTimePickerBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mTimePickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mTimePickerBottomSheet;
    }

    @SuppressWarnings("SameParameterValue")
    private void setCurrentHours(int position, boolean smooth){
        if(smooth) {
            mHoursList.smoothScrollToPosition(position);
        } else {
            mHoursList.scrollToPosition(position);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void setCurrentMinutes(int position, boolean smooth){
        if(smooth) {
            mMinutesList.smoothScrollToPosition(position);
        } else {
            mMinutesList.scrollToPosition(position);
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
