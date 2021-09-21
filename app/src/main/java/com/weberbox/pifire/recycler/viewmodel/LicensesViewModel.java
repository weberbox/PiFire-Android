package com.weberbox.pifire.recycler.viewmodel;

import android.graphics.Color;

import androidx.annotation.NonNull;

import java.util.Random;

public class LicensesViewModel {

    private int mProjectIconColor;
    private String mProjectIcon;
    private String mProjectText;
    private String mProjectLicense;

    public LicensesViewModel(@NonNull final String project, @NonNull final String license) {
        setProjectText(project);
        setProjectLicense(license);
        setProjectIcon(project.substring(0,1).toUpperCase());
        setProjectIconColor(randomColor());
    }

    public int getEventIconColor() {
        return mProjectIconColor;
    }

    public void setProjectIconColor(final int eventIconColor) {
        this.mProjectIconColor = eventIconColor;
    }

    @NonNull
    public String getProjectIcon() {
        return mProjectIcon;
    }

    public void setProjectIcon(@NonNull final String projectIcon) {
        this.mProjectIcon = projectIcon;
    }

    @NonNull
    public String getProjectText() {
        return mProjectText;
    }

    public void setProjectText(@NonNull final String projectText) {
        this.mProjectText = projectText;
    }

    @NonNull
    public String getProjectLicense() {
        return mProjectLicense;
    }

    public void setProjectLicense(@NonNull final String projectLicense) {
        this.mProjectLicense = projectLicense;
    }

    public int randomColor() {
        Random random= new Random();
        return Color.argb(255, random.nextInt(256), random.nextInt(256),
                random.nextInt(256));
    }

}
