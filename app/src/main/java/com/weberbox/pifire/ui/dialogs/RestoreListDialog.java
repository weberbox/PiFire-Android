package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogListViewBinding;
import com.weberbox.pifire.interfaces.BackupRestoreCallback;

import java.util.List;

public class RestoreListDialog {

    private final BackupRestoreCallback mCallBack;
    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final Context mContext;
    private final String mTitle;
    private final String mType;
    private AlertDialog mAlertDialog;
    private ProgressBar mProgressSpinner;
    private ArrayAdapter<String> mFileAdapter;
    private ListView mRestoreList;
    private TextView mNoBackups;

    public RestoreListDialog(Context context, Fragment fragment, String title, String type) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCallBack = (BackupRestoreCallback) fragment;
        mTitle = title;
        mType = type;
    }

    public AlertDialog showDialog() {
        DialogListViewBinding binding = DialogListViewBinding.inflate(mInflater);

        mDialog.setTitle(mTitle);

        mProgressSpinner = binding.dialogListviewPb;
        mNoBackups = binding.dialogListviewTv;
        mRestoreList = binding.dialogListview;

        mFileAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1);
        mRestoreList.setAdapter(mFileAdapter);

        mDialog.setView(binding.getRoot());

        mDialog.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        mAlertDialog = mDialog.create();

        mRestoreList.setOnItemClickListener((parent, view, position, id) -> {
            mAlertDialog.dismiss();
            mCallBack.onFileRestoreRemote(mFileAdapter.getItem(position), mType);
        });

        mAlertDialog.show();

        return mAlertDialog;
    }

    public void populateList(List<String> fileNames) {
        mProgressSpinner.setVisibility(View.INVISIBLE);
        if (fileNames.size() <= 0) {
            mNoBackups.setVisibility(View.VISIBLE);
            mNoBackups.setText(R.string.backups_not_found);
        } else {
            mFileAdapter.addAll(fileNames);
            mRestoreList.setVisibility(View.VISIBLE);
        }
    }

    public void dismiss() {
        mAlertDialog.dismiss();
    }
}
