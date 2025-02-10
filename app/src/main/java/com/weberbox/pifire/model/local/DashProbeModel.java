package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

@SuppressWarnings("unused")
public class DashProbeModel {

    private DashProbe dashProbe;

    public DashProbe getDashProbe() {
        return dashProbe;
    }

    public void setDashProbe(DashProbe dashProbe) {
        this.dashProbe = dashProbe;
    }

    public DashProbeModel withDashProbe(DashProbe dashProbe) {
        this.dashProbe = dashProbe;
        return this;
    }

    public static class DashProbe {
        private String label;
        private String name;
        private String probeType;
        private Double probeTemp;
        private Integer target;
        private Double setTemp;
        private Boolean shutdown;
        private Boolean keepWarm;
        private Boolean notifications;
        private String eta;

        public DashProbe(@NonNull String label, @NonNull String name, @NonNull String probeType) {
            this.label = label;
            this.name = name;
            this.probeType = probeType;
            this.probeTemp = 0.0;
            this.target = 0;
            this.setTemp = 0.0;
            this.shutdown = false;
            this.keepWarm = false;
            this.notifications = false;
            this.eta = null;
        }

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

        public String getProbeType() {
            return probeType;
        }

        public void setProbeType(String probeType) {
            this.probeType = probeType;
        }

        public Double getProbeTemp() {
            return probeTemp;
        }

        public void setProbeTemp(Double probeTemp) {
            this.probeTemp = probeTemp;
        }

        public Integer getTarget() {
            return target;
        }

        public void setTarget(Integer target) {
            this.target = target;
        }

        public Double getSetTemp() {
            return setTemp;
        }

        public void setSetTemp(Double setTemp) {
            this.setTemp = setTemp;
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

        public Boolean getNotifications() {
            return notifications;
        }

        public void setNotifications(Boolean notifications) {
            this.notifications = notifications;
        }

        public String getEta() {
            return eta;
        }

        public void setEta(String eta) {
            this.eta = eta;
        }
    }
}
