package com.weberbox.pifire.model.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class ProbeDataModel {

    public static class ProbeMap {

        @SerializedName("probe_devices")
        @Expose
        private List<ProbeDevice> probeDevices;
        @SerializedName("probe_info")
        @Expose
        private List<ProbeInfo> probeInfo;

        public List<ProbeDevice> getProbeDevices() {
            return probeDevices;
        }

        public void setProbeDevice(List<ProbeDevice> probeDevices) {
            this.probeDevices = probeDevices;
        }

        public ProbeMap withProbeDevice(List<ProbeDevice> probeDevices) {
            this.probeDevices = probeDevices;
            return this;
        }

        public List<ProbeInfo> getProbeInfo() {
            return probeInfo;
        }

        public void setProbeInfo(List<ProbeInfo> probeInfo) {
            this.probeInfo = probeInfo;
        }

        public ProbeMap withProbeInfo(List<ProbeInfo> probeInfo) {
            this.probeInfo = probeInfo;
            return this;
        }
    }

    public static class ProbeDevice {

        @SerializedName("config")
        @Expose
        private Config config;
        @SerializedName("device")
        @Expose
        private String device;
        @SerializedName("module")
        @Expose
        private String module;
        @SerializedName("ports")
        @Expose
        private List<String> ports;

        public Config getConfig() {
            return config;
        }

        public void setConfig(Config config) {
            this.config = config;
        }

        public ProbeDevice withConfig(Config config) {
            this.config = config;
            return this;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public ProbeDevice withDevice(String device) {
            this.device = device;
            return this;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public ProbeDevice withModule(String module) {
            this.module = module;
            return this;
        }

        public List<String> getPorts() {
            return ports;
        }

        public void setPorts(List<String> ports) {
            this.ports = ports;
        }

        public ProbeDevice withPorts(List<String> ports) {
            this.ports = ports;
            return this;
        }
    }

    public static class ProbeInfo {

        @SerializedName("device")
        @Expose
        private String device;
        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("port")
        @Expose
        private String port;
        @SerializedName("profile")
        @Expose
        private ProbeProfileModel profile;
        @SerializedName("type")
        @Expose
        private String type;

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public ProbeInfo withDevice(String device) {
            this.device = device;
            return this;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public ProbeInfo withEnabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public ProbeInfo withLabel(String label) {
            this.label = label;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ProbeInfo withName(String name) {
            this.name = name;
            return this;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public ProbeInfo withPort(String port) {
            this.port = port;
            return this;
        }

        public ProbeProfileModel getProbeProfile() {
            return profile;
        }

        public void setProbeProfile(ProbeProfileModel profile) {
            this.profile = profile;
        }

        public ProbeInfo withProbeProfile(ProbeProfileModel profile) {
            this.profile = profile;
            return this;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public ProbeInfo withType(String type) {
            this.type = type;
            return this;
        }

    }

    public static class ProbeProfileModel {

        @SerializedName("A")
        @Expose
        private Float a;
        @SerializedName("B")
        @Expose
        private Float b;
        @SerializedName("C")
        @Expose
        private Float c;
        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class Config {

        @SerializedName("ADC0_rd")
        @Expose
        private String adc0Rd;
        @SerializedName("ADC1_rd")
        @Expose
        private String adc1Rd;
        @SerializedName("ADC2_rd")
        @Expose
        private String adc2Rd;
        @SerializedName("ADC3_rd")
        @Expose
        private String adc3Rd;
        @SerializedName("i2c_bus_addr")
        @Expose
        private String i2cBusAddr;
        @SerializedName("voltage_ref")
        @Expose
        private String voltageRef;
        @SerializedName("cs")
        @Expose
        private String cs;
        @SerializedName("ref_resistor")
        @Expose
        private String refResistor;
        @SerializedName("rtd_nominal")
        @Expose
        private String rtdNominal;
        @SerializedName("wires")
        @Expose
        private String wires;
        @SerializedName("probes_list")
        @Expose
        private List<String> probesList;

        public String getAdc0Rd() {
            return adc0Rd;
        }

        public void setAdc0Rd(String adc0Rd) {
            this.adc0Rd = adc0Rd;
        }

        public String getAdc1Rd() {
            return adc1Rd;
        }

        public void setAdc1Rd(String adc1Rd) {
            this.adc1Rd = adc1Rd;
        }

        public String getAdc2Rd() {
            return adc2Rd;
        }

        public void setAdc2Rd(String adc2Rd) {
            this.adc2Rd = adc2Rd;
        }

        public String getAdc3Rd() {
            return adc3Rd;
        }

        public void setAdc3Rd(String adc3Rd) {
            this.adc3Rd = adc3Rd;
        }

        public String getI2cBusAddr() {
            return i2cBusAddr;
        }

        public void setI2cBusAddr(String i2cBusAddr) {
            this.i2cBusAddr = i2cBusAddr;
        }

        public String getVoltageRef() {
            return voltageRef;
        }

        public void setVoltageRef(String voltageRef) {
            this.voltageRef = voltageRef;
        }

        public String getCs() {
            return cs;
        }

        public void setCs(String cs) {
            this.cs = cs;
        }

        public String getRefResistor() {
            return refResistor;
        }

        public void setRefResistor(String refResistor) {
            this.refResistor = refResistor;
        }

        public String getRtdNominal() {
            return rtdNominal;
        }

        public void setRtdNominal(String rtdNominal) {
            this.rtdNominal = rtdNominal;
        }

        public String getWires() {
            return wires;
        }

        public void setWires(String wires) {
            this.wires = wires;
        }

        public List<String> getProbesList() {
            return probesList;
        }

        public void setProbesList(List<String> probesList) {
            this.probesList = probesList;
        }

    }

}
