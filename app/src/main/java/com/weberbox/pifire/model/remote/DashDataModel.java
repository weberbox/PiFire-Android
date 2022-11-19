package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@SuppressWarnings("unused")
public class DashDataModel {

    @SerializedName("cur_probe_temps")
    @Expose
    private ProbeTemps curProbeTemps;
    @SerializedName("probes_enabled")
    @Expose
    private ProbesEnabled probesEnabled;
    @SerializedName("probe_titles")
    @Expose
    private ProbeTitles probeTitles;
    @SerializedName("set_points")
    @Expose
    private SetPoints setPoints;
    @SerializedName("notify_req")
    @Expose
    private NotifyReq notifyReq;
    @SerializedName("notify_data")
    @Expose
    private NotifyData notifyData;
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

    public ProbeTemps getProbeTemps() {
        return curProbeTemps;
    }

    public ProbesEnabled getProbesEnabled() {
        return probesEnabled;
    }

    public SetPoints getSetPoints() {
        return setPoints;
    }

    public NotifyReq getNotifyReq() {
        return notifyReq;
    }

    public NotifyData getNotifyData() {
        return notifyData;
    }

    public TimerInfo getTimerInfo() {
        return timerInfo;
    }

    public ProbeTitles getProbeTitles() {
        return probeTitles;
    }


    public static class ProbeTemps {
        @SerializedName("grill_temp")
        @Expose
        private Double grillTemp;
        @SerializedName("probe1_temp")
        @Expose
        private Double probe1Temp;
        @SerializedName("probe2_temp")
        @Expose
        private Double probe2Temp;

        public Double getGrillTemp() {
            return grillTemp;
        }

        public Double getProbeOneTemp() {
            return probe1Temp;
        }

        public Double getProbeTwoTemp() {
            return probe2Temp;
        }
    }

    public static class ProbesEnabled {
        @SerializedName("grill")
        @Expose
        private Boolean grill;
        @SerializedName("probe1")
        @Expose
        private Boolean probe1;
        @SerializedName("probe2")
        @Expose
        private Boolean probe2;

        public Boolean getGrillEnabled() {
            return grill;
        }

        public Boolean getProbeOneEnabled() {
            return probe1;
        }

        public Boolean getProbeTwoEnabled() {
            return probe2;
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

        public String getProbeOneTitle() {
            return probeOneTitle;
        }

        public String getProbeTwoTitle() {
            return probeTwoTitle;
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

        public Integer getGrillTarget() {
            return Objects.requireNonNullElse(grill, 0);
        }

        public Integer getProbeOneTarget() {
            return Objects.requireNonNullElse(probe1, 0);
        }

        public Integer getProbeTwoTarget() {
            return Objects.requireNonNullElse(probe2, 0);
        }

        public Integer getGrillNotifyTarget() {
            return Objects.requireNonNullElse(grillNotify, 0);
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

        public Boolean getGrillNotify() {
            return grill;
        }

        public Boolean getProbeOneNotify() {
            return probe1;
        }

        public Boolean getProbeTwoNotify() {
            return probe2;
        }

        public Boolean getTimerNotify() {
            return timer;
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

        public Boolean getP1Shutdown() {
            return p1Shutdown;
        }

        public Boolean getP2Shutdown() {
            return p2Shutdown;
        }

        public Boolean getTimerShutdown() {
            return timerShutdown;
        }

        public Boolean getP1KeepWarm() {
            return p1KeepWarm;
        }

        public Boolean getP2KeepWarm() {
            return p2KeepWarm;
        }

        public Boolean getTimerKeepWarm() {
            return timerKeepWarm;
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
