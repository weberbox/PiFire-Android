package com.weberbox.pifire.application;

import android.app.Application;
import android.content.ContextWrapper;
import android.os.StrictMode;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.utils.CrashUtils;
import com.weberbox.pifire.utils.FirebaseUtils;
import com.weberbox.pifire.utils.SecurityUtils;
import com.weberbox.pifire.utils.log.CrashReportingTree;
import com.weberbox.pifire.utils.log.DebugLogTree;

import java.net.URISyntaxException;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Credentials;
import timber.log.Timber;

public class PiFireApplication extends Application {

    private Socket mSocket;

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if (!AppConfig.DEBUG) {
            Timber.plant(new CrashReportingTree(getString(R.string.app_name)));
            CrashUtils.initCrashReporting(this,
                    Prefs.getBoolean(getString(R.string.prefs_crash_enable)));
            CrashUtils.setUserEmail(Prefs.getString(getString(R.string.prefs_crash_user_email)));
        } else {
            Timber.plant(new DebugLogTree());
        }

        Timber.tag(getString(R.string.app_name));

        Timber.d("Startup - Application Start");

        String serverUrl = getString(R.string.def_firebase_server_url);

        if (AppConfig.USE_FIREBASE && !serverUrl.isEmpty()) {
            Timber.d("Init Firebase");

            FirebaseUtils.initFirebase(this);
            FirebaseUtils.initNotificationChannels(this);
            String uuid = Prefs.getString(getString(R.string.prefs_notif_firebase_serveruuid));

            if (Prefs.getBoolean(getString(R.string.prefs_notif_firebase_enabled))
                    && !uuid.isEmpty()) {
                FirebaseUtils.toggleFirebaseSubscription(true, uuid);
            }
        }
    }

    public Socket getSocket() {
        if (mSocket == null) {
            startSocket();
        }
        return mSocket;
    }

    private void startSocket() {
        String serverURL = Prefs.getString(getString(R.string.prefs_server_address), ServerConstants.DEFAULT_SOCKET_URL);

        Timber.i("Creating Socket connection to: %s", serverURL);

        IO.Options options = new IO.Options();

        if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth))) {

            String username = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_user);
            String password = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_password);

            String credentials = Credentials.basic(username, password);

            options.extraHeaders = Collections.singletonMap("Authorization",
                    Collections.singletonList(credentials));

            connectSocket(serverURL, options);

        } else {
            connectSocket(serverURL, null);
        }
    }

    private void connectSocket(String serverURL, IO.Options options) {
        if (options != null) {
            try {
                mSocket = IO.socket(serverURL, options);
            } catch (URISyntaxException e) {
                Timber.w(e, "Socket URI Error");
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Timber.w(e,"Socket URI Error");
            }
        }
    }

    public void disconnectSocket() {
        if (mSocket != null) {
            Timber.i("Closing Socket");
            mSocket.disconnect();
            mSocket.close();
            mSocket.off();
            mSocket = null;
        }
    }

    @SuppressWarnings("unused")
    private void strictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }
}
