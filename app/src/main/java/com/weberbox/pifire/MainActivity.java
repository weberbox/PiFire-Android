package com.weberbox.pifire;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.splashscreen.SplashScreen;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.discord.panels.OverlappingPanelsLayout;
import com.discord.panels.OverlappingPanelsLayout.Panel;
import com.discord.panels.PanelsChildGestureRegionObserver;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.ActivityMainPanelsBinding;
import com.weberbox.pifire.databinding.LayoutNavHeaderLeftBinding;
import com.weberbox.pifire.enums.OneSignalResult;
import com.weberbox.pifire.enums.ServerSupport;
import com.weberbox.pifire.interfaces.NavBindingCallback;
import com.weberbox.pifire.interfaces.ServerInfoCallback;
import com.weberbox.pifire.interfaces.SettingsBindingCallback;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.model.remote.DashDataModel;
import com.weberbox.pifire.model.view.MainViewModel;
import com.weberbox.pifire.ui.activities.BaseActivity;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.ui.adapter.MainPagerAdapter;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.ui.fragments.ChangelogFragment;
import com.weberbox.pifire.ui.fragments.InfoFragment;
import com.weberbox.pifire.ui.views.NavListItem;
import com.weberbox.pifire.update.UpdateUtils;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.OneSignalUtils;
import com.weberbox.pifire.utils.SettingsUtils;
import com.weberbox.pifire.utils.VersionUtils;

