package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogTimerActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class TimerActionDialog {

    private final BottomSheetDialog mTimerActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallback mCallBack;
    private final Boolean mPaused;
    private final Context mContext;


    public TimerActionDialog(Context context, Fragment fragment, Boolean paused) {
        mTimerActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallback) fragment;
        mPaused = paused;
        mContext = context;
    }

    public BottomSheetDialog showDialog() {
        DialogTimerActionBinding binding = DialogTimerActionBinding.inflate(mInflater);

        LinearLayout timerLeftButton = binding.timerButtonLeft;
        LinearLayout timerRightButton = binding.timerButtonRight;
        ImageView timerLeftButtonImage = binding.timerButtonLeftImage;
        TextView timerLeftButtonText = binding.timerButtonLeftText;

        if(mPaused) {
            timerLeftButtonImage.setImageResource(R.drawable.ic_timer_start);
            timerLeftButtonText.setText(R.string.timer_start);
        } else {
            timerLeftButtonImage.setImageResource(R.drawable.ic_timer_pause);
            timerLeftButtonText.setText(R.string.timer_pause);
        }

        timerLeftButton.setOnClickListener(v -> {
            mTimerActionsBottomSheet.dismiss();
            if(mPaused) {
                mCallBack.onTimerActionClicked(Constants.ACTION_TIMER_RESTART);
            } else {
                mCallBack.onTimerActionClicked(Constants.ACTION_TIMER_PAUSE);
            }
        });

        timerRightButton.setOnClickListener(v -> {
            mTimerActionsBottomSheet.dismiss();
            mCallBack.onTimerActionClicked(Constants.ACTION_TIMER_STOP);
        });

        mTimerActionsBottomSheet.setContentView(binding.getRoot());

        mTimerActionsBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mTimerActionsBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mTimerActionsBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mTimerActionsBottomSheet;
    }
}
