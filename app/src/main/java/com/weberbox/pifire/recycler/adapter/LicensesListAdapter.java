package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemLicenseListBinding;
import com.weberbox.pifire.model.local.LicensesModel;

import java.util.List;

public class LicensesListAdapter extends RecyclerView.Adapter<LicensesListAdapter.ViewHolder> {

    private List<LicensesModel> list;

    public LicensesListAdapter() {

    }

    @NonNull
    @Override
    public LicensesListAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                             final int viewType) {
        return new ViewHolder(ItemLicenseListBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
        holder.root.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(holder.projectLicense.getText().toString()));
            holder.itemView.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<LicensesModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final TextView projectIcon;
        private final TextView projectText;
        private final TextView projectLicense;

        public ViewHolder(ItemLicenseListBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            projectIcon = binding.licenseIconHolder;
            projectText = binding.licenseProjectName;
            projectLicense = binding.licenseTextHolder;
        }

        public void bindData(final LicensesModel model) {
            ((GradientDrawable) projectIcon.getBackground()).setColor(model.getProjectIconColor());
            projectIcon.setText(model.getProjectIcon());
            projectText.setText(model.getProjectText());
            projectLicense.setText(model.getProjectLicense());
        }
    }
}
