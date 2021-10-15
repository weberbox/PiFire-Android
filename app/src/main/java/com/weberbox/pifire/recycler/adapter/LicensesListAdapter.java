package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.LicensesCallbackInterface;
import com.weberbox.pifire.recycler.viewholder.LicensesViewHolder;
import com.weberbox.pifire.recycler.viewmodel.LicensesViewModel;

import java.util.List;

public class LicensesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<LicensesViewModel> mModel;
    private final LicensesCallbackInterface mCallBack;

    public LicensesListAdapter(final List<LicensesViewModel> viewModel, LicensesCallbackInterface callback) {
        mModel = viewModel;
        mCallBack = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new LicensesViewHolder(view, mCallBack);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((LicensesViewHolder) holder).bindData(mModel.get(position));
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_license_list;
    }
}
