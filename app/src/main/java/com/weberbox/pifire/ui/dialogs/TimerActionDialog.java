package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogTimerActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;

public class TimerActionDialog {
    private static String TAG = TimerActionDialog.class.getSimpleName();

    private DialogTimerActionBinding mBinding;
    private final BottomSheetDialog mTimerActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final Boolean mPaused;


    public TimerActionDialog(Context context, Fragment fragment, Boolean paused) {
        mTimerActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
        mPaused = paused;
    }

    public BottomSheetDialog showDialog() {
        mBinding = DialogTimerActionBinding.inflate(mInflater);

        LinearLayout timerLeftButton = mBinding.timerButtonLeft;
        LinearLayout timerRightButton = mBinding.timerButtonRight;
        ImageView timerLeftButtonImage = mBinding.timerButtonLeftImage;
        TextView timerLeftButtonText = mBinding.timerButtonLeftText;

        if(mPaused) {
            timerLeftButtonImage.setImageResource(R.drawable.ic_timer_start);
            timerLeftButtonText.setText(R.string.timer_start);
        } else {
            timerLeftButtonImage.setImageResource(R.drawable.ic_timer_pause);
            timerLeftButtonText.setText(R.string.timer_pause);
        }

        timerLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerActionsBottomSheet.dismiss();
                if(mPaused) {
                    mCallBack.onTimerActionClicked(Constants.ACTION_TIMER_RESTART);
                } else {
                    mCallBack.onTimerActionClicked(Constants.ACTION_TIMER_PAUSE);
                }
            }
        });

        timerRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerActionsBottomSheet.dismiss();
                mCallBack.onTimerActionClicked(Constants.ACTION_TIMER_STOP);
            }
        });

        mTimerActionsBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mTimerActionsBottomSheet.setContentView(mBinding.getRoot());

        mTimerActionsBottomSheet.show();

        return mTimerActionsBottomSheet;
    }
}
