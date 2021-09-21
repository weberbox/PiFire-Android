package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    public TimerInfo getTimerInfo() {
        return timerInfo;
    }


    public static class ProbeTemps {
        @SerializedName("grill_temp")
        @Expose
        private Integer grillTemp;
        @SerializedName("probe1_temp")
        @Expose
        private Integer probe1Temp;
        @SerializedName("probe2_temp")
        @Expose
        private Integer probe2Temp;

        public Integer getGrillTemp() {
            return grillTemp;
        }

        public Integer getProbeOneTemp() {
            return probe1Temp;
        }

        public Integer getProbeTwoTemp() {
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

    public static class TimerInfo {
        @SerializedName("timer_max")
        @Expose
        public Integer timerMax;
        @SerializedName("timer_current")
        @Expose
        public Integer timerCurrent;
        @SerializedName("timer_time")
        @Expose
        public String timerTime;
        @SerializedName("timer_paused")
        @Expose
        public Boolean timerPaused;
        @SerializedName("timer_finished")
        @Expose
        public Boolean timerFinished;

        public Integer getTimerMax() {
            return timerMax;
        }

        public Integer getTimerCurrent() {
            return timerCurrent;
        }

        public String getTimerTime() {
            return timerTime;
        }

        public Boolean getTimerPaused() {
            return timerPaused;
        }

        public Boolean getTimerFinished() {
            return timerFinished;
        }
    }

    public static GrillResponseModel parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, GrillResponseModel.class);
    }

}
