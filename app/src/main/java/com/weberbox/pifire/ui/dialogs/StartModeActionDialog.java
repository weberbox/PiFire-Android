package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;

public class StartModeActionDialog {
    private static String TAG = StartModeActionDialog.class.getSimpleName();

    private final BottomSheetDialog mModeActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;


    public StartModeActionDialog(Context context, Fragment fragment) {
        mModeActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
    }

    public BottomSheetDialog showDialog(){
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_mode_start_action, null);

        LinearLayout startButton = sheetView.findViewById(R.id.mode_start_button);
        LinearLayout monitorButton = sheetView.findViewById(R.id.mode_monitor_button);
        LinearLayout stopButton = sheetView.findViewById(R.id.mode_stop_button);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_START);
            }
        });

        monitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_MONITOR);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(Constants.ACTION_MODE_STOP);
            }
        });

        mModeActionsBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mModeActionsBottomSheet.setContentView(sheetView);

        mModeActionsBottomSheet.show();

        return mModeActionsBottomSheet;
    }
}
