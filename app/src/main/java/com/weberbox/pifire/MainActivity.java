package com.weberbox.pifire;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
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
import com.weberbox.pifire.ui.activities.InfoActivity;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.BannerTransition;
import com.weberbox.pifire.utils.FirebaseUtils;
import com.weberbox.pifire.utils.Log;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration mAppBarConfiguration;
    private MainViewModel mMainViewModel;
    private ActivityMainBinding mBinding;
    private CardView mOfflineBanner;
    private ConstraintLayout mRootContainer;
    private Socket mSocket;

    private boolean mIsReady = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SplashScreen.installSplashScreen(this);

        String serverURL = Prefs.getString(getString(R.string.prefs_server_address), null);

        if (serverURL == null) {
            Intent i = new Intent(MainActivity.this, ServerSetupActivity.class);
            startActivity(i);
            finish();
        }

        PiFireApplication app = (PiFireApplication) getApplication();
        mSocket = app.getSocket();

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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                boolean handled = NavigationUI.onNavDestinationSelected(menuItem, navController);

                if (!handled) {
                    int id = menuItem.getItemId();
                    if (id == R.id.nav_admin) {
                        if (mSocket != null && mSocket.connected()) {
                            Intent intent = new Intent(MainActivity.this, PreferencesActivity.class);
                            intent.putExtra(Constants.INTENT_SETTINGS_FRAGMENT, Constants.FRAG_ADMIN_SETTINGS);
                            startActivity(intent);
                        } else {
                            AnimUtils.shakeOfflineBanner(MainActivity.this);
                        }
                    } else if (id == R.id.nav_info) {
                        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                        startActivity(intent);
                    }
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
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

        if (Prefs.getBoolean(getString(R.string.prefs_notif_firebase_enabled))) {
            FirebaseUtils.subscribeFirebase();
        }

        mMainViewModel.getServerConnected().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean enabled) {
                mIsReady = true;
                if (enabled != null) {
                    toggleOfflineMessage(enabled);
                }
            }
        });

        connectSocketListenData(mSocket);

//        final View content = mBinding.getRoot();
//        content.getViewTreeObserver().addOnPreDrawListener(
//                new ViewTreeObserver.OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        if (mIsReady) {
//                            content.getViewTreeObserver().removeOnPreDrawListener(this);
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    }
//                });

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean restartSocket = intent.getBooleanExtra(Constants.INTENT_SETUP_RESTART, false);

        PiFireApplication app = (PiFireApplication) getApplication();
        if (restartSocket) {
            app.disconnectSocket();
        }
        mSocket = app.getSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        if (mSocket != null) {
            mSocket.emit(ServerConstants.REQUEST_GRILL_DATA);
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
        mSocket.emit(ServerConstants.REQUEST_GRILL_DATA);
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
            Log.i(TAG, "Socket connected");
            mMainViewModel.setServerConnected(true);
        }
    };

    private final Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Socket disconnected");
            mMainViewModel.setServerConnected(false);
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i(TAG, "Error connecting socket");
            mMainViewModel.setServerConnected(false);
        }
    };

    private final Emitter.Listener updateGrillData = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            mMainViewModel.setDashData(args[0].toString());
        }
    };
}