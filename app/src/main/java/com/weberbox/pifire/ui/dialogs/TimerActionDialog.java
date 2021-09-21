package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
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
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;

public class TimerActionDialog {
    private static String TAG = TimerActionDialog.class.getSimpleName();

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

    public BottomSheetDialog showDialog(){
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_timer_action, null);

        LinearLayout timerLeftButton = sheetView.findViewById(R.id.timer_button_left);
        LinearLayout timerRightButton = sheetView.findViewById(R.id.timer_button_right);
        ImageView timerLeftButtonImage = sheetView.findViewById(R.id.timer_button_left_image);
        TextView timerLeftButtonText = sheetView.findViewById(R.id.timer_button_left_text);

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

        mTimerActionsBottomSheet.setContentView(sheetView);

        mTimerActionsBottomSheet.show();

        return mTimerActionsBottomSheet;
    }
}
