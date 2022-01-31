package com.weberbox.pifire.model.remote;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ServerResponseModel {

    @SerializedName("response")
    @Expose
    private Response response;

    public String getResult() {
        if (response != null) {
            return response.getResult();
        }
        return "";
    }

    public String getMessage() {
        if (response != null) {
            return response.getMessage();
        }
        return "";
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
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

        public void setResult(String result) {
            this.result = result;
        }

        public String getMessage() {
            if (message == null) {
                return "";
            }
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

}
