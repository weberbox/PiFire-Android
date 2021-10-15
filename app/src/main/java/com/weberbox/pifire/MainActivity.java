package com.weberbox.pifire;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.transition.TransitionManager;

import com.google.android.material.navigation.NavigationView;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.ActivityMainBinding;
import com.weberbox.pifire.ui.activities.BaseActivity;
import com.weberbox.pifire.ui.activities.InfoActivity;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.BannerTransition;
import com.weberbox.pifire.updater.AppUpdater;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.UpdateFrom;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel mMainViewModel;
    private ActivityMainBinding mBinding;
    private CardView mOfflineBanner;
    private ConstraintLayout mRootContainer;
    private Socket mSocket;
    private AppUpdater mAppUpdater;
    private int mDownX;

    private boolean mAppIsVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean firstStart = Prefs.getBoolean(getString(R.string.prefs_first_app_start), true);

        if (firstStart) {
            Intent i = new Intent(MainActivity.this, ServerSetupActivity.class);
            startActivity(i);
            finish();
        }

        mSocket = getSocket();

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mMainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setSupportActionBar(mBinding.appBarMain.toolbar);

        mRootContainer = findViewById(R.id.root_container);
        mOfflineBanner = findViewById(R.id.offline_banner);

        AnimatedBottomBar bottomBar = findViewById(R.id.bottom_bar);

        DrawerLayout drawer = mBinding.drawerLayout;
        NavigationView navigationView = mBinding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_settings,
                R.id.nav_pellet_manager,
                R.id.nav_dashboard,
                R.id.nav_history,
                R.id.nav_events)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);

            if (!handled) {
                int id = menuItem.getItemId();
                if (id == R.id.nav_admin) {
                    Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                    intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, Constants.FRAG_ADMIN_SETTINGS);
                    startActivity(intent);
                } else if (id == R.id.nav_info) {
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    startActivity(intent);
                }
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });


        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.activity_main_tabs);
        bottomBar.setupWithNavController(popupMenu.getMenu(), navController);

        View header = navigationView.getHeaderView(0);
        TextView navGrillName = header.findViewById(R.id.nav_head_grill_name);
        View divider = header.findViewById(R.id.nav_head_divider);

        String grillName = Prefs.getString(getString(R.string.prefs_grill_name), "");

        if (!grillName.equals("")) {
            navGrillName.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            navGrillName.setText(grillName);
        } else {
            navGrillName.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        mMainViewModel.getServerConnected().observe(this, enabled -> {
            if (enabled != null) {
                toggleOfflineMessage(enabled);
            }
        });

        connectSocketListenData(mSocket);

        if (savedInstanceState == null) {
            mAppUpdater = new AppUpdater(this)
                    .setDisplay(Display.DIALOG)
                    .setButtonDoNotShowAgain(R.string.disable_button)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(getString(R.string.def_app_update_check_url))
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppIsVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdater != null) {
            mAppUpdater.stop();
        }
        mAppIsVisible = false;
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

    private Socket getSocket() {
        if (mSocket == null) {
            PiFireApplication app = (PiFireApplication) getApplication();
            mSocket = app.getSocket();
        }
        return mSocket;
    }

    public void connectSocketListenData(Socket socket) {
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(ServerConstants.LISTEN_GRILL_DATA, updateGrillData);
    }

    private void toggleOfflineMessage(boolean hide) {
        if (!hide && mOfflineBanner.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(mRootContainer, new BannerTransition());
            mOfflineBanner.setVisibility(View.VISIBLE);
        } else if (hide && mOfflineBanner.getVisibility() == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(mRootContainer, new BannerTransition());
            mOfflineBanner.setVisibility(View.GONE);
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Socket connected");
            mMainViewModel.setServerConnected(true);
        }
    };

    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Socket disconnected");
            if (mAppIsVisible) {
                mMainViewModel.setServerConnected(false);
            }
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Timber.d("Error connecting socket");
            if (mAppIsVisible) {
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
}