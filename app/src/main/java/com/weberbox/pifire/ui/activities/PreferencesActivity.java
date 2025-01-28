package com.weberbox.pifire.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.ui.fragments.preferences.AdminSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.AppSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.ManualSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.NameSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.NotificationSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.PWMSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.PelletSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.ProbeSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.SafetySettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.TimersSettingsFragment;
import com.weberbox.pifire.ui.fragments.preferences.WorkSettingsFragment;
import com.weberbox.pifire.utils.SettingsUtils;

import io.socket.client.Socket;
import timber.log.Timber;

public class PreferencesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PiFireApplication app = (PiFireApplication) getApplication();
        Socket socket = app.getSocket();

        new SettingsUtils(this, settingsSocketCallback).requestSettingsData(socket);

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
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void startSettingsFragment(int fragment) {
        switch (fragment) {
            case Constants.FRAG_ADMIN_SETTINGS -> launchFragment(new AdminSettingsFragment());
            case Constants.FRAG_APP_SETTINGS -> launchFragment(new AppSettingsFragment());
            case Constants.FRAG_PROBE_SETTINGS -> launchFragment(new ProbeSettingsFragment());
            case Constants.FRAG_NAME_SETTINGS -> launchFragment(new NameSettingsFragment());
            case Constants.FRAG_WORK_SETTINGS -> launchFragment(new WorkSettingsFragment());
            case Constants.FRAG_PELLET_SETTINGS -> launchFragment(new PelletSettingsFragment());
            case Constants.FRAG_TIMERS_SETTINGS -> launchFragment(new TimersSettingsFragment());
            case Constants.FRAG_SAFETY_SETTINGS -> launchFragment(new SafetySettingsFragment());
            case Constants.FRAG_NOTIF_SETTINGS ->
                    launchFragment(new NotificationSettingsFragment());
            case Constants.FRAG_MANUAL_SETTINGS -> launchFragment(new ManualSettingsFragment());
            case Constants.FRAG_PWM_SETTINGS -> launchFragment(new PWMSettingsFragment());
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

    private final SettingsSocketCallback settingsSocketCallback = results -> {
        if (!results.isEmpty()) Timber.d("%s Settings Update Failed", results);
    };
}
