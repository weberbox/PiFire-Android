package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.DialogBackupRestoreBinding;
import com.weberbox.pifire.interfaces.BackupRestoreCallbackInterface;
import com.weberbox.pifire.ui.utils.ViewUtils;

public class BackupRestoreDialog {

    private final BottomSheetDialog mBackupRestoreBottomSheet;
    private final LayoutInflater mInflater;
    private final BackupRestoreCallbackInterface mCallBack;
    private final Context mContext;
    private final int mType;
    private int mLeftIcon;
    private int mRightIcon;
    private String mDialogMessage;
    private String mLeftAction;
    private String mRightAction;

    public BackupRestoreDialog(Context context, Fragment fragment, int type) {
        mBackupRestoreBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCallBack = (BackupRestoreCallbackInterface) fragment;
        mType = type;
        mDialogMessage = null;
        mLeftAction = mContext.getString(R.string.cancel);
        mLeftIcon = R.drawable.ic_probe_cancel;
        mRightAction = mContext.getString(R.string.im_sure);
        mRightIcon = R.drawable.ic_setup_finish;
    }

    public BottomSheetDialog showDialog(){
        DialogBackupRestoreBinding binding = DialogBackupRestoreBinding.inflate(mInflater);

        LinearLayout actionLeftButton = binding.actionLeftButton;
        LinearLayout actionRightButton = binding.actionRightButton;
        LinearLayout actionMessageContainer = binding.backupRestoreContainer;
        TextView actionMessage = binding.backupRestoreMessage;
        ImageView actionRightIcon = binding.backupRestoreRightIcon;
        ImageView actionLeftIcon= binding.backupRestoreLeftIcon;
        TextView actionRightText = binding.backupRestoreRightText;
        TextView actionLeftText = binding.backupRestoreLeftText;

        if (mDialogMessage == null) {
            actionMessageContainer.setVisibility(View.GONE);
        } else {
            actionMessage.setText(mDialogMessage);
        }

        if (mLeftAction.equalsIgnoreCase(mContext.getString(R.string.cancel))) {
            actionLeftIcon.setImageDrawable(AppCompatResources.getDrawable(mContext,
                    R.drawable.ic_probe_cancel));
            actionLeftText.setText(R.string.cancel);
            actionLeftButton.setOnClickListener(v -> mBackupRestoreBottomSheet.dismiss());
        } else {
            actionLeftIcon.setImageDrawable(AppCompatResources.getDrawable(mContext, mLeftIcon));
            actionLeftText.setText(mLeftAction);
            actionLeftButton.setOnClickListener(v -> {
                mBackupRestoreBottomSheet.dismiss();
                mCallBack.onRestoreRemote(mType);
            });
        }

        actionRightIcon.setImageDrawable(AppCompatResources.getDrawable(mContext, mRightIcon));
        actionRightText.setText(mRightAction);
        actionRightButton.setOnClickListener(v -> {
            mBackupRestoreBottomSheet.dismiss();
            if (mType == Constants.ACTION_RESTORE_SETTINGS ||
                    mType == Constants.ACTION_RESTORE_PELLETDB) {
                mCallBack.onRestoreLocal(mType);
            } else {
                mCallBack.onBackupData(mType);
            }
        });

        mBackupRestoreBottomSheet.setContentView(binding.getRoot());

        mBackupRestoreBottomSheet.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        mBackupRestoreBottomSheet.show();

        Configuration configuration = mContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            mBackupRestoreBottomSheet.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return mBackupRestoreBottomSheet;
    }

    public void setMessage(String message) {
        mDialogMessage = message;
    }

    public void setLeftAction(String leftAction) {
        mLeftAction = leftAction;
    }

    public void setRightAction(String rightAction) {
        mRightAction = rightAction;
    }

    public void setLeftIcon(int leftIcon) {
        mLeftIcon = leftIcon;
    }

    public void setRightIcon(int rightIcon) {
        mRightIcon = rightIcon;
    }
}
