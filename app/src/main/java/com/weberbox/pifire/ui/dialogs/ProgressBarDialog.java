package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogProgressBarBinding;

public class ProgressBarDialog {

    private DialogProgressBarBinding mBinding;
    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private AlertDialog mAlertDialog;
    private LinearProgressIndicator mProgress;
    private TextView mProgressText;

    public ProgressBarDialog(Context context) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mInflater = LayoutInflater.from(context);
    }

    public AlertDialog.Builder showDialog() {
        mBinding = DialogProgressBarBinding.inflate(mInflater);

        mDialog.setTitle(R.string.downloading);

        mProgress = mBinding.progressBar;
        mProgressText = mBinding.progressBarText;

        mDialog.setView(mBinding.getRoot());

        mAlertDialog = mDialog.create();

        mAlertDialog.show();

        return mDialog;
    }

    public void setProgress(int progress) {
        mProgress.setProgress(progress);
    }

    public void setCancelable(boolean cancelable) {
        mAlertDialog.setCancelable(cancelable);
    }

    public void setMessage(String message) {
        mProgressText.setText(message);
    }

    public void setIndeterminate(boolean indeterminate) {
        mProgress.setIndeterminate(indeterminate);
    }

    public void setMax(Integer max) {
        mProgress.setMax(max);
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }

    public boolean isShowing() {
        return mAlertDialog.isShowing();
    }
}
