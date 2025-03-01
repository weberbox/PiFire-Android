package com.weberbox.pifire.model.local;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LicensesModel {

    @SerializedName("project")
    @Expose
    private String project;
    @SerializedName("license")
    @Expose
    private String license;

    public String getProject() {
        return project;
    }

    public String getLicense() {
        return license;
    }

    public static ArrayList<LicensesModel> parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, new TypeToken<List<LicensesModel>>(){}.getType());
    }

}
