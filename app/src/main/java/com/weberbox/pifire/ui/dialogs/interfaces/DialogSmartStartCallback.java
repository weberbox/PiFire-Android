package com.weberbox.pifire.ui.dialogs.interfaces;

import com.weberbox.pifire.model.local.SmartStartModel;

import java.util.List;

public interface DialogSmartStartCallback {
    void onDialogAdd(List<SmartStartModel> list, Integer temp, Integer startUp, Integer augerOn,
                     Integer pMode);
    void onDialogEdit(List<SmartStartModel> list, int position, Integer temp, Integer startUp,
                      Integer augerOn, Integer pMode);
    void onDialogDelete(int position);
}
