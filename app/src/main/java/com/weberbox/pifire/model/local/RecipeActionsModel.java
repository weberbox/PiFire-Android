package com.weberbox.pifire.model.local;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("FieldCanBeLocal")
public class RecipeActionsModel {

    @SerializedName("deletefile")
    @Expose
    private boolean deleteFile;
    @SerializedName("filename")
    @Expose
    private String filename;

    public RecipeActionsModel withDeleteFile(boolean deleteFile) {
        this.deleteFile = deleteFile;
        return this;
    }

    public RecipeActionsModel withFilename(String filename) {
        this.filename = filename;
        return this;
    }
}
