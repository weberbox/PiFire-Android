package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogTokenTextBinding;

public class FirebaseTokenDialog {

    private DialogTokenTextBinding mBinding;
    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final String mToken;

    public FirebaseTokenDialog(Context context, String token) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mInflater = LayoutInflater.from(context);
        mToken = token;
    }

    public AlertDialog.Builder showDialog() {
        mBinding = DialogTokenTextBinding.inflate(mInflater);

        mDialog.setTitle(R.string.settings_firebase_token);

        final EditText input = mBinding.dialogTextInput;

        input.setText(mToken);

        mDialog.setView(mBinding.getRoot());

        mDialog.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mDialog.show();

        return mDialog;
    }
}
