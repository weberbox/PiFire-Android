package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogInputTextBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;

public class PelletsAddDialog {

    private final PelletsCallbackInterface mCallBack;
    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final Context mContext;
    private final String mTitle;
    private final String mType;
    private String mString;

    public PelletsAddDialog(Context context, Fragment fragment, String type, String title) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCallBack = (PelletsCallbackInterface) fragment;
        mType = type;
        mTitle = title;
    }

    public AlertDialog showDialog() {
        DialogInputTextBinding binding = DialogInputTextBinding.inflate(mInflater);

        mDialog.setTitle(mTitle);

        final EditText input = binding.dialogTextInput;

        mDialog.setView(binding.getRoot());

        mDialog.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            mString = input.getText().toString();
            if (mString.length() != 0) {
                mCallBack.onItemAdded(mType, mString);
                dialog.dismiss();
            }
        });

        mDialog.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = mDialog.create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    input.setError(mContext.getString(R.string.settings_blank_error));
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    input.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        input.requestFocus();

        return alertDialog;
    }
}
