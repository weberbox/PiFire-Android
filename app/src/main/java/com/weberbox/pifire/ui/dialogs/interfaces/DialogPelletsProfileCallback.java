package com.weberbox.pifire.ui.dialogs.interfaces;

import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;

public interface DialogPelletsProfileCallback {
    void onItemDelete(String type, String item, int position);
    void onDeleteConfirmed(String type, String item, int position);
    void onItemAdded(String type, String item);
    void onProfileSelected(String profileName, String profileId);
    void onProfileAdd(PelletProfileModel profile);
    void onProfileEdit(PelletProfileModel profile, boolean load);
    void onProfileOpen(PelletProfileModel profile, int position);
    void onProfileDelete(String profileId, int position);
    void onLogLongClick(String logDate, int position);
}
