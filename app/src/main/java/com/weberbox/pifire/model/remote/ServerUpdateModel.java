package com.weberbox.pifire.model.remote;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weberbox.pifire.model.remote.ServerResponseModel.Response;

@SuppressWarnings("unused")
public class ServerUpdateModel {

    @SerializedName("check_success")
    @Expose
    private Boolean checkSuccess;
    @SerializedName("branches")
    @Expose
    private List<String> branches = null;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("branch_target")
    @Expose
    private String branchTarget;
    @SerializedName("remote_url")
    @Expose
    private String remoteUrl;
    @SerializedName("remote_version")
    @Expose
    private String remoteVersion;
    @SerializedName("commits_behind")
    @Expose
    private Integer commitsBehind;
    @SerializedName("logs_result")
    @Expose
    private List<String> logsResult;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;
    @SerializedName("response")
    @Expose
    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Boolean getCheckSuccess() {
        return checkSuccess;
    }

    public void setCheckSuccess(Boolean checkSuccess) {
        this.checkSuccess = checkSuccess;
    }

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBranchTarget() {
        return branchTarget;
    }

    public void setBranchTarget(String branchTarget) {
        this.branchTarget = branchTarget;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getRemoteVersion() {
        return remoteVersion;
    }

    public void setRemoteVersion(String remoteVersion) {
        this.remoteVersion = remoteVersion;
    }

    public Integer getCommitsBehind() {
        return commitsBehind;
    }

    public void setCommitsBehind(Integer commitsBehind) {
        this.commitsBehind = commitsBehind;
    }

    public List<String> getLogsResult() {
        return logsResult;
    }

    public void setLogsResult(List<String> logsResult) {
        this.logsResult = logsResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static ServerUpdateModel parseJSON(String response) {
        return new Gson().fromJson(response, ServerUpdateModel.class);
    }
}
