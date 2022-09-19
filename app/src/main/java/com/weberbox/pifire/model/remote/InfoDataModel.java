package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class InfoDataModel {

    @SerializedName("cpuinfo")
    @Expose
    private List<String> cpuInfo = new ArrayList<>();
    @SerializedName("ifconfig")
    @Expose
    private List<String> ifConfig = new ArrayList<>();
    @SerializedName("temp")
    @Expose
    private String temp;
    @SerializedName("uptime")
    @Expose
    private String uptime;
    @SerializedName("outpins")
    @Expose
    private HashMap<String, String> outPins = new HashMap<>();
    @SerializedName("inpins")
    @Expose
    private HashMap<String, String> inPins = new HashMap<>();
    @SerializedName("dev_pins")
    @Expose
    private HashMap<String, HashMap<String, String>> devPins = new HashMap<>();

    @SerializedName("server_version")
    @Expose
    private String serverVersion;

    public List<String> getCpuInfo() {
        return cpuInfo;
    }

    public void setCpuInfo(List<String> cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    public List<String> getIfConfig() {
        return ifConfig;
    }

    public void setIfConfig(List<String> ifConfig) {
        this.ifConfig = ifConfig;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public HashMap<String, String> getOutPins() {
        return outPins;
    }

    public void setOutPins(HashMap<String, String> outPins) {
        this.outPins = outPins;
    }

    public HashMap<String, String> getInPins() {
        return inPins;
    }

    public void setInPins(HashMap<String, String> inPins) {
        this.inPins = inPins;
    }

    public HashMap<String, HashMap<String, String>> getDevPins() {
        return devPins;
    }

    public void setDevPins(HashMap<String, HashMap<String, String>> devPins) {
        this.devPins = devPins;
    }

    public String getUpTime() {
        return uptime;
    }

    public void setUpTime(String uptime) {
        this.uptime = uptime;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public static InfoDataModel parseJSON(String response) {
        return new Gson().fromJson(response, InfoDataModel.class);
    }

}
