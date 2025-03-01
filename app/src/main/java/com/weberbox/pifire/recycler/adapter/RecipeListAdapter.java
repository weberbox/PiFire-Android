package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.ItemRecipeBinding;
import com.weberbox.pifire.model.remote.RecipesModel.Asset;
import com.weberbox.pifire.model.remote.RecipesModel.MetaData;
import com.weberbox.pifire.model.remote.RecipesModel.RecipeDetails;
import com.weberbox.pifire.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> implements
        Filterable {

    private final OnRecipeItemCallback callback;
    private final List<RecipeDetails> recipeList;
    private List<RecipeDetails> recipeListFiltered;
    private boolean isInChoiceMode;

    public RecipeListAdapter(@NotNull OnRecipeItemCallback callback) {
        this.recipeList = new ArrayList<>();
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecipeListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        return new ViewHolder(ItemRecipeBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(recipeList.get(position), holder);

        holder.root.setOnClickListener(v -> {
            if (isInChoiceMode) {
                switchSelectedState(holder.getAbsoluteAdapterPosition());
                callback.onRecipeMultiSelect();
            } else {
                callback.onRecipeSelected(holder.recipeImage, recipeList.get(
                        holder.getAbsoluteAdapterPosition()).getFilename());
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
                    holder.getAbsoluteAdapterPosition()).isSelected() ?
                    View.VISIBLE : View.INVISIBLE);
        } else {
            holder.recipeImageSelected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setRecipes(@NotNull List<RecipeDetails> recipeModelList) {
        recipeListFiltered = new ArrayList<>(recipeModelList);
        recipeList.clear();
        recipeList.addAll(recipeModelList);
        notifyDataSetChanged();
    }

    public List<RecipeDetails> getRecipes() {
        return recipeList;
    }

    @SuppressWarnings("unused")
    public List<RecipeDetails> getFilteredRecipes() {
        return recipeListFiltered;
    }

    @SuppressWarnings("unused")
    public boolean isSelected(int position) {
        return recipeList.get(position).isSelected();
    }

    public void switchSelectedState(int position) {
        RecipeDetails recipe = recipeList.get(position);
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

    @SuppressWarnings("unused")
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
                ArrayList<RecipeDetails> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(recipeListFiltered);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (RecipeDetails item : recipeListFiltered) {
                        String title = item.getDetails().getMetadata().getTitle();
                        if (title.toLowerCase().contains(filterPattern)) {
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

        public void bindData(final RecipeDetails recipe, ViewHolder holder) {
            MetaData metadata = recipe.getDetails().getMetadata();
            String thumbFilename = metadata.getThumbnail();
            String encodedThumb = "";
            for (Asset asset : recipe.getDetails().getAssets()) {
                if (asset.getFilename().equals(thumbFilename)) {
                    encodedThumb = asset.getEncodedThumb();
                }
            }


            recipeName.setText(metadata.getTitle());
            recipeDifficulty.setText(metadata.getDifficulty());
            recipeRating.setRating((float) metadata.getRating());
            recipeTime.setText(TimeUtils.formatMinutes(metadata.getCookTime()));

            if (!encodedThumb.isBlank()) {
                Glide.with(holder.itemView.getContext())
                        .asBitmap()
                        .load(decodeImage(encodedThumb))
                        .placeholder(R.drawable.ic_recipe_placeholder)
                        .transition(BitmapTransitionOptions.withCrossFade())
                        .error(R.drawable.ic_recipe_placeholder_error)
                        .into(recipeImage);
            } else {
                Glide.with(holder.itemView.getContext()).clear(recipeImage);
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.ic_recipe_placeholder)
                        .into(recipeImage);
            }
        }
    }

    public interface OnRecipeItemCallback {
        void onRecipeSelected(ImageView imageView, String filename);
        void onRecipeMultiSelect();
    }

    private static Bitmap decodeImage(String encodedThumb) {
        byte[] decodedString = Base64.decode(encodedThumb, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}