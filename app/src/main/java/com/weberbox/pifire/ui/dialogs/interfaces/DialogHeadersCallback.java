package com.weberbox.pifire.ui.dialogs.interfaces;

public interface DialogHeadersCallback {
    void onDialogSave(String key, String value, Integer position);
    void onDialogDelete(int position);
}
