package com.weberbox.pifire.model.local;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ServerInfo {

    @SerializedName("appVersionCode")
    @Expose
    private Integer appVersionCode;
    @SerializedName("minServerInfo")
    @Expose
    private VersionBuild minServerInfo;
    @SerializedName("maxServerInfo")
    @Expose
    private VersionBuild maxServerInfo;

    public Integer getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(Integer appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public VersionBuild getMinServerInfo() {
        return minServerInfo;
    }

    public void setMinServerInfo(VersionBuild minServerInfo) {
        this.minServerInfo = minServerInfo;
    }

    public VersionBuild getMaxServerInfo() {
        return maxServerInfo;
    }

    public void setMaxServerInfo(VersionBuild maxServerInfo) {
        this.maxServerInfo = maxServerInfo;
    }

    public static class VersionBuild {
        @SerializedName("version")
        @Expose
        private String version;
        @SerializedName("build")
        @Expose
        private String build;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getBuild() {
            return build;
        }

        public void setBuild(String build) {
            this.build = build;
        }

    }

    public static ArrayList<ServerInfo> parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, new TypeToken<List<ServerInfo>>(){}.getType());
    }
}
