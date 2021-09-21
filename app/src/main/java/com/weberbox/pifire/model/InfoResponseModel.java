package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class InfoResponseModel {

    @SerializedName("cpuinfo")
    @Expose
    private List<String> cpuinfo = new ArrayList<String>();
    @SerializedName("ifconfig")
    @Expose
    private List<String> ifconfig = new ArrayList<String>();
    @SerializedName("temp")
    @Expose
    private String temp;
    @SerializedName("uptime")
    @Expose
    private String uptime;
    @SerializedName("outpins")
    @Expose
    private OutPins outpins;
    @SerializedName("inpins")
    @Expose
    private InPins inpins;

    public List<String> getCpuInfo() {
        return cpuinfo;
    }

    public void setCpuInfo(List<String> cpuinfo) {
        this.cpuinfo = cpuinfo;
    }

    public List<String> getIfConfig() {
        return ifconfig;
    }

    public void setIfConfig(List<String> ifconfig) {
        this.ifconfig = ifconfig;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public OutPins getOutPins() {
        return outpins;
    }

    public void setOutPins(OutPins outpins) {
        this.outpins = outpins;
    }

    public InPins getInPins() {
        return inpins;
    }

    public void setInPins(InPins inpins) {
        this.inpins = inpins;
    }

    public String getUpTime() {
        return uptime;
    }

    public void setUpTime(String uptime) {
        this.uptime = uptime;
    }

    public class OutPins {

        @SerializedName("auger")
        @Expose
        private String auger;
        @SerializedName("fan")
        @Expose
        private String fan;
        @SerializedName("igniter")
        @Expose
        private String igniter;
        @SerializedName("power")
        @Expose
        private String power;

        public String getAuger() {
            return auger;
        }

        public void setAuger(String auger) {
            this.auger = auger;
        }

        public String getFan() {
            return fan;
        }

        public void setFan(String fan) {
            this.fan = fan;
        }

        public String getIgniter() {
            return igniter;
        }

        public void setIgniter(String igniter) {
            this.igniter = igniter;
        }

        public String getPower() {
            return power;
        }

        public void setPower(String power) {
            this.power = power;
        }

    }

    public class InPins {

        @SerializedName("selector")
        @Expose
        private String selector;

        public String getSelector() {
            return selector;
        }

        public void setSelector(String selector) {
            this.selector = selector;
        }


    }

    public static InfoResponseModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, InfoResponseModel.class);
    }

}
