package com.weberbox.pifire.application;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.secure.SecureCore;
import com.weberbox.pifire.utils.AcraUtils;
import com.weberbox.pifire.utils.Log;
import com.weberbox.pifire.utils.SSLSocketUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.acra.ACRA;

import java.net.URISyntaxException;
import java.util.Collections;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Credentials;


public class PiFireApplication extends Application {
    private static final String TAG = PiFireApplication.class.getSimpleName();

    private static PiFireApplication mInstance;
    private static Resources res;
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
        res = getResources();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();


    }

    public static PiFireApplication getInstance() {
        return mInstance;
    }

    public static Resources getRes() {
        return res;
    }

    public Socket getSocket() {
        if (mSocket == null) {
            startSocket();
        }
        return mSocket;
    }

    private void startSocket() {
        String serverURL = Prefs.getString(getString(R.string.prefs_server_address), ServerConstants.DEFAULT_SOCKET_URL);

        Log.i(TAG, "Creating Socket connection to: " + serverURL);

        IO.Options options = new IO.Options();

        if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth), false)) {

            String username = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_user);
            String password = SecurityUtils.decrypt(this, R.string.prefs_server_basic_auth_password);

            String credentials = Credentials.basic(username, password);

            options.extraHeaders = Collections.singletonMap("Authorization",
                    Collections.singletonList(credentials));

            if (serverURL.startsWith("https://")) {
                SSLSocketUtils.set(options);
            }

            connectSocket(serverURL, options);

        } else {
            if (serverURL.startsWith("https://")) {
                SSLSocketUtils.set(options);
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
                Log.d("Socket URI Error", e.toString());
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Log.d("Socket URI Error", e.toString());
            }
        }
    }

    public void disconnectSocket() {
        if (mSocket != null) {
            Log.d(TAG, "Closing Socket");
            mSocket.disconnect();
            mSocket.close();
            mSocket.off();
            mSocket = null;
        }
    }
}
