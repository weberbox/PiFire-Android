package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogMessageTextBinding;

public class MessageTextDialog {

    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final String mMessage;
    private final String mTitle;

    public MessageTextDialog(Context context, String title, String message) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mInflater = LayoutInflater.from(context);
        mTitle = title;
        mMessage = message;
    }

    public AlertDialog.Builder showDialog() {
        DialogMessageTextBinding binding = DialogMessageTextBinding.inflate(mInflater);

        mDialog.setTitle(mTitle);

        final TextView message = binding.dialogMessageText;

        message.setText(mMessage);

        mDialog.setView(binding.getRoot());

        mDialog.setNegativeButton(android.R.string.ok, (dialog, which) -> dialog.cancel());

        mDialog.show();

        return mDialog;
    }
}
