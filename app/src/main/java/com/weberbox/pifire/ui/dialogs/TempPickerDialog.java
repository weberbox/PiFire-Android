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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogTempPickerBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.recycler.adapter.TempPickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.recycler.viewmodel.TempPickerViewModel;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TempPickerDialog {

    private RecyclerViewFastScroller mFastScroll;
    private RecyclerView mTempList;
    private String mSelectedTemp;
    private final BottomSheetDialog mTempPickerBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final Context mContext;
    private final int mTempType;
    private final int mScrollTemp;
    private final boolean mHoldMode;

    public TempPickerDialog(Context context, Fragment fragment, int tempType, int defaultTemp,
                            boolean hold) {
        mTempPickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
        mContext = context;
        mTempType = tempType;
        mScrollTemp = defaultTemp;
        mHoldMode = hold;
    }

    public BottomSheetDialog showDialog() {
        DialogTempPickerBinding binding = DialogTempPickerBinding.inflate(mInflater);

        RelativeLayout shutdownContainer = binding.probeShutdownContainer;
        SwitchCompat shutdownSwitch = binding.probeShutdownSwitch;
        Button confirmButton = binding.setTempConfirm;
        Button clearButton = binding.setTempClear;

        PickerLayoutManager tempPickerLayoutManager = new PickerLayoutManager(mContext,
                PickerLayoutManager.VERTICAL, false);
        tempPickerLayoutManager.setChangeAlpha(true);
        tempPickerLayoutManager.setScaleDownBy(0.99f);
        tempPickerLayoutManager.setScaleDownDistance(1.9f);

        mTempList = binding.tempList;

        SnapHelper tempSnapHelper = new LinearSnapHelper();
        tempSnapHelper.attachToRecyclerView(mTempList);

        TempPickerAdapter tempPickerAdapter;

        if(mTempType == Constants.PICKER_TYPE_GRILL) {
            mSelectedTemp = String.valueOf(Constants.DEFAULT_GRILL_TEMP_SET);
            tempPickerAdapter = new TempPickerAdapter(
                    generateTemperatureList(Constants.MIN_GRILL_TEMP_SET,
                            (Constants.MAX_GRILL_TEMP_SET + 1)));
        } else {
            if (Prefs.getBoolean(mContext.getString(R.string.prefs_probe_shutdown),
                    mContext.getResources().getBoolean(R.bool.def_probe_shutdown))) {
                shutdownContainer.setVisibility(View.VISIBLE);
            }
            mSelectedTemp = String.valueOf(Constants.DEFAULT_PROBE_TEMP_SET);
            tempPickerAdapter = new TempPickerAdapter(
                    generateTemperatureList(Constants.MIN_PROBE_TEMP_SET,
                            (Constants.MAX_PROBE_TEMP_SET + 1)));
        }

        if(mScrollTemp > 0) {
            mSelectedTemp = String.valueOf(mScrollTemp);
        }

        mTempList.setLayoutManager(tempPickerLayoutManager);
        mTempList.setAdapter(tempPickerAdapter);
        

        tempPickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.temp_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.temp_item_container_two);
                    TextView text = parent_two.findViewById(R.id.temp_item_text_view);
                    mSelectedTemp = text.getText().toString();
                });

        mFastScroll = binding.tempFastScroll;
        mFastScroll.setHandleStateListener(new RecyclerViewFastScroller.HandleStateListener() {
            @Override
            public void onEngaged() {

            }

            @Override
            public void onDragged(float v, int position) {
                mSelectedTemp = String.valueOf(tempPickerAdapter.onChange(position));
            }

            @Override
            public void onReleased() {

            }
        });

        confirmButton.setOnClickListener(v -> {
            mTempPickerBottomSheet.dismiss();
            mCallBack.onTempConfirmClicked(mTempType, mSelectedTemp, mHoldMode,
                    shutdownSwitch.isChecked());
        });

        clearButton.setOnClickListener(v -> {
            mTempPickerBottomSheet.dismiss();
            mCallBack.onTempClearClicked(mTempType);
        });


        mTempPickerBottomSheet.setOnDismissListener(dialogInterface -> {

        });

        mTempPickerBottomSheet.setContentView(binding.getRoot());

        if(mScrollTemp != 0) {
            if(mTempType == Constants.PICKER_TYPE_GRILL) {
                setDefaultTemp(mScrollTemp - Constants.MIN_GRILL_TEMP_SET, false);
            } else {
                setDefaultTemp(mScrollTemp - Constants.MIN_PROBE_TEMP_SET, false);
            }
        }

        if(mHoldMode) {
            clearButton.setVisibility(View.GONE);
        } else {
            clearButton.setVisibility(View.VISIBLE);
        }

        mTempPickerBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mTempPickerBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mTempPickerBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mTempPickerBottomSheet;
    }

    @SuppressWarnings("SameParameterValue")
    private void setDefaultTemp(int position, boolean smooth){
        if (smooth) {
            mTempList.smoothScrollToPosition(position);
        } else {
            mTempList.scrollToPosition(position);
        }
    }

    private static List<TempPickerViewModel> generateTemperatureList(int start, int end) {
        List<TempPickerViewModel> tempPickerViewModelList;

        NumberFormat formatter = new DecimalFormat("00");
        tempPickerViewModelList = IntStream.range(start, end).mapToObj(i ->
                new TempPickerViewModel(formatter.format(i), "f")).collect(Collectors.toList());

        return tempPickerViewModelList;
    }
}
