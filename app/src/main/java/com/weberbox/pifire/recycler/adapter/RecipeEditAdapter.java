package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemRecipeEditItemBinding;
import com.weberbox.pifire.databinding.ItemRecipeEditSectionBinding;
import com.weberbox.pifire.databinding.ItemRecipeEditStepBinding;
import com.weberbox.pifire.interfaces.ItemTouchHelperCallback;
import com.weberbox.pifire.interfaces.RecipeEditCallback;
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
    private final RecipeEditCallback callback;
    private final ObservableList<RecipeItems> list;

    public RecipeEditAdapter(ObservableList<RecipeItems> list,
                             StartDragListener listener, RecipeEditCallback callback) {
        this.list = list;
        this.dragListener = listener;
        this.callback = callback;
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
                setEditTextViewTag(holder.sectionBinding.recipeEditSectionText, item.getValue());
                holder.sectionBinding.recipeEditSectionDrag.setOnTouchListener((v, event) -> {
                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {
                        dragListener.requestDrag(holder);
                    }
                    return false;
                });
                break;
            case RECIPE_TYPE_ITEM:
                setEditTextViewTag(holder.itemBinding.recipeEditItemQty, item.getQuantity());
                setEditTextViewTag(holder.itemBinding.recipeEditItemUnit, item.getUnit());
                setEditTextViewTag(holder.itemBinding.recipeEditItemValue, item.getValue());
                holder.itemBinding.recipeEditItemDrag.setOnTouchListener((v, event) -> {
                    if (event.getAction() ==
                            MotionEvent.ACTION_DOWN) {
                        dragListener.requestDrag(holder);
                    }
                    return false;
                });
                break;
            case RECIPE_TYPE_STEP:
                setEditTextViewTag(holder.stepBinding.recipeEditStepText, item.getValue());
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

    private void setEditTextViewTag(EditText editText, String value) {
        editText.setTag(value);
        editText.setText(value);
        editText.setTag(null);
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
        list.addOnListChangedCallback(observable);
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
                    if (sectionBinding.recipeEditSectionText.getTag() == null) {
                        callback.onRecipeUpdated();
                    }
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
                    if (itemBinding.recipeEditItemQty.getTag() == null) {
                        callback.onRecipeUpdated();
                    }
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
                    if (itemBinding.recipeEditItemUnit.getTag() == null) {
                        callback.onRecipeUpdated();
                    }
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
                    if (itemBinding.recipeEditItemValue.getTag() == null) {
                        callback.onRecipeUpdated();
                    }
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
                    if (stepBinding.recipeEditStepText.getTag() == null) {
                        callback.onRecipeUpdated();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private final ObservableList.OnListChangedCallback<ObservableList<RecipeItems>> observable =
            new ObservableList.OnListChangedCallback<>() {
        @Override
        public void onChanged(ObservableList<RecipeItems> sender) {
            callback.onRecipeUpdated();
        }

        @Override
        public void onItemRangeChanged(ObservableList<RecipeItems> sender, int positionStart,
                                       int itemCount) {
            callback.onRecipeUpdated();
        }

        @Override
        public void onItemRangeInserted(ObservableList<RecipeItems> sender, int positionStart,
                                        int itemCount) {
            callback.onRecipeUpdated();
        }

        @Override
        public void onItemRangeMoved(ObservableList<RecipeItems> sender, int fromPosition,
                                     int toPosition, int itemCount) {
            callback.onRecipeUpdated();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<RecipeItems> sender, int positionStart,
                                       int itemCount) {
            callback.onRecipeUpdated();
        }
    };
}
