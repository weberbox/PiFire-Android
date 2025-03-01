package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponseModel {

    @SerializedName("response")
    @Expose
    private Response response;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("message")
    @Expose
    private String message;

    public String getResult() {
        if (response != null) {
            return response.getResult();
        } else if (result != null) {
            return result;
        }
        return "";
    }

    public String getMessage() {
        if (response != null) {
            return response.getMessage();
        } else if (message != null) {
            return message;
        }
        return "";
    }

    public Response getResponse() {
        return response;
    }

    public static ServerResponseModel parseJSON(String response) {
        return new Gson().fromJson(response, ServerResponseModel.class);
    }

    public static class Response {

        @SerializedName("result")
        @Expose
        private String result;
        @SerializedName("message")
        @Expose
        private String message;

        public String getResult() {
            if (result == null) {
                return "";
            }
            return result;
        }

        public String getMessage() {
            if (message == null) {
                return "";
            }
            return message;
        }

    }

}
