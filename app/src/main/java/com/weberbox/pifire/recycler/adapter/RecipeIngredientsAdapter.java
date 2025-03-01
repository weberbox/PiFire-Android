package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemRecipeIngredientsBinding;
import com.weberbox.pifire.model.remote.RecipesModel.Ingredient;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientsAdapter extends RecyclerView.Adapter<RecipeIngredientsAdapter.ViewHolder> {

    private final List<Ingredient> list;

    public RecipeIngredientsAdapter() {
        this.list = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeIngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  final int viewType) {
        return new ViewHolder(ItemRecipeIngredientsBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ingredient ingredient = list.get(position);
        holder.bindData(ingredient, holder, position);
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setRecipeIngredients(@NotNull List<Ingredient> ingredients) {
        list.clear();
        list.addAll(ingredients);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView number;
        private final TextView quantity;
        private final TextView ingredient;

        public ViewHolder(ItemRecipeIngredientsBinding binding) {
            super(binding.getRoot());
            number = binding.ingredientsNumber;
            quantity = binding.ingredientsQuantity;
            ingredient = binding.ingredientsIngredient;
        }

        public void bindData(Ingredient ingredient, ViewHolder holder, int position) {
            holder.number.setText(String.valueOf(position + 1));
            holder.quantity.setText(ingredient.getQuantity());
            holder.ingredient.setText(ingredient.getName());
        }
    }
}
