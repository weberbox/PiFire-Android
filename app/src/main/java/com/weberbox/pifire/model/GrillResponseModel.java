package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class GrillResponseModel {

    @SerializedName("cur_probe_temps")
    @Expose
    private ProbeTemps curProbeTemps;
    @SerializedName("probes_enabled")
    @Expose
    private ProbesEnabled probesEnabled;
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
    @SerializedName("hopper_level")
    @Expose
    private Integer hopperLevel;

    public String getCurrentMode() {
        return currentMode;
    }

    public Boolean getSmokePlus() {
        return smokePlus;
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


    public static class ProbeTemps {
        @SerializedName("grill_temp")
        @Expose
        private String grillTemp;
        @SerializedName("probe1_temp")
        @Expose
        private String probe1Temp;
        @SerializedName("probe2_temp")
        @Expose
        private String probe2Temp;

        public String getGrillTemp() {
            return grillTemp;
        }

        public String getProbeOneTemp() {
            return probe1Temp;
        }

        public String getProbeTwoTemp() {
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

        public Integer getGrillTarget() {
            return grill;
        }

        public Integer getProbeOneTarget() {
            return probe1;
        }

        public Integer getProbeTwoTarget() {
            return probe2;
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

    public static GrillResponseModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, GrillResponseModel.class);
    }

}
