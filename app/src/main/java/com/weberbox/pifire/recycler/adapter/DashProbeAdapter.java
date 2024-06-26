package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.ItemDashProbeBinding;
import com.weberbox.pifire.interfaces.DashProbeCallback;
import com.weberbox.pifire.model.local.DashProbeModel.DashProbe;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.views.DashProbeCard;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TempUtils;

import java.util.List;

import timber.log.Timber;

public class DashProbeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<DashProbe> list;
    private final TempUtils tempUtils;
    private final DashProbeCallback callback;

    public DashProbeAdapter(final List<DashProbe> list, TempUtils tempUtils,
                            DashProbeCallback callback) {
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
            if (probe.getType().equals(Constants.DASH_PROBE_PRIMARY)) {
                vh.probe.getProbeIcon().setImageResource(R.drawable.ic_grill_thermometer);
                vh.probe.setSetTempEnabled(true);
                if (probe.getSetTemp() > 0) {
                    vh.probe.getProbeSetTemp().setText(StringUtils.formatTemp(probe.getSetTemp(),
                            tempUtils.isFahrenheit()));
                } else {
                    vh.probe.getProbeSetTemp().setText(
                            vh.itemView.getContext().getString(R.string.placeholder_none));
                }
            } else {
                vh.probe.getProbeIcon().setImageResource(R.drawable.ic_grill_probe);
                vh.probe.setSetTempEnabled(false);
                AnimUtils.fadeAnimation(vh.probe.getProbeShutdown(), 300,
                        probe.getShutdown() ? Constants.FADE_IN : Constants.FADE_OUT);
                AnimUtils.fadeAnimation(vh.probe.getProbeKeepWarm(), 300,
                        probe.getKeepWarm() ? Constants.FADE_IN : Constants.FADE_OUT);
            }
            if (probe.getValue() > 0) {
                vh.probe.getProbeTemp().setText(StringUtils.formatTemp(probe.getValue(),
                        tempUtils.isFahrenheit()));
                vh.probe.setProbeTempProgress(probe.getValue().intValue());
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
                    if (probe.getType().equals(Constants.DASH_PROBE_PRIMARY)) {
                        vh.probe.getProbeTempProgress().setMax(tempUtils.getMaxGrillTemp());
                    } else {
                        vh.probe.getProbeTempProgress().setMax(tempUtils.getMaxProbeTemp());
                    }
                    vh.probe.getProbeTargetTemp().setText(
                            vh.itemView.getContext().getString(R.string.placeholder_none));
                }
            }
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressWarnings("unused")
    public void addProbe(DashProbe probe) {
        list.add(list.size(), probe);
        notifyItemInserted(list.size());
    }

    @SuppressWarnings("unused")
    public void removeProbe(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void updateProbe(DashProbe probe) {
        int position = list.indexOf(probe);
        list.set(position, probe);
        notifyItemChanged(position);
    }

    public List<DashProbe> getDashProbes() {
        return list;
    }

    @SuppressWarnings("unused")
    public DashProbe getDashProbe(String label) {
        for (DashProbe probe : list) {
            if (probe.getLabel().equals(label)) {
                return probe;
            }
        }
        return null;
    }

    public DashProbe getGrillProbe() {
        for (DashProbe probe : list) {
            if (probe.getType().equals(Constants.DASH_PROBE_PRIMARY)) {
                return probe;
            }
        }
        return null;
    }

    public static class ProbeViewHolder extends RecyclerView.ViewHolder {

        private final DashProbeCard probe;

        public ProbeViewHolder(ItemDashProbeBinding binding) {
            super(binding.getRoot());
            probe = binding.dashProbe;
        }
    }
}
