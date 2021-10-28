package com.weberbox.pifire.recycler.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.recycler.viewholder.PelletsProfileEditViewHolder;

import java.util.List;

public class PelletProfileEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<PelletProfileModel> mModel;
    private final List<String> mBrandsList;
    private final List<String> mWoodsList;
    private final PelletsCallbackInterface mCallBack;
    private final Activity mActivity;

    public PelletProfileEditAdapter(Activity activity, final List<String> brands, List<String> woods,
                                    final List<PelletProfileModel> viewModels,
                                    PelletsCallbackInterface callback) {
        mModel = viewModels;
        mCallBack = callback;
        mBrandsList = brands;
        mWoodsList = woods;
        mActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new PelletsProfileEditViewHolder(mActivity, view, mCallBack, mBrandsList, mWoodsList);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((PelletsProfileEditViewHolder) holder).bindData(mModel.get(position));
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_pellets_list_edit;
    }
}