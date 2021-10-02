package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPelletsDeleteBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;


public class PelletsDeleteDialog {

    private DialogPelletsDeleteBinding mBinding;
    private final BottomSheetDialog mPelletsActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final PelletsCallbackInterface mCallBack;
    private final String mType;
    private final String mItem;
    private final int mPosition;


    public PelletsDeleteDialog(Context context, Fragment fragment, String type, String item, int position) {
        mPelletsActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (PelletsCallbackInterface) fragment;
        mType = type;
        mItem = item;
        mPosition = position;
    }

    public BottomSheetDialog showDialog() {
        mBinding = DialogPelletsDeleteBinding.inflate(mInflater);

        LinearLayout actionLeftButton = mBinding.pelletsCancelButton;
        LinearLayout actionRightButton = mBinding.pelletsPositiveButton;

        actionLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPelletsActionsBottomSheet.dismiss();
            }
        });

        actionRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPelletsActionsBottomSheet.dismiss();
                mCallBack.onDeleteConfirmed(mType, mItem, mPosition);
            }
        });

        mPelletsActionsBottomSheet.setContentView(mBinding.getRoot());

        mPelletsActionsBottomSheet.show();

        return mPelletsActionsBottomSheet;
    }
}
