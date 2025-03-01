package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemRecipeInstructionsBinding;
import com.weberbox.pifire.model.remote.RecipesModel.Instruction;
import com.weberbox.pifire.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeInstructionsAdapter extends RecyclerView.Adapter<RecipeInstructionsAdapter.ViewHolder> {

    private final List<Instruction> list;
    private boolean limited;

    public RecipeInstructionsAdapter(boolean limited) {
        this.list = new ArrayList<>();
        this.limited = limited;
    }

    @NonNull
    @Override
    public RecipeInstructionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  final int viewType) {
        return new ViewHolder(ItemRecipeInstructionsBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(list.get(position), holder);
        holder.root.setOnClickListener(view ->
                holder.checkBox.setChecked(!holder.checkBox.isChecked()));
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setRecipeInstructions(@NotNull List<Instruction> instructions) {
        list.clear();
        list.addAll(instructions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (limited) {
            return Math.min(list.size(), AppConfig.RECYCLER_LIMIT);
        } else {
            return list.size();
        }
    }

    public void setLimitEnabled(boolean enabled) {
        this.limited = enabled;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final MaterialCheckBox checkBox;
        private final TextView direction;
        private final TextView ingredients;
        private final TextView step;

        public ViewHolder(ItemRecipeInstructionsBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            checkBox = binding.instructionsCheckbox;
            direction = binding.instructionsDirection;
            ingredients = binding.instructionsIngredient;
            step = binding.instructionsStep;
        }

        public void bindData(Instruction instruction, ViewHolder holder) {
            holder.direction.setText(instruction.getText());
            holder.ingredients.setText(StringUtils.toBulletedList(instruction.getIngredients()));
            holder.step.setText(String.valueOf(instruction.getStep()));
        }
    }
}
