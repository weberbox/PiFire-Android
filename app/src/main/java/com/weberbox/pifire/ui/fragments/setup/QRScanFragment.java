package com.weberbox.pifire.ui.fragments.setup;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupQrScanBinding;
import com.weberbox.pifire.interfaces.AuthDialogCallback;
import com.weberbox.pifire.ui.dialogs.MessageTextDialog;
import com.weberbox.pifire.ui.dialogs.SetupUserPassDialog;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class QRScanFragment extends Fragment implements AuthDialogCallback {

    private FragmentSetupQrScanBinding mBinding;
    private CompoundBarcodeView mBarcodeView;
    private ProgressBar mConnectProgress;
    private Socket mSocket;
    private String mServerAddress;
    private String mValidURL;
    private String mSecure;
    private String mUnSecure;
    private Boolean mIsConnecting = false;

    public static QRScanFragment getInstance(){
        return new QRScanFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupQrScanBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mConnectProgress = mBinding.qrCodeScanProgressbar;
        mBarcodeView = mBinding.barcodeScanner;
        mBarcodeView.getBarcodeView().getCameraSettings().setAutoFocusEnabled(true);
        mBarcodeView.getBarcodeView().getCameraSettings().setContinuousFocusEnabled(true);
        mBarcodeView.setStatusText(null);
        mBarcodeView.decodeContinuous(mBarCodeCallback);

        mSecure = getString(R.string.https_scheme);
        mUnSecure = getString(R.string.http_scheme);

    }

    @Override
    public void onResume() {
        mBarcodeView.resume();
        forceScreenOn();
        super.onResume();
    }

    @Override
    public void onPause() {
        mBarcodeView.pause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        clearForceScreenOn();
        mBinding = null;
        if(mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        }
    }

    private final BarcodeCallback mBarCodeCallback = new BarcodeCallback() {

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                mConnectProgress.setVisibility(View.VISIBLE);
                mServerAddress = result.getText();
                verifyURLAndTestConnect(mServerAddress);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    @Override
    public void onAuthDialogSave(boolean success) {
        if (success) {
            Prefs.putBoolean(getString(R.string.prefs_server_basic_auth), true);
            if(!mServerAddress.isEmpty() && !mIsConnecting) {
                verifyURLAndTestConnect(mServerAddress);
            }
        } else {
            AlertUtils.createErrorAlert(getActivity(), R.string.setup_error, false);
        }
    }

    @Override
    public void onAuthDialogCancel() {
        if (mConnectProgress.isShown()) mConnectProgress.setVisibility(View.GONE);
    }

    private void verifyURLAndTestConnect(String url) {
        if(url.length() != 0 && isValidUrl(url)) {
            if(url.startsWith(mUnSecure) || url.startsWith(mSecure)) {
                testServerConnection(url);
            } else {
                testServerConnection(mUnSecure + url);
            }
        } else {
            mConnectProgress.setVisibility(View.GONE);
            if (!Alerter.isShowing()) {
                AlertUtils.createErrorAlert(getActivity(), R.string.setup_invalid_url_alert,
                        false);
            }
        }
    }

    private void testServerConnection(String url) {
        if(!mIsConnecting) {
            mConnectProgress.setVisibility(View.VISIBLE);
            String credentials;

            if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth), false)) {
                String username = SecurityUtils.decrypt(getActivity(),
                        R.string.prefs_server_basic_auth_user);
                String password = SecurityUtils.decrypt(getActivity(),
                        R.string.prefs_server_basic_auth_password);
                credentials = Credentials.basic(username, password);
            } else {
                credentials = null;
            }

            OkHttpClient client = HTTPUtils.createHttpClient(true, true);
            Request request = HTTPUtils.createHttpRequest(url, credentials);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Timber.d(e, "Request onFailure");
                    if (getActivity() != null) {
                        if (e.getMessage() != null && e.getMessage()
                                .contains("CertPathValidatorException")) {
                            getActivity().runOnUiThread(() -> {
                                mConnectProgress.setVisibility(View.GONE);
                                MessageTextDialog dialog = new MessageTextDialog(getActivity(),
                                        getString(R.string.setup_server_self_signed_title),
                                        getString(R.string.setup_server_self_signed));
                                dialog.showDialog();
                            });
                        } else {
                            getActivity().runOnUiThread(() -> {
                                mConnectProgress.setVisibility(View.GONE);
                                if (!Alerter.isShowing()) {
                                    AlertUtils.createErrorAlert(getActivity(), e.getMessage(),
                                            false);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull final Response response) {
                    if (!response.isSuccessful()) {
                        Timber.d("Response: %s", response.toString());
                        if (response.code() == 401) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    mConnectProgress.setVisibility(View.GONE);
                                    SetupUserPassDialog dialog = new SetupUserPassDialog(
                                            getActivity(), QRScanFragment.this);
                                    dialog.showDialog();
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    mConnectProgress.setVisibility(View.GONE);
                                    AlertUtils.createErrorAlert(getActivity(),
                                            getString(R.string.setup_server_connect_error,
                                                    String.valueOf(response.code()),
                                                    response.message()), false);
                                });
                            }
                        }
                    } else {
                        testSocketConnection(url, credentials);
                    }
                }
            });
        }
    }

    private void testSocketConnection(String Url, String credentials) {
        if(!mIsConnecting) {
            IO.Options options = new IO.Options();

            if (credentials != null) {
                options.extraHeaders = Collections.singletonMap("Authorization",
                        Collections.singletonList(credentials));

                connectSocket(Url, options);
            } else {
                connectSocket(Url, null);
            }

            mIsConnecting = true;

            mValidURL = Url;
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        }
    }

    private void connectSocket(String serverURL, IO.Options options) {
        if (options != null) {
            try {
                mSocket = IO.socket(serverURL, options);
            } catch (URISyntaxException e) {
                Timber.w(e, "Socket URI Error");
                mConnectProgress.setVisibility(View.GONE);
                if(!Alerter.isShowing() && getActivity() != null) {
                    AlertUtils.createErrorAlert(getActivity(), R.string.setup_error, false);
                }
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Timber.w(e,"Socket URI Error");
                mConnectProgress.setVisibility(View.GONE);
                if(!Alerter.isShowing() && getActivity() != null) {
                    AlertUtils.createErrorAlert(getActivity(), R.string.setup_error, false);
                }
            }
        }
    }

    private void storeValidURL() {
        if(mValidURL != null) {
            Prefs.putString(getString(R.string.prefs_server_address), mValidURL);
            SetupFinishFragment setupCompeteFragment = SetupFinishFragment.getInstance();
            if(getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.server_setup_fragment, setupCompeteFragment,
                                SetupFinishFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            mConnectProgress.setVisibility(View.GONE);
            AlertUtils.createErrorAlert(getActivity(), R.string.setup_invalid_url_alert, false);
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Timber.d("Socket Connected Storing URL");
                    mConnectProgress.setVisibility(View.GONE);
                    mIsConnecting = false;
                    if(Alerter.isShowing()) {
                        Alerter.hide();
                    }
                    storeValidURL();
                });
            }
        }
    };

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Timber.d("Error Connecting to Socket");
                    mConnectProgress.setVisibility(View.GONE);
                    mSocket.disconnect();
                    mSocket.close();
                    mIsConnecting = false;
                    AlertUtils.createErrorAlert(getActivity(), R.string.setup_invalid_url_alert,
                            false);
                });
            }
        }
    };

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    private void forceScreenOn() {
        if (getActivity() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void clearForceScreenOn() {
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
