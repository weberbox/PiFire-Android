package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class PelletResponseModel {

    @SerializedName("archive")
    @Expose
    private Map<String, PelletProfileModel> profiles = new HashMap<>();
    @SerializedName("brands")
    @Expose
    private List<String> brands = new ArrayList<>();
    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("log")
    @Expose
    private Map<String, String> log = new HashMap<>();
    @SerializedName("woods")
    @Expose
    private List<String> woods = new ArrayList<>();

    public Map<String, PelletProfileModel> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, PelletProfileModel> profiles) {
        this.profiles = profiles;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Map<String, String> getLogs() {
        return log;
    }

    public void setLogs(Map<String, String> log) {
        this.log = log;
    }

    public List<String> getWoods() {
        return woods;
    }

    public void setWoods(List<String> woods) {
        this.woods = woods;
    }

    public static class Current {

        @SerializedName("date_loaded")
        @Expose
        private String dateLoaded;
        @SerializedName("hopper_level")
        @Expose
        private Integer hopperLevel;
        @SerializedName("pelletid")
        @Expose
        private String pelletid;

        public String getDateLoaded() {
            return dateLoaded;
        }

        public void setDateLoaded(String dateLoaded) {
            this.dateLoaded = dateLoaded;
        }

        public Integer getHopperLevel() {
            return hopperLevel;
        }

        public void setHopperLevel(Integer hopperLevel) {
            this.hopperLevel = hopperLevel;
        }

        public String getPelletId() {
            return pelletid;
        }

        public void setPelletId(String pelletid) {
            this.pelletid = pelletid;
        }

    }

    public static PelletResponseModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, PelletResponseModel.class);
    }

}
