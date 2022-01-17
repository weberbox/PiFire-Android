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
import com.weberbox.pifire.databinding.DialogModeRunActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class RunModeActionDialog {

    private final BottomSheetDialog mModeActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallback mCallBack;
    private final Context mContext;
    private final boolean mShutdown;


    public RunModeActionDialog(Context context, Fragment fragment, boolean shutdown) {
        mModeActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallback) fragment;
        mShutdown = shutdown;
        mContext = context;
    }

    public BottomSheetDialog showDialog(){
        DialogModeRunActionBinding binding = DialogModeRunActionBinding.inflate(mInflater);

        LinearLayout smokeButton = binding.modeSmokeButton;
        LinearLayout holdButton = binding.modeHoldButton;
        LinearLayout rightButton = binding.modeShutdownButton;

        ImageView rightButtonImg = binding.modeShutdownButtonImg;
        TextView rightButtonText = binding.modeShutdownButtonText;

        if(mShutdown) {
            rightButtonImg.setImageResource(R.drawable.ic_timer_stop);
            rightButtonText.setText(R.string.timer_stop);
        }

        smokeButton.setOnClickListener(v -> {
            mModeActionsBottomSheet.dismiss();
            mCallBack.onModeActionClicked(Constants.ACTION_MODE_SMOKE);
        });

        holdButton.setOnClickListener(v -> {
            mModeActionsBottomSheet.dismiss();
            mCallBack.onModeActionClicked(Constants.ACTION_MODE_HOLD);
        });

        rightButton.setOnClickListener(v -> {
            mModeActionsBottomSheet.dismiss();
            if(mShutdown) {
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_STOP);
            } else {
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_SHUTDOWN);
            }
        });

        mModeActionsBottomSheet.setContentView(binding.getRoot());

        mModeActionsBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mModeActionsBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mModeActionsBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mModeActionsBottomSheet;
    }
}
