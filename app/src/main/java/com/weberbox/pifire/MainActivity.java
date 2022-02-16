package com.weberbox.pifire;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.discord.panels.OverlappingPanelsLayout;
import com.google.android.material.navigation.NavigationView;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.ActivityMainPanelsBinding;
import com.weberbox.pifire.interfaces.SettingsCallback;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.ui.activities.BaseActivity;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.ui.fragments.ChangelogFragment;
import com.weberbox.pifire.updater.AppUpdater;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.OneSignalUtils;
import com.weberbox.pifire.utils.SettingsUtils;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration appBarConfiguration;
    private OverlappingPanelsLayout panelsLayout;
    private ActivityMainPanelsBinding binding;
    private SettingsUtils settingsUtils;
    private MainViewModel mainViewModel;
    private TextView actionBarText;
    private FrameLayout startPanel;
    private AppUpdater appUpdater;
    private FrameLayout endPanel;
    private Socket socket;
    private int downX;

    private boolean firstLaunch = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        boolean firstStart = Prefs.getBoolean(getString(R.string.prefs_first_app_start), true);

        if (firstStart) {
            Intent i = new Intent(MainActivity.this, ServerSetupActivity.class);
            startActivity(i);
            finish();
        }

        PiFireApplication app = (PiFireApplication) getApplication();
        socket = app.getSocket();

        settingsUtils = new SettingsUtils(this, settingsCallback);

        binding = ActivityMainPanelsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.settingsLayout.setCallback(this);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(binding.appBarMain.toolbar);

        if (getSupportActionBar() != null) {
            setupActionBar(getSupportActionBar());
        }

        panelsLayout = binding.overlappingPanels;
        startPanel = binding.startPanel;
        endPanel = binding.endPanel;

        AnimatedBottomBar bottomBar = findViewById(R.id.bottom_bar);

        NavigationView navigationView = binding.navView;
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_recipes,
                R.id.nav_pellet_manager,
                R.id.nav_dashboard,
                R.id.nav_history,
                R.id.nav_events)
                .setOpenableLayout(null)
                .build();

        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);

            if (!handled) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_admin) {
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(this,
                            R.anim.slide_in_left, R.anim.slide_out_left);
                    Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                    intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, Constants.FRAG_ADMIN_SETTINGS);
                    startActivity(intent, options.toBundle());
                } else if (id == R.id.nav_info) {
                    navController.navigate(R.id.nav_info);
                } else if (id == R.id.nav_settings) {
                    navController.navigate(R.id.nav_settings);
                } else if (id == R.id.nav_changelog) {
                    showChangelog();
                }
            }
            panelsLayout.closePanels();
            return true;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            actionBarText.setText(destination.getLabel());
            int dest = destination.getId();

            if (dest == R.id.nav_settings) {
                panelsLayout.setEndPanelLockState(OverlappingPanelsLayout.LockState.CLOSE);
            } else if (dest == R.id.nav_history) {
                panelsLayout.setStartPanelLockState(OverlappingPanelsLayout.LockState.CLOSE);
                panelsLayout.setEndPanelLockState(OverlappingPanelsLayout.LockState.CLOSE);
            } else {
                panelsLayout.setStartPanelLockState(OverlappingPanelsLayout.LockState.UNLOCKED);
                panelsLayout.setEndPanelLockState(OverlappingPanelsLayout.LockState.UNLOCKED);
            }

            if (dest == R.id.nav_info || dest == R.id.nav_settings) {
                bottomBar.setVisibility(View.GONE);
            } else {
                bottomBar.setVisibility(View.VISIBLE);
            }
        });


        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.activity_main_tabs);
        bottomBar.setupWithNavController(popupMenu.getMenu(), navController);

        View header = navigationView.getHeaderView(0);
        TextView navGrillName = header.findViewById(R.id.nav_head_grill_name);
        View divider = header.findViewById(R.id.nav_head_divider);

        String grillName = Prefs.getString(getString(R.string.prefs_grill_name), "");

        if (grillName.isEmpty()) {
            navGrillName.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
            navGrillName.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            navGrillName.setText(grillName);
        }

        if (Prefs.getBoolean(getString(R.string.prefs_show_changelog), true)) {
            Prefs.putBoolean(getString(R.string.prefs_show_changelog), false);
            showChangelog();
        }

        mainViewModel.getServerConnected().observe(this, connected -> {
            if (connected != null) {
                AlertUtils.toggleOfflineAlert(this, connected);

                if (AppConfig.USE_ONESIGNAL) {
                    if (connected && firstLaunch) {
                        firstLaunch = false;
                        int registrationResult = OneSignalUtils.checkRegistration(this);
                        if (registrationResult == Constants.ONESIGNAL_NOT_REGISTERED ||
                                registrationResult == Constants.ONESIGNAL_APP_UPDATED) {
                            OneSignalUtils.registerDevice(this, socket, registrationResult);
                        }
                    }
                }
            }
        });

        mainViewModel.getStartPanelStateChange().observe(this, state ->
                panelsLayout.handleStartPanelState(state));

        mainViewModel.getEndPanelStateChange().observe(this, state ->
                panelsLayout.handleEndPanelState(state));

        connectSocketListenData(socket);

        String updaterUrl = getString(R.string.def_app_update_check_url);

        if (savedInstanceState == null && !updaterUrl.isEmpty()) {
            appUpdater = new AppUpdater(this)
                    .setDisplay(Display.DIALOG)
                    .setButtonDoNotShowAgain(R.string.disable_button)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(updaterUrl)
                    .showEvery(Integer.parseInt(Prefs.getString(getString(
                            R.string.prefs_app_updater_frequency),
                            getString(R.string.def_app_updater_frequency))));
            appUpdater.start();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean restartSocket = intent.getBooleanExtra(Constants.INTENT_SETUP_RESTART, false);
        if (restartSocket) {
            PiFireApplication app = (PiFireApplication) getApplication();
            app.disconnectSocket();
            this.recreate();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (startPanel.isShown() || endPanel.isShown()) {
            panelsLayout.closePanels();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        panelsLayout.registerStartPanelStateListeners(panelState ->
                mainViewModel.setStartPanelStateChange(panelState));
        panelsLayout.registerEndPanelStateListeners(panelState ->
                mainViewModel.setEndPanelStateChange(panelState));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (appUpdater != null) {
            appUpdater.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        socket.disconnect();
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(ServerConstants.LISTEN_GRILL_DATA, updateGrillData);
    }

    private final SettingsCallback settingsCallback = result -> {
        if (!result) Timber.d("Update Settings Failed");
    };

    public void onClickAppSettings() {
        startPreferenceActivity(Constants.FRAG_APP_SETTINGS);
    }

    public void onClickProbeSettings() {
        startPreferenceActivity(Constants.FRAG_PROBE_SETTINGS);
    }

    public void onClickNameSettings() {
        startPreferenceActivity(Constants.FRAG_NAME_SETTINGS);
    }

    public void onClickWorkSettings() {
        startPreferenceActivity(Constants.FRAG_WORK_SETTINGS);
    }

    public void onClickPelletSettings() {
        startPreferenceActivity(Constants.FRAG_PELLET_SETTINGS);
    }

    public void onClickTimersSettings() {
        startPreferenceActivity(Constants.FRAG_SHUTDOWN_SETTINGS);
    }

    public void onClickHistorySettings() {
        startPreferenceActivity(Constants.FRAG_HISTORY_SETTINGS);
    }

    public void onClickSafetySettings() {
        startPreferenceActivity(Constants.FRAG_SAFETY_SETTINGS);
    }

    public void onClickNotificationsSettings() {
        startPreferenceActivity(Constants.FRAG_NOTIF_SETTINGS);
    }

    private void startPreferenceActivity(int fragment) {
        panelsLayout.closePanels();
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this,
                R.anim.slide_in_right, R.anim.slide_out_right);
        Intent intent = new Intent(this, PreferencesActivity.class);
        intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, fragment);
        startActivity(intent, options.toBundle());
    }

    private void setupActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.layout_actionbar);
        View view = actionBar.getCustomView();
        actionBarText = view.findViewById(R.id.action_bar_text);
        ImageButton navButton = view.findViewById(R.id.action_bar_button);
        navButton.setOnClickListener(v ->
                panelsLayout.openStartPanel());
    }

    private void showChangelog() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(android.R.id.content, new ChangelogFragment())
                .addToBackStack(null)
                .commit();
    }

    public void connectSocketListenData(Socket socket) {
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(ServerConstants.LISTEN_GRILL_DATA, updateGrillData);
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Socket connected");
            mainViewModel.setServerConnected(true);
            settingsUtils.requestSettingsData(socket);
        }
    };

    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Socket disconnected");
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                mainViewModel.setServerConnected(false);
            }
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Error connecting socket");
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                mainViewModel.setServerConnected(false);
            }
        }
    };

    private final Emitter.Listener updateGrillData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args.length > 0 && args[0] != null) {
                mainViewModel.setDashData(args[0].toString());
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getRawX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (Math.abs(downX - x) > 5) {
                    return super.dispatchTouchEvent(event);
                }
                final int reducePx = 25;
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                outRect.inset(reducePx, reducePx);
                if (!outRect.contains(x, y)) {
                    v.clearFocus();
                    boolean touchTargetIsEditText = false;
                    for (View vi : v.getRootView().getTouchables()) {
                        if (vi instanceof EditText) {
                            Rect clickedViewRect = new Rect();
                            vi.getGlobalVisibleRect(clickedViewRect);
                            clickedViewRect.inset(reducePx, reducePx);
                            if (clickedViewRect.contains(x, y)) {
                                touchTargetIsEditText = true;
                                break;
                            }
                        }
                    }
                    if (!touchTargetIsEditText) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}