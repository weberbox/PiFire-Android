package com.weberbox.pifire.ui.fragments.setup;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupQrScanBinding;
import com.weberbox.pifire.interfaces.AuthDialogCallback;
import com.weberbox.pifire.ui.dialogs.MessageTextDialog;
import com.weberbox.pifire.ui.dialogs.SetupUserPassDialog;
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
    private Snackbar mErrorSnack;
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

        mErrorSnack = Snackbar.make(view, R.string.setup_error, Snackbar.LENGTH_LONG);

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
            showSnackBarMessage(getActivity(), getString(R.string.setup_error));
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
            showSnackBarMessage(getActivity(), getString(R.string.setup_invalid_url_snack));
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
                                if (mConnectProgress.isShown()) mConnectProgress.setVisibility(View.GONE);
                                MessageTextDialog dialog = new MessageTextDialog(getActivity(),
                                        getString(R.string.setup_server_self_signed_title),
                                        getString(R.string.setup_server_self_signed));
                                dialog.showDialog();
                            });
                        } else {
                            getActivity().runOnUiThread(() ->
                                    showSnackBarMessage(getActivity(), e.getMessage()));
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
                                    SetupUserPassDialog dialog = new SetupUserPassDialog(
                                            getActivity(), QRScanFragment.this);
                                    dialog.showDialog();
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> showSnackBarMessage(getActivity(),
                                        getString(R.string.setup_server_connect_error,
                                                String.valueOf(response.code()), response.message())));
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
                showSnackBarMessage(getActivity(), getString(R.string.setup_error));
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Timber.w(e,"Socket URI Error");
                if(!mErrorSnack.isShown() && getActivity() != null) {
                    showSnackBarMessage(getActivity(), getString(R.string.setup_error));
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
            showSnackBarMessage(getActivity(), getString(R.string.setup_invalid_url_snack));
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
                    if(mErrorSnack.isShown()) {
                        mErrorSnack.dismiss();
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
                    showSnackBarMessage(getActivity(), getString(R.string.setup_invalid_url_snack));
                });
            }
        }
    };

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    private void showSnackBarMessage(Activity activity, String message) {
        if(!mErrorSnack.isShown() && activity != null) {
            if (mConnectProgress.isShown()) mConnectProgress.setVisibility(View.GONE);
            mErrorSnack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
            mErrorSnack.setTextColor(activity.getColor(R.color.colorWhite));
            mErrorSnack.setText(message);
            mErrorSnack.show();
        }
    }
}
