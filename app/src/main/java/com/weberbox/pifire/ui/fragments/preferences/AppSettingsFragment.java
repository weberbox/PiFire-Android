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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.utils.executors.AppExecutors;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.ProgressDialog;
import com.weberbox.pifire.updater.AppUpdater;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.database.DatabaseUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.TimeUtils;

import java.util.List;

public class AppSettingsFragment extends PreferenceFragmentCompat {

    private AppUpdater appUpdater;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_app_settings, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Preference updateCheck = findPreference(getString(R.string.prefs_app_updater_check_now));
        PreferenceCategory crashCat = findPreference(getString(R.string.prefs_crash_cat));
        SwitchPreferenceCompat crashEnabled = findPreference(getString(R.string.prefs_crash_enable));
        SwitchPreferenceCompat devCrashEnabled = findPreference(getString(R.string.prefs_dev_crash_enable));
        Preference serverSettings = findPreference(getString(R.string.prefs_server_settings));
        PreferenceCategory updaterCat = findPreference(getString(R.string.prefs_app_updater_cat));
        SwitchPreferenceCompat updaterEnabled = findPreference(getString(R.string.prefs_app_updater_enabled));
        Preference exportRecipes = findPreference(getString(R.string.prefs_recipe_db_backup));
        Preference importRecipes = findPreference(getString(R.string.prefs_recipe_db_restore));
        Preference clearRecipes = findPreference(getString(R.string.prefs_recipe_db_clear));

