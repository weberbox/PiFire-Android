package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPrefsListBinding;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class PrefsListDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final Context context;
    private final Preference preference;

    public PrefsListDialog(@NotNull Context context, @NotNull Preference preference) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.preference = preference;
    }

    public BottomSheetDialog showDialog() {
        DialogPrefsListBinding binding = DialogPrefsListBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        AutoCompleteTextView input = binding.dialogPrefsListText;

        String title = "";

        if (preference.getTitle() != null) {
            title = preference.getTitle().toString();
        }

        binding.dialogPrefsListHeaderTitle.setText(title);

        List<CharSequence> entries = Arrays.asList(((ListPreference) preference).getEntries());
        List<CharSequence> values = Arrays.asList(((ListPreference) preference).getEntryValues());

        ArrayAdapter<CharSequence> listAdapter = new ArrayAdapter<>(context,
                R.layout.item_menu_popup, entries);
        input.setAdapter(listAdapter);

        input.setText(((ListPreference) preference).getEntry(), false);

        saveButton.setOnClickListener(v -> {
            Editable edit = input.getText();
            if (edit != null) {
                int position = listAdapter.getPosition(edit.toString());
                ((ListPreference) preference).setValue(values.get(position).toString());
            }
            bottomSheetDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

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
                .margin(WindowInsetsCompat.Type.systemBars(), Side.BOTTOM)
                .applyToView(binding.dialogContainer);

        bottomSheetDialog.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }
}
