package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class SettingsResponseModel {

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
    @SerializedName("firebase")
    @Expose
    private Firebase firebase;
    @SerializedName("probe_types")
    @Expose
    private ProbeTypes probeTypes;
    @SerializedName("grill_probe_settings")
    @Expose
    private GrillProbeSettings grillProbeSettings;
    @SerializedName("cycle_data")
    @Expose
    private CycleData cycleData;
    @SerializedName("smoke_plus")
    @Expose
    private SmokePlus smokePlus;
    @SerializedName("safety")
    @Expose
    private Safety safety;
    @SerializedName("pelletlevel")
    @Expose
    private PelletLevel pelletLevel;

    public Versions getVersions() {
        return versions;
    }

    public void setVersions(Versions versions) {
        this.versions = versions;
    }

    public HistoryPage getHistoryPage() {
        return historyPage;
    }

    public void setHistoryPage(HistoryPage historyPage) {
        this.historyPage = historyPage;
    }

    public ProbeSettings getProbeSettings() {
        return probeSettings;
    }

    public void setProbeSettings(ProbeSettings probeSettings) {
        this.probeSettings = probeSettings;
    }

    public Globals getGlobals() {
        return globals;
    }

    public void setGlobals(Globals globals) {
        this.globals = globals;
    }

    public Ifttt getIfttt() {
        return ifttt;
    }

    public void setIfttt(Ifttt ifttt) {
        this.ifttt = ifttt;
    }

    public PushBullet getPushBullet() {
        return pushbullet;
    }

    public void setPushBullet(PushBullet pushbullet) {
        this.pushbullet = pushbullet;
    }

    public Firebase getFirebase() {
        return firebase;
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
    }

    public Pushover getPushover() {
        return pushover;
    }

    public void setPushover(Pushover pushover) {
        this.pushover = pushover;
    }

    public ProbeTypes getProbeTypes() {
        return probeTypes;
    }

    public void setProbeTypes(ProbeTypes probeTypes) {
        this.probeTypes = probeTypes;
    }

    public GrillProbeSettings getGrillProbeSettings() {
        return grillProbeSettings;
    }

    public void setGrillProbeSettings(GrillProbeSettings grillProbeSettings) {
        this.grillProbeSettings = grillProbeSettings;
    }

    public CycleData getCycleData() {
        return cycleData;
    }

    public void setCycleData(CycleData cycleData) {
        this.cycleData = cycleData;
    }

    public SmokePlus getSmokePlus() {
        return smokePlus;
    }

    public void setSmokePlus(SmokePlus smokePlus) {
        this.smokePlus = smokePlus;
    }

    public Safety getSafety() {
        return safety;
    }

    public void setSafety(Safety safety) {
        this.safety = safety;
    }

    public PelletLevel getPellets() {
        return pelletLevel;
    }

    public void PelletLevel(PelletLevel pelletLevel) {
        this.pelletLevel = pelletLevel;
    }

    public static class CycleData {

        @SerializedName("PB")
        @Expose
        private String pb;
        @SerializedName("Ti")
        @Expose
        private String ti;
        @SerializedName("Td")
        @Expose
        private String td;
        @SerializedName("HoldCycleTime")
        @Expose
        private String holdCycleTime;
        @SerializedName("SmokeCycleTime")
        @Expose
        private String smokeCycleTime;
        @SerializedName("PMode")
        @Expose
        private String pMode;
        @SerializedName("u_min")
        @Expose
        private String uMin;
        @SerializedName("u_max")
        @Expose
        private String uMax;

        public String getPb() {
            return pb;
        }

        public void setPb(String pb) {
            this.pb = pb;
        }

        public String getTi() {
            return ti;
        }

        public void setTi(String ti) {
            this.ti = ti;
        }

        public String getTd() {
            return td;
        }

        public void setTd(String td) {
            this.td = td;
        }

        public String getHoldCycleTime() {
            return holdCycleTime;
        }

        public void setHoldCycleTime(String holdCycleTime) {
            this.holdCycleTime = holdCycleTime;
        }

        public String getSmokeCycleTime() {
            return smokeCycleTime;
        }

        public void setSmokeCycleTime(String smokeCycleTime) {
            this.smokeCycleTime = smokeCycleTime;
        }

        public String getPMode() {
            return pMode;
        }

        public void setPMode(String pMode) {
            this.pMode = pMode;
        }

        public String getuMin() {
            return uMin;
        }

        public void setuMin(String uMin) {
            this.uMin = uMin;
        }

        public String getuMax() {
            return uMax;
        }

        public void setuMax(String uMax) {
            this.uMax = uMax;
        }

    }

    public static class Versions {

        @SerializedName("android")
        @Expose
        private Integer android;
        @SerializedName("server")
        @Expose
        private Integer server;

        public Integer getAndroidVersion() {
            return android;
        }

        public void setAndroidVersion(Integer android) {
            this.android = android;
        }

        public Integer getServerVersion() {
            return server;
        }

        public void setServerVersion(Integer server) {
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
        private String shutdownTimer;
        @SerializedName("four_probes")
        @Expose
        private Boolean fourProbes;
        @SerializedName("units")
        @Expose
        private String units;

        public String getGrillName() {
            return grillName;
        }

        public void setGrillName(String grillName) {
            this.grillName = grillName;
        }

        public Boolean getDebugMode() {
            return debugMode;
        }

        public void setDebugMode(Boolean debugMode) {
            this.debugMode = debugMode;
        }

        public String getPageTheme() {
            return pageTheme;
        }

        public void setPageTheme(String pageTheme) {
            this.pageTheme = pageTheme;
        }

        public String getTriggerLevel() {
            return triggerLevel;
        }

        public void setTriggerLevel(String triggerlevel) {
            this.triggerLevel = triggerlevel;
        }

        public String getButtonsLevel() {
            return buttonsLevel;
        }

        public void setButtonsLevel(String buttonslevel) {
            this.buttonsLevel = buttonslevel;
        }

        public String getShutdownTimer() {
            return shutdownTimer;
        }

        public void setShutdownTimer(String shutdownTimer) {
            this.shutdownTimer = shutdownTimer;
        }

        public Boolean getFourProbes() {
            return fourProbes;
        }

        public void setFourProbes(Boolean fourProbes) {
            this.fourProbes = fourProbes;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String units) {
            this.units = units;
        }

    }

    public static class Safety {

        @SerializedName("minstartuptemp")
        @Expose
        private String minStartupTemp;
        @SerializedName("maxstartuptemp")
        @Expose
        private String maxStartupTemp;
        @SerializedName("maxtemp")
        @Expose
        private String maxTemp;
        @SerializedName("reigniteretries")
        @Expose
        private String reigniteRetries;

        public String getMinStartupTemp() {
            return minStartupTemp;
        }

        public void setMinStartupTemp(String minstartuptemp) {
            this.minStartupTemp = minstartuptemp;
        }

        public String getMaxStartupTemp() {
            return maxStartupTemp;
        }

        public void setMaxStartupTemp(String maxstartuptemp) {
            this.maxStartupTemp = maxstartuptemp;
        }

        public String getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(String maxtemp) {
            this.maxTemp = maxtemp;
        }

        public String getReigniteRetries() {
            return reigniteRetries;
        }

        public void setReigniteRetries(String reigniteretries) {
            this.reigniteRetries = reigniteretries;
        }

    }

    public static class PelletLevel {

        @SerializedName("empty")
        @Expose
        private String empty;
        @SerializedName("full")
        @Expose
        private String full;

        public String getEmpty() {
            return empty;
        }

        public void setEmpty(String empty) {
            this.empty = empty;
        }

        public String getFull() {
            return full;
        }

        public void setFull(String full) {
            this.full = full;
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

        public String getAPIKey() {
            return aPIKey;
        }

        public void setAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
        }

        public String getUserKeys() {
            return userKeys;
        }

        public void setUserKeys(String userKeys) {
            this.userKeys = userKeys;
        }

        public String getPublicURL() {
            return publicURL;
        }

        public void setPublicURL(String publicURL) {
            this.publicURL = publicURL;
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

        public String getAPIKey() {
            return aPIKey;
        }

        public void setAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
        }

        public String getPublicURL() {
            return publicURL;
        }

        public void setPublicURL(String publicURL) {
            this.publicURL = publicURL;
        }

    }

    public static class Firebase {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("ServerKey")
        @Expose
        private String serverKey;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getServerKey() {
            return serverKey;
        }

        public void setServerKey(String serverKey) {
            this.serverKey = serverKey;
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

        public String getAPIKey() {
            return aPIKey;
        }

        public void setAPIKey(String aPIKey) {
            this.aPIKey = aPIKey;
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

        public String getGrill1type() {
            return grill1type;
        }

        public void setGrill1type(String grill1type) {
            this.grill1type = grill1type;
        }

        public String getGrill2type() {
            return grill2type;
        }

        public void setGrill2type(String grill2type) {
            this.grill2type = grill2type;
        }

        public String getProbe1type() {
            return probe1type;
        }

        public void setProbe1type(String probe1type) {
            this.probe1type = probe1type;
        }

        public String getProbe2type() {
            return probe2type;
        }

        public void setProbe2type(String probe2type) {
            this.probe2type = probe2type;
        }

    }

    public static class ProbeSettings {

        @SerializedName("probes_enabled")
        @Expose
        private List<String> probesEnabled = null;
        @SerializedName("probe_profiles")
        @Expose
        private Map<String, ProbeProfileModel> probeProfiles = new HashMap<>();

        public Map<String, ProbeProfileModel> getProbeProfiles() {
            return probeProfiles;
        }

        public void setProbeProfiles(Map<String, ProbeProfileModel> probeProfiles) {
            this.probeProfiles = probeProfiles;
        }

        public List<String> getProbesEnabled() {
            return probesEnabled;
        }

        public void setProbesEnabled(List<String> probesEnabled) {
            this.probesEnabled = probesEnabled;
        }

    }

    public static class HistoryPage {

        @SerializedName("minutes")
        @Expose
        private String minutes;
        @SerializedName("clearhistoryonstart")
        @Expose
        private Boolean clearhistoryonstart;
        @SerializedName("autorefresh")
        @Expose
        private String autorefresh;
        @SerializedName("datapoints")
        @Expose
        private String datapoints;

        public String getMinutes() {
            return minutes;
        }

        public void setMinutes(String minutes) {
            this.minutes = minutes;
        }

        public Boolean getClearHistoryOnStart() {
            return clearhistoryonstart;
        }

        public void setClearHistoryOnStart(Boolean clearhistoryonstart) {
            this.clearhistoryonstart = clearhistoryonstart;
        }

        public String getAutoRefresh() {
            return autorefresh;
        }

        public void setAutoRefresh(String autorefresh) {
            this.autorefresh = autorefresh;
        }

        public String getDataPoints() {
            return datapoints;
        }

        public void setDataPoints(String datapoints) {
            this.datapoints = datapoints;
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
        private List<String> grillProbeEnabled = null;

        public Map<String, GrillProbeModel> getGrillProbes() {
            return grillProbes;
        }

        public void setGrillProbes(Map<String, GrillProbeModel> grillProbes) {
            this.grillProbes = grillProbes;
        }

        public String getGrillProbe() {
            return grillProbe;
        }

        public void setGrillProbe(String grillProbe) {
            this.grillProbe = grillProbe;
        }

        public List<String> getGrillProbeEnabled() {
            return grillProbeEnabled;
        }

        public void setGrillProbeEnabled(List<String> grillProbeEnabled) {
            this.grillProbeEnabled = grillProbeEnabled;
        }

    }

    public static class SmokePlus {

        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("min_temp")
        @Expose
        private String minTemp;
        @SerializedName("max_temp")
        @Expose
        private String maxTemp;
        @SerializedName("cycle")
        @Expose
        private String cycle;
        @SerializedName("frequency")
        @Expose
        private String frequency;
        @SerializedName("duty_cycle")
        @Expose
        private String dutyCycle;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public String getMinTemp() {
            return minTemp;
        }

        public void setMinTemp(String minTemp) {
            this.minTemp = minTemp;
        }

        public String getMaxTemp() {
            return maxTemp;
        }

        public void setMaxTemp(String maxTemp) {
            this.maxTemp = maxTemp;
        }

        public String getCycle() {
            return cycle;
        }

        public void setCycle(String cycle) {
            this.cycle = cycle;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getDutyCycle() {
            return dutyCycle;
        }

        public void setDutyCycle(String dutyCycle) {
            this.dutyCycle = dutyCycle;
        }

    }

    public static SettingsResponseModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, SettingsResponseModel.class);
    }

}
