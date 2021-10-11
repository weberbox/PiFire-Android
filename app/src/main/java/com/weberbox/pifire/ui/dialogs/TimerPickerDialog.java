package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogTimerPickerBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.interfaces.OnScrollStopListener;
import com.weberbox.pifire.recycler.adapter.TimePickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.recycler.viewmodel.TimePickerViewModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TimerPickerDialog {

    private DialogTimerPickerBinding mBinding;
    private final BottomSheetDialog mTimePickerBottomSheet;
    private final DashboardCallbackInterface mCallBack;
    private RecyclerView mHoursList;
    private RecyclerView mMinutesList;
    private String mHoursSelected = "00";
    private String mMinutesSelected = "00";
    private final LayoutInflater mInflater;
    private final Context mContext;
    private final int mScrollHours;
    private final int mScrollMinutes;

    public TimerPickerDialog(Context context, Fragment fragment) {
        mTimePickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
        mContext = context;
        mScrollHours = 0;
        mScrollMinutes = 0;
    }

    public TimerPickerDialog(Context context, Fragment frag, int hours, int minutes) {
        mTimePickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) frag;
        mContext = context;
        mScrollHours = hours;
        mScrollMinutes = minutes;
    }

    public BottomSheetDialog showDialog() {
        mBinding = DialogTimerPickerBinding.inflate(mInflater);

        RelativeLayout shutdownContainer = mBinding.timerShutdownContainer;
        SwitchCompat shutdownSwitch = mBinding.timerShutdownSwitch;
        Button confirmButton = mBinding.setTimerConfirm;

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

        mHoursList = mBinding.hoursList;
        mMinutesList = mBinding.minutesList;

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

        if (Prefs.getBoolean(mContext.getString(R.string.prefs_timer_shutdown),
                mContext.getResources().getBoolean(R.bool.def_timer_shutdown))) {
            shutdownContainer.setVisibility(View.VISIBLE);
        }

        hoursPickerLayoutManager.setOnScrollStopListener(
                new OnScrollStopListener() {
                    @Override
                    public void selectedView(View view) {
                        LinearLayout parent = view.findViewById(R.id.timer_item_container);
                        TextView text = parent.findViewById(R.id.timer_item_text_view);
                        mHoursSelected =  text.getText().toString();
                    }
                });

        minsPickerLayoutManager.setOnScrollStopListener(
                new OnScrollStopListener() {
                    @Override
                    public void selectedView(View view) {
                        LinearLayout parent = view.findViewById(R.id.timer_item_container);
                        TextView text = parent.findViewById(R.id.timer_item_text_view);
                        mMinutesSelected =  text.getText().toString();
                    }
                });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimePickerBottomSheet.dismiss();
                mCallBack.onTimerConfirmClicked(mHoursSelected, mMinutesSelected,
                        shutdownSwitch.isChecked());
            }
        });

        mTimePickerBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mTimePickerBottomSheet.setContentView(mBinding.getRoot());

        if(mScrollHours != 0 && mScrollMinutes != 0) {
            setCurrentHours(mScrollHours, false);
            setCurrentMinutes(mScrollMinutes, false);
        }

        mTimePickerBottomSheet.show();

        return mTimePickerBottomSheet;
    }

    private void setCurrentHours(int position, boolean smooth){
        if(smooth) {
            mHoursList.smoothScrollToPosition(position);
        } else {
            mHoursList.scrollToPosition(position);
        }
    }

    private void setCurrentMinutes(int position, boolean smooth){
        if(smooth) {
            mMinutesList.smoothScrollToPosition(position);
        } else {
            mMinutesList.scrollToPosition(position);
        }
    }

    private List<TimePickerViewModel> generateTimeList(int start, int end) {
        List<TimePickerViewModel> timePickerViewModelList;

        NumberFormat formatter = new DecimalFormat("00");
        timePickerViewModelList = IntStream.range(start, end).mapToObj(i ->
                new TimePickerViewModel(formatter.format(i))).collect(Collectors.toList());

        return timePickerViewModelList;
    }
}
