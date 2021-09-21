package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.HistoryCallbackInterface;


public class HistoryDeleteDialog {
    private static String TAG = HistoryDeleteDialog.class.getSimpleName();

    private final BottomSheetDialog mHistoryActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final HistoryCallbackInterface mCallBack;


    public HistoryDeleteDialog(Context context, Fragment fragment) {
        mHistoryActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (HistoryCallbackInterface) fragment;
    }

    public BottomSheetDialog showDialog(){
        @SuppressLint("InflateParams")
        View sheetView = mInflater.inflate(R.layout.dialog_history_delete, null);

        LinearLayout actionLeftButton = sheetView.findViewById(R.id.history_cancel_button);
        LinearLayout actionRightButton = sheetView.findViewById(R.id.history_delete_button);

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

        mHistoryActionsBottomSheet.setContentView(sheetView);

        mHistoryActionsBottomSheet.show();

        return mHistoryActionsBottomSheet;
    }
}
