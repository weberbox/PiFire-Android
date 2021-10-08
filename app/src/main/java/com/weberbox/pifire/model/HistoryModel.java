package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class HistoryModel {

    @SerializedName("grill_settemp_list")
    @Expose
    private List<Integer> grillSetTempList = null;
    @SerializedName("grill_temp_list")
    @Expose
    private List<Integer> grillTempList = null;
    @SerializedName("label_time_list")
    @Expose
    private List<String> labelTimeList = null;
    @SerializedName("probe1_settemp_list")
    @Expose
    private List<Integer> probe1SetTempList = null;
    @SerializedName("probe1_temp_list")
    @Expose
    private List<Integer> probe1TempList = null;
    @SerializedName("probe2_settemp_list")
    @Expose
    private List<Integer> probe2SetTempList = null;
    @SerializedName("probe2_temp_list")
    @Expose
    private List<Integer> probe2TempList = null;

    public List<Integer> getGrillSetTempList() {
        return grillSetTempList;
    }

    public void setGrillSetTempList(List<Integer> grillSetTempList) {
        this.grillSetTempList = grillSetTempList;
    }

    public List<Integer> getGrillTempList() {
        return grillTempList;
    }

    public void setGrillTempList(List<Integer> grillTempList) {
        this.grillTempList = grillTempList;
    }

    public List<String> getLabelTimeList() {
        return labelTimeList;
    }

    public void setLabelTimeList(List<String> labelTimeList) {
        this.labelTimeList = labelTimeList;
    }

    public List<Integer> getProbe1SetTempList() {
        return probe1SetTempList;
    }

    public void setProbe1SetTempList(List<Integer> probe1SetTempList) {
        this.probe1SetTempList = probe1SetTempList;
    }

    public List<Integer> getProbe1TempList() {
        return probe1TempList;
    }

    public void setProbe1TempList(List<Integer> probe1TempList) {
        this.probe1TempList = probe1TempList;
    }

    public List<Integer> getProbe2SetTempList() {
        return probe2SetTempList;
    }

    public void setProbe2SetTempList(List<Integer> probe2SetTempList) {
        this.probe2SetTempList = probe2SetTempList;
    }

    public List<Integer> getProbe2TempList() {
        return probe2TempList;
    }

    public void setProbe2TempList(List<Integer> probe2TempList) {
        this.probe2TempList = probe2TempList;
    }

    public static HistoryModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, HistoryModel.class);
    }

}