package com.weberbox.pifire.ui.fragments.setup;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.FragmentSetupUrlBinding;
import com.weberbox.pifire.interfaces.AuthDialogCallback;
import com.weberbox.pifire.model.view.SetupViewModel;
import com.weberbox.pifire.ui.dialogs.MessageTextDialog;
import com.weberbox.pifire.ui.dialogs.SetupUserPassDialog;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
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

public class URLSetupFragment extends Fragment implements AuthDialogCallback {

    private FragmentSetupUrlBinding mBinding;
    private TextInputEditText mServerAddress;
    private TextInputLayout mServerURLLayout;
    private AutoCompleteTextView mServerScheme;
    private ProgressBar mConnectProgress;
    private NavController mNavController;
    private Socket mSocket;
    private String mValidURL;
    private String mSecure;
    private String mUnSecure;

    private boolean mIsConnecting = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupUrlBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);

        mServerAddress = mBinding.serverAddress;
        mServerURLLayout = mBinding.serverAddressLayout;
        mConnectProgress = mBinding.connectProgressbar;

        mServerScheme = mBinding.serverAddressSchemeTv;

        mSecure = getString(R.string.https_scheme);
        mUnSecure = getString(R.string.http_scheme);

        String[] scheme = new String[] {mUnSecure, mSecure};

        if (getActivity() != null) {
            ArrayAdapter<String> schemesAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.item_menu_popup, scheme);
            mServerScheme.setAdapter(schemesAdapter);
        }

        mServerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mServerURLLayout.setError(getString(R.string.setup_blank_url));
                } else {
                    mServerURLLayout.setError(null);
                }
            }
        });

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity())
                .get(SetupViewModel.class);
        setupViewModel.getFab().observe(getViewLifecycleOwner(), setupFab ->
                setupFab.setOnClickListener(v -> {
            if (mServerAddress.getText() != null && !mIsConnecting) {
                verifyURLAndTestConnect(mServerAddress.getText().toString());
            }
        }));

        setupViewModel.getQRData().observe(getViewLifecycleOwner(), serverAddress -> {
            if (serverAddress != null && !serverAddress.isEmpty()) {
                try {
                    URI uri = new URI(serverAddress);
                    mServerAddress.setText(uri.getHost());
                    if (serverAddress.startsWith(mSecure)) {
                        mServerScheme.setText(mSecure, false);
                    }
                } catch (URISyntaxException e) {
                    AlertUtils.createErrorAlert(requireActivity(), R.string.setup_invalid_url_alert,
                            false);
                    Timber.w(e, "Invalid URI");
                }
            }
        });

        if (setupViewModel.getQRData().getValue() == null) {
            setupViewModel.setQRData(Prefs.getString(getString(R.string.prefs_server_address), ""));
        }

        ImageView scanQRButton = mBinding.useQrcode;
        scanQRButton.setOnClickListener(view1 -> {
            if (Alerter.isShowing()) {
                Alerter.hide();
            }
            requestPermissionCamera();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        }
    }

    @Override
    public void onAuthDialogSave(boolean success) {
        if (success) {
            Prefs.putBoolean(getString(R.string.prefs_server_basic_auth), true);
            if (mServerAddress.getText() != null && !mIsConnecting) {
                verifyURLAndTestConnect(mServerAddress.getText().toString());
            }
        } else {
            showAlerter(getActivity(), getString(R.string.setup_error));
        }
    }

    @Override
    public void onAuthDialogCancel() {
        if (mConnectProgress.isShown()) mConnectProgress.setVisibility(View.GONE);
    }

    private void verifyURLAndTestConnect(String address) {
        if (address.length() != 0) {
            if (isValidUrl(address)) {
                String scheme = String.valueOf(mServerScheme.getText());
                String url = scheme + address;
                mServerAddress.onEditorAction(EditorInfo.IME_ACTION_DONE);
                testServerConnection(url);
            } else {
                mServerURLLayout.setError(getString(R.string.setup_invalid_url));
            }
        } else {
            mServerURLLayout.setError(getString(R.string.setup_blank_url));
        }
    }

    private void testServerConnection(String url) {
        if (!mIsConnecting) {
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
                                dialog.getDialog().show();
                            });
                        } else {
                            getActivity().runOnUiThread(() -> {
                                mConnectProgress.setVisibility(View.GONE);
                                showAlerter(getActivity(), e.getMessage());
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
                                            getActivity(), URLSetupFragment.this);
                                    dialog.showDialog();
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    mConnectProgress.setVisibility(View.GONE);
                                    showAlerter(getActivity(),
                                            getString(R.string.setup_server_connect_error,
                                                    String.valueOf(response.code()), response.message()));
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
        if (!mIsConnecting) {
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

    private void storeValidURL() {
        if (mValidURL != null) {
            Prefs.putString(getString(R.string.prefs_server_address), mValidURL);
            if (AppConfig.USE_ONESIGNAL) {
                mNavController.navigate(R.id.nav_setup_push);
            } else {
                mNavController.navigate(R.id.nav_setup_finish);
            }
        } else {
            showAlerter(getActivity(), getString(R.string.setup_error));
        }
    }

    private void connectSocket(String serverURL, IO.Options options) {
        if (options != null) {
            try {
                mSocket = IO.socket(serverURL, options);
            } catch (URISyntaxException e) {
                Timber.w(e, "Socket URI Error");
                mServerURLLayout.setError(getString(R.string.setup_error));
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Timber.w(e, "Socket URI Error");
                mServerURLLayout.setError(getString(R.string.setup_error));
            }
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() != null) {
                Timber.d("Socket Connected Storing URL");
                getActivity().runOnUiThread(() -> {
                    mConnectProgress.setVisibility(View.GONE);
                    mIsConnecting = false;
                    if (Alerter.isShowing()) {
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
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Timber.d("Error Connecting to Socket");
                    mConnectProgress.setVisibility(View.GONE);
                    mSocket.disconnect();
                    mSocket.close();
                    mIsConnecting = false;
                    showAlerter(getActivity(), getString(R.string.setup_cannot_connect));
                });
            }
        }
    };

    private boolean isValidUrl(String url) {
        if (url.startsWith(mSecure) || url.startsWith(mUnSecure)) {
            return false;
        }
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    private void showAlerter(Activity activity, String message) {
        AlertUtils.createErrorAlert(activity, message, false);
    }

    private void requestPermissionCamera() {
        TedPermission.create()
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (getActivity() != null) {
                            mNavController.navigate(R.id.nav_setup_scan_qr);
                        }
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        AlertUtils.createErrorAlert(getActivity(), R.string.app_permissions_denied,
                                false);
                    }
                })
                .check();
    }
}
