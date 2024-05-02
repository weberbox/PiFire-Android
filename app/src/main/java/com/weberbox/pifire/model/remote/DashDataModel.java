package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unused")
public class DashDataModel {

    @SerializedName("probe_info")
    @Expose
    private DashProbeInfo dashProbeInfo;
    @SerializedName("notify_data")
    @Expose
    private ArrayList<NotifyData> notifyData;
    @SerializedName("timer_info")
    @Expose
    private TimerInfo timerInfo;
    @SerializedName("current_mode")
    @Expose
    private String currentMode;
    @SerializedName("smoke_plus")
    @Expose
    private Boolean smokePlus;
    @SerializedName("pwm_control")
    @Expose
    private Boolean pwmControl;
    @SerializedName("hopper_level")
    @Expose
    private Integer hopperLevel;

    public DashProbeInfo getDashProbeInfo() {
        return dashProbeInfo;
    }

    public ArrayList<NotifyData> getNotifyData() {
        return notifyData;
    }

    public TimerInfo getTimerInfo() {
        return timerInfo;
    }

    public String getCurrentMode() {
        return currentMode;
    }

    public Boolean getSmokePlus() {
        return smokePlus;
    }

    public Boolean getPwmControl() {
        if (pwmControl == null) {
            return false;
        }
        return pwmControl;
    }

    public Integer getHopperLevel() {
        return hopperLevel;
    }

    public static class DashProbeInfo {
        @SerializedName("P")
        @Expose
        private Map<String, Double> primaryProbe;
        @SerializedName("F")
        @Expose
        private Map<String, Double> foodProbes;
        @SerializedName("PSP")
        @Expose
        private Double primarySetPoint;
        @SerializedName("NT")
        @Expose
        private Map<String, Double> notifyTarget;

        public Map<String, Double> getPrimaryProbe() {
            return primaryProbe;
        }

        public Map<String, Double> getFoodProbes() {
            return foodProbes;
        }

        public Double getPrimarySetPoint() {
            return primarySetPoint;
        }

        public Map<String, Double> getNotifyTarget() {
            return notifyTarget;
        }
    }

    public static class NotifyData {

        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("req")
        @Expose
        private Boolean req;
        @SerializedName("target")
        @Expose
        private Integer target;
        @SerializedName("shutdown")
        @Expose
        private Boolean shutdown;
        @SerializedName("keep_warm")
        @Expose
        private Boolean keepWarm;
        @SerializedName("last_check")
        @Expose
        private Double lastCheck;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getReq() {
            return req;
        }

        public void setReq(Boolean req) {
            this.req = req;
        }

        public Integer getTarget() {
            return target;
        }

        public void setTarget(Integer target) {
            this.target = target;
        }

        public Boolean getShutdown() {
            return shutdown;
        }

        public void setShutdown(Boolean shutdown) {
            this.shutdown = shutdown;
        }

        public Boolean getKeepWarm() {
            return keepWarm;
        }

        public void setKeepWarm(Boolean keepWarm) {
            this.keepWarm = keepWarm;
        }

        public Double getLastCheck() {
            return lastCheck;
        }

        public void setLastCheck(Double lastCheck) {
            this.lastCheck = lastCheck;
        }

    }

    public static class TimerInfo {
        @SerializedName("timer_paused")
        @Expose
        public Boolean timerPaused;
        @SerializedName("timer_start_time")
        @Expose
        public String timerStartTime;
        @SerializedName("timer_end_time")
        @Expose
        public String timerEndTime;
        @SerializedName("timer_paused_time")
        @Expose
        public String timerPausedTime;
        @SerializedName("timer_active")
        @Expose
        public Boolean timerActive;

        public Boolean getTimerPaused() {
            return timerPaused;
        }

        public long getTimerStartTime() {
            if (timerStartTime != null) {
                return Long.parseLong(timerStartTime);
            }
            return 0;
        }

        public long getTimerEndTime() {
            if (timerEndTime != null) {
                return Long.parseLong(timerEndTime);
            }
            return 0;
        }

        public long getTimerPauseTime() {
            if (timerPausedTime != null) {
                return Long.parseLong(timerPausedTime);
            }
            return 0;
        }

        public Boolean getTimerActive() {
            return timerActive;
        }
    }

    public static DashDataModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, DashDataModel.class);
    }

}
