package com.weberbox.pifire.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.ActivityPreferencesBinding;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
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

import org.jetbrains.annotations.NotNull;

import dev.chrisbanes.insetter.Insetter;
import io.socket.client.Socket;
import timber.log.Timber;

public class PreferencesActivity extends BaseActivity implements ToolbarTitleCallback {

    private ActivityPreferencesBinding binding;
    boolean adminTrans = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        adminTrans = intent.getBooleanExtra(Constants.INTENT_TRANS_ADMIN, false);
        int fragment = intent.getIntExtra(Constants.INTENT_SETTINGS_FRAGMENT, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (adminTrans) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_left,
                        android.R.anim.fade_out);
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_right,
                        R.anim.slide_out_right);
            } else {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right,
                        android.R.anim.fade_out);
                overrideActivityTransition(OVERRIDE_TRANSITION_CLOSE, R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
        }

        binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.statusBars())
                .applyToView(binding.appBar);

        Socket socket = ((PiFireApplication) getApplication()).getSocket();

        new SettingsUtils(this, settingsSocketCallback).requestSettingsData(socket);

        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            startSettingsFragment(fragment);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (adminTrans) {
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
            } else {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
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
            case Constants.FRAG_NOTIF_SETTINGS -> launchFragment(new NotificationSettingsFragment());
            case Constants.FRAG_MANUAL_SETTINGS -> launchFragment(new ManualSettingsFragment());
            case Constants.FRAG_PWM_SETTINGS -> launchFragment(new PWMSettingsFragment());
        }
    }

    private void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(binding.fragmentContainer.getId(), fragment)
                .commit();
    }

    private final SettingsSocketCallback settingsSocketCallback = results -> {
        if (!results.isEmpty()) Timber.d("%s Settings Update Failed", results);
    };

    @Override
    public void onTitleChange(@NotNull String title) {
        binding.toolbarLayout.setTitle(title);
    }
}
