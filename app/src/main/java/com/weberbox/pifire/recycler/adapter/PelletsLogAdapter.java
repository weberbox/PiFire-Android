package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewholder.PelletsLogViewHolder;
import com.weberbox.pifire.recycler.viewmodel.PelletLogViewModel;

import java.util.List;

public class PelletsLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PelletLogViewModel> models;

    public PelletsLogAdapter(final List<PelletLogViewModel> viewModels) {
        models = viewModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PelletsLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((PelletsLogViewHolder) holder).bindData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_pellets_list_log;
    }
}

