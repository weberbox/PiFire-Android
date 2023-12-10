package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogProbeEditBinding;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeInfo;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogProbeCallback;

import java.util.ArrayList;
import java.util.List;

public class ProbeEditDialog {

    private final DialogProbeCallback callback;
    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final ProbeInfo probeInfo;
    private final List<ProbeProfileModel> profiles;
    private final Context context;
    private final int position;
    private String name;
    private String profile;

    public ProbeEditDialog(Context context, int position, ProbeInfo probeInfo,
                           List<ProbeProfileModel> profiles, DialogProbeCallback callback) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.position = position;
        this.probeInfo = probeInfo;
        this.profiles = profiles;
        this.callback = callback;
    }

    public AlertDialog showDialog() {
        DialogProbeEditBinding binding = DialogProbeEditBinding.inflate(inflater);

        dialog.setTitle(R.string.dialog_probe_edit);

        final TextInputEditText probeNameTv = binding.probeEditNameTv;
        final AutoCompleteTextView probeProfileTv = binding.probeEditProfileTv;

        List<String> profileNames = new ArrayList<>();

        ProbeProfileModel probeProfile = probeInfo.getProbeProfile();

        if (probeProfile != null) {
            probeProfileTv.setText(probeProfile.getName());
        }

        for (ProbeProfileModel profile : profiles) {
            profileNames.add(profile.getName());
        }

        ArrayAdapter<String> profileAdapter = new ArrayAdapter<>(context,
                R.layout.item_menu_popup, profileNames);

        probeProfileTv.setAdapter(profileAdapter);

        String probeName = probeInfo.getName();

        if (probeName != null) {
            probeNameTv.setText(probeName);
        }

        dialog.setView(binding.getRoot());

        dialog.setPositiveButton(R.string.save, (dialog, which) -> {
            if (probeNameTv.getText() != null) {
                name = probeNameTv.getText().toString();
                profile = probeProfileTv.getText().toString();
                if (name.length() != 0) {
                    callback.onProbeUpdated(position, name, profile);
                    dialog.dismiss();
                }
            }
        });

        dialog.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = dialog.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(probeName != null);

        probeNameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        probeNameTv.requestFocus();

        return alertDialog;
    }
}


