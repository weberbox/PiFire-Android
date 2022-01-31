package com.weberbox.pifire.ui.dialogs;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogProgressLinearBinding;

public class UpdaterProgressDialog {

    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final Activity mActivity;
    private final int mTitle;
    private AlertDialog mAlertDialog;
    private TextView mUpdaterLogOutput;

    public UpdaterProgressDialog(Activity activity, @StringRes int title) {
        mDialog = new AlertDialog.Builder(activity, R.style.AlertDialogThemeMaterial);
        mInflater = LayoutInflater.from(activity);
        mActivity = activity;
        mTitle = title;
    }

    public AlertDialog createDialog() {
        DialogProgressLinearBinding binding = DialogProgressLinearBinding.inflate(mInflater);

        final Handler handler  = new Handler();

        mDialog.setTitle(mTitle);

        mUpdaterLogOutput = binding.dialogProgressMessage;

        mDialog.setView(binding.getRoot());

        mAlertDialog = mDialog.create();

        mAlertDialog.setOnDismissListener(dialog -> {
            handler.removeCallbacks(runnable);
            mActivity.finish();
        });

        mAlertDialog.show();

        mAlertDialog.setCancelable(false);

        handler.postDelayed(runnable, 30000);

        return mAlertDialog;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mAlertDialog.isShowing()) {
                mAlertDialog.dismiss();
            }
        }
    };

    public void setCancelable(boolean cancelable) {
        mAlertDialog.setCancelable(cancelable);
    }

    public void setOutputMessage(String message) {
        mUpdaterLogOutput.setText(message);
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }

    public boolean isShowing() {
        return mAlertDialog.isShowing();
    }
}
