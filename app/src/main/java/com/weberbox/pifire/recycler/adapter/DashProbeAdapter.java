package com.weberbox.pifire.recycler.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.ItemDashProbeBinding;
import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;
import com.weberbox.pifire.ui.views.DashProbeCard;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TempUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;

public class DashProbeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DashProbe> list;
    private final TempUtils tempUtils;
    private final DashProbeCallback callback;

    public DashProbeAdapter(@NotNull final List<DashProbe> list, @NotNull TempUtils tempUtils,
                            @NotNull DashProbeCallback callback) {
        this.list = list;
        this.tempUtils = tempUtils;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProbeViewHolder(ItemDashProbeBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DashProbe probe = list.get(position);
        try {
            ProbeViewHolder vh = (ProbeViewHolder) holder;
            vh.probe.setOnClickListener(view -> callback.onProbeClick(probe));
            vh.probe.setOnLongClickListener(v -> {
                callback.onProbeLongClick(probe);
                return true;
            });
            vh.probe.getProbeName().setText(probe.getName());
            if (isPrimaryProbe(probe)) {
                vh.probe.getProbeIcon().setImageResource(R.drawable.ic_grill_thermometer);
                vh.probe.setIsPrimaryProbe(true);
                if (probe.getSetTemp() > 0) {
                    vh.probe.getProbeSetTemp().setText(StringUtils.formatTemp(probe.getSetTemp(),
                            tempUtils.isFahrenheit()));
                } else {
                    vh.probe.getProbeSetTemp().setText(
                            vh.itemView.getContext().getString(R.string.placeholder_none));
                }
            } else {
                vh.probe.getProbeIcon().setImageResource(R.drawable.ic_grill_probe);
                vh.probe.setIsPrimaryProbe(false);
                vh.probe.getProbeShutdown().setVisibility(probe.getShutdown() ? VISIBLE : GONE);
                vh.probe.getProbeKeepWarm().setVisibility(probe.getKeepWarm() ? VISIBLE : GONE);
            }
            if (probe.getProbeTemp() > 0) {
                vh.probe.getProbeTemp().setText(StringUtils.formatTemp(probe.getProbeTemp(),
                        tempUtils.isFahrenheit()));
                vh.probe.setProbeTempProgress(probe.getProbeTemp().intValue());
            } else {
                vh.probe.getProbeTemp().setText(
                        vh.itemView.getContext().getString(R.string.placeholder_temp));
                vh.probe.setProbeTempProgress(0);
            }
            if (probe.getTarget() != null) {
                if (probe.getTarget() > 0) {
                    vh.probe.getProbeTempProgress().setMax(probe.getTarget());
                    vh.probe.getProbeTargetTemp().setText(StringUtils.formatTemp(probe.getTarget(),
                            tempUtils.isFahrenheit()));
                } else {
                    if (isPrimaryProbe(probe)) {
                        vh.probe.getProbeTempProgress().setMax(tempUtils.getMaxGrillTemp());
                    } else {
                        vh.probe.getProbeTempProgress().setMax(tempUtils.getMaxProbeTemp());
                    }
                    vh.probe.getProbeTargetTemp().setText(
                            vh.itemView.getContext().getString(R.string.placeholder_none));
                }
            }
            if (probe.getEta() != null) {
                vh.probe.setEtaEnabled(true);
                vh.probe.getProbeEta().setText(probe.getEta());
            } else {
                vh.probe.setEtaEnabled(false);
                vh.probe.getProbeEta().setText(
                        vh.itemView.getContext().getString(R.string.placeholder_none));
            }
            vh.probe.getProbeNotifications().setVisibility(probe.getNotifications() ? VISIBLE : GONE);
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressWarnings("unused")
    public void addProbe(@NotNull DashProbe probe) {
        list.add(list.size(), probe);
        notifyItemInserted(list.size() - 1);
    }

    @SuppressWarnings("unused")
    public void removeProbe(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void updateProbe(@NotNull DashProbe probe) {
        int position = list.indexOf(probe);
        list.set(position, probe);
        notifyItemChanged(position);
    }

    public List<DashProbe> getDashProbes() {
        return list;
    }

    @SuppressWarnings("unused")
    public DashProbe getDashProbe(@NotNull String label) {
        for (DashProbe probe : list) {
            if (probe.getLabel().equals(label)) {
                return probe;
            }
        }
        return null;
    }

    public DashProbe getGrillProbe() {
        for (DashProbe probe : list) {
            if (isPrimaryProbe(probe)) {
                return probe;
            }
        }
        return null;
    }

    private boolean isPrimaryProbe(@NotNull DashProbe probe) {
        return probe.getProbeType().equals(Constants.DASH_PROBE_PRIMARY);
    }

    public static class ProbeViewHolder extends RecyclerView.ViewHolder {

        private final DashProbeCard probe;

        public ProbeViewHolder(ItemDashProbeBinding binding) {
            super(binding.getRoot());
            probe = binding.dashProbe;
        }
    }

    public interface DashProbeCallback {
        void onProbeClick(DashProbe probe);
        void onProbeLongClick(DashProbe probe);
    }
}
