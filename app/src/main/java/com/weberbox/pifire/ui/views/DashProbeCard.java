package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.LayoutDashProbeFrameBinding;

@SuppressWarnings("unused")
public class DashProbeCard extends CardView {

    private TextView probeName;
    private TextView probeSetTemp;
    private TextView probeTargetTitle;
    private TextView probeEtaTitle;
    private TextView probeEta;
    private ImageView shutdown;
    private ImageView keepWarm;
    private ImageView notifications;
    private Group setTempContainer;
    private ProgressBar probeTempProgress;
    private TextView probeTargetTemp;
    private TextView probeTemp;
    private ImageView probeIcon;
    private ImageView probeShutdown;
    private ImageView probeKeepWarm;
    private boolean isPrimaryProbe;
    private boolean etaEnabled;

    public DashProbeCard(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public DashProbeCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DashProbeCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutDashProbeFrameBinding binding = LayoutDashProbeFrameBinding.inflate(
                LayoutInflater.from(context), this, true);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DashProbeCard);

            String name = typedArray.getString(R.styleable.DashProbeCard_dash_probe_name);
            isPrimaryProbe = typedArray.getBoolean(R.styleable.DashProbeCard_dash_probe_is_primary,
                    false);
            etaEnabled = typedArray.getBoolean(R.styleable.DashProbeCard_dash_probe_eta,
                    false);
            int icon = typedArray.getResourceId(R.styleable.DashProbeCard_dash_probe_icon,
                    R.drawable.ic_grill_probe);

            name = name == null ? "" : name;

            setTempContainer = binding.dashProbeSetGroup;
            probeTargetTitle = binding.dashProbeTargetTitle;
            shutdown = binding.dashProbeShutdown;
            keepWarm = binding.dashProbeKeepWarm;
            probeName = binding.dashProbeName;
            probeTemp = binding.dashProbeTemp;
            probeIcon = binding.dashProbeIcon;
            probeTargetTemp = binding.dashProbeTarget;
            probeSetTemp = binding.dashProbeSetTemp;
            probeTempProgress = binding.dashProbeTempProgress;
            probeShutdown = binding.dashProbeShutdown;
            probeKeepWarm = binding.dashProbeKeepWarm;
            notifications = binding.dashProbeNotifications;
            probeEtaTitle = binding.dashProbeEtaTitle;
            probeEta = binding.dashProbeEta;

            probeName.setText(name);
            probeIcon.setImageResource(icon);

            showSetTemp(isPrimaryProbe);
            showEta(etaEnabled);

            typedArray.recycle();
        }
    }

    private void showSetTemp(boolean isPrimaryProbe) {
        if (isPrimaryProbe) {
            setTempContainer.setVisibility(VISIBLE);
            shutdown.setVisibility(GONE);
            keepWarm.setVisibility(GONE);
            probeTargetTitle.setGravity(Gravity.END);
            probeTargetTemp.setGravity(Gravity.END);
        } else {
            setTempContainer.setVisibility(GONE);
            probeTargetTitle.setGravity(Gravity.START);
            probeTargetTemp.setGravity(Gravity.START);
        }
    }

    private void showEta(boolean etaEnabled) {
        if (etaEnabled) {
            probeEtaTitle.setVisibility(VISIBLE);
            probeEta.setVisibility(VISIBLE);
        } else {
            probeEtaTitle.setVisibility(GONE);
            probeEta.setVisibility(GONE);
        }
    }

    public TextView getProbeName() {
        return probeName;
    }

    public void setProbeName(String text) {
        probeName.setText(text);
    }

    public boolean getIsPrimaryProbe() {
        return isPrimaryProbe;
    }

    public void setIsPrimaryProbe(boolean isPrimaryProbe) {
        this.isPrimaryProbe = isPrimaryProbe;
        showSetTemp(isPrimaryProbe);
    }

    public boolean getEtaEnabled() {
        return etaEnabled;
    }

    public void setEtaEnabled(boolean etaEnabled) {
        this.etaEnabled = etaEnabled;
        showEta(etaEnabled);
    }

    public ImageView getProbeIcon() {
        return probeIcon;
    }

    public void setHeaderIcon(int icon) {
        probeIcon.setImageResource(icon);
    }

    public TextView getProbeTemp() {
        return probeTemp;
    }

    public void setProbeTemp(String temp) {
        probeTemp.setText(temp);
    }

    public ProgressBar getProbeTempProgress() {
        return probeTempProgress;
    }

    public void setProbeTempProgress(int progress) {
        probeTempProgress.setProgress(progress);
    }

    public TextView getProbeTargetTemp() {
        return probeTargetTemp;
    }

    public void setProbeTargetTemp(String target) {
        probeTargetTemp.setText(target);
    }

    public TextView getProbeSetTemp() {
        return probeSetTemp;
    }

    public void setProbeSetTemp(String temp) {
        probeSetTemp.setText(temp);
    }

    public ImageView getProbeShutdown() {
        return probeShutdown;
    }

    public ImageView getProbeKeepWarm() {
        return probeKeepWarm;
    }

    public TextView getProbeEta() {
        return probeEta;
    }

    public ImageView getProbeNotifications() {
        return notifications;
    }

}
