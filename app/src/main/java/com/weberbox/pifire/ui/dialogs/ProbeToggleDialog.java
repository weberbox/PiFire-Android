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
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;

public class ProbeToggleDialog {
    private static String TAG = ProbeToggleDialog.class.getSimpleName();

    private final BottomSheetDialog mProbeActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final int mType;
    private final boolean mEnabled;


    public ProbeToggleDialog(Context context, Fragment fragment, int type, boolean enabled) {
        mProbeActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
        mType = type;
        mEnabled = enabled;
    }

    public BottomSheetDialog showDialog(){
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_probe_action, null);

        LinearLayout actionLeftButton = sheetView.findViewById(R.id.probe_button_left);
        LinearLayout actionRightButton = sheetView.findViewById(R.id.probe_button_right);
        TextView actionRightText = sheetView.findViewById(R.id.probe_button_right_text);
        ImageView actionRightImage = sheetView.findViewById(R.id.probe_button_right_image);

        if(mEnabled) {
            actionRightImage.setImageResource(R.drawable.ic_probe_disable);
            actionRightText.setText(R.string.probe_disable);
        } else {
            actionRightImage.setImageResource(R.drawable.ic_grill_monitor);
            actionRightText.setText(R.string.probe_enable);
        }

        actionLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProbeActionsBottomSheet.dismiss();
            }
        });

        actionRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProbeActionsBottomSheet.dismiss();
                mCallBack.onModeActionClicked(mType);
            }
        });

        mProbeActionsBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });

        mProbeActionsBottomSheet.setContentView(sheetView);

        mProbeActionsBottomSheet.show();

        return mProbeActionsBottomSheet;
    }
}