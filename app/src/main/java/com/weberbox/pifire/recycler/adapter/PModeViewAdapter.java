package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewholder.PModeViewHolder;
import com.weberbox.pifire.recycler.viewmodel.PModeViewModel;

import java.util.List;


public class PModeViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PModeViewModel> mModel;

    public PModeViewAdapter(final List<PModeViewModel> viewModel) {
        mModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PModeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PModeViewHolder) holder).bindData(mModel.get(position));
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_pmode_table;
    }
}