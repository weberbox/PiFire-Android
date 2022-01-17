package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ItemRecipeBinding;
import com.weberbox.pifire.interfaces.OnRecipeItemCallback;
import com.weberbox.pifire.model.local.RecipesModel;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> implements
        Filterable {

    private final OnRecipeItemCallback callback;
    private List<RecipesModel> recipeList;
    private List<RecipesModel> recipeListFiltered;
    private boolean isInChoiceMode;

    public RecipesAdapter(OnRecipeItemCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecipesAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                        final int viewType) {
        return new ViewHolder(ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final RecipesModel recipe = recipeList.get(position);
        holder.bindData(recipe, holder);

        holder.root.setOnClickListener(v -> {
            if (isInChoiceMode) {
                switchSelectedState(holder.getAbsoluteAdapterPosition());
                callback.onRecipeMultiSelect();
            } else {
                callback.onRecipeSelected(recipeList.get(holder.getAbsoluteAdapterPosition()).getId());
            }
        });

        holder.root.setOnLongClickListener(v -> {
            if (!isInChoiceMode) {
                beginChoiceMode(holder.getAbsoluteAdapterPosition());
            } else {
                clearSelectedState();
                callback.onRecipeMultiSelect();
            }
            return true;
        });

        if (isInChoiceMode) {
            holder.recipeImageSelected.setVisibility(recipeList.get(
                    holder.getAbsoluteAdapterPosition()).isSelected() ? View.VISIBLE : View.INVISIBLE);
        } else {
            holder.recipeImageSelected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList == null ? 0 : recipeList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRecipes(List<RecipesModel> recipeModelList) {
        recipeListFiltered = new ArrayList<>(recipeModelList);
        recipeList = recipeModelList;
        notifyDataSetChanged();
    }

    public List<RecipesModel> getRecipes() {
        return recipeList;
    }

    public List<RecipesModel> getFilteredRecipes() {
        return recipeListFiltered;
    }

    public boolean isSelected(int position) {
        return recipeList.get(position).isSelected();
    }

    public void switchSelectedState(int position) {
        RecipesModel recipe = recipeList.get(position);
        recipe.setSelected(!recipe.isSelected());
        notifyItemChanged(position);
    }

    public void clearSelectedState() {
        setIsInChoiceMode(false);
        for (int i = 0; i < recipeList.size(); i++) {
            if (recipeList.get(i).isSelected()) {
                recipeList.get(i).setSelected(false);
                notifyItemChanged(i);
            }
        }
    }

    public int getSelectedItemCount() {
        int amount = 0;
        for (int i = 0; i < recipeList.size(); i++) {
            if (recipeList.get(i).isSelected()) {
                amount++;
            }
        }
        return amount;
    }

    public void setIsInChoiceMode(boolean isInChoiceMode) {
        this.isInChoiceMode = isInChoiceMode;
    }

    public boolean getIsInChoiceMode() {
        return isInChoiceMode;
    }

    public void beginChoiceMode(int position) {
        recipeList.get(position).setSelected(true);
        setIsInChoiceMode(true);
        notifyItemChanged(position);
        callback.onRecipeMultiSelect();
    }

    @SuppressLint("NotifyDataSetChanged")
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<RecipesModel> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(recipeListFiltered);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (RecipesModel item : recipeListFiltered) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                recipeList.clear();
                recipeList.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final RatingBar recipeRating;
        private final TextView recipeName;
        private final TextView recipeTime;
        private final TextView recipeDifficulty;
        private final ImageView recipeImage;
        private final ImageView recipeImageSelected;

        public ViewHolder(final ItemRecipeBinding binding) {
            super(binding.getRoot());

            root = binding.getRoot();
            recipeName = binding.recipeName;
            recipeTime = binding.recipeTime;
            recipeDifficulty = binding.recipeDifficulty;
            recipeRating = binding.recipeRating;
            recipeImage = binding.recipeItemImage;
            recipeImageSelected = binding.recipeItemSelected;
        }

        public void bindData(final RecipesModel recipe, ViewHolder holder) {
            String time = recipe.getTime();
            String rating = recipe.getRating();
            String difficulty = recipe.getDifficulty();
            String image = recipe.getImage();

            recipeName.setText(recipe.getName());

            if (time != null) {
                recipeTime.setText(time);
            } else {
                recipeTime.setText(R.string.placeholder_none);
            }

            if (difficulty != null) {
                recipeDifficulty.setText(difficulty);
            } else {
                recipeDifficulty.setText(R.string.placeholder_none);
            }

            if (rating != null) {
                recipeRating.setRating(Float.parseFloat(rating));
            } else {
                recipeRating.setRating(0);
            }

            if (image != null) {
                Glide.with(holder.itemView.getContext())
                        .load(Uri.parse(image))
                        .placeholder(R.drawable.ic_recipe_placeholder)
                        .error(R.drawable.ic_recipe_placeholder_error)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(recipeImage);
            } else {
                Glide.with(holder.itemView.getContext()).clear(recipeImage);
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_recipe_placeholder)
                        .into(recipeImage);
            }
        }
    }
}