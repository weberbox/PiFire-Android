package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogScrollPickerBinding;
import com.weberbox.pifire.interfaces.RecipeEditCallback;
import com.weberbox.pifire.recycler.adapter.RecipeDiffAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.Arrays;
import java.util.List;

public class RecipeDiffDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final RecipeEditCallback callBack;
    private final Context context;
    private String difficulty;

    public RecipeDiffDialog(Context context, RecipeEditCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        callBack = callback;
        this.context = context;
    }

    public BottomSheetDialog showDialog() {
        DialogScrollPickerBinding binding = DialogScrollPickerBinding.inflate(inflater);

        Button confirmButton = binding.setProfileLoad;

        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(1.2f);

        RecyclerView recyclerView = binding.profileList;

        SnapHelper profileSnapHelper = new LinearSnapHelper();
        profileSnapHelper.attachToRecyclerView(recyclerView);

        List<String> difficulties = Arrays.asList(
                context.getResources().getStringArray(R.array.recipe_difficulties));

        RecipeDiffAdapter adapter = new RecipeDiffAdapter(difficulties);

        recyclerView.setLayoutManager(pickerLayoutManager);
        recyclerView.setAdapter(adapter);

        difficulty = difficulties.get(0);

        pickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.profile_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.profile_item_container_two);
                    TextView text = parent_two.findViewById(R.id.profile_item_text_view);
                    difficulty = text.getText().toString();
                });

        confirmButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            callBack.onRecipeDifficulty(difficulty);
        });

        bottomSheetDialog.setContentView(binding.getRoot());

        bottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog)dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        bottomSheetDialog.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
        }

        return bottomSheetDialog;
    }
}
