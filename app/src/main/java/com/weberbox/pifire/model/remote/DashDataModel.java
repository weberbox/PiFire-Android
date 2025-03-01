package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weberbox.pifire.utils.adapters.CustomTypeAdapterFactory;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unused")
public class DashDataModel {

    @SerializedName("status_info")
    @Expose
    private StatusInfo statusInfo;
    @SerializedName("control_info")
    @Expose
    private ControlDataModel controlInfo;
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

    public StatusInfo getStatusInfo() {
        return statusInfo;
    }

    public ControlDataModel getControlInfo() {
        return controlInfo;
    }

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
        @SerializedName("eta")
        @Expose
        private Integer eta;
        @SerializedName("shutdown")
        @Expose
        private Boolean shutdown;
        @SerializedName("keep_warm")
        @Expose
        private Boolean keepWarm;
        @SerializedName("last_check")
        @Expose
        private Double lastCheck;
        @SerializedName("reignite")
        @Expose
        private Boolean reignite;
        @SerializedName("condition")
        @Expose
        private String condition;
        @SerializedName("triggered")
        @Expose
        private Boolean triggered;

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

        public Integer getEta() {
            return eta;
        }

        public void setEta(Integer eta) {
            this.eta = eta;
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

        public Boolean getReignite() {
            return reignite;
        }

        public void setReignite(Boolean reignite) {
            this.reignite = reignite;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public Boolean getTriggered() {
            return triggered;
        }

        public void setTriggered(Boolean triggered) {
            this.triggered = triggered;
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

    public static class StatusInfo {

        @SerializedName("hopper_level")
        @Expose
        private Integer hopperLevel;
        @SerializedName("lid_open_detected")
        @Expose
        private Boolean lidOpenDetected;
        @SerializedName("lid_open_endtime")
        @Expose
        private Integer lidOpenEndTime;
        @SerializedName("mode")
        @Expose
        private String displayMode;
        @SerializedName("p_mode")
        @Expose
        private Integer pMode;
        @SerializedName("prime_amount")
        @Expose
        private Integer primeAmount;
        @SerializedName("prime_duration")
        @Expose
        private Integer primeDuration;
        @SerializedName("recipe")
        @Expose
        private Boolean recipeMode;
        @SerializedName("recipe_paused")
        @Expose
        private Boolean recipePaused;
        @SerializedName("s_plus")
        @Expose
        private Boolean smokePlus;
        @SerializedName("shutdown_duration")
        @Expose
        private Integer shutdownDuration;
        @SerializedName("start_duration")
        @Expose
        private Integer startupDuration;
        @SerializedName("start_time")
        @Expose
        private Double startTime;
        @SerializedName("startup_timestamp")
        @Expose
        private Double startupTimestamp;
        @SerializedName("units")
        @Expose
        private String units;
        @SerializedName("outpins")
        @Expose
        private OutPins outPins;
        @SerializedName("notify_data")
        @Expose
        private ArrayList<NotifyData> notifyData;
        @SerializedName("timer")
        @Expose
        private Timer timer;

        public Integer getHopperLevel() {
            return hopperLevel;
        }

        public Boolean getLidOpenDetected() {
            return lidOpenDetected;
        }

        public Integer getLidOpenEndTime() {
            return lidOpenEndTime;
        }

        public String getDisplayMode() {
            return displayMode;
        }

        public Integer getPMode() {
            return pMode;
        }

        public Integer getPrimeAmount() {
            return primeAmount;
        }

        public Integer getPrimeDuration() {
            return primeDuration;
        }

        public Boolean getRecipeMode() {
            return recipeMode;
        }

        public Boolean getRecipePaused() {
            return recipePaused;
        }

        public Boolean getSmokePlus() {
            return smokePlus;
        }

        public Integer getShutdownDuration() {
            return shutdownDuration;
        }

        public Integer getStartupDuration() {
            return startupDuration;
        }

        public Double getStartTime() {
            return startTime;
        }

        public Double getStartupTimestamp() {
            return startupTimestamp;
        }

        public String getUnits() {
            return units;
        }

        public OutPins getOutPins() {
            return outPins;
        }

        public ArrayList<NotifyData> getNotifyData() {
            return notifyData;
        }

        public Timer getTimer() {
            return timer;
        }

    }

    public static class OutPins {
        @SerializedName("auger")
        @Expose
        private Boolean auger;
        @SerializedName("fan")
        @Expose
        private Boolean fan;
        @SerializedName("igniter")
        @Expose
        private Boolean igniter;
        @SerializedName("power")
        @Expose
        private Boolean power;
        @SerializedName("pwm")
        @Expose
        private Double pwm;

        public Boolean getAuger() {
            return auger;
        }

        public Boolean getFan() {
            return fan;
        }

        public Boolean getIgniter() {
            return igniter;
        }

        public Boolean getPower() {
            return power;
        }

        public Double getPwm() {
            return pwm;
        }

    }

    public static class Timer {

        @SerializedName("start")
        @Expose
        private Double start;
        @SerializedName("end")
        @Expose
        private Double end;
        @SerializedName("paused")
        @Expose
        private Double paused;
        @SerializedName("shutdown")
        @Expose
        private Boolean shutdown;

        public Double getStart() {
            return start;
        }

        public Double getEnd() {
            return end;
        }

        public Double getPaused() {
            return paused;
        }

        public Boolean getShutdown() {
            return shutdown;
        }

    }

    public static DashDataModel parseJSON(String response) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapterFactory(new CustomTypeAdapterFactory())
                .create();
        return gson.fromJson(response, DashDataModel.class);
    }

}
