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
import com.weberbox.pifire.databinding.DialogModeRunActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;

public class RunModeActionDialog {

    private DialogModeRunActionBinding mBinding;
    private final BottomSheetDialog mModeActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final boolean mShutdown;


    public RunModeActionDialog(Context context, Fragment fragment, boolean shutdown) {
        mModeActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
        mShutdown = shutdown;
    }

    public BottomSheetDialog showDialog(){
        mBinding = DialogModeRunActionBinding.inflate(mInflater);

        LinearLayout smokeButton = mBinding.modeSmokeButton;
        LinearLayout holdButton = mBinding.modeHoldButton;
        LinearLayout rightButton = mBinding.modeShutdownButton;

        ImageView rightButtonImg = mBinding.modeShutdownButtonImg;
        TextView rightButtonText = mBinding.modeShutdownButtonText;

        if(mShutdown) {
            rightButtonImg.setImageResource(R.drawable.ic_timer_stop);
            rightButtonText.setText(R.string.timer_stop);
        }

        smokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_SMOKE);
            }
        });

        holdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_HOLD);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                if(mShutdown) {
                    mCallBack.onModeActionClicked(Constants.ACTION_MODE_STOP);
                } else {
                    mCallBack.onModeActionClicked(Constants.ACTION_MODE_SHUTDOWN);
                }
            }
        });

        mModeActionsBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mModeActionsBottomSheet.setContentView(mBinding.getRoot());

        mModeActionsBottomSheet.show();

        return mModeActionsBottomSheet;
    }
}
