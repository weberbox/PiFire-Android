package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class HistoryDataModel {

    @SerializedName("grill_settemp_list")
    @Expose
    private List<Double> grillSetTempList = null;
    @SerializedName("grill_temp_list")
    @Expose
    private List<Double> grillTempList = null;
    @SerializedName("label_time_list")
    @Expose
    private List<String> labelTimeList = null;
    @SerializedName("probe1_settemp_list")
    @Expose
    private List<Double> probe1SetTempList = null;
    @SerializedName("probe1_temp_list")
    @Expose
    private List<Double> probe1TempList = null;
    @SerializedName("probe2_settemp_list")
    @Expose
    private List<Double> probe2SetTempList = null;
    @SerializedName("probe2_temp_list")
    @Expose
    private List<Double> probe2TempList = null;

    public List<Double> getGrillSetTempList() {
        return grillSetTempList;
    }

    public void setGrillSetTempList(List<Double> grillSetTempList) {
        this.grillSetTempList = grillSetTempList;
    }

    public List<Double> getGrillTempList() {
        return grillTempList;
    }

    public void setGrillTempList(List<Double> grillTempList) {
        this.grillTempList = grillTempList;
    }

    public List<String> getLabelTimeList() {
        return labelTimeList;
    }

    public void setLabelTimeList(List<String> labelTimeList) {
        this.labelTimeList = labelTimeList;
    }

    public List<Double> getProbe1SetTempList() {
        return probe1SetTempList;
    }

    public void setProbe1SetTempList(List<Double> probe1SetTempList) {
        this.probe1SetTempList = probe1SetTempList;
    }

    public List<Double> getProbe1TempList() {
        return probe1TempList;
    }

    public void setProbe1TempList(List<Double> probe1TempList) {
        this.probe1TempList = probe1TempList;
    }

    public List<Double> getProbe2SetTempList() {
        return probe2SetTempList;
    }

    public void setProbe2SetTempList(List<Double> probe2SetTempList) {
        this.probe2SetTempList = probe2SetTempList;
    }

    public List<Double> getProbe2TempList() {
        return probe2TempList;
    }

    public void setProbe2TempList(List<Double> probe2TempList) {
        this.probe2TempList = probe2TempList;
    }

    public static HistoryDataModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, HistoryDataModel.class);
    }

}