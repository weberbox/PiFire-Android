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

    private AppBarConfiguration mAppBarConfiguration;
    private OverlappingPanelsLayout mPanelsLayout;
    private ActivityMainPanelsBinding mBinding;
    private SettingsUtils mSettingsUtils;
    private MainViewModel mMainViewModel;
    private TextView mActionBarText;
    private FrameLayout mStartPanel;
    private AppUpdater mAppUpdater;
    private FrameLayout mEndPanel;
    private Socket mSocket;
    private int mDownX;

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
        mSocket = app.getSocket();

        mSettingsUtils = new SettingsUtils(this, settingsCallback);

        mBinding = ActivityMainPanelsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.settingsLayout.setCallback(this);

        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(mBinding.appBarMain.toolbar);

        if (getSupportActionBar() != null) {
            setupActionBar(getSupportActionBar());
        }

        mPanelsLayout = mBinding.overlappingPanels;
        mStartPanel = mBinding.startPanel;
        mEndPanel = mBinding.endPanel;

        AnimatedBottomBar bottomBar = findViewById(R.id.bottom_bar);

        NavigationView navigationView = mBinding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
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
                }
            }
            mPanelsLayout.closePanels();
            return true;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            mActionBarText.setText(destination.getLabel());

            if (destination.getId() == R.id.nav_settings) {
                mPanelsLayout.setEndPanelLockState(OverlappingPanelsLayout.LockState.CLOSE);
            } else {
                mPanelsLayout.setEndPanelLockState(OverlappingPanelsLayout.LockState.UNLOCKED);
            }

            if (destination.getId() == R.id.nav_info || destination.getId() == R.id.nav_settings) {
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

        mMainViewModel.getServerConnected().observe(this, connected -> {
            if (connected != null) {
                AlertUtils.toggleOfflineAlert(this, connected);

                if (connected) {
                    if (AppConfig.USE_ONESIGNAL) {
                        if (OneSignalUtils.checkRegistration(MainActivity.this) ==
                                Constants.ONESIGNAL_NOT_REGISTERED) {
                            OneSignalUtils.registerDevice(MainActivity.this, mSocket);
                        }
                    }
                }
            }
        });

        mMainViewModel.getStartPanelStateChange().observe(this, state ->
                mPanelsLayout.handleStartPanelState(state));

        mMainViewModel.getEndPanelStateChange().observe(this, state ->
                mPanelsLayout.handleEndPanelState(state));

        connectSocketListenData(mSocket);

        String updaterUrl = getString(R.string.def_app_update_check_url);

        if (savedInstanceState == null && !updaterUrl.isEmpty()) {
            mAppUpdater = new AppUpdater(this)
                    .setDisplay(Display.DIALOG)
                    .setButtonDoNotShowAgain(R.string.disable_button)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(updaterUrl)
                    .showEvery(Integer.parseInt(Prefs.getString(getString(
                            R.string.prefs_app_updater_frequency),
                            getString(R.string.def_app_updater_frequency))));
            mAppUpdater.start();
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
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (mStartPanel.isShown() || mEndPanel.isShown()) {
            mPanelsLayout.closePanels();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPanelsLayout.registerStartPanelStateListeners(panelState ->
                mMainViewModel.setStartPanelStateChange(panelState));
        mPanelsLayout.registerEndPanelStateListeners(panelState ->
                mMainViewModel.setEndPanelStateChange(panelState));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdater != null) {
            mAppUpdater.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBinding = null;
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(ServerConstants.LISTEN_GRILL_DATA, updateGrillData);
    }

    private final SettingsCallback settingsCallback = result -> {
        if (!result) Timber.d("Update Settings Failed");
    };

    public void settingsOnClick(String settings) {
        switch (settings) {
            case Constants.DB_SET_APP:
                startPreferenceActivity(Constants.FRAG_APP_SETTINGS);
                break;
            case Constants.DB_SET_PROBE:
                startPreferenceActivity(Constants.FRAG_PROBE_SETTINGS);
                break;
            case Constants.DB_SET_NAME:
                startPreferenceActivity(Constants.FRAG_NAME_SETTINGS);
                break;
            case Constants.DB_SET_WORK:
                startPreferenceActivity(Constants.FRAG_WORK_SETTINGS);
                break;
            case Constants.DB_SET_PELLETS:
                startPreferenceActivity(Constants.FRAG_PELLET_SETTINGS);
                break;
            case Constants.DB_SET_SHUTDOWN:
                startPreferenceActivity(Constants.FRAG_SHUTDOWN_SETTINGS);
                break;
            case Constants.DB_SET_HISTORY:
                startPreferenceActivity(Constants.FRAG_HISTORY_SETTINGS);
                break;
            case Constants.DB_SET_SAFETY:
                startPreferenceActivity(Constants.FRAG_SAFETY_SETTINGS);
                break;
            case Constants.DB_SET_NOTIF:
                startPreferenceActivity(Constants.FRAG_NOTIF_SETTINGS);
                break;
        }
    }

    private void startPreferenceActivity(int fragment) {
        mPanelsLayout.closePanels();
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
        mActionBarText = view.findViewById(R.id.action_bar_text);
        ImageButton navButton = view.findViewById(R.id.action_bar_button);
        navButton.setOnClickListener(v ->
                mPanelsLayout.openStartPanel());
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
            mMainViewModel.setServerConnected(true);
            mSettingsUtils.requestSettingsData(mSocket);
        }
    };

    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Socket disconnected");
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                mMainViewModel.setServerConnected(false);
            }
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Error connecting socket");
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                mMainViewModel.setServerConnected(false);
            }
        }
    };

    private final Emitter.Listener updateGrillData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mMainViewModel.setDashData(args[0].toString());
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) event.getRawX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (Math.abs(mDownX - x) > 5) {
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