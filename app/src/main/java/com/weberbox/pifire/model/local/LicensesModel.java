package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class LicensesModel {

    private int projectIconColor;
    private String projectIcon;
    private String projectText;
    private String projectLicense;

    public LicensesModel(@NonNull final String project, @NonNull final String license) {
        setProjectText(project);
        setProjectLicense(license);
        setProjectIcon(project.substring(0, 1).toUpperCase());
    }

    @NonNull
    public String getProjectIcon() {
        return projectIcon;
    }

    public void setProjectIcon(@NonNull final String projectIcon) {
        this.projectIcon = projectIcon;
    }

    @NonNull
    public String getProjectText() {
        return projectText;
    }

    public void setProjectText(@NonNull final String projectText) {
        this.projectText = projectText;
    }

    @NonNull
    public String getProjectLicense() {
        return projectLicense;
    }

    private void setProjectLicense(@NonNull final String projectLicense) {
        this.projectLicense = projectLicense;
    }

}
