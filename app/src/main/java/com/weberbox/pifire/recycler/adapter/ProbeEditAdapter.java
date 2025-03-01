package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemProbeEditBinding;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeInfo;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;
import com.weberbox.pifire.ui.dialogs.ProbeEditDialog;
import com.weberbox.pifire.ui.dialogs.ProbeEditDialog.DialogProbeCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProbeEditAdapter extends RecyclerView.Adapter<ProbeEditAdapter.ViewHolder> {

    private final List<ProbeProfileModel> profiles;
    private final DialogProbeCallback callback;
    private final List<ProbeInfo> list;

    public ProbeEditAdapter(@NotNull final List<ProbeInfo> list,
                            @NotNull List<ProbeProfileModel> profiles,
                            @NotNull DialogProbeCallback callback) {
        this.list = list;
        this.profiles = profiles;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ProbeEditAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                          final int viewType) {
        return new ViewHolder(ItemProbeEditBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ProbeInfo probe = list.get(position);
        holder.bindData(probe);

        Context context = holder.itemView.getContext();

        holder.itemView.setOnClickListener(v -> {
            ProbeEditDialog probeEditDialog = new ProbeEditDialog(context, position, probe,
                    profiles, callback);
            probeEditDialog.showDialog();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateProbeType(int position, String name) {
        list.get(position).setName(name);
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView probeName;
        private final TextView probeProfile;

        public ViewHolder(ItemProbeEditBinding binding) {
            super(binding.getRoot());
            probeName = binding.title;
            probeProfile = binding.summary;
        }

        public void bindData(final ProbeInfo probeInfo) {
            probeName.setText(probeInfo.getName());
            probeProfile.setText(probeInfo.getProbeProfile().getName());
        }
    }
}