import java.util.Collections;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements
        PanelsChildGestureRegionObserver.GestureRegionsListener, ServerInfoCallback {

    private OverlappingPanelsLayout panelsLayout;
    private ActivityMainPanelsBinding binding;
    private AnimatedBottomBar bottomBar;
    private SettingsUtils settingsUtils;
    private MainViewModel mainViewModel;
    private ViewPager2 viewPager;
    private TextView actionBarText;
    private FrameLayout startPanel;
    private UpdateUtils updateUtils;
    private FrameLayout endPanel;
    private NavListItem navDashboard, navPellets, navEvents;
    private Socket socket;
    private int downX;

    private boolean firstLaunch = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        if (VersionUtils.checkFirstRun(this)) {
            Intent i = new Intent(MainActivity.this, ServerSetupActivity.class);
            startActivity(i);
            finish();
            return;
        }

        if (Prefs.getBoolean(getString(R.string.prefs_show_changelog), true)) {
            Prefs.putBoolean(getString(R.string.prefs_show_changelog), false);
            showFragment(new ChangelogFragment());
        }

        getOnBackPressedDispatcher().addCallback(this, onBackCallback);

        socket = ((PiFireApplication) getApplication()).getSocket();

        settingsUtils = new SettingsUtils(this, settingsSocketCallback);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_panels);

        binding.settingsLayoutPanel.setCallback(settingsBindingCallback);
        binding.navLayoutPanel.setCallback(navBindingCallback);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(binding.appBarMainPanel.toolbar);

        if (getSupportActionBar() != null) {
            setupActionBar(getSupportActionBar());
        }

        panelsLayout = binding.overlappingPanels;
        startPanel = binding.startPanel;
        endPanel = binding.endPanel;

        navDashboard = binding.navLayoutPanel.navDashboard;
        navPellets = binding.navLayoutPanel.navPellets;
        navEvents = binding.navLayoutPanel.navEvents;

        bottomBar = binding.appBarMainPanel.contentMain.bottomBar;
        viewPager = binding.appBarMainPanel.contentMain.viewPager;

        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(pagerAdapter.getItemCount() - 1);
        viewPager.setCurrentItem(Constants.FRAG_DASHBOARD, false);
        viewPager.registerOnPageChangeCallback(onPageChangeCallback);

        bottomBar.setupWithViewPager2(viewPager);
        actionBarText.setText(R.string.menu_dashboard);

        PanelsChildGestureRegionObserver.Provider.get().register(viewPager);

        LayoutNavHeaderLeftBinding header = binding.navLayoutPanel.navLeftHeader;
        TextView navGrillName = header.navHeadGrillName;

        String grillName = Prefs.getString(getString(R.string.prefs_grill_name), "");

        if (grillName.isEmpty()) {
            navGrillName.setVisibility(View.GONE);
        } else {
            navGrillName.setVisibility(View.VISIBLE);
            navGrillName.setText(grillName);
        }

        if (!Prefs.getBoolean(getString(R.string.prefs_dc_fan))) {
            binding.settingsLayoutPanel.settingsPwm.setVisibility(View.GONE);
        }

        mainViewModel.getServerConnected().observe(this, connected -> {
            if (connected != null) {
                AlertUtils.toggleOfflineAlert(this, connected);

                if (connected && firstLaunch) {
                    firstLaunch = false;
                    OneSignalResult registrationResult = OneSignalUtils.checkRegistration(this);
                    if (registrationResult == OneSignalResult.ONESIGNAL_NOT_REGISTERED ||
                            registrationResult == OneSignalResult.ONESIGNAL_APP_UPDATED) {
                        OneSignalUtils.registerDevice(this, socket, registrationResult);
                    }
                }
            }
        });

        mainViewModel.getStartPanelStateChange().observe(this, state ->
                panelsLayout.handleStartPanelState(state));

        mainViewModel.getEndPanelStateChange().observe(this, state ->
                panelsLayout.handleEndPanelState(state));

        connectSocketListenData(socket);

        VersionUtils.checkSupportedServerVersion(this);

        if (AppConfig.IS_PLAY_BUILD) {
            updateUtils = new UpdateUtils(this, activityResultLauncher);
            if (savedInstanceState == null) {
                updateUtils.checkForUpdate(false, false);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConfig.IS_PLAY_BUILD && updateUtils != null) {
            updateUtils.checkForUpdate(false, true);
        }
        PanelsChildGestureRegionObserver.Provider.get().addGestureRegionsUpdateListener(this);
        panelsLayout.registerStartPanelStateListeners(panelState ->
                mainViewModel.setStartPanelStateChange(panelState));
        panelsLayout.registerEndPanelStateListeners(panelState ->
                mainViewModel.setEndPanelStateChange(panelState));
    }

    @Override
    protected void onPause() {
        super.onPause();
        PanelsChildGestureRegionObserver.Provider.get().removeGestureRegionsUpdateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppConfig.IS_PLAY_BUILD && updateUtils != null) {
            updateUtils.stopAppUpdater();
        }
    }

    @Override
    public void onDestroy() {
        if (binding != null) {
            binding = null;
        }
        if (socket != null) {
            socket.disconnect();
            socket.off(Socket.EVENT_CONNECT, onConnect);
            socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.off(ServerConstants.LISTEN_GRILL_DATA, updateGrillData);
        }
        if (viewPager != null) {
            PanelsChildGestureRegionObserver.Provider.get().unregister(viewPager);
        }
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean restartSocket = intent.getBooleanExtra(Constants.INTENT_SETUP_RESTART, false);
        if (restartSocket) {
            socket = null;
            socket = ((PiFireApplication) getApplication()).getSocket();
            connectSocketListenData(socket);
        }
    }

    @Override
    public void onGestureRegionsUpdate(@NonNull List<Rect> list) {
        panelsLayout.setChildGestureRegions(list);
    }

    private void closePanelsDelayed() {
        Panel panel = panelsLayout.getSelectedPanel();
        if (panel == Panel.START || panel == Panel.END) {
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    panelsLayout.closePanels(), 800L);
        }
    }

    private final SettingsSocketCallback settingsSocketCallback = result -> {
        switch (result) {
            case GENERAL -> showSettingsError(getString(R.string.error_settings_general));
            case PROBES, GLOBALS, VERSIONS, IFTTT, PUSHBULLET, PUSHOVER, ONESIGNAL, INFLUXDB,
                    APPRISE, CYCLE_DATA, KEEP_WARM, SMOKE_PLUS, PWM, SAFETY, PELLET_LEVEL,
                    MODULES ->
                    showSettingsError(getString(R.string.error_settings_section, result));
            default -> {
            }
        }
    };

    private void showSettingsError(String error) {
        runOnUiThread(() -> Alerter.create(MainActivity.this)
                .setText(error)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14AllerBold)
                .enableInfiniteDuration(true)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .show());
    }

    private final OnBackPressedCallback onBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (startPanel.isShown() || endPanel.isShown()) {
                panelsLayout.closePanels();
            } else if (viewPager.getCurrentItem() != Constants.FRAG_DASHBOARD) {
                viewPager.setCurrentItem(Constants.FRAG_DASHBOARD);
            } else {
                finish();
            }
        }
    };

    @Override
    public void onServerInfo(ServerSupport result, String version, String build) {
        switch (result) {
            case SUPPORTED -> Timber.d("Server Version Supported");
            case UNSUPPORTED_MIN -> {
                Timber.d("Min Server Version Unsupported");
                showUnsupportedDialog(getString(R.string.dialog_unsupported_server_min_message,
                        version, build.isBlank() ? "0" : build,
                        Prefs.getString("prefs_server_version", "1.0.0"),
                        Prefs.getString("prefs_server_build", "0")));
            }
            case UNSUPPORTED_MAX -> {
                Timber.d("Max Server Version Unsupported");
                showUnsupportedDialog(getString(R.string.dialog_unsupported_server_max_message,
                        version, build.isBlank() ? "0" : build,
                        Prefs.getString("prefs_server_version", "1.0.0"),
                        Prefs.getString("prefs_server_build", "0")));
            }
            case FAILED -> VersionUtils.getRawSupportedVersion(this, this);
            case UNTESTED -> {
                Timber.d("Unlisted Version in ServerInfo");
                showUntestedDialog(getString(R.string.dialog_untested_app_version_message));
            }
        }
    }

    private void showUnsupportedDialog(String message) {
        runOnUiThread(() -> {
            MaterialDialogText dialog = new MaterialDialogText.Builder(MainActivity.this)
                    .setTitle(getString(R.string.dialog_unsupported_server_version_title))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.exit),
                            (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                                finish();
                            })
                    .build();
            dialog.show();
        });
    }

    private void showUntestedDialog(String message) {
        runOnUiThread(() -> {
            MaterialDialogText dialog = new MaterialDialogText.Builder(MainActivity.this)
                    .setTitle(getString(R.string.dialog_untested_app_version_title))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.close),
                            (dialogInterface, which) -> dialogInterface.dismiss())
                    .build();
            dialog.show();
        });
    }

    private final SettingsBindingCallback settingsBindingCallback = fragment -> {
        closePanelsDelayed();
        Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
        intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, fragment);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out);
    };

    private final NavBindingCallback navBindingCallback = new NavBindingCallback() {
        @Override
        public void onNavItemClick(int fragment) {
            panelsLayout.closePanels();
            viewPager.setCurrentItem(fragment, true);
        }

        @Override
        public void onNavAdmin() {
            closePanelsDelayed();
            Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
            intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, Constants.FRAG_ADMIN_SETTINGS);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        }

        @Override
        public void onNavInfo() {
            closePanelsDelayed();
            showFragment(new InfoFragment());
        }

        @Override
        public void onNavChangelog() {
            closePanelsDelayed();
            showFragment(new ChangelogFragment());
        }
    };

    private final OnPageChangeCallback onPageChangeCallback = new OnPageChangeCallback() {

        private boolean settled = false;

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            bottomBar.selectTabAt(position, true);
            if (bottomBar.getSelectedTab() != null) {
                actionBarText.setText(bottomBar.getSelectedTab().getTitle());
            }
            navDashboard.setNavSelected(position == Constants.FRAG_DASHBOARD);
            navPellets.setNavSelected(position == Constants.FRAG_PELLETS);
            navEvents.setNavSelected(position == Constants.FRAG_EVENTS);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                settled = false;
            }
            if (state == ViewPager2.SCROLL_STATE_SETTLING) {
                settled = true;
            }
            if (state == ViewPager2.SCROLL_STATE_IDLE && !settled) {
                if (viewPager.getCurrentItem() == 0) {
                    onGestureRegionsUpdate(Collections.emptyList());
                    panelsLayout.openStartPanel();
                } else if (viewPager.getAdapter() != null && viewPager.getCurrentItem() ==
                        viewPager.getAdapter().getItemCount() - 1) {
                    onGestureRegionsUpdate(Collections.emptyList());
                    panelsLayout.openEndPanel();
                }
            }
        }
    };

    private void setupActionBar(ActionBar actionBar) {
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.layout_actionbar);
        View view = actionBar.getCustomView();
        actionBarText = view.findViewById(R.id.action_bar_text);
        ImageButton navButton = view.findViewById(R.id.action_bar_nav_button);
        ImageButton configButton = view.findViewById(R.id.action_bar_config_button);
        navButton.setOnClickListener(v -> {
            onGestureRegionsUpdate(Collections.emptyList());
            panelsLayout.openStartPanel();
        });
        configButton.setOnClickListener(v -> {
            onGestureRegionsUpdate(Collections.emptyList());
            panelsLayout.openEndPanel();
        });
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_enter, R.animator.fragment_fade_exit)
                .replace(android.R.id.content, fragment)
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
                try {
                    mainViewModel.setDashData(DashDataModel.parseJSON(args[0].toString()));
                } catch (JsonSyntaxException e) {
                    Timber.w(e, "Dash JSON parsing error");
                }
            }
        }
    };

    private final ActivityResultLauncher<IntentSenderRequest> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result != null && updateUtils != null) {
                    updateUtils.handleUpdateRequest(result.getResultCode());
                }
            });

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