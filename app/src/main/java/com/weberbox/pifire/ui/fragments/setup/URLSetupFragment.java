package com.weberbox.pifire.ui.fragments.setup;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
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
import com.weberbox.pifire.databinding.FragmentSetupUrlBinding;
import com.weberbox.pifire.enums.ServerSupport;
import com.weberbox.pifire.enums.SettingsResult;
import com.weberbox.pifire.interfaces.ServerInfoCallback;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.model.view.SetupViewModel;
import com.weberbox.pifire.ui.activities.ServerSetupActivity;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.ui.dialogs.UserPassDialog;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogAuthCallback;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.SecurityUtils;
import com.weberbox.pifire.utils.SettingsUtils;
import com.weberbox.pifire.utils.VersionUtils;

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

public class URLSetupFragment extends Fragment implements DialogAuthCallback, ServerInfoCallback {

    private FragmentSetupUrlBinding binding;
    private TextInputEditText serverAddress;
    private TextInputLayout serverURLLayout;
    private AutoCompleteTextView serverScheme;
    private SettingsUtils settingsUtils;
    private ProgressBar connectProgress;
    private NavController navController;
    private Socket socket;
    private String validURL;
    private String secure;
    private String unSecure;

    private boolean isConnecting = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetupUrlBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        serverAddress = binding.serverAddress;
        serverURLLayout = binding.serverAddressLayout;

        connectProgress = ((ServerSetupActivity) requireActivity()).getProgressBar();

        serverScheme = binding.serverAddressSchemeTv;

        secure = getString(R.string.https_scheme);
        unSecure = getString(R.string.http_scheme);

        String[] scheme = new String[]{unSecure, secure};

