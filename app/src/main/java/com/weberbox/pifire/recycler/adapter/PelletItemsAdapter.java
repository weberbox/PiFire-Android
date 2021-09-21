package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.recycler.viewholder.PelletsItemViewHolder;
import com.weberbox.pifire.recycler.viewmodel.PelletItemViewModel;

import java.util.List;

public class PelletItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private PelletsCallbackInterface mCallback;
    private final List<PelletItemViewModel> models;

    public PelletItemsAdapter(final List<PelletItemViewModel> viewModels, PelletsCallbackInterface callback) {
        models = viewModels;
        mCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PelletsItemViewHolder(view, mCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((PelletsItemViewHolder) holder).bindData(models.get(position));
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_pellets_list;
    }

}
