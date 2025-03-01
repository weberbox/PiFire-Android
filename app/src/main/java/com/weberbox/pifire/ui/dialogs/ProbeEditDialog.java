package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogProbeEditBinding;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeInfo;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class ProbeEditDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogProbeCallback callback;
    private final ProbeInfo probeInfo;
    private final List<ProbeProfileModel> profiles;
    private final Context context;
    private final int position;

    public ProbeEditDialog(@NotNull Context context, int position, @NotNull ProbeInfo probeInfo,
                           @NotNull List<ProbeProfileModel> profiles,
                           @NotNull DialogProbeCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.position = position;
        this.probeInfo = probeInfo;
        this.profiles = profiles;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogProbeEditBinding binding = DialogProbeEditBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        TextInputEditText probeNameTv = binding.probeEditNameTv;
        TextInputLayout probeNameLayout = binding.probeEditName;
        AutoCompleteTextView probeProfileTv = getAutoCompleteTextView(binding);

        String probeName = probeInfo.getName();

        if (probeName != null) {
            probeNameTv.setText(probeName);
        }

        saveButton.setOnClickListener(v -> {
            Editable probeNameEdit = probeNameTv.getText();
            if (probeNameEdit != null) {
                String name = probeNameEdit.toString();
                String profile = probeProfileTv.getText().toString();
                if (name.isEmpty()) {
                    probeNameLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    callback.onProbeUpdated(position, name, profile);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        probeNameTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    probeNameLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    probeNameLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bottomSheetDialog.setContentView(binding.getRoot());

        bottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.getRoot().setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = context.getResources().getDimension(R.dimen.radiusTop);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() +
                        (int) radius, radius);
            }
        });
        binding.getRoot().setClipToOutline(true);

        binding.getRoot().setBackgroundColor(ContextCompat.getColor(context,
                R.color.material_dialog_background));

        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars() |
                        WindowInsetsCompat.Type.ime(), Side.BOTTOM)
                .applyToView(binding.dialogContainer);

        bottomSheetDialog.show();

        probeNameTv.requestFocus();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }

    @NonNull
    private AutoCompleteTextView getAutoCompleteTextView(DialogProbeEditBinding binding) {
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
        return probeProfileTv;
    }

    public interface DialogProbeCallback {
        void onProbeUpdated(int position, @NotNull String probeName, @NotNull String probeProfile);
    }
}
