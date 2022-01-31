package com.weberbox.pifire.model.remote;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class PostDataModel {

    @SerializedName("onesignal_device")
    @Expose
    private OneSignalDevice oneSignalDevice;
    @SerializedName("pellets_action")
    @Expose
    private PelletsAction pelletsAction;

    public OneSignalDevice getOneSignalDevice() {
        return oneSignalDevice;
    }

    public void setOneSignalDevice(OneSignalDevice oneSignalDevice) {
        this.oneSignalDevice = oneSignalDevice;
    }

    public PostDataModel withOneSignalDevice(OneSignalDevice oneSignalDevice) {
        this.oneSignalDevice = oneSignalDevice;
        return this;
    }

    public PelletsAction getPelletsAction() {
        return pelletsAction;
    }

    public void setPelletsPelletsAction(PelletsAction pelletsAction) {
        this.pelletsAction = pelletsAction;
    }

    public PostDataModel withPelletsAction(PelletsAction pelletsAction) {
        this.pelletsAction = pelletsAction;
        return this;
    }

    public static class OneSignalDevice{

        @SerializedName("onesignal_player_id")
        @Expose
        private String oneSignalPlayerID;

        public String getOneSignalPlayerID() {
            return oneSignalPlayerID;
        }

        public void setOneSignalPlayerID(String oneSignalPlayerID) {
            this.oneSignalPlayerID = oneSignalPlayerID;
        }

        public OneSignalDevice withOneSignalPlayerID(String oneSignalPlayerID) {
            this.oneSignalPlayerID = oneSignalPlayerID;
            return this;
        }

    }

    public static class PelletsAction {

        @SerializedName("profile")
        @Expose
        private String profile;
        @SerializedName("brand_name")
        @Expose
        private String brandName;
        @SerializedName("wood_type")
        @Expose
        private String woodType;
        @SerializedName("rating")
        @Expose
        private Integer rating;
        @SerializedName("comments")
        @Expose
        private String comments;
        @SerializedName("add_and_load")
        @Expose
        private Boolean addAndLoad;
        @SerializedName("delete_wood")
        @Expose
        private String deleteWood;
        @SerializedName("new_wood")
        @Expose
        private String newWood;
        @SerializedName("delete_brand")
        @Expose
        private String deleteBrand;
        @SerializedName("new_brand")
        @Expose
        private String newBrand;
        @SerializedName("log_item")
        @Expose
        private String logItem;

        public String getProfile() {
            return profile;
        }

        public void setProfile(String profile) {
            this.profile = profile;
        }

        public PelletsAction withProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public PelletsAction withBrandName(String brandName) {
            this.brandName = brandName;
            return this;
        }

        public String getWoodType() {
            return woodType;
        }

        public void setWoodType(String woodType) {
            this.woodType = woodType;
        }

        public PelletsAction withWoodType(String woodType) {
            this.woodType = woodType;
            return this;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }

        public PelletsAction withRating(Integer rating) {
            this.rating = rating;
            return this;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public PelletsAction withComments(String comments) {
            this.comments = comments;
            return this;
        }

        public Boolean getAddAndLoad() {
            return addAndLoad;
        }

        public void setComments(Boolean addAndLoad) {
            this.addAndLoad = addAndLoad;
        }

        public PelletsAction withAddAndLoad(Boolean addAndLoad) {
            this.addAndLoad = addAndLoad;
            return this;
        }

        public String getDeleteWood() {
            return deleteWood;
        }

        public void setDeleteWood(String deleteWood) {
            this.deleteWood = deleteWood;
        }

        public PelletsAction withDeleteWood(String deleteWood) {
            this.deleteWood = deleteWood;
            return this;
        }

        public String getNewWood() {
            return newWood;
        }

        public void setNewWood(String newWood) {
            this.newWood = newWood;
        }

        public PelletsAction withNewWood(String newWood) {
            this.newWood = newWood;
            return this;
        }

        public String getDeleteBrand() {
            return deleteBrand;
        }

        public void setDeleteBrand(String deleteBrand) {
            this.deleteBrand = deleteBrand;
        }

        public PelletsAction withDeleteBrand(String deleteBrand) {
            this.deleteBrand = deleteBrand;
            return this;
        }

        public String getNewBrand() {
            return newBrand;
        }

        public void setNewBrand(String newBrand) {
            this.newBrand = newBrand;
        }

        public PelletsAction withNewBrand(String newBrand) {
            this.newBrand = newBrand;
            return this;
        }

        public String getLogItem() {
            return logItem;
        }

        public void setLogItem(String logItem) {
            this.logItem = logItem;
        }

        public PelletsAction withLogItem(String logItem) {
            this.logItem = logItem;
            return this;
        }

    }

}
