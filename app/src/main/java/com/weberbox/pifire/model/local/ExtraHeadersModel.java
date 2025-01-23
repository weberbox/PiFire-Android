package com.weberbox.pifire.model.local;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ExtraHeadersModel {

    @SerializedName("headerKey")
    @Expose
    private String headerKey;
    @SerializedName("headerValue")
    @Expose
    private String headerValue;

    public ExtraHeadersModel(@NonNull final String key, @NonNull final String value) {
        setHeaderKey(key);
        setHeaderValue(value);
    }

    public String getHeaderKey() {
        return headerKey;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public static ArrayList<ExtraHeadersModel> parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, new TypeToken<List<ExtraHeadersModel>>(){}.getType());
    }
}
