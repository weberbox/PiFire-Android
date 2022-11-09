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
import com.weberbox.pifire.constants.Constants;
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
    private final RecipeEditCallback callback;
    private final Context context;
    private RecyclerView recyclerView;
    private Integer difficulty;

    public RecipeDiffDialog(Context context, Integer difficulty, RecipeEditCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.difficulty = difficulty;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogScrollPickerBinding binding = DialogScrollPickerBinding.inflate(inflater);

        Button confirmButton = binding.btnItemConfirm;

        PickerLayoutManager pickerLayoutManager = new PickerLayoutManager(context,
                PickerLayoutManager.VERTICAL, false);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(1.2f);

        recyclerView = binding.scrollList;

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        List<Integer> difficulties = Arrays.asList(
                Constants.RECIPE_DIF_BEGIN,
                Constants.RECIPE_DIF_EASY,
                Constants.RECIPE_DIF_MOD,
                Constants.RECIPE_DIF_HARD,
                Constants.RECIPE_DIF_V_HARD);

        RecipeDiffAdapter adapter = new RecipeDiffAdapter(difficulties);

        recyclerView.setLayoutManager(pickerLayoutManager);
        recyclerView.setAdapter(adapter);

        pickerLayoutManager.setOnScrollStopListener(
                view -> {
                    LinearLayout parent = view.findViewById(R.id.picker_item_container);
                    RelativeLayout parent_two = parent.findViewById(R.id.picker_item_container_two);
                    TextView text = parent_two.findViewById(R.id.picker_item_text_view);
                    difficulty = (Integer) text.getTag();
                });

        confirmButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            callback.onRecipeDifficulty(difficulty);
        });

        bottomSheetDialog.setContentView(binding.getRoot());

        setDefault(difficulty, false);

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

    @SuppressWarnings("SameParameterValue")
    private void setDefault(int position, boolean smooth){
        if (smooth) {
            recyclerView.smoothScrollToPosition(position);
        } else {
            recyclerView.scrollToPosition(position);
        }
    }
}
