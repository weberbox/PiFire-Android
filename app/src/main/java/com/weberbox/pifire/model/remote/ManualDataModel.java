package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ManualDataModel {

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
        @SerializedName("pwm")
        @Expose
        private Integer pwm;

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

        public Integer getPWM() {
            return pwm;
        }

    }

    public static ManualDataModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, ManualDataModel.class);
    }

}
