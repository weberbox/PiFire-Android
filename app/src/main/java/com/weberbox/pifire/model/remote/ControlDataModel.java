package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weberbox.pifire.model.remote.DashDataModel.NotifyData;
import com.weberbox.pifire.utils.adapters.CustomTypeAdapterFactory;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("unused")
public class ControlDataModel {

    @SerializedName("updated")
    @Expose
    private Boolean updated;
    @SerializedName("mode")
    @Expose
    private String mode;
    @SerializedName("next_mode")
    @Expose
    private String nextMode;
    @SerializedName("s_plus")
    @Expose
    private Boolean smokePlus;
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
    private Recipe recipe;
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
    @SerializedName("primary_setpoint")
    @Expose
    private Integer primarySetPoint;
    @SerializedName("notify_data")
    @Expose
    private ArrayList<NotifyData> notifyData;
    @SerializedName("timer")
    @Expose
    private Timer timer;
    @SerializedName("manual")
    @Expose
    private Manual manual;
    @SerializedName("prime_amount")
    @Expose
    private Integer primeAmount;
    @SerializedName("smartstart")
    @Expose
    private SmartStart smartStart;
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

    public String getCurrentMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public ControlDataModel withMode(String mode) {
        this.mode = mode;
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

    public Boolean getSmokePlus() {
        return smokePlus;
    }

    public void setSmokePlus(Boolean smokePlus) {
        this.smokePlus = smokePlus;
    }

    public ControlDataModel withSmokePlus(Boolean smokePlus) {
        this.smokePlus = smokePlus;
        return this;
    }

    public Boolean getPwmControl() {
        return pwmControl;
    }

    public void setPWMControl(Boolean pwmControl) {
        this.pwmControl = pwmControl;
    }

    public ControlDataModel withPWMControl(Boolean pwmControl) {
        this.pwmControl = pwmControl;
        return this;
    }

    public Integer getDutyCycle() {
        return dutyCycle;
    }

    public void setDutyCycle(Integer dutyCycle) {
        this.dutyCycle = dutyCycle;
    }

    public ControlDataModel withDutyCycle(Integer dutyCycle) {
        this.dutyCycle = dutyCycle;
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

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public ControlDataModel withRecipe(Recipe recipe) {
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

    public Integer getPrimarySetPoint() {
        return primarySetPoint;
    }

    public void setPrimarySetPoint(Integer primarySetPoint) {
        this.primarySetPoint = primarySetPoint;
    }

    public ControlDataModel withPrimarySetPoint(Integer primarySetPoint) {
        this.primarySetPoint = primarySetPoint;
        return this;
    }

    public ArrayList<NotifyData> getNotifyData() {
        return notifyData;
    }

    public void setNotifyData(ArrayList<NotifyData> notifyData) {
        this.notifyData = notifyData;
    }

    public ControlDataModel withNotifyData(ArrayList<NotifyData> notifyData) {
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

    public static class Recipe {
        @SerializedName("filename")
        @Expose
        private String fileName;
        @SerializedName("start_step")
        @Expose
        private Integer smartStep;
        @SerializedName("step")
        @Expose
        private Integer step;
        @SerializedName("step_data")
        @Expose
        private StepData stepData;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Recipe withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Integer getSmartStep() {
            return smartStep;
        }

        public void setSmartStep(Integer smartStep) {
            this.smartStep = smartStep;
        }

        public Recipe withSmartStep(Integer smartStep) {
            this.smartStep = smartStep;
            return this;
        }

        public Integer getStep() {
            return step;
        }

        public void setStep(Integer step) {
            this.step = step;
        }

        public Recipe withStep(Integer step) {
            this.step = step;
            return this;
        }

        public StepData getStepData() {
            return stepData;
        }

        public void setStepData(StepData stepData) {
            this.stepData = stepData;
        }

        public Recipe withStepData(StepData stepData) {
            this.stepData = stepData;
            return this;
        }
    }

    public static class StepData {

        @SerializedName("hold_temp")
        @Expose
        private Integer holdTemp;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("mode")
        @Expose
        private String mode;
        @SerializedName("notify")
        @Expose
        private Boolean notify;
        @SerializedName("pause")
        @Expose
        private Boolean pause;
        @SerializedName("timer")
        @Expose
        private Integer timer;
        @SerializedName("triggered")
        @Expose
        private Boolean triggered;
        @SerializedName("trigger_temps")
        @Expose
        private Map<String, Double> triggerTemps;

        public Integer getHoldTemp() {
            return holdTemp;
        }

        public void setHoldTemp(Integer holdTemp) {
            this.holdTemp = holdTemp;
        }

        public StepData withHoldTemp(Integer holdTemp) {
            this.holdTemp = holdTemp;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public StepData withMessage(String message) {
            this.message = message;
            return this;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public StepData withMode(String mode) {
            this.mode = mode;
            return this;
        }

        public Boolean getNotify() {
            return notify;
        }

        public void setNotify(Boolean notify) {
            this.notify = notify;
        }

        public StepData withNotify(Boolean notify) {
            this.notify = notify;
            return this;
        }

        public Boolean getPause() {
            return pause;
        }

        public void setPause(Boolean pause) {
            this.pause = pause;
        }

        public StepData withPause(Boolean pause) {
            this.pause = pause;
            return this;
        }

        public Integer getTimer() {
            return timer;
        }

        public void setTimer(Integer timer) {
            this.timer = timer;
        }

        public StepData withTimer(Integer timer) {
            this.timer = timer;
            return this;
        }

        public Boolean getTriggered() {
            return triggered;
        }

        public void setTriggered(Boolean triggered) {
            this.triggered = triggered;
        }

        public StepData withTriggered(Boolean triggered) {
            this.triggered = triggered;
            return this;
        }

        public Map<String, Double> getTriggerTemps() {
            return triggerTemps;
        }

        public void setTriggered(Map<String, Double> triggerTemps) {
            this.triggerTemps = triggerTemps;
        }

        public StepData withTriggered(Map<String, Double> triggerTemps) {
            this.triggerTemps = triggerTemps;
            return this;
        }

    }

    public static class Timer {

        @SerializedName("start")
        @Expose
        private Double start;
        @SerializedName("paused")
        @Expose
        private Double paused;
        @SerializedName("end")
        @Expose
        private Double end;
        @SerializedName("shutdown")
        @Expose
        private Boolean shutdown;

        public Double getStart() {
            return start;
        }

        public void setStart(Double start) {
            this.start = start;
        }

        public Timer withStart(Double start) {
            this.start = start;
            return this;
        }

        public Double getPaused() {
            return paused;
        }

        public void setPaused(Double paused) {
            this.paused = paused;
        }

        public Timer withPaused(Double paused) {
            this.paused = paused;
            return this;
        }

        public Double getEnd() {
            return end;
        }

        public void setEnd(Double end) {
            this.end = end;
        }

        public Timer withEnd(Double end) {
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

    public static class SmartStart {

        @SerializedName("startuptemp")
        @Expose
        private Integer startupTemp;
        @SerializedName("profile_selected")
        @Expose
        private Integer profileSelected;

        public Integer getStartupTemp() {
            return startupTemp;
        }

        public void setStartupTemp(Integer startupTemp) {
            this.startupTemp = startupTemp;
        }

        public SmartStart withStartupTemp(Integer startupTemp) {
            this.startupTemp = startupTemp;
            return this;
        }

        public Integer getProfileSelected() {
            return profileSelected;
        }

        public void setProfileSelected(Integer profileSelected) {
            this.profileSelected = profileSelected;
        }

        public SmartStart withProfileSelected(Integer profileSelected) {
            this.profileSelected = profileSelected;
            return this;
        }
    }

    public static ControlDataModel parseJSON(String response) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .registerTypeAdapterFactory(new CustomTypeAdapterFactory())
                .create();
        return gson.fromJson(response, ControlDataModel.class);
    }
}