        if (serverSettings != null) {
            serverSettings.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    final FragmentManager fm = getActivity().getSupportFragmentManager();
                    final FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(android.R.id.content, new ServerSettingsFragment())
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            });
        }

        if (crashCat != null && crashEnabled != null) {
            if (getString(R.string.def_sentry_io_dsn).isEmpty()) {
                crashCat.setEnabled(false);
                crashEnabled.setChecked(false);
                crashEnabled.setSummary(getString(R.string.settings_enable_crash_disabled));
            }
        }

        if (devCrashEnabled != null) {
            if (AppConfig.IS_DEV_BUILD) {
                devCrashEnabled.setVisible(true);
            }
        }

        if (updaterCat != null && updaterEnabled != null) {
            if (getString(R.string.def_app_update_check_url).isEmpty()) {
                updaterCat.setEnabled(false);
                updaterEnabled.setChecked(false);
                updaterEnabled.setSummary(getString(R.string.updater_update_disabled));
            }
        }

        if (updateCheck != null) {
            updateCheck.setOnPreferenceClickListener(preference -> {
                if (getActivity() != null) {
                    appUpdater = new AppUpdater(getActivity())
                            .setDisplay(Display.DIALOG)
                            .setButtonDoNotShowAgain(false)
                            .showAppUpToDate(true)
                            .showAppUpdateError(true)
                            .setForceCheck(true)
                            .setView(view)
                            .setUpdateFrom(UpdateFrom.JSON)
                            .setUpdateJSON(getString(R.string.def_app_update_check_url));
                    appUpdater.start();
                }
                return true;
            });
        }

        if (exportRecipes != null) {
            exportRecipes.setOnPreferenceClickListener(preference -> {
                exportDatabase();
                return true;
            });
        }

        if (importRecipes != null) {
            importRecipes.setOnPreferenceClickListener(preference -> {
                importDatabase();
                return true;
            });
        }

        if (clearRecipes != null) {
            clearRecipes.setOnPreferenceClickListener(preference -> {
                clearDatabase();
                return true;
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_app);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (appUpdater != null) {
            appUpdater.stop();
        }
    }

    private void exportDatabase() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");
        intent.putExtra(Intent.EXTRA_TITLE, Constants.DB_RECIPES_BACKUP_FILENAME +
                TimeUtils.getFormattedDate(System.currentTimeMillis(), "MM-dd-yy_hhmmss"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        }
        createFileResultLauncher.launch(intent);
    }

    private void importDatabase() {
        if (getActivity() != null) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder(getActivity())
                    .setTitle(getString(R.string.dialog_confirm_action))
                    .setMessage(getString(R.string.settings_recipe_db_import_message))
                    .setAutoDismiss(true)
                    .setNegativeButton(getString(R.string.cancel),
                            (dialogInterface, which) -> {
                            })
                    .setPositiveButtonWithColor(getString(R.string.settings_recipe_db_import_button),
                            R.color.dialog_positive_button_color_red,
                            (dialogInterface, which) -> requestPermissionAndBrowseFile())
                    .build();
            dialog.show();
        }
    }

    private void clearDatabase() {
        if (getActivity() != null) {
            BottomButtonDialog dialog = new BottomButtonDialog.Builder(getActivity())
                    .setTitle(getString(R.string.dialog_confirm_action))
                    .setMessage(getString(R.string.settings_recipe_db_clear_message))
                    .setAutoDismiss(true)
                    .setNegativeButton(getString(R.string.cancel),
                            (dialogInterface, which) -> {
                            })
                    .setPositiveButtonWithColor(getString(R.string.delete),
                            R.color.dialog_positive_button_color_red,
                            (dialogInterface, which) -> {
                                if (getActivity().getDatabasePath(Constants.DB_RECIPES).exists()) {
                                    RecipeDatabase db = RecipeDatabase.getInstance(getActivity()
                                            .getApplicationContext());
                                    AppExecutors.getInstance().diskIO().execute(db::close);
                                    RecipeDatabase.clearInstance();
                                    if (getActivity().deleteDatabase(Constants.DB_RECIPES)) {
                                        AppExecutors.getInstance().diskIO().execute(() ->
                                                FileUtils.clearImgDir(getActivity()));
                                        AlertUtils.createAlert(getActivity(),
                                                R.string.settings_recipe_db_cleared, 1000);
                                    } else {
                                        AlertUtils.createErrorAlert(getActivity(),
                                                R.string.settings_recipe_db_clear_failed, false);
                                    }
                                } else {
                                    AlertUtils.createAlert(getActivity(),
                                            R.string.settings_recipe_db_not_exist, 1000);
                                }
                            })
                    .build();
            dialog.show();
        }
    }

    private final ActivityResultLauncher<Intent> createFileResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri fileUri = data.getData();

                        if (getActivity() != null) {
                            ProgressDialog dialog = new ProgressDialog.Builder(requireActivity())
                                    .setTitle(getString(R.string.exporting))
                                    .build();
                            dialog.getProgressIndicator().setIndeterminate(true);
                            dialog.show();

                            RecipeDatabase rb = RecipeDatabase.getInstance(getActivity()
                                    .getApplicationContext());

                            AppExecutors.getInstance().diskIO().execute(() -> {
                                final List<RecipesModel> recipeModels = rb.recipeDao().loadAllRecipes();
                                DatabaseUtils.exportDatabase(getActivity(), fileUri, recipeModels,
                                        success -> getActivity().runOnUiThread(() -> {
                                            dialog.dismiss();
                                            if (success) {
                                                AlertUtils.createAlert(getActivity(),
                                                        R.string.backup_success, 1000);
                                            } else {
                                                AlertUtils.createAlert(getActivity(),
                                                        R.string.backup_failed, 1000);
                                            }
                                        }));
                            });
                        }
                    }
                }
            });

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
        intent.setType("application/zip");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS);
        }
        intent = Intent.createChooser(intent, getString(R.string.file_picker_text));
        pickerResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null && result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri fileUri = data.getData();
                        if (getActivity() != null) {
                            ProgressDialog dialog = new ProgressDialog.Builder(requireActivity())
                                    .setTitle(getString(R.string.importing))
                                    .build();
                            dialog.getProgressIndicator().setIndeterminate(true);
                            dialog.show();

                            RecipeDatabase db = RecipeDatabase.getInstance(getActivity()
                                    .getApplicationContext());

                            DatabaseUtils.importDatabase(getActivity(), fileUri, db, success ->
                                    getActivity().runOnUiThread(() -> {
                                dialog.dismiss();
                                if (success) {
                                    AlertUtils.createAlert(getActivity(),
                                            R.string.restore_success, 1000);
                                } else {
                                    AlertUtils.createAlert(getActivity(),
                                            R.string.restore_failed, 1000);
                                }
                            }));

                        }
                    }
                }
            });
}
