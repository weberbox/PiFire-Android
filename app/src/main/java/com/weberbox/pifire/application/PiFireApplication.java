package com.weberbox.pifire.application;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.secure.SecureCore;
import com.weberbox.pifire.utils.AcraUtils;
import com.weberbox.pifire.utils.FirebaseUtils;
import com.weberbox.pifire.utils.SSLSocketUtils;
import com.weberbox.pifire.utils.SecurityUtils;
import com.weberbox.pifire.utils.log.CrashReportingTree;
import com.weberbox.pifire.utils.log.DebugLogTree;

import org.acra.ACRA;

import java.net.URISyntaxException;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Credentials;
import timber.log.Timber;

public class PiFireApplication extends Application {

    private static PiFireApplication mInstance;
    private static Resources mRes;
    private Socket mSocket;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String url = SecureCore.getAcraUrl();
        String login = SecureCore.getAcraLogin();
        String auth = SecureCore.getAcraAuth();

        if (!url.isEmpty() && !login.isEmpty() && !auth.isEmpty()) {
            ACRA.init(this, AcraUtils.buildAcra(this, url, login, auth));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        mRes = getResources();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if(BuildConfig.DEBUG){
            Timber.plant(new DebugLogTree());
        } else {
            Timber.plant(new CrashReportingTree(getString(R.string.app_name)));
        }

        Timber.tag(getString(R.string.app_name));

        Timber.d("Startup - Application Start");

        if (AppConfig.USE_FIREBASE) {
            Timber.d("Init Firebase");

            FirebaseUtils.initFirebase(this);
            if (Prefs.getBoolean(getString(R.string.prefs_notif_firebase_enabled), true)) {
                FirebaseUtils.initNotificationChannels(this);
                FirebaseUtils.subscribeFirebase();
            }
        }
    }

    public static PiFireApplication getInstance() {
        return mInstance;
    }

    public static Resources getRes() {
        return mRes;
    }

    public Socket getSocket() {
        if (mSocket == null) {
            startSocket();
        }
        return mSocket;
    }

    private void startSocket() {
        String serverURL = Prefs.getString(getString(R.string.prefs_server_address), ServerConstants.DEFAULT_SOCKET_URL);

        boolean allowSelfSignedCerts = Prefs.getBoolean(getString(R.string.prefs_server_unsigned_cert),
                getResources().getBoolean(R.bool.def_security_unsigned_cert));

        Timber.i("Creating Socket connection to: %s", serverURL);

        IO.Options options = new IO.Options();

        if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth), false)) {

            String username = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_user);
            String password = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_password);

            String credentials = Credentials.basic(username, password);

            options.extraHeaders = Collections.singletonMap("Authorization",
                    Collections.singletonList(credentials));

            if (serverURL.startsWith(getString(R.string.https_scheme)) && allowSelfSignedCerts) {
                SSLSocketUtils.set(serverURL, options);
            }

            connectSocket(serverURL, options);

        } else {
            if (serverURL.startsWith(getString(R.string.https_scheme)) && allowSelfSignedCerts) {
                SSLSocketUtils.set(serverURL, options);
                connectSocket(serverURL, options);
            } else {
                connectSocket(serverURL, null);
            }
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
}
