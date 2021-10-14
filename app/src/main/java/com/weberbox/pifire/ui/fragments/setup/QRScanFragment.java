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
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSetupQrScanBinding;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.SSLSocketUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Credentials;
import timber.log.Timber;

public class QRScanFragment extends Fragment {
    public static final String TAG = QRScanFragment.class.getSimpleName();

    private FragmentSetupQrScanBinding mBinding;
    private CompoundBarcodeView mBarcodeView;
    private ProgressBar mConnectProgress;
    private Snackbar mErrorSnack;
    private ConstraintLayout mSelfSignedNote;
    private Socket mSocket;
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

        mSelfSignedNote = mBinding.selfSignedNoteContainer;
        AppCompatCheckBox allowSelfSigned = mBinding.setupEnableSelfSigned;

        allowSelfSigned.setChecked(Prefs.getBoolean(getString(R.string.prefs_server_unsigned_cert)));

        mSecure = getString(R.string.https_scheme);
        mUnSecure = getString(R.string.http_scheme);

        allowSelfSigned.setOnCheckedChangeListener((buttonView, isChecked) ->
                Prefs.putBoolean(getString(R.string.prefs_server_unsigned_cert), isChecked));

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
                verifyURLAndTestConnect(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void verifyURLAndTestConnect(String url) {
        if(url.length() !=0 && isValidUrl(url)) {
            if(url.startsWith(mUnSecure) || url.startsWith(mSecure)) {
                testConnection(url);
            } else {
                testConnection(mUnSecure + url);
            }
        } else {
            mConnectProgress.setVisibility(View.GONE);
            if(!mErrorSnack.isShown() && getActivity() != null) {
                showSnackBarMessage(getActivity(), R.string.setup_invalid_url_snack);
            }
        }
    }

    private void testConnection(String Url) {
        if(!mIsConnecting) {
            IO.Options options = new IO.Options();

            boolean allowSelfSignedCerts = Prefs.getBoolean(getString(R.string.prefs_server_unsigned_cert),
                    getResources().getBoolean(R.bool.def_security_unsigned_cert));

            if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth), false)) {
                String username = SecurityUtils.decrypt(getActivity(), R.string.prefs_server_basic_auth_user);
                String password = SecurityUtils.decrypt(getActivity(), R.string.prefs_server_basic_auth_password);

                String credentials = Credentials.basic(username, password);

                options.extraHeaders = Collections.singletonMap("Authorization",
                        Collections.singletonList(credentials));

                if (Url.startsWith(mSecure) && allowSelfSignedCerts) {
                    SSLSocketUtils.set(Url, options);
                }

                connectSocket(Url, options);
            } else {
                if (Url.startsWith(mSecure) && allowSelfSignedCerts) {
                    SSLSocketUtils.set(Url, options);
                    connectSocket(Url, options);
                } else {
                    connectSocket(Url, null);
                }
            }

            mConnectProgress.setVisibility(View.VISIBLE);
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
                if(!mErrorSnack.isShown() && getActivity() != null) {
                    showSnackBarMessage(getActivity(), R.string.setup_error);
                }
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Timber.w(e,"Socket URI Error");
                if(!mErrorSnack.isShown() && getActivity() != null) {
                    showSnackBarMessage(getActivity(), R.string.setup_error);
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
                        .add(R.id.server_setup_fragment, setupCompeteFragment, SetupFinishFragment.TAG)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            mConnectProgress.setVisibility(View.GONE);
            if(!mErrorSnack.isShown() && getActivity() != null) {
                showSnackBarMessage(getActivity(), R.string.setup_invalid_url_snack);
            }
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Timber.d("Successful connection address ok");
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
                    Timber.d("Error connecting bad address");
                    mConnectProgress.setVisibility(View.GONE);
                    mSocket.disconnect();
                    mSocket.close();
                    mIsConnecting = false;
                    if(!mErrorSnack.isShown()) {
                        showSnackBarMessage(getActivity(), R.string.setup_invalid_url_snack);
                    }
                });
            }
        }
    };

    private void showSnackBarMessage(Activity activity, int message) {
        AnimUtils.fadeAnimation(mSelfSignedNote, 300, Constants.FADE_IN);
        mErrorSnack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        mErrorSnack.setTextColor(activity.getColor(R.color.colorWhite));
        mErrorSnack.setText(message);
        mErrorSnack.show();
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }
}
