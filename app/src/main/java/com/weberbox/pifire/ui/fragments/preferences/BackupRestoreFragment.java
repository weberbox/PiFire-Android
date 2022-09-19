package com.weberbox.pifire.ui.fragments.preferences;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogRestoreCallback;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.BottomIconDialog;
import com.weberbox.pifire.ui.dialogs.RestoreListDialog;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.StringUtils;
import com.weberbox.pifire.utils.TimeUtils;
import com.weberbox.pifire.utils.VersionUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import timber.log.Timber;

public class BackupRestoreFragment extends PreferenceFragmentCompat implements
        DialogRestoreCallback {

    private Socket socket;
    private String jsonData;
    private String type;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_backup_restore, rootKey);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Preference backupSettings = findPreference(getString(R.string.prefs_admin_backup_settings));
        Preference restoreSettings = findPreference(getString(R.string.prefs_admin_restore_settings));
        Preference backupPelletDB = findPreference(getString(R.string.prefs_admin_backup_pelletdb));
        Preference restorePelletDB = findPreference(getString(R.string.prefs_admin_restore_pelletdb));


        if (backupSettings != null) {
            backupSettings.setOnPreferenceClickListener(preference -> {
                showBackupDialog(Constants.ACTION_BACKUP_SETTINGS);
                return true;
            });
        }

        if (restoreSettings != null) {
            restoreSettings.setOnPreferenceClickListener(preference -> {
                showRestoreDialog(Constants.ACTION_RESTORE_SETTINGS);
                return true;
            });
        }

        if (backupPelletDB != null) {
            backupPelletDB.setOnPreferenceClickListener(preference -> {
                showBackupDialog(Constants.ACTION_BACKUP_PELLETDB);
                return true;
            });
        }

        if (restorePelletDB != null) {
            restorePelletDB.setOnPreferenceClickListener(preference -> {
                showRestoreDialog(Constants.ACTION_RESTORE_PELLETDB);
                return true;
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_admin_backup_restore);
        }
    }

    private void showBackupDialog(int backupType) {
        if (socket != null && socket.connected()) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                    .setAutoDismiss(true)
                    .setTitle(getString(R.string.dialog_confirm_action))
                    .setMessage(getString(R.string.dialog_backup_message))
                    .setNegativeButton(getString(R.string.cancel),
                            (dialogInterface, which) -> {
                            })
                    .setPositiveButton(getString(R.string.backup),
                            (dialogInterface, which) -> {
                                switch (backupType) {
                                    case Constants.ACTION_BACKUP_SETTINGS:
                                        requestBackupData(socket, Constants.BACKUP_SETTINGS);
                                        break;
                                    case Constants.ACTION_BACKUP_PELLETDB:
                                        requestBackupData(socket, Constants.BACKUP_PELLETDB);
                                        break;
                                }
                            })
                    .build();
            dialog.show();
        } else {
            AlertUtils.createErrorAlert(getActivity(), R.string.settings_error_offline, false);
        }
    }

    private void showRestoreDialog(int restoreType) {
        if (socket != null && socket.connected()) {
            BottomIconDialog dialog = new BottomIconDialog.Builder(requireActivity())
                    .setAutoDismiss(true)
                    .setNegativeButton(getString(R.string.dialog_remote),
                            R.drawable.ic_remote_storage, (dialogInterface, which) -> {
                                switch (restoreType) {
                                    case Constants.ACTION_RESTORE_SETTINGS:
                                        requestBackupList(socket, Constants.BACKUP_SETTINGS);
                                        break;
                                    case Constants.ACTION_RESTORE_PELLETDB:
                                        requestBackupList(socket, Constants.BACKUP_PELLETDB);
                                        break;
                                }
                            })
                    .setPositiveButton(getString(R.string.dialog_local),
                            R.drawable.ic_local_storage, (dialogInterface, which) -> {
                                String type = null;
                                switch (restoreType) {
                                    case Constants.ACTION_RESTORE_SETTINGS:
                                        type = Constants.BACKUP_SETTINGS;
                                        break;
                                    case Constants.ACTION_RESTORE_PELLETDB:
                                        type = Constants.BACKUP_PELLETDB;
                                        break;
                                }
                                this.type = type;
                                requestPermissionAndBrowseFile();
                            })
                    .build();
            dialog.show();
        } else {
            AlertUtils.createErrorAlert(getActivity(), R.string.settings_error_offline, false);
        }
    }

    @Override
    public void onFileRestoreRemote(String fileName, String type) {
        if (socket != null && socket.connected()) {
            ServerControl.backupRestoreRemoteEmit(socket, type, fileName, response -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (VersionUtils.isSupported(ServerVersions.V_127)) {
                            ServerResponseModel result = ServerResponseModel.parseJSON(response);
                            if (result.getResponse() != null &&
                                    result.getResponse().getResult().equals("success")) {
                                AlertUtils.createAlert(getActivity(), R.string.restore_success,
                                        1000);
                            } else {
                                AlertUtils.createErrorAlert(getActivity(), R.string.restore_failed,
                                        false);
                            }
                        }
                    });
                }
            });
        }
    }

    private void restoreBackupLocal(String jsonData) {
        if (socket != null && socket.connected()) {
            if (type != null) {
                ServerControl.backupRestoreLocalEmit(socket, type, jsonData, response -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            ServerResponseModel result = ServerResponseModel.parseJSON(response);
                            if (result.getResponse() != null &&
                                    result.getResponse().getResult().equals("success")) {
                                AlertUtils.createAlert(getActivity(),
                                        R.string.restore_success, 1000);
                            } else {
                                AlertUtils.createErrorAlert(getActivity(),
                                        R.string.restore_failed, false);
                            }
                        });
                    }
                });
            }
        }
    }

    private void requestBackupData(Socket socket, String backupType) {
        ServerControl.backupDataGetEmit(socket, backupType, response -> {
            if (getActivity() != null) {
                jsonData = response;
                String currentTime = TimeUtils.getFormattedDate(System.currentTimeMillis(),
                        "MM-dd-yy_hhmmss");
                switch (backupType) {
                    case Constants.BACKUP_SETTINGS:
                        createFile(Constants.BACKUP_SETTINGS_FILENAME + currentTime);
                        break;
                    case Constants.BACKUP_PELLETDB:
                        createFile(Constants.BACKUP_PELLETDB_FILENAME + currentTime);
                        break;
                }
            }
        });
    }

    private void requestBackupList(Socket socket, String backupType) {
        RestoreListDialog restoreDialog = new RestoreListDialog(getActivity(),
                BackupRestoreFragment.this,
                getString(R.string.dialog_restore_title), backupType);
        restoreDialog.showDialog();

        ServerControl.backupListGetEmit(socket, backupType, response -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    ArrayList<String> fileNames = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            fileNames.add(jsonArray.getString(i));
                        }
                        restoreDialog.populateList(fileNames);
                    } catch (JSONException e) {
                        Timber.w(e, "Failed to create file list");
                        restoreDialog.dismiss();
                        AlertUtils.createErrorAlert(getActivity(), R.string.backup_failed, false);
                    }
                });
            }
        });
    }

    private void requestPermissionAndBrowseFile() {
        TedPermission.create()
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        openFileBrowser();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        AlertUtils.createErrorAlert(getActivity(), R.string.file_permission_denied,
                                false);
                    }
                })
                .check();
    }

    private void openFileBrowser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        }
        intent = Intent.createChooser(intent, getString(R.string.file_picker_text));
        pickerResultLauncher.launch(intent);
    }

    private void createFile(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        }
        createFileResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri fileUri = data.getData();
                        if (getActivity() != null) {
                            try {
                                InputStream is = getActivity().getContentResolver()
                                        .openInputStream(fileUri);
                                if (is != null) {
                                    String jsonData = StringUtils.streamToString(is);
                                    is.close();
                                    restoreBackupLocal(jsonData);
                                }
                            } catch (IOException e) {
                                Timber.w(e, "Failed to restore backup file");
                                AlertUtils.createErrorAlert(getActivity(), R.string.restore_failed,
                                        false);
                            }
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> createFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri fileUri = data.getData();
                        if (getActivity() != null) {
                            try {
                                OutputStream os = getActivity().getContentResolver()
                                        .openOutputStream(fileUri);
                                if (os != null) {
                                    os.write(jsonData.getBytes());
                                    os.close();
                                }
                                AlertUtils.createAlert(getActivity(), R.string.backup_success,
                                        1000);
                            } catch (IOException e) {
                                Timber.w(e, "Failed to write backup file");
                                AlertUtils.createErrorAlert(getActivity(), R.string.backup_failed,
                                        false);
                            }
                        }
                    }
                }
            });
}
