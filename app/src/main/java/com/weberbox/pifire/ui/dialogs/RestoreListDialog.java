package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogListViewBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogRestoreCallback;

import java.util.List;

public class RestoreListDialog {

    private final DialogRestoreCallback callback;
    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final Context context;
    private final String title;
    private final String type;
    private AlertDialog alertDialog;
    private ProgressBar progressSpinner;
    private ArrayAdapter<String> adapter;
    private ListView restoreList;
    private TextView noBackups;

    public RestoreListDialog(Context context, String title, String type,
                             DialogRestoreCallback callback) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.type = type;
        this.callback = callback;
    }

    public AlertDialog showDialog() {
        DialogListViewBinding binding = DialogListViewBinding.inflate(inflater);

        dialog.setTitle(title);

        progressSpinner = binding.dialogListviewPb;
        noBackups = binding.dialogListviewTv;
        restoreList = binding.dialogListview;

        adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        restoreList.setAdapter(adapter);

        dialog.setView(binding.getRoot());

        dialog.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        alertDialog = dialog.create();

        restoreList.setOnItemClickListener((parent, view, position, id) -> {
            alertDialog.dismiss();
            callback.onFileRestoreRemote(adapter.getItem(position), type);
        });

        alertDialog.show();

        return alertDialog;
    }

    public void populateList(List<String> fileNames) {
        progressSpinner.setVisibility(View.INVISIBLE);
        if (fileNames.size() <= 0) {
            noBackups.setVisibility(View.VISIBLE);
            noBackups.setText(R.string.backups_not_found);
        } else {
            adapter.addAll(fileNames);
            restoreList.setVisibility(View.VISIBLE);
        }
    }

    public void dismiss() {
        alertDialog.dismiss();
    }
}
