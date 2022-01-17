package com.weberbox.pifire.interfaces;

public interface BackupRestoreCallback {
    void onBackupData(int type);
    void onRestoreLocal(int type);
    void onRestoreRemote(int type);
    void onFileRestoreRemote(String fileName, String type);
}
