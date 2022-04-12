package com.weberbox.pifire.updater.objects;

import java.net.URL;
import java.util.ArrayList;

public class Update {
    private String latestVersion;
    private Integer latestVersionCode;
    private String releaseNotes;
    private Boolean forceUpdate;
    private ArrayList<Integer> forceUpdateVersionCodes;
    private URL apk;

    public Update() {}

    public Update(String latestVersion, Integer latestVersionCode) {
        this.latestVersion = latestVersion;
        this.latestVersionCode = latestVersionCode;
    }

    public Update(String latestVersion, URL apk) {
        this.latestVersion = latestVersion;
        this.apk = apk;
    }

    public Update(String latestVersion, String releaseNotes, URL apk) {
        this.latestVersion = latestVersion;
        this.apk = apk;
        this.releaseNotes = releaseNotes;
    }

    public Update(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk) {
        this(latestVersion, releaseNotes, apk);
        this.latestVersionCode = latestVersionCode;
    }

    public Update(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk,
                  Boolean forceUpdate) {
        this(latestVersion, releaseNotes, apk);
        this.latestVersionCode = latestVersionCode;
        this.forceUpdate = forceUpdate;
    }

    public Update(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk,
                  Boolean forceUpdate, ArrayList<Integer> forceUpdateVersionCodes) {
        this(latestVersion, releaseNotes, apk);
        this.latestVersionCode = latestVersionCode;
        this.forceUpdate = forceUpdate;
        this.forceUpdateVersionCodes = forceUpdateVersionCodes;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Integer getLatestVersionCode() {
        return latestVersionCode;
    }

    public void setLatestVersionCode(Integer versionCode) {
        this.latestVersionCode = versionCode;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public ArrayList<Integer> getForceUpdateVersionCodes() {
        return forceUpdateVersionCodes;
    }

    public void setForceUpdateVersionCodes(ArrayList<Integer> forceUpdateVersionCodes) {
        this.forceUpdateVersionCodes = forceUpdateVersionCodes;
    }

    public URL getUrlToDownload() {
        return apk;
    }

    public void setUrlToDownload(URL apk) {
        this.apk = apk;
    }
}
