package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogHistoryDeleteBinding;
import com.weberbox.pifire.interfaces.HistoryCallbackInterface;


public class HistoryDeleteDialog {
    private static String TAG = HistoryDeleteDialog.class.getSimpleName();

    private DialogHistoryDeleteBinding mBinding;
    private final BottomSheetDialog mHistoryActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final HistoryCallbackInterface mCallBack;


    public HistoryDeleteDialog(Context context, Fragment fragment) {
        mHistoryActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (HistoryCallbackInterface) fragment;
    }

    public BottomSheetDialog showDialog(){
        mBinding = DialogHistoryDeleteBinding.inflate(mInflater);

        LinearLayout actionLeftButton = mBinding.historyCancelButton;
        LinearLayout actionRightButton = mBinding.historyDeleteButton;

        actionLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoryActionsBottomSheet.dismiss();
            }
        });

        actionRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHistoryActionsBottomSheet.dismiss();
                mCallBack.onHistoryDelete();
            }
        });

        mHistoryActionsBottomSheet.setContentView(mBinding.getRoot());

        mHistoryActionsBottomSheet.show();

        return mHistoryActionsBottomSheet;
    }
}
