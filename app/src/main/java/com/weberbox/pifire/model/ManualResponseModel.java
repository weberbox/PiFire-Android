package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ManualResponseModel {

    @SerializedName("manual")
    @Expose
    private Manual manual;
    @SerializedName("mode")
    @Expose
    private String mode;

    public String getMode() {return mode; }
    public Manual getManual() {
        return manual;
    }

    public static class Manual {

        @SerializedName("change")
        @Expose
        private Boolean change;
        @SerializedName("fan")
        @Expose
        private Boolean fan;
        @SerializedName("auger")
        @Expose
        private Boolean auger;
        @SerializedName("igniter")
        @Expose
        private Boolean igniter;
        @SerializedName("power")
        @Expose
        private Boolean power;

        public Boolean getChange() {
            return change;
        }

        public Boolean getFan() {
            return fan;
        }

        public Boolean getAuger() {
            return auger;
        }

        public Boolean getIgniter() {
            return igniter;
        }

        public Boolean getPower() {
            return power;
        }

    }

    public static ManualResponseModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, ManualResponseModel.class);
    }

}
