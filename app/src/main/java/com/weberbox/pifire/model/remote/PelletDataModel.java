package com.weberbox.pifire.model.remote;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class PelletDataModel {

    @SerializedName("archive")
    @Expose
    private Map<String, PelletProfileModel> profiles;
    @SerializedName("brands")
    @Expose
    private List<String> brands;
    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("log")
    @Expose
    private Map<String, String> log;
    @SerializedName("woods")
    @Expose
    private List<String> woods;

    public Map<String, PelletProfileModel> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, PelletProfileModel> profiles) {
        this.profiles = profiles;
    }

    public List<String> getBrands() {
        return brands;
    }

    public Current getCurrent() {
        return current;
    }

    public Map<String, String> getLogs() {
        return log;
    }

    public List<String> getWoods() {
        return woods;
    }

    public static class Current {

        @SerializedName("date_loaded")
        @Expose
        private String dateLoaded;
        @SerializedName("est_usage")
        @Expose
        private Double estUsage;
        @SerializedName("hopper_level")
        @Expose
        private Integer hopperLevel;
        @SerializedName("pelletid")
        @Expose
        private String pelletid;

        public String getDateLoaded() {
            return dateLoaded;
        }

        public Double getEstimatedUsage() {
            return estUsage;
        }

        public String getPelletId() {
            return pelletid;
        }

    }

    public static class PelletProfileModel {

        @SerializedName("brand")
        @Expose
        private String brand;
        @SerializedName("comments")
        @Expose
        private String comments;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("rating")
        @Expose
        private Integer rating;
        @SerializedName("wood")
        @Expose
        private String wood;

        public PelletProfileModel(@NonNull final String brand, @NonNull final String wood,
                                  @NonNull final Integer rating, @NonNull final String comments,
                                  @NonNull final String id) {
            this.brand = brand;
            this.wood = wood;
            this.rating = rating;
            this.comments = comments;
            this.id = id;
        }

        public String getBrand() {
            return brand;
        }

        public String getComments() {
            return comments;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Integer getRating() {
            return rating;
        }

        public String getWood() {
            return wood;
        }

    }

    public static PelletDataModel parseJSON(String response) {
        return new Gson().fromJson(response, PelletDataModel.class);
    }

}
