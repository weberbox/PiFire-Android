package com.weberbox.pifire.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UpdateInfo {

    @SerializedName("versionCode")
    @Expose
    private Integer versionCode;
    @SerializedName("updatePriority")
    @Expose
    private Integer updatePriority;

    public Integer getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getUpdatePriority() {
        return updatePriority;
    }

    public void setUpdatePriority(Integer updatePriority) {
        this.updatePriority = updatePriority;
    }

    public static ArrayList<UpdateInfo> parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, new TypeToken<List<UpdateInfo>>(){}.getType());
    }
}
