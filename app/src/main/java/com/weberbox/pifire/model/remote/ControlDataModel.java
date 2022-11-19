package com.weberbox.pifire.model.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ControlDataModel {

    @SerializedName("updated")
    @Expose
    private Boolean updated;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("s_plus")
    @Expose
    private Boolean sPlus;
    @SerializedName("pwm_control")
    @Expose
    private Boolean pwmControl;
    @SerializedName("duty_cycle")
    @Expose
    private Integer dutyCycle;
    @SerializedName("hopper_check")
    @Expose
    private Boolean hopperCheck;
    @SerializedName("recipe")
    @Expose
    private String recipe;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("probe_profile_update")
    @Expose
    private Boolean probeProfileUpdate;
    @SerializedName("settings_update")
    @Expose
    private Boolean settingsUpdate;
    @SerializedName("distance_update")
    @Expose
    private Boolean distanceUpdate;
    @SerializedName("units_change")
    @Expose
    private Boolean unitsChange;
    @SerializedName("tuning_mode")
    @Expose
    private Boolean tuningMode;
    @SerializedName("safety")
    @Expose
    private Safety safety;
    @SerializedName("setpoints")
    @Expose
    private SetPoints setpoints;
    @SerializedName("notify_req")
    @Expose
    private NotifyReq notifyReq;
    @SerializedName("notify_data")
    @Expose
    private NotifyData notifyData;
    @SerializedName("timer")
    @Expose
    private Timer timer;
    @SerializedName("manual")
    @Expose
    private Manual manual;
    @SerializedName("probe_titles")
    @Expose
    private ProbeTitles probeTitles;
    @SerializedName("prime_amount")
    @Expose
    private Integer primeAmount;
    @SerializedName("next_mode")
    @Expose
    private String nextMode;
    @SerializedName("response")
    @Expose
    private ServerResponseModel response;

    public ServerResponseModel getResponse() {
        return response;
    }

    public Boolean getUpdated() {
        return updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

    public ControlDataModel withUpdated(Boolean updated) {
        this.updated = updated;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ControlDataModel withMode(String mode) {
        this.mode = mode;
        return this;
    }

    public Integer getPrimeAmount() {
        return primeAmount;
    }

    public void setPrimeAmount(Integer primeAmount) {
        this.primeAmount = primeAmount;
    }

    public ControlDataModel withPrimeAmount(Integer primeAmount) {
        this.primeAmount = primeAmount;
        return this;
    }

    public String getNextMode() {
        return nextMode;
    }

    public void setNextMode(String nextMode) {
        this.nextMode = nextMode;
    }

    public ControlDataModel withNextMode(String nextMode) {
        this.nextMode = nextMode;
        return this;
    }

    public Boolean getsPlus() {
        return sPlus;
    }

    public void setsPlus(Boolean sPlus) {
        this.sPlus = sPlus;
    }

    public ControlDataModel withsPlus(Boolean sPlus) {
        this.sPlus = sPlus;
        return this;
    }

    public Boolean getPWMControl() {
        return pwmControl;
    }

    public void setPWMControl(Boolean pwmControl) {
        this.pwmControl = pwmControl;
    }

    public ControlDataModel withPWMControl(Boolean pwmControl) {
        this.pwmControl = pwmControl;
        return this;
    }

    public Boolean getHopperCheck() {
        return hopperCheck;
    }

    public void setHopperCheck(Boolean hopperCheck) {
        this.hopperCheck = hopperCheck;
    }

    public ControlDataModel withHopperCheck(Boolean hopperCheck) {
        this.hopperCheck = hopperCheck;
        return this;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public ControlDataModel withRecipe(String recipe) {
        this.recipe = recipe;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ControlDataModel withStatus(String status) {
        this.status = status;
        return this;
    }

    public Boolean getProbeProfileUpdate() {
        return probeProfileUpdate;
    }

    public void setProbeProfileUpdate(Boolean probeProfileUpdate) {
        this.probeProfileUpdate = probeProfileUpdate;
    }

    public ControlDataModel withProbeProfileUpdate(Boolean probeProfileUpdate) {
        this.probeProfileUpdate = probeProfileUpdate;
        return this;
    }

    public Boolean getUnitsChange() {
        return unitsChange;
    }

    public void setUnitsChange(Boolean unitsChange) {
        this.unitsChange = unitsChange;
    }

    public ControlDataModel withUnitsChange(Boolean unitsChange) {
        this.unitsChange = unitsChange;
        return this;
    }

    public Boolean getTuningMode() {
        return tuningMode;
    }

    public void setTuningMode(Boolean tuningMode) {
        this.tuningMode = tuningMode;
    }

    public ControlDataModel withTuningMode(Boolean tuningMode) {
        this.tuningMode = tuningMode;
        return this;
    }

    public Safety getSafety() {
        return safety;
    }

    public void setSafety(Safety safety) {
        this.safety = safety;
    }

    public ControlDataModel withSafety(Safety safety) {
        this.safety = safety;
        return this;
    }

    public SetPoints getSetPoints() {
        return setpoints;
    }

    public void setSetPoints(SetPoints setpoints) {
        this.setpoints = setpoints;
    }

    public ControlDataModel withSetPoints(SetPoints setpoints) {
        this.setpoints = setpoints;
        return this;
    }

    public NotifyReq getNotifyReq() {
        return notifyReq;
    }

    public void setNotifyReq(NotifyReq notifyReq) {
        this.notifyReq = notifyReq;
    }

    public ControlDataModel withNotifyReq(NotifyReq notifyReq) {
        this.notifyReq = notifyReq;
        return this;
    }

    public NotifyData getNotifyData() {
        return notifyData;
    }

    public void setNotifyData(NotifyData notifyData) {
        this.notifyData = notifyData;
    }

    public ControlDataModel withNotifyData(NotifyData notifyData) {
        this.notifyData = notifyData;
        return this;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public ControlDataModel withTimer(Timer timer) {
        this.timer = timer;
        return this;
    }

    public Manual getManual() {
        return manual;
    }

    public void setManual(Manual manual) {
        this.manual = manual;
    }

    public ControlDataModel withManual(Manual manual) {
        this.manual = manual;
        return this;
    }

    public ProbeTitles getProbeTitles() {
        return probeTitles;
    }

    public void setManual(ProbeTitles probeTitles) {
        this.probeTitles = probeTitles;
    }

    public ControlDataModel withProbeTitles(ProbeTitles probeTitles) {
        this.probeTitles = probeTitles;
        return this;
    }

    public Boolean getSettingsUpdate() {
        return settingsUpdate;
    }

    public void setSettingsUpdate(Boolean settingsUpdate) {
        this.settingsUpdate = settingsUpdate;
    }

    public ControlDataModel withSettingsUpdate(Boolean settingsUpdate) {
        this.settingsUpdate = settingsUpdate;
        return this;
    }

    public Boolean getDistanceUpdate() {
        return distanceUpdate;
    }

    public void setDistanceUpdate(Boolean distanceUpdate) {
        this.distanceUpdate = distanceUpdate;
    }

    public ControlDataModel withDistanceUpdate(Boolean distanceUpdate) {
        this.distanceUpdate = distanceUpdate;
        return this;
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

        public void setChange(Boolean change) {
            this.change = change;
        }

        public Manual withChange(Boolean change) {
            this.change = change;
            return this;
        }

        public Boolean getFan() {
            return fan;
        }

        public void setFan(Boolean fan) {
            this.fan = fan;
        }

        public Manual withFan(Boolean fan) {
            this.fan = fan;
            return this;
        }

        public Boolean getAuger() {
            return auger;
        }

        public void setAuger(Boolean auger) {
            this.auger = auger;
        }

        public Manual withAuger(Boolean auger) {
            this.auger = auger;
            return this;
        }

        public Boolean getIgniter() {
            return igniter;
        }

        public void setIgniter(Boolean igniter) {
            this.igniter = igniter;
        }

        public Manual withIgniter(Boolean igniter) {
            this.igniter = igniter;
            return this;
        }

        public Boolean getPower() {
            return power;
        }

        public void setPower(Boolean power) {
            this.power = power;
        }

        public Manual withPower(Boolean power) {
            this.power = power;
            return this;
        }

        public Integer getPWM() {
            return pwm;
        }

        public void setPWM(Integer pwm) {
            this.pwm = pwm;
        }

        public Manual withPWM(Integer pwm) {
            this.pwm = pwm;
            return this;
        }

    }

    public static class NotifyData {

        @SerializedName("hopper_low")
        @Expose
        private Boolean hopperLow;
        @SerializedName("p1_shutdown")
        @Expose
        private Boolean p1Shutdown;
        @SerializedName("p2_shutdown")
        @Expose
        private Boolean p2Shutdown;
        @SerializedName("timer_shutdown")
        @Expose
        private Boolean timerShutdown;
        @SerializedName("p1_keep_warm")
        @Expose
        private Boolean p1KeepWarm;
        @SerializedName("p2_keep_warm")
        @Expose
        private Boolean p2KeepWarm;
        @SerializedName("timer_keep_warm")
        @Expose
        private Boolean timerKeepWarm;

        public Boolean getHopperLow() {
            return hopperLow;
        }

        public void setHopperLow(Boolean hopperLow) {
            this.hopperLow = hopperLow;
        }

        public NotifyData withHopperLow(Boolean hopperLow) {
            this.hopperLow = hopperLow;
            return this;
        }

        public Boolean getP1Shutdown() {
            return p1Shutdown;
        }

        public void setP1Shutdown(Boolean p1Shutdown) {
            this.p1Shutdown = p1Shutdown;
        }

        public NotifyData withP1Shutdown(Boolean p1Shutdown) {
            this.p1Shutdown = p1Shutdown;
            return this;
        }

        public Boolean getP2Shutdown() {
            return p2Shutdown;
        }

        public void setP2Shutdown(Boolean p2Shutdown) {
            this.p2Shutdown = p2Shutdown;
        }

        public NotifyData withP2Shutdown(Boolean p2Shutdown) {
            this.p2Shutdown = p2Shutdown;
            return this;
        }

        public Boolean getTimerShutdown() {
            return timerShutdown;
        }

        public void setTimerShutdown(Boolean timerShutdown) {
            this.timerShutdown = timerShutdown;
        }

        public NotifyData withTimerShutdown(Boolean timerShutdown) {
            this.timerShutdown = timerShutdown;
            return this;
        }

        public Boolean getP1KeepWarm() {
            return p1KeepWarm;
        }

        public void setP1KeepWarm(Boolean p1KeepWarm) {
            this.p1KeepWarm = p1KeepWarm;
        }

        public NotifyData withP1KeepWarm(Boolean p1KeepWarm) {
            this.p1KeepWarm = p1KeepWarm;
            return this;
        }

        public Boolean getP2KeepWarm() {
            return p2KeepWarm;
        }

        public void setP2KeepWarm(Boolean p2KeepWarm) {
            this.p2KeepWarm = p2KeepWarm;
        }

        public NotifyData withP2KeepWarm(Boolean p2KeepWarm) {
            this.p2KeepWarm = p2KeepWarm;
            return this;
        }

        public Boolean getTimerKeepWarm() {
            return timerKeepWarm;
        }

        public void setTimerKeepWarm(Boolean timerKeepWarm) {
            this.timerKeepWarm = timerKeepWarm;
        }

        public NotifyData withTimerKeepWarm(Boolean timerKeepWarm) {
            this.timerKeepWarm = timerKeepWarm;
            return this;
        }

    }

    public static class NotifyReq {

        @SerializedName("grill")
        @Expose
        private Boolean grill;
        @SerializedName("probe1")
        @Expose
        private Boolean probe1;
        @SerializedName("probe2")
        @Expose
        private Boolean probe2;
        @SerializedName("timer")
        @Expose
        private Boolean timer;

        public Boolean getGrill() {
            return grill;
        }

        public void setGrill(Boolean grill) {
            this.grill = grill;
        }

        public NotifyReq withGrill(Boolean grill) {
            this.grill = grill;
            return this;
        }

        public Boolean getProbe1() {
            return probe1;
        }

        public void setProbe1(Boolean probe1) {
            this.probe1 = probe1;
        }

        public NotifyReq withProbe1(Boolean probe1) {
            this.probe1 = probe1;
            return this;
        }

        public Boolean getProbe2() {
            return probe2;
        }

        public void setProbe2(Boolean probe2) {
            this.probe2 = probe2;
        }

        public NotifyReq withProbe2(Boolean probe2) {
            this.probe2 = probe2;
            return this;
        }

        public Boolean getTimer() {
            return timer;
        }

        public void setTimer(Boolean timer) {
            this.timer = timer;
        }

        public NotifyReq withTimer(Boolean timer) {
            this.timer = timer;
            return this;
        }

    }

    public static class ProbeTitles {
        @SerializedName("grill_title")
        @Expose
        public String grillTitle;
        @SerializedName("probe1_title")
        @Expose
        public String probeOneTitle;
        @SerializedName("probe2_title")
        @Expose
        public String probeTwoTitle;

        public String getGrillTitle() {
            return grillTitle;
        }

        public void setGrillTitle(String grillTitle) {
            this.grillTitle = grillTitle;
        }

        public ProbeTitles grillTitle(String grillTitle) {
            this.grillTitle = grillTitle;
            return this;
        }

        public String getProbeOneTitle() {
            return probeOneTitle;
        }

        public void setProbeOneTitle(String probeOneTitle) {
            this.probeOneTitle = probeOneTitle;
        }

        public ProbeTitles probeOneTitle(String probeOneTitle) {
            this.probeOneTitle = probeOneTitle;
            return this;
        }

        public String getProbeTwoTitle() {
            return probeTwoTitle;
        }

        public void setProbeTwoTitle(String probeTwoTitle) {
            this.probeTwoTitle = probeTwoTitle;
        }

        public ProbeTitles probeTwoTitle(String probeTwoTitle) {
            this.probeTwoTitle = probeTwoTitle;
            return this;
        }

    }

    public static class Safety {

        @SerializedName("startuptemp")
        @Expose
        private Integer startupTemp;
        @SerializedName("afterstarttemp")
        @Expose
        private Integer afterStartTemp;
        @SerializedName("reigniteretries")
        @Expose
        private Integer reigniteRetries;
        @SerializedName("reignitelaststate")
        @Expose
        private String reigniteLastState;

        public Integer getStartupTemp() {
            return startupTemp;
        }

        public void setStartupTemp(Integer startupTemp) {
            this.startupTemp = startupTemp;
        }

        public Safety withStartupTemp(Integer startupTemp) {
            this.startupTemp = startupTemp;
            return this;
        }

        public Integer getAfterStartTemp() {
            return afterStartTemp;
        }

        public void setAfterStartTemp(Integer afterStartTemp) {
            this.afterStartTemp = afterStartTemp;
        }

        public Safety withAfterStartTemp(Integer afterStartTemp) {
            this.afterStartTemp = afterStartTemp;
            return this;
        }

        public Integer getReigniteRetries() {
            return reigniteRetries;
        }

        public void setReigniteRetries(Integer reigniteRetries) {
            this.reigniteRetries = reigniteRetries;
        }

        public Safety withReigniteRetries(Integer reigniteRetries) {
            this.reigniteRetries = reigniteRetries;
            return this;
        }

        public String getReigniteLastState() {
            return reigniteLastState;
        }

        public void setReigniteLastState(String reigniteLastState) {
            this.reigniteLastState = reigniteLastState;
        }

        public Safety withReigniteLastState(String reigniteLastState) {
            this.reigniteLastState = reigniteLastState;
            return this;
        }

    }

    public static class SetPoints {

        @SerializedName("grill")
        @Expose
        private Integer grill;
        @SerializedName("probe1")
        @Expose
        private Integer probe1;
        @SerializedName("probe2")
        @Expose
        private Integer probe2;
        @SerializedName("grill_notify")
        @Expose
        private Integer grillNotify;

        public Integer getGrill() {
            return grill;
        }

        public void setGrill(Integer grill) {
            this.grill = grill;
        }

        public SetPoints withGrill(Integer grill) {
            this.grill = grill;
            return this;
        }

        public Integer getProbe1() {
            return probe1;
        }

        public void setProbe1(Integer probe1) {
            this.probe1 = probe1;
        }

        public SetPoints withProbe1(Integer probe1) {
            this.probe1 = probe1;
            return this;
        }

        public Integer getProbe2() {
            return probe2;
        }

        public void setProbe2(Integer probe2) {
            this.probe2 = probe2;
        }

        public SetPoints withProbe2(Integer probe2) {
            this.probe2 = probe2;
            return this;
        }

        public Integer getGrillNotify() {
            return grillNotify;
        }

        public void setGrillNotify(Integer grillNotify) {
            this.grillNotify = grillNotify;
        }

        public SetPoints withGrillNotify(Integer grillNotify) {
            this.grillNotify = grillNotify;
            return this;
        }

    }

    public static class Timer {

        @SerializedName("start")
        @Expose
        private Float start;
        @SerializedName("paused")
        @Expose
        private Float paused;
        @SerializedName("end")
        @Expose
        private Float end;
        @SerializedName("shutdown")
        @Expose
        private Boolean shutdown;

        public Float getStart() {
            return start;
        }

        public void setStart(Float start) {
            this.start = start;
        }

        public Timer withStart(Float start) {
            this.start = start;
            return this;
        }

        public Float getPaused() {
            return paused;
        }

        public void setPaused(Float paused) {
            this.paused = paused;
        }

        public Timer withPaused(Float paused) {
            this.paused = paused;
            return this;
        }

        public Float getEnd() {
            return end;
        }

        public void setEnd(Float end) {
            this.end = end;
        }

        public Timer withEnd(Float end) {
            this.end = end;
            return this;
        }

        public Boolean getShutdown() {
            return shutdown;
        }

        public void setShutdown(Boolean shutdown) {
            this.shutdown = shutdown;
        }

        public Timer withShutdown(Boolean shutdown) {
            this.shutdown = shutdown;
            return this;
        }

    }
}