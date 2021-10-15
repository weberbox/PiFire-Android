package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPelletsDeleteBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.ui.utils.ViewUtils;


public class PelletsDeleteDialog {

    private final BottomSheetDialog mPelletsActionsBottomSheet;
    private final LayoutInflater mInflater;
    private final PelletsCallbackInterface mCallBack;
    private final String mType;
    private final String mItem;
    private final Context mContext;
    private final int mPosition;


    public PelletsDeleteDialog(Context context, Fragment fragment, String type, String item, int position) {
        mPelletsActionsBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (PelletsCallbackInterface) fragment;
        mType = type;
        mItem = item;
        mPosition = position;
        mContext = context;
    }

    public BottomSheetDialog showDialog() {
        DialogPelletsDeleteBinding binding = DialogPelletsDeleteBinding.inflate(mInflater);

        LinearLayout actionLeftButton = binding.pelletsCancelButton;
        LinearLayout actionRightButton = binding.pelletsPositiveButton;

        actionLeftButton.setOnClickListener(v -> mPelletsActionsBottomSheet.dismiss());

        actionRightButton.setOnClickListener(v -> {
            mPelletsActionsBottomSheet.dismiss();
            mCallBack.onDeleteConfirmed(mType, mItem, mPosition);
        });

        mPelletsActionsBottomSheet.setContentView(binding.getRoot());

        mPelletsActionsBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mPelletsActionsBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mPelletsActionsBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mPelletsActionsBottomSheet;
    }
}
