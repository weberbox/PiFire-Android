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
import com.weberbox.pifire.utils.OneSignalUtils;
import com.weberbox.pifire.utils.SecurityUtils;
import com.weberbox.pifire.utils.TimberUtils;

import java.net.URI;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Credentials;
import timber.log.Timber;

public class PiFireApplication extends Application {

    private Socket socket;

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if (!AppConfig.DEBUG || Prefs.getBoolean(getString(R.string.prefs_dev_crash_enable))) {
            CrashUtils.initCrashReporting(this);
        } else {
            TimberUtils.configTimber();
        }

        Timber.d("Startup - Application Start");

        OneSignalUtils.initNotificationChannels(this);
        OneSignalUtils.initOneSignal(this);
    }

    public Socket getSocket() {
        if (socket == null) {
            return socket = startSocket();
        }
        return socket;
    }

    private Socket startSocket() {
        URI serverUri = URI.create(Prefs.getString(getString(R.string.prefs_server_address),
                ServerConstants.DEFAULT_SOCKET_URL));

        Timber.i("Creating Socket connection");

        IO.Options options = new IO.Options();

        if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth))) {

            String username = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_user);
            String password = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_password);

            String credentials = Credentials.basic(username, password);

            options.extraHeaders = Collections.singletonMap("Authorization",
                    Collections.singletonList(credentials));

            return IO.socket(serverUri, options);
        } else {
            return IO.socket(serverUri);
        }
    }

    public void disconnectSocket() {
        if (socket != null) {
            Timber.i("Closing Socket");
            socket.disconnect();
            socket.close();
            socket.off();
            socket = null;
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
