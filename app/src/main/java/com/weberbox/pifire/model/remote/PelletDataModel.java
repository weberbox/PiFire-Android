package com.weberbox.pifire.model.remote;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class PelletDataModel {

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
            setBrand(brand);
            setWood(wood);
            setRating(rating);
            setComments(comments);
            setId(id);
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
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

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public String getWood() {
            return wood;
        }

        public void setWood(String wood) {
            this.wood = wood;
        }

    }

    public static PelletDataModel parseJSON(String response) {
        return new Gson().fromJson(response, PelletDataModel.class);
    }

}
