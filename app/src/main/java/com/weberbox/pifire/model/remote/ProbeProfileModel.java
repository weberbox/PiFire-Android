package com.weberbox.pifire.model.remote;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ProbeProfileModel {

    @SerializedName("Vs")
    @Expose
    private String vs;
    @SerializedName("Rd")
    @Expose
    private String rd;
    @SerializedName("A")
    @Expose
    private String a;
    @SerializedName("B")
    @Expose
    private String b;
    @SerializedName("C")
    @Expose
    private String c;
    @SerializedName("name")
    @Expose
    private String name;

    public String getVs() {
        return vs;
    }

    public void setVs(String vs) {
        this.vs = vs;
    }

    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
