package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewholder.TempPickerViewHolder;
import com.weberbox.pifire.recycler.viewmodel.TempPickerViewModel;

import java.util.ArrayList;
import java.util.List;

public class TempPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TempPickerViewModel> models = new ArrayList<>();

    public TempPickerAdapter(final List<TempPickerViewModel> viewModels) {
        if (viewModels != null) {
            this.models.addAll(viewModels);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TempPickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((TempPickerViewHolder) holder).bindData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_picker_temp;
    }
}