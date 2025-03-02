package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemRecipeInstructionsBinding;
import com.weberbox.pifire.databinding.ItemViewMoreBinding;
import com.weberbox.pifire.model.remote.RecipesModel.Instruction;
import com.weberbox.pifire.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class RecipeInstructionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private final List<Instruction> list;
    private final int limitAmount;
    private boolean limitEnabled;
    private RecyclerView recyclerView;

    @SuppressWarnings("unused")
    public RecipeInstructionsAdapter(boolean limitEnabled, int limitAmount) {
        this.list = new ArrayList<>();
        this.limitEnabled = limitEnabled;
        this.limitAmount = limitAmount;
    }

    public RecipeInstructionsAdapter(boolean limitEnabled) {
        this.list = new ArrayList<>();
        this.limitEnabled = limitEnabled;
        this.limitAmount = AppConfig.RECYCLER_LIMIT;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  final int viewType) {
        if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(ItemViewMoreBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ItemsViewHolder(ItemRecipeInstructionsBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.bindData(list.get(position), vh);
                vh.root.setOnClickListener(view ->
                        vh.checkBox.setChecked(!vh.checkBox.isChecked()));
            } else if (holder instanceof FooterViewHolder vh) {
                vh.bindData(vh.itemView.getContext(), limitEnabled);
                vh.viewAll.setOnClickListener(view -> toggleViewAll(vh.itemView.getContext()));

            }
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setRecipeInstructions(@NotNull List<Instruction> instructions) {
        list.clear();
        list.addAll(instructions);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (limitEnabled) {
            return Math.min(list.size(), limitAmount + 1);
        } else {
            if (list.size() > limitAmount) {
                return list.size() + 1;
            } else {
                return list.size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (limitEnabled) {
            if (position == limitAmount) {
                return FOOTER_VIEW;
            } else {
                return super.getItemViewType(position);
            }
        } else {
            if (position == list.size()) {
                return FOOTER_VIEW;
            } else {
                return super.getItemViewType(position);
            }
        }
    }

    @SuppressWarnings("NotifyDataSetChanged")
    private void toggleViewAll(Context context) {
        LayoutAnimationController animation;
        if (limitEnabled) {
            animation = AnimationUtils.loadLayoutAnimation(context,
                    R.anim.fall_down_animation);
        } else {
            animation = AnimationUtils.loadLayoutAnimation(context,
                    R.anim.slide_up_animation);
        }
        recyclerView.setLayoutAnimation(animation);
        this.limitEnabled = !limitEnabled;
        notifyDataSetChanged();
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final MaterialCheckBox checkBox;
        private final TextView direction;
        private final TextView ingredients;
        private final TextView step;

        public ItemsViewHolder(ItemRecipeInstructionsBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            checkBox = binding.instructionsCheckbox;
            direction = binding.instructionsDirection;
            ingredients = binding.instructionsIngredient;
            step = binding.instructionsStep;
        }

        public void bindData(Instruction instruction, ItemsViewHolder holder) {
            holder.direction.setText(instruction.getText());
            holder.ingredients.setText(StringUtils.toBulletedList(instruction.getIngredients()));
            holder.step.setText(String.valueOf(instruction.getStep()));
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        private final TextView viewAll;

        public FooterViewHolder(ItemViewMoreBinding binding) {
            super(binding.getRoot());
            viewAll = binding.viewAllButton;
        }

        public void bindData(Context context, boolean limitEnabled) {
            if (limitEnabled) {
                viewAll.setText(context.getString(R.string.view_all));
            } else {
                viewAll.setText(context.getString(R.string.view_less));
            }
        }

    }
}
