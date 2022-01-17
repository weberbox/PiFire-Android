package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemRecipeEditItemBinding;

import com.weberbox.pifire.databinding.ItemRecipeEditSectionBinding;
import com.weberbox.pifire.databinding.ItemRecipeEditStepBinding;
import com.weberbox.pifire.interfaces.ItemTouchHelperCallback;
import com.weberbox.pifire.interfaces.StartDragListener;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;

import java.util.Collections;
import java.util.List;

public class RecipeEditAdapter extends RecyclerView.Adapter<RecipeEditAdapter.ViewHolder>
        implements ItemTouchHelperCallback {

    public static final int RECIPE_ITEM_SECTION = 0;
    public static final int RECIPE_TYPE_ITEM = 1;
    public static final int RECIPE_STEP_SECTION = 2;
    public static final int RECIPE_TYPE_STEP = 3;

    private final StartDragListener dragListener;
    private final List<RecipeItems> list;

    public RecipeEditAdapter(List<RecipeItems> list, StartDragListener listener) {
        this.list = list;
        this.dragListener = listener;
    }

    @NonNull
    @Override
    public RecipeEditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == RECIPE_ITEM_SECTION || viewType == RECIPE_STEP_SECTION) {
            return new ViewHolder(ItemRecipeEditSectionBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else if (viewType == RECIPE_TYPE_ITEM) {
            return new ViewHolder(ItemRecipeEditItemBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ViewHolder(ItemRecipeEditStepBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeItems item = list.get(position);
        switch (holder.getItemViewType()) {
            case RECIPE_ITEM_SECTION:
            case RECIPE_STEP_SECTION:
                holder.sectionBinding.recipeEditSectionText.setText(item.getValue());
                holder.sectionBinding.recipeEditSectionDrag.setOnTouchListener((v, event) -> {
                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {
                        dragListener.requestDrag(holder);
                    }
                    return false;
                });
                break;
            case RECIPE_TYPE_ITEM:
                holder.itemBinding.recipeEditItemQty.setText(item.getQuantity());
                holder.itemBinding.recipeEditItemUnit.setText(item.getUnit());
                holder.itemBinding.recipeEditItemValue.setText(item.getValue());
                holder.itemBinding.recipeEditItemDrag.setOnTouchListener((v, event) -> {
                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {
                        dragListener.requestDrag(holder);
                    }
                    return false;
                });
                break;
            case RECIPE_TYPE_STEP:
                holder.stepBinding.recipeEditStepText.setText(item.getValue());
                holder.stepBinding.recipeEditStepDrag.setOnTouchListener((v, event) -> {
                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {
                        dragListener.requestDrag(holder);
                    }
                    return false;
                });
                break;
        }
    }

    public void addNewRecipeItem(int type) {
        list.add(list.size(), createRecipeItem(type));
        notifyItemInserted(list.size() + 1);
    }

    public void removeRecipeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public List<RecipeItems> getRecipeItems() {
        return list.size() == 0 ? null : list;
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

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RecyclerView.ViewHolder holder) {
        //holder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(RecyclerView.ViewHolder holder) {
        //holder.rowView.setBackgroundColor(Color.WHITE);
    }

    private RecipeItems createRecipeItem(int type) {
        int count = 1;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getType() == type) {
                count++;
            }
        }
        return new RecipeItems(count, type, null, null, null);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ItemRecipeEditSectionBinding sectionBinding;
        private ItemRecipeEditItemBinding itemBinding;
        private ItemRecipeEditStepBinding stepBinding;

        public ViewHolder(ItemRecipeEditSectionBinding binding) {
            super(binding.getRoot());
            sectionBinding = binding;
            sectionBinding.recipeEditSectionText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAbsoluteAdapterPosition()).setValue(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        public ViewHolder(ItemRecipeEditItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;
            itemBinding.recipeEditItemQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAbsoluteAdapterPosition()).setQuantity(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            itemBinding.recipeEditItemUnit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAbsoluteAdapterPosition()).setUnit(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            itemBinding.recipeEditItemValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAbsoluteAdapterPosition()).setValue(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        public ViewHolder(ItemRecipeEditStepBinding binding) {
            super(binding.getRoot());
            stepBinding = binding;
            stepBinding.recipeEditStepText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAbsoluteAdapterPosition()).setValue(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
}
