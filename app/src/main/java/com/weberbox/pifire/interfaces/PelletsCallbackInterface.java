package com.weberbox.pifire.interfaces;

import com.weberbox.pifire.model.PelletProfileModel;

public interface PelletsCallbackInterface {
    void onItemDelete(String type, String item, int position);
    void onDeleteConfirmed(String type, String item, int position);
    void onItemAdded(String type, String item);
    void onProfileSelected(String profileName, String profileId);
    void onProfileEdit(PelletProfileModel model);
    void onProfileDelete(String profileId, int position);
    void onLogLongClick(String logDate, int position);
}
