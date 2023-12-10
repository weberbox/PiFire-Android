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
    private ImageView shutdown;
    private ImageView keepWarm;
    private Group setTempContainer;
    private ProgressBar probeTempProgress;
    private TextView probeTargetTemp;
    private TextView probeTemp;
    private ImageView probeIcon;
    private ImageView probeShutdown;
    private ImageView probeKeepWarm;
    private boolean setTempEnabled;

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
            setTempEnabled = typedArray.getBoolean(R.styleable.DashProbeCard_dash_probe_set_temp,
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

            probeName.setText(name);
            probeIcon.setImageResource(icon);

            updateSetTemp(setTempEnabled);

            typedArray.recycle();
        }
    }

    private void updateSetTemp(boolean setTempEnabled) {
        if (!setTempEnabled) {
            setTempContainer.setVisibility(GONE);
            probeTargetTitle.setGravity(Gravity.START);
            probeTargetTemp.setGravity(Gravity.START);
        } else {
            setTempContainer.setVisibility(VISIBLE);
            shutdown.setVisibility(GONE);
            keepWarm.setVisibility(GONE);
            probeTargetTitle.setGravity(Gravity.END);
            probeTargetTemp.setGravity(Gravity.END);
        }
    }

    public TextView getProbeName() {
        return probeName;
    }

    public void setProbeName(String text) {
        probeName.setText(text);
    }

    public boolean getSetTempEnabled() {
        return setTempEnabled;
    }

    public void setSetTempEnabled(boolean enabled) {
        this.setTempEnabled = enabled;
        updateSetTemp(setTempEnabled);
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

}
