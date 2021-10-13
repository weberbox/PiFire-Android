package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogHistoryDeleteBinding;
import com.weberbox.pifire.interfaces.HistoryCallbackInterface;
import com.weberbox.pifire.ui.utils.ViewUtils;


public class HistoryDeleteDialog {

    private final BottomSheetDialog mHistoryActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final HistoryCallbackInterface mCallBack;
    private final Context mContext;


    public HistoryDeleteDialog(Context context, Fragment fragment) {
        mHistoryActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (HistoryCallbackInterface) fragment;
        mContext = context;
    }

    public BottomSheetDialog showDialog(){
        DialogHistoryDeleteBinding binding = DialogHistoryDeleteBinding.inflate(mInflater);

        LinearLayout actionLeftButton = binding.historyCancelButton;
        LinearLayout actionRightButton = binding.historyDeleteButton;

        actionLeftButton.setOnClickListener(v -> mHistoryActionsBottomSheet.dismiss());

        actionRightButton.setOnClickListener(v -> {
            mHistoryActionsBottomSheet.dismiss();
            mCallBack.onHistoryDelete();
        });

        mHistoryActionsBottomSheet.setContentView(binding.getRoot());

        mHistoryActionsBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mHistoryActionsBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mHistoryActionsBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mHistoryActionsBottomSheet;
    }
}
