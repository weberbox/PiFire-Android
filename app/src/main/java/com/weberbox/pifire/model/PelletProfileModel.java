package com.weberbox.pifire.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PelletProfileModel {

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
