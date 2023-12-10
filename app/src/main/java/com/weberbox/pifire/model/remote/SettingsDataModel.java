package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeMap;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SettingsDataModel {

    @SerializedName("versions")
    @Expose
    private Versions versions;
    @SerializedName("probe_settings")
    @Expose
    private ProbeSettings probeSettings;
    @SerializedName("globals")
    @Expose
    private Globals globals;
    @SerializedName("notify_services")
    @Expose
    private NotifyServices notifyServices;
    @SerializedName("cycle_data")
    @Expose
    private CycleData cycleData;
    @SerializedName("controller")
    @Expose
    private Controller controller;
    @SerializedName("keep_warm")
    @Expose
    private KeepWarm keepWarm;
    @SerializedName("smoke_plus")
    @Expose
    private SmokePlus smokePlus;
    @SerializedName("safety")
    @Expose
    private Safety safety;
    @SerializedName("pelletlevel")
    @Expose
    private PelletLevel pelletLevel;
    @SerializedName("lastupdated")
    @Expose
    private LastUpdated lastUpdated;
    @SerializedName("modules")
    @Expose
    private Modules modules;
    @SerializedName("smartstart")
    @Expose
    private SmartStart smartStart;
    @SerializedName("pwm")
    @Expose
    private PWM pwm;
    @SerializedName("start_to_mode")
    @Expose
    private StartToMode startToMode;

    public Versions getVersions() {
        return versions;
    }

    public void setVersions(Versions versions) {
        this.versions = versions;
    }

    public SettingsDataModel withVersions(Versions versions) {
        this.versions = versions;
        return this;
    }

    public ProbeSettings getProbeSettings() {
        return probeSettings;
    }

    public void setProbeSettings(ProbeSettings probeSettings) {
        this.probeSettings = probeSettings;
    }

    public SettingsDataModel withProbeSettings(ProbeSettings probeSettings) {
        this.probeSettings = probeSettings;
        return this;
    }

    public Globals getGlobals() {
        return globals;
    }

    public void setGlobals(Globals globals) {
        this.globals = globals;
    }

    public SettingsDataModel withGlobals(Globals globals) {
        this.globals = globals;
        return this;
    }

    public NotifyServices getNotifyServices() {
        return notifyServices;
    }

    public void setNotifyServices(NotifyServices notifyServices) {
        this.notifyServices = notifyServices;
    }

    public SettingsDataModel withNotifyServices(NotifyServices notifyServices) {
        this.notifyServices = notifyServices;
        return this;
    }

    public CycleData getCycleData() {
        return cycleData;
    }

    public void setCycleData(CycleData cycleData) {
        this.cycleData = cycleData;
    }

    public SettingsDataModel withCycleData(CycleData cycleData) {
        this.cycleData = cycleData;
        return this;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public SettingsDataModel withController(Controller controller) {
        this.controller = controller;
        return this;
    }

    public KeepWarm getKeepWarm() {
        return keepWarm;
    }

    public void setKeepWarm(KeepWarm keepWarm) {
        this.keepWarm = keepWarm;
    }

    public SettingsDataModel withKeepWarm(KeepWarm keepWarm) {
        this.keepWarm = keepWarm;
        return this;
    }

    public SmokePlus getSmokePlus() {
        return smokePlus;
    }

    public void setSmokePlus(SmokePlus smokePlus) {
        this.smokePlus = smokePlus;
    }

    public SettingsDataModel withSmokePlus(SmokePlus smokePlus) {
        this.smokePlus = smokePlus;
        return this;
    }

    public Safety getSafety() {
        return safety;
    }

    public void setSafety(Safety safety) {
        this.safety = safety;
    }

    public SettingsDataModel withSafety(Safety safety) {
        this.safety = safety;
        return this;
    }

    public SmartStart getSmartStart() {
        return smartStart;
    }

    public void setSmartStart(SmartStart smartStart) {
        this.smartStart = smartStart;
    }

    public SettingsDataModel withSmartStart(SmartStart smartStart) {
        this.smartStart = smartStart;
        return this;
    }

    public PelletLevel getPellets() {
        return pelletLevel;
    }

    public void PelletLevel(PelletLevel pelletLevel) {
        this.pelletLevel = pelletLevel;
    }

    public SettingsDataModel withPelletLevel(PelletLevel pelletlevel) {
        this.pelletLevel = pelletlevel;
        return this;
    }

    public Modules getModules() {
        return modules;
    }

    public void Modules(Modules modules) {
        this.modules = modules;
    }

    public LastUpdated getLastUpdated() {
        return lastUpdated;
    }

    public void LastUpdated(LastUpdated lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public SettingsDataModel withLastUpdated(LastUpdated lastupdated) {
        this.lastUpdated = lastupdated;
        return this;
    }

    public PWM getPWM() {
        return pwm;
    }

    public void setPWM(PWM pwm) {
        this.pwm = pwm;
    }

    public SettingsDataModel withPWM(PWM pwm) {
        this.pwm = pwm;
        return this;
    }

    public StartToMode getStartToMode() {
        return startToMode;
    }

    public void setStartToMode(StartToMode startToMode) {
        this.startToMode = startToMode;
    }

    public SettingsDataModel withStartToMode(StartToMode startToMode) {
        this.startToMode = startToMode;
        return this;
    }

    public static class Versions {

        @SerializedName("server")
        @Expose
        private String server;
        @SerializedName("cookfile")
        @Expose
        private String cookFile;
        @SerializedName("recipe")
        @Expose
        private String recipe;
        @SerializedName("build")
        @Expose
        private String build;

        public String getServerVersion() {
            return server;
        }

        public void setServerVersion(String server) {
            this.server = server;
        }

        public String getCookFileVersion() {
            return cookFile;
        }

        public void setCookFileVersion(String cookFile) {
            this.cookFile = cookFile;
        }

        public String getRecipeVersion() {
            return recipe;
        }

        public void setRecipeVersion(String recipe) {
            this.recipe = recipe;
        }

        public String getServerBuild() {
            return build;
        }

        public void setServerBuild(String build) {
            this.build = build;
        }

    }

    public static class ProbeSettings {

        @SerializedName("probe_map")
        @Expose
        private ProbeMap probeMap;
        @SerializedName("probe_profiles")
        @Expose
        private Map<String, ProbeProfileModel> probeProfiles = new HashMap<>();

        public ProbeMap getProbeMap() {
            return probeMap;
        }

        public void setProbeMap(ProbeMap probeMap) {
            this.probeMap = probeMap;
        }

        public ProbeSettings withProbeMap(ProbeMap probeMap) {
            this.probeMap = probeMap;
            return this;
        }

        public Map<String, ProbeProfileModel> getProbeProfiles() {
            return probeProfiles;
        }

        public void setProbeProfiles(Map<String, ProbeProfileModel> probeProfiles) {
            this.probeProfiles = probeProfiles;
        }

        public ProbeSettings withProbeProfiles(Map<String, ProbeProfileModel> probeProfiles) {
            this.probeProfiles = probeProfiles;
            return this;
        }

    }

    public static class Globals {

        @SerializedName("grill_name")
        @Expose
        private String grillName;
        @SerializedName("debug_mode")
        @Expose
        private Boolean debugMode;
        @SerializedName("page_theme")
        @Expose
        private String pageTheme;
        @SerializedName("triggerlevel")
        @Expose
        private String triggerLevel;
        @SerializedName("buttonslevel")
        @Expose
        private String buttonsLevel;
        @SerializedName("disp_rotation")
        @Expose
        private Integer displayRotation;
        @SerializedName("prime_ignition")
        @Expose
        private Boolean primeIgnition;
        @SerializedName("shutdown_timer")
        @Expose
        private Integer shutdownTimer;
        @SerializedName("startup_timer")
        @Expose
        private Integer startUpTimer;
        @SerializedName("startup_exit_temp")
        @Expose
        private Integer startExitTemp;
        @SerializedName("auto_power_off")
        @Expose
        private Boolean autoPowerOff;
        @SerializedName("boot_to_monitor")
        @Expose
        private Boolean bootToMonitor;
        @SerializedName("dc_fan")
        @Expose
        private Boolean dcFan;
        @SerializedName("standalone")
        @Expose
        private Boolean standalone;
        @SerializedName("units")
        @Expose
        private String units;
        @SerializedName("augerrate")
        @Expose
        private Float augerRate;
        @SerializedName("first_time_setup")
        @Expose
        private Boolean firstTimeSetup;
        @SerializedName("ext_data")
        @Expose
        private Boolean extData;
        @SerializedName("global_control_panel")
        @Expose
        private Boolean globalControlPanel;
        @SerializedName("updated_message")
        @Expose
        private Boolean updatedMessage;
        @SerializedName("venv")
        @Expose
        private Boolean venv;
        @SerializedName("real_hw")
        @Expose
        private Boolean realHw;

        public String getGrillName() {
            return grillName;
        }

        public void setGrillName(String grillName) {
            this.grillName = grillName;
        }

        public Globals withGrillName(String grillName) {
            this.grillName = grillName;
            return this;
        }

        public Boolean getDebugMode() {
            return debugMode;
        }

        public void setDebugMode(Boolean debugMode) {
            this.debugMode = debugMode;
        }

        public Globals withDebugMode(Boolean debugMode) {
            this.debugMode = debugMode;
            return this;
        }

        public String getPageTheme() {
            return pageTheme;
        }

        public void setPageTheme(String pageTheme) {
            this.pageTheme = pageTheme;
        }

        public Globals withPageTheme(String pageTheme) {
            this.pageTheme = pageTheme;
            return this;
        }

        public String getTriggerLevel() {
            return triggerLevel;
        }

        public void setTriggerLevel(String triggerLevel) {
            this.triggerLevel = triggerLevel;
        }

        public Globals withTriggerLevel(String triggerLevel) {
            this.triggerLevel = triggerLevel;
            return this;
        }

        public String getButtonsLevel() {
            return buttonsLevel;
        }

        public void setButtonsLevel(String buttonsLevel) {
            this.buttonsLevel = buttonsLevel;
        }

        public Globals withButtonsLevel(String buttonsLevel) {
            this.buttonsLevel = buttonsLevel;
            return this;
        }

        public Boolean getPrimeIgnition() {
            return primeIgnition;
        }

        public void setPrimeIgnition(Boolean primeIgnition) {
            this.primeIgnition = primeIgnition;
        }

        public Globals withPrimeIgnition(Boolean primeIgnition) {
            this.primeIgnition = primeIgnition;
            return this;
        }

        public Integer getShutdownTimer() {
            return shutdownTimer;
        }

        public void setShutdownTimer(Integer shutdownTimer) {
            this.shutdownTimer = shutdownTimer;
        }

        public Globals withShutdownTimer(Integer shutdownTimer) {
            this.shutdownTimer = shutdownTimer;
            return this;
        }

        public Integer getStartUpTimer() {
            return startUpTimer;
        }

        public void setStartUpTimer(Integer startUpTimer) {
            this.startUpTimer = startUpTimer;
        }

        public Globals withStartupTimer(Integer startUpTimer) {
            this.startUpTimer = startUpTimer;
            return this;
        }

        public Integer getStartExitTemp() {
            return startExitTemp;
        }

        public void setStartExitTemp(Integer startExitTemp) {
            this.startExitTemp = startExitTemp;
        }

        public Globals withStartExitTemp(Integer startExitTemp) {
            this.startExitTemp = startExitTemp;
            return this;
        }

        public Boolean getAutoPowerOff() {
            return autoPowerOff;
        }

        public void setAutoPowerOff(Boolean powerOff) {
            this.autoPowerOff = powerOff;
        }

        public Globals withAutoPowerOff(Boolean powerOff) {
            this.autoPowerOff = powerOff;
            return this;
        }

        public Boolean getBootToMonitor() {
            return bootToMonitor;
        }

        public void setBootToMonitor(Boolean bootToMonitor) {
            this.bootToMonitor = bootToMonitor;
        }

        public Globals withBootToMonitor(Boolean bootToMonitor) {
            this.bootToMonitor = bootToMonitor;
            return this;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

        public Globals withUnits(String units) {
            this.units = units;
            return this;
        }

        public Float getAugerRate() {
            return augerRate;
        }

        public void setAugerRate(Float augerRate) {
            this.augerRate = augerRate;
        }

        public Globals withAugerRate(Float augerRate) {
            this.augerRate = augerRate;
            return this;
        }

        public Boolean getDCFan() {
            return dcFan;
        }

        public void setDCFan(Boolean dcFan) {
            this.dcFan = dcFan;
        }

        public Globals withDCFan(Boolean dcFan) {
            this.dcFan = dcFan;
            return this;
        }

        public Boolean getStandalone() {
            return standalone;
        }

        public void setStandalone(Boolean standalone) {
            this.standalone = standalone;
        }

        public Globals withStandalone(Boolean standalone) {
            this.standalone = standalone;
            return this;
        }

        public Boolean getFirstTimeSetup() {
            return firstTimeSetup;
        }

        public void setFirstTimeSetup(Boolean firstTimeSetup) {
            this.firstTimeSetup = firstTimeSetup;
        }

        public Globals withFirstTimeSetup(Boolean firstTimeSetup) {
            this.firstTimeSetup = firstTimeSetup;
            return this;
        }

        public Boolean getExtData() {
            return extData;
        }

        public void setExtData(Boolean extData) {
            this.extData = extData;
        }

        public Globals withExtData(Boolean extData) {
            this.extData = extData;
            return this;
        }

        public Boolean getGlobalControlPanel() {
            return globalControlPanel;
        }

        public void setGlobalControlPanel(Boolean globalControlPanel) {
            this.globalControlPanel = globalControlPanel;
        }

        public Globals withGlobalControlPanel(Boolean globalControlPanel) {
            this.globalControlPanel = globalControlPanel;
            return this;
        }

        public Boolean getUpdatedMessage() {
            return updatedMessage;
        }

        public void setUpdatedMessage(Boolean updatedMessage) {
            this.updatedMessage = updatedMessage;
        }

        public Globals withUpdatedMessage(Boolean updatedMessage) {
            this.updatedMessage = updatedMessage;
            return this;
        }

        public Boolean getVenv() {
            return venv;
        }

        public void setVenv(Boolean venv) {
            this.venv = venv;
        }

        public Globals withVenv(Boolean venv) {
            this.venv = venv;
            return this;
        }

        public Boolean getRealHw() {
            return realHw;
        }

        public void setRealHw(Boolean realHw) {
            this.realHw = realHw;
        }

        public Globals withRealHw(Boolean realHw) {
            this.realHw = realHw;
            return this;
        }

    }

    public static class NotifyServices {

        @SerializedName("apprise")
        @Expose
        private Apprise apprise;
        @SerializedName("ifttt")
        @Expose
        private Ifttt ifttt;
        @SerializedName("pushbullet")
        @Expose
        private PushBullet pushbullet;
        @SerializedName("pushover")
        @Expose
        private Pushover pushover;
        @SerializedName("onesignal")
        @Expose
        private OneSignalPush onesignal;
        @SerializedName("influxdb")
        @Expose
        private InfluxDB influxDB;

        public Apprise getApprise() {
            return apprise;
        }

        public void setApprise(Apprise apprise) {
            this.apprise = apprise;
        }

        public NotifyServices withApprise(Apprise apprise) {
            this.apprise = apprise;
            return this;
        }

        public Ifttt getIfttt() {
            return ifttt;
        }

        public void setIfttt(Ifttt ifttt) {
            this.ifttt = ifttt;
        }

        public NotifyServices withIfttt(Ifttt ifttt) {
            this.ifttt = ifttt;
            return this;
        }

        public PushBullet getPushBullet() {
            return pushbullet;
        }

        public void setPushBullet(PushBullet pushbullet) {
            this.pushbullet = pushbullet;
        }

        public NotifyServices withPushBullet(PushBullet pushbullet) {
            this.pushbullet = pushbullet;
            return this;
        }

        public OneSignalPush getOneSignal() {
            return onesignal;
        }

        public void setOneSignal(OneSignalPush onesignal) {
            this.onesignal = onesignal;
        }

        public NotifyServices withOneSignal(OneSignalPush onesignal) {
            this.onesignal = onesignal;
            return this;
        }

        public Pushover getPushover() {
            return pushover;
        }

        public void setPushover(Pushover pushover) {
            this.pushover = pushover;
        }

        public NotifyServices withPushover(Pushover pushover) {
            this.pushover = pushover;
            return this;
        }

        public InfluxDB getInfluxDB() {
            return influxDB;
        }

        public void setInfluxDB(InfluxDB influxDB) {
            this.influxDB = influxDB;
        }

        public NotifyServices withInfluxdb(InfluxDB influxdb) {
            this.influxDB = influxdb;
            return this;
        }

    }

    public static class CycleData {

        @SerializedName("HoldCycleTime")
        @Expose
        private Integer holdCycleTime;
        @SerializedName("SmokeOnCycleTime")
        @Expose
        private Integer smokeOnCycleTime;
        @SerializedName("SmokeOffCycleTime")
        @Expose
        private Integer smokeOffCycleTime;
        @SerializedName("PMode")
        @Expose
        private Integer pMode;
        @SerializedName("u_min")
        @Expose
        private Float uMin;
        @SerializedName("u_max")
        @Expose
        private Float uMax;
        @SerializedName("LidOpenDetectEnabled")
        @Expose
        private Boolean lidOpenDetectEnabled;
        @SerializedName("LidOpenThreshold")
        @Expose
        private Integer lidOpenThreshold;
        @SerializedName("LidOpenPauseTime")
        @Expose
        private Integer lidOpenPauseTime;

        public Integer getHoldCycleTime() {
            return holdCycleTime;
        }

        public void setHoldCycleTime(Integer holdCycleTime) {
            this.holdCycleTime = holdCycleTime;
        }

        public CycleData withHoldCycleTime(Integer holdCycleTime) {
            this.holdCycleTime = holdCycleTime;
            return this;
        }

        public Integer getSmokeOnCycleTime() {
            return smokeOnCycleTime;
        }

        public void setSmokeOnCycleTime(Integer smokeOnCycleTime) {
            this.smokeOnCycleTime = smokeOnCycleTime;
        }

        public CycleData withSmokeOnCycleTime(Integer smokeOnCycleTime) {
            this.smokeOnCycleTime = smokeOnCycleTime;
            return this;
        }

        public Integer getSmokeOffCycleTime() {
            return smokeOffCycleTime;
        }

        public void setSmokeOffCycleTime(Integer smokeOffCycleTime) {
            this.smokeOffCycleTime = smokeOffCycleTime;
        }

        public CycleData withSmokeOffCycleTime(Integer smokeOffCycleTime) {
            this.smokeOffCycleTime = smokeOffCycleTime;
            return this;
        }

        public Integer getPMode() {
            return pMode;
        }

        public void setPMode(Integer pMode) {
            this.pMode = pMode;
        }

        public CycleData withPMode(Integer pMode) {
            this.pMode = pMode;
            return this;
        }

        public Float getuMin() {
            return uMin;
        }

        public void setuMin(Float uMin) {
            this.uMin = uMin;
        }

        public CycleData withuMin(Float uMin) {
            this.uMin = uMin;
            return this;
        }

        public Float getuMax() {
            return uMax;
        }

        public void setuMax(Float uMax) {
            this.uMax = uMax;
        }

        public CycleData withuMax(Float uMax) {
            this.uMax = uMax;
            return this;
        }

        public Boolean getLidOpenDetectEnabled() {
            return lidOpenDetectEnabled;
        }

        public void setLidOpenDetectEnabled(Boolean lidOpenDetectEnabled) {
            this.lidOpenDetectEnabled = lidOpenDetectEnabled;
        }

        public CycleData withLidOpenDetectEnabled(Boolean lidOpenDetectEnabled) {
            this.lidOpenDetectEnabled = lidOpenDetectEnabled;
            return this;
        }

        public Integer getLidOpenThreshold() {
            return lidOpenThreshold;
        }

        public void setLidOpenThreshold(Integer lidOpenThreshold) {
            this.lidOpenThreshold = lidOpenThreshold;
        }

        public CycleData withLidOpenThreshold(Integer lidOpenThreshold) {
            this.lidOpenThreshold = lidOpenThreshold;
            return this;
        }

        public Integer getLidOpenPauseTime() {
            return lidOpenPauseTime;
        }

        public void setLidOpenPauseTime(Integer lidOpenPauseTime) {
            this.lidOpenPauseTime = lidOpenPauseTime;
        }

        public CycleData withLidOpenPauseTime(Integer lidOpenPauseTime) {
            this.lidOpenPauseTime = lidOpenPauseTime;
            return this;
        }

    }

    public static class Controller {
        @SerializedName("selected")
        @Expose
        private String selected;

        public String getController() {
            return selected;
        }

        public void setController(String selected) {
            this.selected = selected;
        }

        public Controller withController(String selected) {
            this.selected = selected;
            return this;
        }
    }

    public static class KeepWarm {

        @SerializedName("temp")
        @Expose
        private Integer temp;
        @SerializedName("s_plus")
        @Expose
        private Boolean sPlus;

        public Integer getTemp() {
            return temp;
        }

        public void setTemp(Integer temp) {
            this.temp = temp;
        }

        public KeepWarm withTemp(Integer temp) {
            this.temp = temp;
            return this;
        }

        public Boolean getSPlus() {
            return sPlus;
        }

        public void setSPlus(Boolean sPlus) {
            this.sPlus = sPlus;
        }

        public KeepWarm withSPlus(Boolean sPlus) {
            this.sPlus = sPlus;
            return this;
        }
    }

    public static class SmokePlus {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("min_temp")
        @Expose
        private Integer minTemp;
        @SerializedName("max_temp")
        @Expose
        private Integer maxTemp;
        @SerializedName("cycle")
        @Expose
        private Integer cycle;
        @SerializedName("on_time")
        @Expose
        private Integer onTime;
        @SerializedName("off_time")
        @Expose
        private Integer offTime;
        @SerializedName("frequency")
        @Expose
        private Integer frequency;
        @SerializedName("duty_cycle")
        @Expose
        private Integer dutyCycle;
        @SerializedName("fan_ramp")
        @Expose
        private Boolean fanRamp;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public SmokePlus withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Integer getMinTemp() {
            return minTemp;
        }

        public void setMinTemp(Integer minTemp) {
            this.minTemp = minTemp;
        }

        public SmokePlus withMinTemp(Integer minTemp) {
            this.minTemp = minTemp;
            return this;
        }

        public Integer getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(Integer maxTemp) {
            this.maxTemp = maxTemp;
        }

        public SmokePlus withMaxTemp(Integer maxTemp) {
            this.maxTemp = maxTemp;
            return this;
        }

        public Integer getCycle() {
            return cycle;
        }

        public void setCycle(Integer cycle) {
            this.cycle = cycle;
        }

        public SmokePlus withCycle(Integer cycle) {
            this.cycle = cycle;
            return this;
        }

        public Integer getOnTime() {
            return onTime;
        }

        public void setOnTime(Integer onTime) {
            this.onTime = onTime;
        }

        public SmokePlus withOnTime(Integer onTime) {
            this.onTime = onTime;
            return this;
        }

        public Integer getOffTime() {
            return offTime;
        }

        public void setOffTime(Integer offTime) {
            this.offTime = offTime;
        }

        public SmokePlus withOffTime(Integer offTime) {
            this.offTime = offTime;
            return this;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }

        public SmokePlus withFrequency(Integer frequency) {
            this.frequency = frequency;
            return this;
        }

        public Integer getDutyCycle() {
            return dutyCycle;
        }

        public void setDutyCycle(Integer dutyCycle) {
            this.dutyCycle = dutyCycle;
        }

        public SmokePlus withDutyCycle(Integer dutyCycle) {
            this.dutyCycle = dutyCycle;
            return this;
        }

        public Boolean getFanRamp() {
            return fanRamp;
        }

        public void setFanRamp(Boolean enabled) {
            this.fanRamp = enabled;
        }

        public SmokePlus withFanRamp(Boolean enabled) {
            this.fanRamp = enabled;
            return this;
        }

    }

    public static class Safety {

        @SerializedName("minstartuptemp")
        @Expose
        private Integer minStartupTemp;
        @SerializedName("maxstartuptemp")
        @Expose
        private Integer maxStartupTemp;
        @SerializedName("maxtemp")
        @Expose
        private Integer maxTemp;
        @SerializedName("reigniteretries")
        @Expose
        private Integer reigniteRetries;

        public Integer getMinStartupTemp() {
            return minStartupTemp;
        }

        public void setMinStartupTemp(Integer minStartupTemp) {
            this.minStartupTemp = minStartupTemp;
        }

        public Safety withMinStartupTemp(Integer minStartupTemp) {
            this.minStartupTemp = minStartupTemp;
            return this;
        }

        public Integer getMaxStartupTemp() {
            return maxStartupTemp;
        }

        public void setMaxStartupTemp(Integer maxStartupTemp) {
            this.maxStartupTemp = maxStartupTemp;
        }

        public Safety withMaxStartupTemp(Integer maxStartupTemp) {
            this.maxStartupTemp = maxStartupTemp;
            return this;
        }

        public Integer getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(Integer maxTemp) {
            this.maxTemp = maxTemp;
        }

        public Safety withMaxTemp(Integer maxTemp) {
            this.maxTemp = maxTemp;
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

    }

    public static class PelletLevel {

        @SerializedName("warning_enabled")
        @Expose
        private Boolean warningEnabled;
        @SerializedName("warning_level")
        @Expose
        private Integer warningLevel;
        @SerializedName("warning_time")
        @Expose
        private Integer warningTime;
        @SerializedName("empty")
        @Expose
        private Integer empty;
        @SerializedName("full")
        @Expose
        private Integer full;

        public Boolean getWarningEnabled() {
            return warningEnabled;
        }

        public void setWarningEnabled(Boolean enabled) {
            this.warningEnabled = enabled;
        }

        public PelletLevel withWarningEnabled(Boolean warningEnabled) {
            this.warningEnabled = warningEnabled;
            return this;
        }

        public Integer getWarningLevel() {
            return warningLevel;
        }

        public void setWarningLevel(Integer level) {
            this.warningLevel = level;
        }

        public PelletLevel withWarningLevel(Integer warningLevel) {
            this.warningLevel = warningLevel;
            return this;
        }

        public Integer getWarningTime() {
            return warningTime;
        }

        public void setWarningTime(Integer time) {
            this.warningTime = time;
        }

        public PelletLevel withWarningTime(Integer time) {
            this.warningTime = time;
            return this;
        }

        public Integer getEmpty() {
            return empty;
        }

        public void setEmpty(Integer empty) {
            this.empty = empty;
        }

        public PelletLevel withEmpty(Integer empty) {
            this.empty = empty;
            return this;
        }

        public Integer getFull() {
            return full;
        }

        public void setFull(Integer full) {
            this.full = full;
        }

        public PelletLevel withFull(Integer full) {
            this.full = full;
            return this;
        }

    }

    public static class LastUpdated {

        @SerializedName("time")
        @Expose
        private Integer time;

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }

        public LastUpdated withTime(Integer time) {
            this.time = time;
            return this;
        }

    }

    public static class Modules {

        @SerializedName("display")
        @Expose
        private String display;
        @SerializedName("dist")
        @Expose
        private String dist;
        @SerializedName("grillplat")
        @Expose
        private String grillplat;

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public Modules withDisplay(String display) {
            this.display = display;
            return this;
        }

        public String getDist() {
            return dist;
        }

        public void setDist(String dist) {
            this.dist = dist;
        }

        public Modules withDistance(String distance) {
            this.dist = distance;
            return this;
        }

        public String getGrillPlat() {
            return grillplat;
        }

        public void setGrillPlat(String grillplat) {
            this.grillplat = grillplat;
        }

        public Modules withPlatform(String platform) {
            this.grillplat = platform;
            return this;
        }

    }

    public static class SmartStart {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("exit_temp")
        @Expose
        private Integer exitTemp;
        @SerializedName("profiles")
        @Expose
        private List<SSProfile> profiles = null;
        @SerializedName("temp_range_list")
        @Expose
        private List<Integer> tempRangeList = null;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public SmartStart withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Integer getExitTemp() {
            return exitTemp;
        }

        public void setExitTemp(Integer exitTemp) {
            this.exitTemp = exitTemp;
        }

        public SmartStart withExitTemp(Integer exitTemp) {
            this.exitTemp = exitTemp;
            return this;
        }

        public List<SSProfile> getProfiles() {
            return profiles;
        }

        public void setProfiles(List<SSProfile> profiles) {
            this.profiles = profiles;
        }

        public SmartStart withProfiles(List<SSProfile> profiles) {
            this.profiles = profiles;
            return this;
        }

        public List<Integer> getTempRangeList() {
            return tempRangeList;
        }

        public void setTempRangeList(List<Integer> tempRangeList) {
            this.tempRangeList = tempRangeList;
        }

        public SmartStart withTempRangeList(List<Integer> tempRangeList) {
            this.tempRangeList = tempRangeList;
            return this;
        }

    }

    public static class PWM {

        @SerializedName("pwm_control")
        @Expose
        private Boolean pwmControl;
        @SerializedName("update_time")
        @Expose
        private Integer updateTime;
        @SerializedName("frequency")
        @Expose
        private Integer frequency;
        @SerializedName("min_duty_cycle")
        @Expose
        private Integer minDutyCycle;
        @SerializedName("max_duty_cycle")
        @Expose
        private Integer maxDutyCycle;
        @SerializedName("temp_range_list")
        @Expose
        private List<Integer> tempRangeList = null;
        @SerializedName("profiles")
        @Expose
        private List<PWMProfile> profiles = null;

        public Boolean getPWMControl() {
            return pwmControl;
        }

        public void setPWMControl(Boolean enabled) {
            this.pwmControl = enabled;
        }

        public PWM withPWMControl(Boolean enabled) {
            this.pwmControl = enabled;
            return this;
        }

        public Integer getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Integer updateTime) {
            this.updateTime = updateTime;
        }

        public PWM withUpdateTime(Integer updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }

        public PWM withFrequency(Integer frequency) {
            this.frequency = frequency;
            return this;
        }

        public Integer getMinDutyCycle() {
            return minDutyCycle;
        }

        public void setMinDutyCycle(Integer minDutyCycle) {
            this.minDutyCycle = minDutyCycle;
        }

        public PWM withMinDutyCycle(Integer minDutyCycle) {
            this.minDutyCycle = minDutyCycle;
            return this;
        }

        public Integer getMaxDutyCycle() {
            return maxDutyCycle;
        }

        public void setMaxDutyCycle(Integer maxDutyCycle) {
            this.maxDutyCycle = maxDutyCycle;
        }

        public PWM withMaxDutyCycle(Integer maxDutyCycle) {
            this.maxDutyCycle = maxDutyCycle;
            return this;
        }

        public List<PWMProfile> getProfiles() {
            return profiles;
        }

        public void setProfiles(List<PWMProfile> profiles) {
            this.profiles = profiles;
        }

        public PWM withProfiles(List<PWMProfile> profiles) {
            this.profiles = profiles;
            return this;
        }

        public List<Integer> getTempRangeList() {
            return tempRangeList;
        }

        public void setTempRangeList(List<Integer> tempRangeList) {
            this.tempRangeList = tempRangeList;
        }

        public PWM withTempRangeList(List<Integer> tempRangeList) {
            this.tempRangeList = tempRangeList;
            return this;
        }

    }

    public static class StartToMode {

        @SerializedName("after_startup_mode")
        @Expose
        private String afterStartUpMode;
        @SerializedName("primary_setpoint")
        @Expose
        private Integer primarySetPoint;

        public String getAfterStartUpMode() {
            return afterStartUpMode;
        }

        public void setAfterStartUpMode(String afterStartUpMode) {
            this.afterStartUpMode = afterStartUpMode;
        }

        public StartToMode withAfterStartUpMode(String afterStartUpMode) {
            this.afterStartUpMode = afterStartUpMode;
            return this;
        }

        public Integer getPrimarySetPoint() {
            return primarySetPoint;
        }

        public void setPrimarySetPoint(Integer primarySetPoint) {
            this.primarySetPoint = primarySetPoint;
        }

        public StartToMode withPrimarySetPoint(Integer primarySetPoint) {
            this.primarySetPoint = primarySetPoint;
            return this;
        }

    }

    // Notify Services Helpers

    public static class Pushover {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("APIKey")
        @Expose
        private String aPIKey;
        @SerializedName("UserKeys")
        @Expose
        private String userKeys;
        @SerializedName("PublicURL")
        @Expose
        private String publicURL;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Pushover withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getAPIKey() {
            return aPIKey;
        }

        public void setAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
        }

        public Pushover withAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
            return this;
        }

        public String getUserKeys() {
            return userKeys;
        }

        public void setUserKeys(String userKeys) {
            this.userKeys = userKeys;
        }

        public Pushover withUserKeys(String userKeys) {
            this.userKeys = userKeys;
            return this;
        }

        public String getPublicURL() {
            return publicURL;
        }

        public void setPublicURL(String publicURL) {
            this.publicURL = publicURL;
        }

        public Pushover withPublicURL(String publicURL) {
            this.publicURL = publicURL;
            return this;
        }

    }

    public static class PushBullet {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("APIKey")
        @Expose
        private String aPIKey;
        @SerializedName("PublicURL")
        @Expose
        private String publicURL;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public PushBullet withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getAPIKey() {
            return aPIKey;
        }

        public void setAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
        }

        public PushBullet withAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
            return this;
        }

        public String getPublicURL() {
            return publicURL;
        }

        public void setPublicURL(String publicURL) {
            this.publicURL = publicURL;
        }

        public PushBullet withPublicURL(String publicURL) {
            this.publicURL = publicURL;
            return this;
        }

    }

    public static class OneSignalPush {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("uuid")
        @Expose
        private String uuid;
        @SerializedName("app_id")
        @Expose
        private String appID;
        @SerializedName("devices")
        @Expose
        private Map<String, OneSignalDeviceInfo> devices = new HashMap<>();

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public OneSignalPush withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getServerUUID() {
            return uuid;
        }

        public void setServerUUID(String uuid) {
            this.uuid = uuid;
        }

        public OneSignalPush withUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public String getAppID() {
            return appID;
        }

        public void setAppID(String appID) {
            this.appID = appID;
        }

        public OneSignalPush withAppId(String appID) {
            this.appID = appID;
            return this;
        }

        public Map<String, OneSignalDeviceInfo> getOneSignalDevices() {
            return devices;
        }

        public void setOneSignalDevices(Map<String, OneSignalDeviceInfo> devices) {
            this.devices = devices;
        }

        public OneSignalPush withOneSignalDevices(Map<String, OneSignalDeviceInfo> devices) {
            this.devices = devices;
            return this;
        }

        public static OneSignalPush parseJSON(String response) {
            return new Gson().fromJson(response, OneSignalPush.class);
        }
    }

    public static class OneSignalDeviceInfo {

        @SerializedName("device_name")
        @Expose
        private String deviceName;
        @SerializedName("friendly_name")
        @Expose
        private String friendlyName = "";
        @SerializedName("app_version")
        @Expose
        private String appVersion;

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public OneSignalDeviceInfo withDeviceName(String deviceName) {
            this.deviceName = deviceName;
            return this;
        }

        public String getFriendlyName() {
            return friendlyName;
        }

        public void setFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        public OneSignalDeviceInfo withFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
            return this;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public OneSignalDeviceInfo withAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

    }

    public static class InfluxDB {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("token")
        @Expose
        private String token;
        @SerializedName("org")
        @Expose
        private String org;
        @SerializedName("bucket")
        @Expose
        private String bucket;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public InfluxDB withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public InfluxDB withUrl(String url) {
            this.url = url;
            return this;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public InfluxDB withToken(String token) {
            this.token = token;
            return this;
        }

        public String getOrg() {
            return org;
        }

        public void setOrg(String org) {
            this.org = org;
        }

        public InfluxDB withOrg(String org) {
            this.org = org;
            return this;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }

        public InfluxDB withBucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

    }

    public static class Apprise {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("locations")
        @Expose
        private List<String> locations = null;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Apprise withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public List<String> getLocations() {
            return locations;
        }

        public void setLocations(List<String> locations) {
            this.locations = locations;
        }

        public Apprise withLocations(List<String> locations) {
            this.locations = locations;
            return this;
        }

    }

    public static class Ifttt {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("APIKey")
        @Expose
        private String aPIKey;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Ifttt withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getAPIKey() {
            return aPIKey;
        }

        public void setAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
        }

        public Ifttt withAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
            return this;
        }

    }

    // Smart Start Helpers

    public static class SSProfile {

        @SerializedName("augerontime")
        @Expose
        private Integer augerOnTime;
        @SerializedName("p_mode")
        @Expose
        private Integer pMode;
        @SerializedName("startuptime")
        @Expose
        private Integer startUpTime;

        public Integer getAugerOnTime() {
            return augerOnTime;
        }

        public void setAugerOnTime(Integer augerOnTime) {
            this.augerOnTime = augerOnTime;
        }

        public SSProfile withAugerOnTime(Integer augerOnTime) {
            this.augerOnTime = augerOnTime;
            return this;
        }

        public Integer getPMode() {
            return pMode;
        }

        public void setPMode(Integer pMode) {
            this.pMode = pMode;
        }

        public SSProfile withPMode(Integer pMode) {
            this.pMode = pMode;
            return this;
        }

        public Integer getStartUpTime() {
            return startUpTime;
        }

        public void setStartUpTime(Integer startUpTime) {
            this.startUpTime = startUpTime;
        }

        public SSProfile withStartUpTime(Integer startUpTime) {
            this.startUpTime = startUpTime;
            return this;
        }

    }

    // PWM Helpers

    public static class PWMProfile {

        @SerializedName("duty_cycle")
        @Expose
        private Integer dutyCycle;

        public Integer getDutyCycle() {
            return dutyCycle;
        }

        public void setDutyCycle(Integer dutyCycle) {
            this.dutyCycle = dutyCycle;
        }

        public PWMProfile withDutyCycle(Integer dutyCycle) {
            this.dutyCycle = dutyCycle;
            return this;
        }

    }

    public static SettingsDataModel parseJSON(String response) {
        return new Gson().fromJson(response, SettingsDataModel.class);
    }
}
