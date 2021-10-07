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
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogTempPickerBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.interfaces.OnScrollStopListener;
import com.weberbox.pifire.recycler.adapter.TempPickerAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.recycler.viewmodel.TempPickerViewModel;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemperaturePickerDialog {

    private DialogTempPickerBinding mBinding;
    private RecyclerView mTempList;
    private String mSelectedTemp;
    private TempPickerAdapter mTempAdapter;
    private final BottomSheetDialog mTempPickerBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final Context mContext;
    private final int mTempType;
    private final int mScrollTemp;
    private final boolean mHoldMode;

    public TemperaturePickerDialog(Context context, Fragment fragment, int tempType, int defaultTemp,
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
        mBinding = DialogTempPickerBinding.inflate(mInflater);

        RelativeLayout shutdownContainer = mBinding.probeShutdownContainer;
        SwitchCompat shutdownSwitch = mBinding.probeShutdownSwitch;
        Button confirmButton = mBinding.setTempConfirm;
        Button clearButton = mBinding.setTempClear;

        PickerLayoutManager tempPickerLayoutManager = new PickerLayoutManager(mContext,
                PickerLayoutManager.VERTICAL, false);
        tempPickerLayoutManager.setChangeAlpha(true);
        tempPickerLayoutManager.setScaleDownBy(0.99f);
        tempPickerLayoutManager.setScaleDownDistance(1.9f);

        mTempList = mBinding.tempList;

        SnapHelper tempSnapHelper = new LinearSnapHelper();
        tempSnapHelper.attachToRecyclerView(mTempList);

        if(mTempType == Constants.PICKER_TYPE_GRILL) {
            mSelectedTemp = String.valueOf(Constants.DEFAULT_GRILL_TEMP_SET);
            mTempAdapter = new TempPickerAdapter(
                    generateTemperatureList(Constants.MIN_GRILL_TEMP_SET,
                            (Constants.MAX_GRILL_TEMP_SET + 1)));
        } else {
            if (Prefs.getBoolean(mContext.getString(R.string.prefs_probe_shutdown),
                    mContext.getResources().getBoolean(R.bool.def_probe_shutdown))) {
                shutdownContainer.setVisibility(View.VISIBLE);
            }
            mSelectedTemp = String.valueOf(Constants.DEFAULT_PROBE_TEMP_SET);
            mTempAdapter = new TempPickerAdapter(
                    generateTemperatureList(Constants.MIN_PROBE_TEMP_SET, (Constants.MAX_PROBE_TEMP_SET + 1)));
        }

        if(mScrollTemp > 0) {
            mSelectedTemp = String.valueOf(mScrollTemp);
        }

        mTempList.setLayoutManager(tempPickerLayoutManager);
        mTempList.setAdapter(mTempAdapter);
        

        tempPickerLayoutManager.setOnScrollStopListener(
                new OnScrollStopListener() {
                    @Override
                    public void selectedView(View view) {
                        LinearLayout parent = view.findViewById(R.id.temp_item_container);
                        RelativeLayout parent_two = parent.findViewById(R.id.temp_item_container_two);
                        TextView text = parent_two.findViewById(R.id.temp_item_text_view);
                        mSelectedTemp = text.getText().toString();
                    }
                });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTempPickerBottomSheet.dismiss();
                mCallBack.onTempConfirmClicked(mTempType, mSelectedTemp, mHoldMode,
                        shutdownSwitch.isChecked());
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTempPickerBottomSheet.dismiss();
                mCallBack.onTempClearClicked(mTempType);
            }
        });


        mTempPickerBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mTempPickerBottomSheet.setContentView(mBinding.getRoot());

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

        mTempPickerBottomSheet.show();

        return mTempPickerBottomSheet;
    }

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
