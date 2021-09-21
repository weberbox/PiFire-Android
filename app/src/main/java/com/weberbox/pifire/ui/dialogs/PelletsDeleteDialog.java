package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;


public class PelletsDeleteDialog {
    private static final String TAG = PelletsDeleteDialog.class.getSimpleName();

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
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_pellets_delete, null);

        LinearLayout actionLeftButton = sheetView.findViewById(R.id.pellets_cancel_button);
        LinearLayout actionRightButton = sheetView.findViewById(R.id.pellets_positive_button);

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

        mPelletsActionsBottomSheet.setContentView(sheetView);

        mPelletsActionsBottomSheet.show();

        return mPelletsActionsBottomSheet;
    }
}
