package com.weberbox.pifire.interfaces;

public interface PWMControlCallback {
    void onPWMControlEdit(int position);
    void onPWMControlDelete(int position);
    void onPWMControlAdd();
}
