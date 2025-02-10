package com.weberbox.pifire.ui.dialogs.interfaces;

import com.weberbox.pifire.model.local.PWMControlModel;

import java.util.List;

public interface DialogPWMCallback {
    void onDialogAdd(List<PWMControlModel> list, Integer temp, Integer dutyCycle);
    void onDialogEdit(List<PWMControlModel> list, int position, Integer temp, Integer dutyCycle);
    void onDialogDelete(int position);
}
