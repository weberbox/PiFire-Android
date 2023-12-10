package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

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
        private String type;
        private Double value;
        private Integer target;
        private Double setTemp;
        private Boolean shutdown;
        private Boolean keepWarm;

        public DashProbe(@NonNull String label, @NonNull String name, @NonNull String type,
                         @NonNull Double value, @NonNull Integer target, @NonNull Double setTemp,
                         @NotNull Boolean shutdown, @NotNull Boolean keepWarm) {
            this.label = label;
            this.name = name;
            this.type = type;
            this.value = value;
            this.target = target;
            this.setTemp = setTemp;
            this.shutdown = shutdown;
            this.keepWarm = keepWarm;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getValue() {
            return value;
        }

        public void setValue(Double value) {
            this.value = value;
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
    }
}
