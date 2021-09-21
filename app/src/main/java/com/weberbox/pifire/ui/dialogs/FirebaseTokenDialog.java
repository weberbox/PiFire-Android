package com.weberbox.pifire.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.weberbox.pifire.R;

public class FirebaseTokenDialog {
    private static final String TAG = FirebaseTokenDialog.class.getSimpleName();

    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final String mToken;

    public FirebaseTokenDialog(Context context, String token) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mInflater = LayoutInflater.from(context);
        mToken = token;
    }

    public AlertDialog.Builder showDialog() {
        @SuppressLint("InflateParams")
        View dialogView = mInflater.inflate(R.layout.dialog_token_text, null);

        mDialog.setTitle(R.string.settings_firebase_token);

        final EditText input = (EditText) dialogView.findViewById(R.id.dialog_text_input);

        input.setText(mToken);

        mDialog.setView(dialogView);

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
