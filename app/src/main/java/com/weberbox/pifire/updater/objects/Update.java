package com.weberbox.pifire.updater.objects;

import java.net.URL;

public class Update {
    private String mVersion;
    private Integer mVersionCode;
    private String mReleaseNotes;
    private Boolean mForceUpdate;
    private Integer mForceUpdateVersion;
    private URL mApk;

    public Update() {}

    public Update(String latestVersion, Integer latestVersionCode) {
        mVersion = latestVersion;
        mVersionCode = latestVersionCode;
    }

    public Update(String latestVersion, URL apk) {
        mVersion = latestVersion;
        mApk = apk;
    }

    public Update(String latestVersion, String releaseNotes, URL apk) {
        mVersion = latestVersion;
        mApk = apk;
        mReleaseNotes = releaseNotes;
    }

    public Update(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk) {
        this(latestVersion, releaseNotes, apk);
        mVersionCode = latestVersionCode;
    }

    public Update(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk,
                  Boolean forceUpdate) {
        this(latestVersion, releaseNotes, apk);
        mVersionCode = latestVersionCode;
        mForceUpdate = forceUpdate;
    }

    public Update(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk,
                  Boolean forceUpdate, Integer forceUpdateVersion) {
        this(latestVersion, releaseNotes, apk);
        mVersionCode = latestVersionCode;
        mForceUpdate = forceUpdate;
        mForceUpdateVersion = forceUpdateVersion;
    }

    public String getLatestVersion() {
        return mVersion;
    }

    public void setLatestVersion(String latestVersion) {
        mVersion = latestVersion;
    }

    public Integer getLatestVersionCode() {
        return mVersionCode;
    }

    public void setLatestVersionCode(Integer versionCode) {
        mVersionCode = versionCode;
    }

    public String getReleaseNotes() {
        return mReleaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        mReleaseNotes = releaseNotes;
    }

    public Boolean getForceUpdate() {
        return mForceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        mForceUpdate = forceUpdate;
    }

    public Integer getForceUpdateVersion() {
        return mForceUpdateVersion;
    }

    public void setForceUpdateVersion(Integer forceUpdateVersion) {
        mForceUpdateVersion = forceUpdateVersion;
    }

    public URL getUrlToDownload() {
        return mApk;
    }

    public void setUrlToDownload(URL apk) {
        mApk = apk;
    }
}
