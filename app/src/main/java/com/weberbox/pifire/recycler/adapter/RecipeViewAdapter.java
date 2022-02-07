package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemRecipeViewItemBinding;
import com.weberbox.pifire.databinding.ItemRecipeViewSectionBinding;
import com.weberbox.pifire.databinding.ItemRecipeViewStepBinding;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

public class RecipeViewAdapter extends RecyclerView.Adapter<RecipeViewAdapter.ViewHolder> {

    public static final int RECIPE_ITEM_SECTION = 0;
    public static final int RECIPE_TYPE_ITEM = 1;
    public static final int RECIPE_STEP_SECTION = 2;
    public static final int RECIPE_TYPE_STEP = 3;

    private final List<RecipeItems> list;

    public RecipeViewAdapter(List<RecipeItems> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecipeViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RECIPE_ITEM_SECTION || viewType == RECIPE_STEP_SECTION) {
            return new ViewHolder(ItemRecipeViewSectionBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else if (viewType == RECIPE_TYPE_STEP) {
            return new ViewHolder(ItemRecipeViewStepBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ViewHolder(ItemRecipeViewItemBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeItems items = list.get(position);
        switch (holder.getItemViewType()) {
            case RECIPE_ITEM_SECTION:
            case RECIPE_STEP_SECTION:
                holder.sectionBinding.recipeItemSection.setText(items.getValue());
                break;
            case RECIPE_TYPE_ITEM:
                TextView item = holder.itemBinding.recipeItemViewText;
                AppCompatCheckBox checkBox = holder.itemBinding.recipeItemViewCheckbox;
                String[] recipeItem = { items.getQuantity(), items.getUnit() , items.getValue() };
                item.setText(StringUtils.cleanStrings(recipeItem, " "));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        item.setPaintFlags(item.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        item.setTextColor(Color.GRAY);
                    } else {
                        item.setPaintFlags(0);
                        item.setTextColor(Color.WHITE);
                    }
                });
                break;
            case RECIPE_TYPE_STEP:
                TextView num = holder.stepBinding.recipeItemViewStep;
                TextView step = holder.stepBinding.recipeItemViewStepText;
                step.setText(items.getValue());
                num.setText(String.valueOf(items.getKey()));
                holder.stepBinding.recipeItemViewStepHolder.setOnClickListener(v -> {
                    if((num.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) !=
                            Paint.STRIKE_THRU_TEXT_FLAG) {
                        num.setPaintFlags(num.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        step.setPaintFlags(step.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        num.setTextColor(Color.GRAY);
                        step.setTextColor(Color.GRAY);
                    } else {
                        num.setPaintFlags(0);
                        step.setPaintFlags(0);
                        num.setTextColor(Color.WHITE);
                        step.setTextColor(Color.WHITE);
                    }
                });
                break;
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecipeItems(List<RecipeItems> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRecipeViewSectionBinding sectionBinding;
        private ItemRecipeViewItemBinding itemBinding;
        private ItemRecipeViewStepBinding stepBinding;

        public ViewHolder(ItemRecipeViewSectionBinding binding) {
            super(binding.getRoot());
            sectionBinding = binding;
        }

        public ViewHolder(ItemRecipeViewItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
        }

        public ViewHolder(ItemRecipeViewStepBinding binding) {
            super(binding.getRoot());
            stepBinding = binding;
        }
    }
}