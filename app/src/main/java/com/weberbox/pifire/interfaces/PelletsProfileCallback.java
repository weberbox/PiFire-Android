package com.weberbox.pifire.interfaces;

import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;

public interface PelletsProfileCallback {
    void onItemDelete(String type, String item, int position);
    void onDeleteConfirmed(String type, String item, int position);
    void onItemAdded(String type, String item);
    void onProfileSelected(String profileName, String profileId);
    void onProfileEdit(PelletProfileModel model);
    void onProfileDelete(String profileId, int position);
    void onLogLongClick(String logDate, int position);
}
