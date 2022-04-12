package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemLicenseListBinding;
import com.weberbox.pifire.model.local.LicensesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LicensesListAdapter extends RecyclerView.Adapter<LicensesListAdapter.ViewHolder> {

    private List<LicensesModel> list;
    private boolean limited;

    public LicensesListAdapter(boolean limited) {
        this.list = new ArrayList<>();
        this.limited = limited;
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
        if (limited) {
            return Math.min(list.size(), AppConfig.RECYCLER_LIMIT);
        } else {
            return list == null ? 0 : list.size();
        }
    }

    public void setLimitEnabled(boolean enabled) {
        limited = enabled;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<LicensesModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final int[] colors;
        private final Random random;
        private final CardView cardView;
        private final TextView projectIcon;
        private final TextView projectText;
        private final TextView projectLicense;

        public ViewHolder(ItemLicenseListBinding binding) {
            super(binding.getRoot());
            colors = binding.getRoot().getContext().getResources().getIntArray(
                    R.array.licenses_color_list);
            random = new Random();
            root = binding.getRoot();
            cardView = binding.licenseItemViewContainer;
            projectIcon = binding.licenseIconHolder;
            projectText = binding.licenseProjectName;
            projectLicense = binding.licenseTextHolder;
        }

        public void bindData(final LicensesModel license) {
            int randomColor = random.nextInt(colors.length);
            cardView.setCardBackgroundColor(colors[randomColor]);
            projectIcon.setBackgroundColor(colors[randomColor]);
            projectIcon.setText(license.getProjectIcon());
            projectText.setText(license.getProjectText());
            projectLicense.setText(license.getProjectLicense());
        }
    }
}
