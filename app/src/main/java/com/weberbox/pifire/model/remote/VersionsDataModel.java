package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class VersionsDataModel {
    @SerializedName("settings")
    @Expose
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setVersions(Settings settings) {
        this.settings = settings;
    }

    public VersionsDataModel withVersions(Settings settings) {
        this.settings = settings;
        return this;
    }

    public static class Settings {

        @SerializedName("versions")
        @Expose
        private Versions versions;

        public Versions getVersions() {
            return versions;
        }

        public void setVersions(Versions versions) {
            this.versions = versions;
        }

        public Settings withVersions(Versions versions) {
            this.versions = versions;
            return this;
        }

    }

    public static class Versions {

        @SerializedName("server")
        @Expose
        private String server;
        @SerializedName("build")
        @Expose
        private String build;

        public String getServerVersion() {
            return server;
        }

        public void setServerVersion(String server) {
            this.server = server;
        }

        public String getServerBuild() {
            return build;
        }

        public void setServerBuild(String build) {
            this.build = build;
        }

    }

    public static VersionsDataModel parseJSON(String response) {
        return new Gson().fromJson(response, VersionsDataModel.class);
    }
}
