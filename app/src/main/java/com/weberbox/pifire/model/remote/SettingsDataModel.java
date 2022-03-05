package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SettingsDataModel {

    @SerializedName("versions")
    @Expose
    private Versions versions;
    @SerializedName("history_page")
    @Expose
    private HistoryPage historyPage;
    @SerializedName("probe_settings")
    @Expose
    private ProbeSettings probeSettings;
    @SerializedName("globals")
    @Expose
    private Globals globals;
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
    @SerializedName("probe_types")
    @Expose
    private ProbeTypes probeTypes;
    @SerializedName("grill_probe_settings")
    @Expose
    private GrillProbeSettings grillProbeSettings;
    @SerializedName("cycle_data")
    @Expose
    private CycleData cycleData;
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
    @SerializedName("inpins")
    @Expose
    private InPins inPins;
    @SerializedName("outpins")
    @Expose
    private OutPins outPins;
    @SerializedName("modules")
    @Expose
    private Modules modules;

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

    public HistoryPage getHistoryPage() {
        return historyPage;
    }

    public void setHistoryPage(HistoryPage historyPage) {
        this.historyPage = historyPage;
    }

    public SettingsDataModel withHistoryPage(HistoryPage historyPage) {
        this.historyPage = historyPage;
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

    public Ifttt getIfttt() {
        return ifttt;
    }

    public void setIfttt(Ifttt ifttt) {
        this.ifttt = ifttt;
    }

    public SettingsDataModel withIfttt(Ifttt ifttt) {
        this.ifttt = ifttt;
        return this;
    }

    public PushBullet getPushBullet() {
        return pushbullet;
    }

    public void setPushBullet(PushBullet pushbullet) {
        this.pushbullet = pushbullet;
    }

    public SettingsDataModel withPushBullet(PushBullet pushbullet) {
        this.pushbullet = pushbullet;
        return this;
    }

    public OneSignalPush getOneSignal() {
        return onesignal;
    }

    public void setOneSignal(OneSignalPush onesignal) {
        this.onesignal = onesignal;
    }

    public SettingsDataModel withOneSignal(OneSignalPush onesignal) {
        this.onesignal = onesignal;
        return this;
    }

    public Pushover getPushover() {
        return pushover;
    }

    public void setPushover(Pushover pushover) {
        this.pushover = pushover;
    }

    public SettingsDataModel withPushover(Pushover pushover) {
        this.pushover = pushover;
        return this;
    }

    public InfluxDB getInfluxDB() {
        return influxDB;
    }

    public void setInfluxDB(InfluxDB influxDB) {
        this.influxDB = influxDB;
    }

    public SettingsDataModel withInfluxdb(InfluxDB influxdb) {
        this.influxDB = influxdb;
        return this;
    }

    public ProbeTypes getProbeTypes() {
        return probeTypes;
    }

    public void setProbeTypes(ProbeTypes probeTypes) {
        this.probeTypes = probeTypes;
    }

    public SettingsDataModel withProbeTypes(ProbeTypes probeTypes) {
        this.probeTypes = probeTypes;
        return this;
    }

    public GrillProbeSettings getGrillProbeSettings() {
        return grillProbeSettings;
    }

    public void setGrillProbeSettings(GrillProbeSettings grillProbeSettings) {
        this.grillProbeSettings = grillProbeSettings;
    }

    public SettingsDataModel withGrillProbeSettings(GrillProbeSettings grillProbeSettings) {
        this.grillProbeSettings = grillProbeSettings;
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

    public InPins getInPins() {
        return inPins;
    }

    public void InPins(InPins inPins) {
        this.inPins = inPins;
    }

    public OutPins getOutPins() {
        return outPins;
    }

    public void OutPins(OutPins outPins) {
        this.outPins = outPins;
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

    public static class CycleData {

        @SerializedName("PB")
        @Expose
        private Float pb;
        @SerializedName("Ti")
        @Expose
        private Float ti;
        @SerializedName("Td")
        @Expose
        private Float td;
        @SerializedName("HoldCycleTime")
        @Expose
        private Integer holdCycleTime;
        @SerializedName("SmokeCycleTime")
        @Expose
        private Integer smokeCycleTime;
        @SerializedName("PMode")
        @Expose
        private Integer pMode;
        @SerializedName("u_min")
        @Expose
        private Float uMin;
        @SerializedName("u_max")
        @Expose
        private Float uMax;
        @SerializedName("center")
        @Expose
        private Float center;

        public Float getPb() {
            return pb;
        }

        public void setPb(Float pb) {
            this.pb = pb;
        }

        public CycleData withPb(Float pb) {
            this.pb = pb;
            return this;
        }

        public Float getTi() {
            return ti;
        }

        public void setTi(Float ti) {
            this.ti = ti;
        }

        public CycleData withTi(Float ti) {
            this.ti = ti;
            return this;
        }

        public Float getTd() {
            return td;
        }

        public void setTd(Float td) {
            this.td = td;
        }

        public CycleData withTd(Float td) {
            this.td = td;
            return this;
        }

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

        public Integer getSmokeCycleTime() {
            return smokeCycleTime;
        }

        public void setSmokeCycleTime(Integer smokeCycleTime) {
            this.smokeCycleTime = smokeCycleTime;
        }

        public CycleData withSmokeCycleTime(Integer smokeCycleTime) {
            this.smokeCycleTime = smokeCycleTime;
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

        public Float getCenter() {
            return center;
        }

        public void setCenter(Float center) {
            this.center = center;
        }

        public CycleData withCenter(Float center) {
            this.center = center;
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

    public static class Versions {

        @SerializedName("server")
        @Expose
        private String server;

        public String getServerVersion() {
            return server;
        }

        public void setServerVersion(String server) {
            this.server = server;
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
        @SerializedName("shutdown_timer")
        @Expose
        private Integer shutdownTimer;
        @SerializedName("startup_timer")
        @Expose
        private Integer startUpTimer;
        @SerializedName("auto_power_off")
        @Expose
        private Boolean autoPowerOff;
        @SerializedName("four_probes")
        @Expose
        private Boolean fourProbes;
        @SerializedName("units")
        @Expose
        private String units;
        @SerializedName("augerrate")
        @Expose
        private Double augerRate;

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

        public Boolean getFourProbes() {
            return fourProbes;
        }

        public void setFourProbes(Boolean fourProbes) {
            this.fourProbes = fourProbes;
        }

        public Globals withFourProbes(Boolean fourProbes) {
            this.fourProbes = fourProbes;
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

        public Double getAugerRate() {
            return augerRate;
        }

        public void setAugerRate(Double augerRate) {
            this.augerRate = augerRate;
        }

        public Globals withAugerRate(Double augerRate) {
            this.augerRate = augerRate;
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

    public static class ProbeTypes {

        @SerializedName("grill0type")
        @Expose
        private String grill0type;
        @SerializedName("grill1type")
        @Expose
        private String grill1type;
        @SerializedName("grill2type")
        @Expose
        private String grill2type;
        @SerializedName("probe1type")
        @Expose
        private String probe1type;
        @SerializedName("probe2type")
        @Expose
        private String probe2type;

        public String getGrill0type() {
            return grill0type;
        }

        public void setGrill0type(String grill0type) {
            this.grill0type = grill0type;
        }

        public ProbeTypes withGrill0type(String grill0type) {
            this.grill0type = grill0type;
            return this;
        }

        public String getGrill1type() {
            return grill1type;
        }

        public void setGrill1type(String grill1type) {
            this.grill1type = grill1type;
        }

        public ProbeTypes withGrill1type(String grill1type) {
            this.grill1type = grill1type;
            return this;
        }

        public String getGrill2type() {
            return grill2type;
        }

        public void setGrill2type(String grill2type) {
            this.grill2type = grill2type;
        }

        public ProbeTypes withGrill2type(String grill2type) {
            this.grill2type = grill2type;
            return this;
        }

        public String getProbe1type() {
            return probe1type;
        }

        public void setProbe1type(String probe1type) {
            this.probe1type = probe1type;
        }

        public ProbeTypes withProbe1type(String probe1type) {
            this.probe1type = probe1type;
            return this;
        }

        public String getProbe2type() {
            return probe2type;
        }

        public void setProbe2type(String probe2type) {
            this.probe2type = probe2type;
        }

        public ProbeTypes withProbe2type(String probe2type) {
            this.probe2type = probe2type;
            return this;
        }

    }

    public static class ProbeSettings {

        @SerializedName("probes_enabled")
        @Expose
        private List<Integer> probesEnabled = null;
        @SerializedName("probe_profiles")
        @Expose
        private Map<String, ProbeProfileModel> probeProfiles = new HashMap<>();
        @SerializedName("probe_sources")
        @Expose
        private List<String> probeSources = null;

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

        public List<Integer> getProbesEnabled() {
            return probesEnabled;
        }

        public void setProbesEnabled(List<Integer> probesEnabled) {
            this.probesEnabled = probesEnabled;
        }

        public ProbeSettings withProbesEnabled(List<Integer> probesEnabled) {
            this.probesEnabled = probesEnabled;
            return this;
        }

        public List<String> getProbeSources() {
            return probeSources;
        }

        public void setProbeSources(List<String> probeSources) {
            this.probeSources = probeSources;
        }

        public ProbeSettings withProbeSources(List<String> probeSources) {
            this.probeSources = probeSources;
            return this;
        }

    }

    public static class HistoryPage {

        @SerializedName("minutes")
        @Expose
        private Integer minutes;
        @SerializedName("clearhistoryonstart")
        @Expose
        private Boolean clearHistoryOnStart;
        @SerializedName("autorefresh")
        @Expose
        private String autoRefresh;
        @SerializedName("datapoints")
        @Expose
        private Integer dataPoints;

        public Integer getMinutes() {
            return minutes;
        }

        public void setMinutes(Integer minutes) {
            this.minutes = minutes;
        }

        public HistoryPage withMinutes(Integer minutes) {
            this.minutes = minutes;
            return this;
        }

        public Boolean getClearHistoryOnStart() {
            return clearHistoryOnStart;
        }

        public void setClearHistoryOnStart(Boolean clearHistoryOnStart) {
            this.clearHistoryOnStart = clearHistoryOnStart;
        }

        public HistoryPage withClearHistoryOnStart(Boolean clearHistoryOnStart) {
            this.clearHistoryOnStart = clearHistoryOnStart;
            return this;
        }

        public String getAutoRefresh() {
            return autoRefresh;
        }

        public void setAutoRefresh(String autoRefresh) {
            this.autoRefresh = autoRefresh;
        }

        public HistoryPage withAutoRefresh(String autoRefresh) {
            this.autoRefresh = autoRefresh;
            return this;
        }

        public Integer getDataPoints() {
            return dataPoints;
        }

        public void setDataPoints(Integer dataPoints) {
            this.dataPoints = dataPoints;
        }

        public HistoryPage withDataPoints(Integer dataPoints) {
            this.dataPoints = dataPoints;
            return this;
        }

    }

    public static class GrillProbeSettings {

        @SerializedName("grill_probes")
        @Expose
        private Map<String, GrillProbeModel> grillProbes = new HashMap<>();
        @SerializedName("grill_probe")
        @Expose
        private String grillProbe;
        @SerializedName("grill_probe_enabled")
        @Expose
        private List<Integer> grillProbeEnabled = null;

        public Map<String, GrillProbeModel> getGrillProbes() {
            return grillProbes;
        }

        public void setGrillProbes(Map<String, GrillProbeModel> grillProbes) {
            this.grillProbes = grillProbes;
        }

        public GrillProbeSettings withGrillProbes(Map<String, GrillProbeModel> grillProbes) {
            this.grillProbes = grillProbes;
            return this;
        }

        public String getGrillProbe() {
            return grillProbe;
        }

        public void setGrillProbe(String grillProbe) {
            this.grillProbe = grillProbe;
        }

        public GrillProbeSettings withGrillProbe(String grillProbe) {
            this.grillProbe = grillProbe;
            return this;
        }

        public List<Integer> getGrillProbeEnabled() {
            return grillProbeEnabled;
        }

        public void setGrillProbeEnabled(List<Integer> grillProbeEnabled) {
            this.grillProbeEnabled = grillProbeEnabled;
        }

        public GrillProbeSettings withGrillProbeEnabled(List<Integer> grillProbeEnabled) {
            this.grillProbeEnabled = grillProbeEnabled;
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
        @SerializedName("frequency")
        @Expose
        private Integer frequency;
        @SerializedName("duty_cycle")
        @Expose
        private Integer dutyCycle;

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

    }

    public static class OutPins {

        @SerializedName("auger")
        @Expose
        private Integer auger;
        @SerializedName("fan")
        @Expose
        private Integer fan;
        @SerializedName("igniter")
        @Expose
        private Integer igniter;
        @SerializedName("power")
        @Expose
        private Integer power;

        public Integer getAuger() {
            return auger;
        }

        public void setAuger(Integer auger) {
            this.auger = auger;
        }

        public Integer getFan() {
            return fan;
        }

        public void setFan(Integer fan) {
            this.fan = fan;
        }

        public Integer getIgniter() {
            return igniter;
        }

        public void setIgniter(Integer igniter) {
            this.igniter = igniter;
        }

        public Integer getPower() {
            return power;
        }

        public void setPower(Integer power) {
            this.power = power;
        }

    }

    public static class InPins {

        @SerializedName("selector")
        @Expose
        private Integer selector;

        public Integer getSelector() {
            return selector;
        }

        public void setSelector(Integer selector) {
            this.selector = selector;
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

        @SerializedName("adc")
        @Expose
        private String adc;
        @SerializedName("display")
        @Expose
        private String display;
        @SerializedName("dist")
        @Expose
        private String dist;
        @SerializedName("grillplat")
        @Expose
        private String grillplat;

        public String getAdc() {
            return adc;
        }

        public void setAdc(String adc) {
            this.adc = adc;
        }

        public Modules withAdc(String adc) {
            this.adc = adc;
            return this;
        }

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

    public static class GrillProbeModel {

        @SerializedName("name")
        @Expose
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public GrillProbeModel withName(String name) {
            this.name = name;
            return this;
        }

    }

    public static class ProbeProfileModel {

        @SerializedName("Vs")
        @Expose
        private Float vs;
        @SerializedName("Rd")
        @Expose
        private Integer rd;
        @SerializedName("A")
        @Expose
        private Float a;
        @SerializedName("B")
        @Expose
        private Float b;
        @SerializedName("C")
        @Expose
        private Float c;
        @SerializedName("name")
        @Expose
        private String name;

        public Float getVs() {
            return vs;
        }

        public void setVs(Float vs) {
            this.vs = vs;
        }

        public Integer getRd() {
            return rd;
        }

        public void setRd(Integer rd) {
            this.rd = rd;
        }

        public Float getA() {
            return a;
        }

        public void setA(Float a) {
            this.a = a;
        }

        public Float getB() {
            return b;
        }

        public void setB(Float b) {
            this.b = b;
        }

        public Float getC() {
            return c;
        }

        public void setC(Float c) {
            this.c = c;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static SettingsDataModel parseJSON(String response) {
        return new Gson().fromJson(response, SettingsDataModel.class);
    }
}
