package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogActionDeleteBinding;
import com.weberbox.pifire.interfaces.DeleteDialogCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class DeleteActionDialog {

    private final BottomSheetDialog mHistoryActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final DeleteDialogCallback mCallBack;
    private final Context mContext;
    private final String mMessage;


    public DeleteActionDialog(Context context, String message, DeleteDialogCallback callback) {
        mHistoryActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = callback;
        mContext = context;
        mMessage = message;
    }

    public BottomSheetDialog showDialog(){
        DialogActionDeleteBinding binding = DialogActionDeleteBinding.inflate(mInflater);

        LinearLayout actionLeftButton = binding.dialogDeleteCancel;
        LinearLayout actionRightButton = binding.dialogDeleteConfirm;
        TextView message = binding.dialogDeleteText;

        message.setText(mMessage);

        actionLeftButton.setOnClickListener(v -> mHistoryActionsBottomSheet.dismiss());

        actionRightButton.setOnClickListener(v -> {
            mHistoryActionsBottomSheet.dismiss();
            mCallBack.onDialogDelete();
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