        if (getActivity() != null) {
            ArrayAdapter<String> schemesAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.item_menu_popup, scheme);
            serverScheme.setAdapter(schemesAdapter);
        }

        settingsUtils = new SettingsUtils(requireActivity(), settingsSocketCallback);

        serverAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    serverURLLayout.setError(getString(R.string.setup_blank_url));
                } else {
                    serverURLLayout.setError(null);
                }
            }
        });

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity())
                .get(SetupViewModel.class);
        setupViewModel.getFabEvent().observe(getViewLifecycleOwner(), unused -> {
            if (serverAddress.getText() != null && !isConnecting) {
                verifyURLAndTestConnect(serverAddress.getText().toString());
            }
        });

        setupViewModel.getQRData().observe(getViewLifecycleOwner(), serverAddress -> {
            if (serverAddress != null && !serverAddress.isEmpty()) {
                try {
                    URI uri = new URI(serverAddress);
                    this.serverAddress.setText(uri.getHost());
                    if (serverAddress.startsWith(secure)) {
                        serverScheme.setText(secure, false);
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

        ImageView scanQRButton = binding.useQrcode;
        if (!cameraAvailable()) {
            scanQRButton.setVisibility(View.GONE);
        }

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
        if (socket != null) {
            socket.disconnect();
            socket.off(Socket.EVENT_CONNECT, onConnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        }
    }

    @Override
    public void onAuthDialogSave(boolean success) {
        if (success) {
            Prefs.putBoolean(getString(R.string.prefs_server_basic_auth), true);
            if (serverAddress.getText() != null && !isConnecting) {
                verifyURLAndTestConnect(serverAddress.getText().toString());
            }
        } else {
            showAlerter(getActivity(), getString(R.string.setup_error));
        }
    }

    @Override
    public void onAuthDialogCancel() {
        if (connectProgress.isShown()) connectProgress.setVisibility(View.GONE);
    }

    private void verifyURLAndTestConnect(String address) {
        if (address.length() != 0) {
            if (isValidUrl(address)) {
                String scheme = String.valueOf(serverScheme.getText());
                String url = scheme + address;
                serverAddress.onEditorAction(EditorInfo.IME_ACTION_DONE);
                testServerConnection(url);
            } else {
                serverURLLayout.setError(getString(R.string.setup_invalid_url));
            }
        } else {
            serverURLLayout.setError(getString(R.string.setup_blank_url));
        }
    }

    private void testServerConnection(String url) {
        if (!isConnecting) {
            connectProgress.setVisibility(View.VISIBLE);
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
                                connectProgress.setVisibility(View.GONE);
                                MaterialDialogText dialog = new MaterialDialogText.Builder(
                                        requireActivity())
                                        .setTitle(getString(R.string.setup_server_self_signed_title))
                                        .setMessage(getString(R.string.setup_server_self_signed))
                                        .setPositiveButton(getString(R.string.close),
                                                (dialogInterface, which) ->
                                                        dialogInterface.dismiss())
                                        .build();
                                dialog.show();
                            });
                        } else {
                            getActivity().runOnUiThread(() -> {
                                connectProgress.setVisibility(View.GONE);
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
                                    connectProgress.setVisibility(View.GONE);
                                    UserPassDialog dialog = new UserPassDialog(
                                            getActivity(), R.string.setup_server_auth_required,
                                            URLSetupFragment.this);
                                    dialog.showDialog();
                                });
                            }
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    connectProgress.setVisibility(View.GONE);
                                    showAlerter(getActivity(),
                                            getString(R.string.setup_server_connect_error,
                                                    String.valueOf(response.code()), response.message()));
                                });
                            }
                        }
                    } else {
                        testSocketConnection(url, credentials);
                    }
                    response.close();
                }
            });
        }
    }

    private void testSocketConnection(String Url, String credentials) {
        if (!isConnecting) {
            IO.Options options = new IO.Options();

            isConnecting = true;

            if (credentials != null) {
                options.extraHeaders = Collections.singletonMap("Authorization",
                        Collections.singletonList(credentials));

                createSocket(Url, options);
            } else {
                createSocket(Url, null);
            }

            validURL = Url;
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        }
    }

    private void storeValidURL() {
        if (validURL != null) {
            Prefs.putString(getString(R.string.prefs_server_address), validURL);
            navController.navigate(R.id.nav_setup_push);
        } else {
            showAlerter(getActivity(), getString(R.string.setup_error));
        }
    }

    private void createSocket(String serverURL, IO.Options options) {
        try {
            if (options != null) {
                socket = IO.socket(serverURL, options);
            } else {
                socket = IO.socket(serverURL);
            }
        } catch (URISyntaxException e) {
            Timber.w(e, "Socket URI Error");
            isConnecting = false;
            serverURLLayout.setError(getString(R.string.setup_error));
        }
    }

    private final Emitter.Listener onConnect = args -> settingsUtils.requestSettingsData(socket);

    private final Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Timber.d("Error Connecting to Socket");
                    connectProgress.setVisibility(View.GONE);
                    socket.disconnect();
                    socket.close();
                    isConnecting = false;
                    showAlerter(getActivity(), getString(R.string.setup_cannot_connect));
                });
            }
        }
    };

    private final SettingsSocketCallback settingsSocketCallback = result -> {
        if (result == SettingsResult.SUCCESS) {
            VersionUtils.checkSupportedServerVersion(this);
        } else {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Timber.d("Error Connecting to Socket");
                    connectProgress.setVisibility(View.GONE);
                    socket.disconnect();
                    socket.close();
                    isConnecting = false;
                    showAlerter(getActivity(),
                            getString(R.string.setup_settings_error));
                });
            }
        }
    };

    @Override
    public void onServerInfo(ServerSupport result, String version, String build) {
        switch (result) {
            case SUPPORTED -> completeSetup();
            case UNSUPPORTED_MIN -> {
                Timber.d("Min Server Version Unsupported");
                showUnsupportedDialog(getString(R.string.dialog_unsupported_server_min_message,
                        version, build.isBlank() ? "0" : build));
            }
            case UNSUPPORTED_MAX -> {
                Timber.d("Max Server Version Unsupported");
                showUnsupportedDialog(getString(R.string.dialog_unsupported_server_max_message,
                        version, build.isBlank() ? "0" : build));
            }
            case FAILED -> VersionUtils.getRawSupportedVersion(requireActivity(), this);
        }
    }

    private void showUnsupportedDialog(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                connectProgress.setVisibility(View.GONE);
                socket.disconnect();
                socket.close();
                isConnecting = false;
                MaterialDialogText dialog = new MaterialDialogText.Builder(getActivity())
                        .setTitle(getString(R.string.dialog_unsupported_server_version_title))
                        .setMessage(message)
                        .setPositiveButton(getString(android.R.string.ok),
                                (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();
                dialog.show();
            });
        }
    }

    private void completeSetup() {
        if (getActivity() != null) {
            Timber.d("Socket Connected Storing URL");
            getActivity().runOnUiThread(() -> {
                connectProgress.setVisibility(View.GONE);
                isConnecting = false;
                if (Alerter.isShowing()) {
                    Alerter.hide();
                }
                storeValidURL();
            });
        }
    }

    private boolean isValidUrl(String url) {
        if (url.startsWith(secure) || url.startsWith(unSecure)) {
            return false;
        }
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    private void showAlerter(Activity activity, String message) {
        AlertUtils.createErrorAlert(activity, message, false);
    }

    public boolean cameraAvailable() {
        return requireActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY);
    }

    private void requestPermissionCamera() {
        TedPermission.create()
                .setPermissions(Manifest.permission.CAMERA)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (getActivity() != null) {
                            navController.navigate(R.id.nav_setup_scan_qr);
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
