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
import com.weberbox.pifire.databinding.DialogProbeActionBinding;
import com.weberbox.pifire.interfaces.DashboardCallbackInterface;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class ProbeToggleDialog {

    private final BottomSheetDialog mProbeActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DashboardCallbackInterface mCallBack;
    private final Context mContext;
    private final int mType;
    private final boolean mEnabled;


    public ProbeToggleDialog(Context context, Fragment fragment, int type, boolean enabled) {
        mProbeActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (DashboardCallbackInterface) fragment;
        mType = type;
        mEnabled = enabled;
        mContext = context;
    }

    public BottomSheetDialog showDialog(){
        DialogProbeActionBinding binding = DialogProbeActionBinding.inflate(mInflater);

        LinearLayout actionLeftButton = binding.probeButtonLeft;
        LinearLayout actionRightButton = binding.probeButtonRight;
        TextView actionRightText = binding.probeButtonRightText;
        ImageView actionRightImage = binding.probeButtonRightImage;

        if(mEnabled) {
            actionRightImage.setImageResource(R.drawable.ic_probe_disable);
            actionRightText.setText(R.string.probe_disable);
        } else {
            actionRightImage.setImageResource(R.drawable.ic_grill_monitor);
            actionRightText.setText(R.string.probe_enable);
        }

        actionLeftButton.setOnClickListener(v -> mProbeActionsBottomSheet.dismiss());

        actionRightButton.setOnClickListener(v -> {
            mProbeActionsBottomSheet.dismiss();
            mCallBack.onModeActionClicked(mType);
        });

        mProbeActionsBottomSheet.setContentView(binding.getRoot());

        mProbeActionsBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mProbeActionsBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mProbeActionsBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mProbeActionsBottomSheet;
    }
}