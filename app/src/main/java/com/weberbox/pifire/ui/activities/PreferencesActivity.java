package com.weberbox.pifire.ui.activities;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.fragments.preferences.AdminSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.AppSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.HistorySettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.NameSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.NotificationSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.PelletSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.ProbeSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.SafetySettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.ShutdownSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.WorkSettingsFragment;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        int fragment = intent.getIntExtra(Constants.INTENT_SETTINGS_FRAGMENT, 0);

        if (savedInstanceState == null) {
            startSettingsFragment(fragment);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startSettingsFragment(int fragment) {
        switch (fragment) {
            case Constants.FRAG_APP_SETTINGS:
                launchFragment(new AppSettingsFragment());
                setActionBarTitle(R.string.settings_app);
                break;
            case Constants.FRAG_PROBE_SETTINGS:
                launchFragment(new ProbeSettingsFragment());
                setActionBarTitle(R.string.settings_probe);
                break;
            case Constants.FRAG_NAME_SETTINGS:
                launchFragment(new NameSettingsFragment());
                setActionBarTitle(R.string.settings_grill_name);
                break;
            case Constants.FRAG_WORK_SETTINGS:
                launchFragment(new WorkSettingsFragment());
                setActionBarTitle(R.string.settings_work);
                break;
            case Constants.FRAG_PELLET_SETTINGS:
                launchFragment(new PelletSettingsFragment());
                setActionBarTitle(R.string.settings_pellets);
                break;
            case Constants.FRAG_SHUTDOWN_SETTINGS:
                launchFragment(new ShutdownSettingsFragment());
                setActionBarTitle(R.string.settings_shutdown);
                break;
            case Constants.FRAG_HISTORY_SETTINGS:
                launchFragment(new HistorySettingsFragment());
                setActionBarTitle(R.string.settings_history);
                break;
            case Constants.FRAG_SAFETY_SETTINGS:
                launchFragment(new SafetySettingsFragment());
                setActionBarTitle(R.string.settings_safety);
                break;
            case Constants.FRAG_NOTIF_SETTINGS:
                launchFragment(new NotificationSettingsFragment());
                setActionBarTitle(R.string.settings_notifications);
                break;
            case Constants.FRAG_ADMIN_SETTINGS:
                launchFragment(new AdminSettingsFragment());
                setActionBarTitle(R.string.settings_admin);
                break;
        }
    }

    private void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .commit();
    }

    public void setActionBarTitle(int title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
