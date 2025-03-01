package com.weberbox.pifire.ui.fragments.setup;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonSyntaxException;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentSetupUrlBinding;
import com.weberbox.pifire.interfaces.SetupProgressCallback;
import com.weberbox.pifire.model.local.ExtraHeadersModel;
import com.weberbox.pifire.model.remote.VersionsDataModel;
import com.weberbox.pifire.model.view.SetupViewModel;
import com.weberbox.pifire.ui.dialogs.CredentialsDialog;
import com.weberbox.pifire.ui.dialogs.CredentialsDialog.DialogAuthCallback;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.NetworkUtils;
import com.weberbox.pifire.utils.SecurityUtils;
import com.weberbox.pifire.utils.SettingsUtils;
import com.weberbox.pifire.utils.VersionUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import timber.log.Timber;

public class URLSetupFragment extends Fragment {

    private FragmentSetupUrlBinding binding;
    private TextInputEditText serverAddress;
    private TextInputLayout serverURLLayout;
    private SetupProgressCallback progressCallback;
    private SettingsUtils settingsUtils;
    private NavController navController;
    private Socket socket;

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

        settingsUtils = new SettingsUtils(requireActivity());

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
                    if (isValidUrl(s.toString())) {
                        serverURLLayout.setError(null);
                    } else {
                        serverURLLayout.setError(getString(R.string.setup_invalid_url));

                    }
                }
            }
        });

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity())
                .get(SetupViewModel.class);
        setupViewModel.getFabEvent().observe(getViewLifecycleOwner(), unused -> {
            if (serverAddress.getText() != null && !isConnecting) {
                if (isValidUrl(serverAddress.getText().toString())) {
                    String baseUrl = createUrl(serverAddress.getText().toString());
                    setupViewModel.setAddress(baseUrl);
                    verifyServerUrl(baseUrl);
                } else {
                    serverURLLayout.setError(getString(R.string.setup_invalid_url));
                }
            }
        });

        setupViewModel.getAddress().observe(getViewLifecycleOwner(), serverAddress -> {
            if (serverAddress != null && !serverAddress.isEmpty()) {
                this.serverAddress.setText(serverAddress);
            }
        });

        if (setupViewModel.getAddress().getValue() == null) {
            setupViewModel.setAddress(Prefs.getString(getString(R.string.prefs_server_address), ""));
        }

        ImageView scanQRButton = binding.useQrcode;
        if (!isCameraAvailable()) {
            scanQRButton.setVisibility(View.GONE);
        }

        scanQRButton.setOnClickListener(view1 -> {
            if (Alerter.isShowing()) {
                Alerter.hide();
            }
            if (isGooglePlayServicesAvailable(requireActivity())) {
                if (!isConnecting) openQRCodeScanner(requireActivity(), setupViewModel);
            } else {
                showAlertError(getString(R.string.setup_error_play_services));
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            progressCallback = (SetupProgressCallback) context;
        } catch (ClassCastException e) {
            Timber.e(e, "Activity does not implement callback");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (socket != null) {
            socket.disconnect();
            socket.off(Socket.EVENT_CONNECT);
            socket.off(Socket.EVENT_CONNECT_ERROR);
            socket = null;
        }
    }

    private void verifyServerUrl(String baseUrl) {
        if (!baseUrl.isEmpty()) {
            if (isValidUrl(baseUrl)) {
                serverAddress.onEditorAction(EditorInfo.IME_ACTION_DONE);
                testConnectionAndFetchVersions(baseUrl);
            } else {
                serverURLLayout.setError(getString(R.string.setup_invalid_url));
            }
        } else {
            serverURLLayout.setError(getString(R.string.setup_blank_url));
        }
    }

    private void testConnectionAndFetchVersions(String baseUrl) {
        if (!isConnecting) {
            if (NetworkUtils.isNetworkAvailable(requireActivity().getApplication())) {
                setConnecting(true);

                String url = baseUrl + ServerConstants.URL_API_SETTINGS;
                HTTPUtils.createHttpGet(requireActivity(), url, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        if (e.getMessage() != null && e.getMessage()
                                .contains("CertPathValidatorException")) {
                            showSelfSignedCertDialog();
                        } else {
                            showAlertErrorAndLog(getString(R.string.http_on_failure),
                                    "HTTP onFailure");
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response)
                            throws IOException {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                checkSupportedVersion(response.body().string(), baseUrl);
                            } else {
                                showAlertErrorAndLog(getString(R.string.http_response_null),
                                        "Response Body Null");
                            }
                        } else {
                            if (response.code() == 401) {
                                showCredentialsDialog(baseUrl);
                            } else {
                                showAlertErrorAndLog(getString(R.string.http_unsuccessful,
                                                String.valueOf(response.code()),
                                                HTTPUtils.getReasonPhrase(response.code())),
                                        "Response Unsuccessful");
                            }
                        }
                        response.close();
                    }
                });
            } else {
                showNoNetworkDialog();
            }
        }
    }

    private void checkSupportedVersion(String response, String baseUrl) {
        VersionsDataModel versionsDataModel = VersionsDataModel.parseJSON(response);
        try {
            VersionsDataModel.Versions versions = versionsDataModel.getSettings().getVersions();
            if (versions != null) {
                String prefsVersion = getString(R.string.prefs_server_version);
                String prefsBuild = getString(R.string.prefs_server_build);
                Prefs.putString(prefsVersion, versions.getServerVersion());
                Prefs.putString(prefsBuild, versions.getServerBuild());
                VersionUtils.checkSupportedServerVersion((result, version, build) -> {
                    switch (result) {
                        case SUPPORTED -> getFullSettingsData(baseUrl);
                        case UNSUPPORTED_MIN -> {
                            Timber.d("Min Server Version Unsupported");
                            showUnsupportedDialog(
                                    getString(R.string.dialog_unsupported_server_min_message,
                                            version, build.isBlank() ? "0" : build,
                                            Prefs.getString(prefsVersion, "1.0.0"),
                                            Prefs.getString(prefsBuild, "0")));
                        }
                        case UNSUPPORTED_MAX -> {
                            Timber.d("Max Server Version Unsupported");
                            showUnsupportedDialog(
                                    getString(R.string.dialog_unsupported_server_max_message,
                                            version, build.isBlank() ? "0" : build,
                                            Prefs.getString(prefsVersion, "1.0.0"),
                                            Prefs.getString(prefsBuild, "0")));
                        }
                        case UNTESTED -> {
                            Timber.d("Unlisted Version in ServerInfo");
                            showUntestedDialog(
                                    getString(R.string.dialog_untested_app_version_message),
                                    baseUrl);
                        }
                    }
                });
            } else {
                showAlertErrorAndLog(getString(R.string.setup_versions_null),
                        "Setup Versions Null");
            }

        } catch (IllegalStateException | JsonSyntaxException |
                 NullPointerException e) {
            showAlertErrorAndLog(getString(R.string.setup_versions_error),
                    "Versions JSON Error");
        }
    }

    private void getFullSettingsData(String baseURl) {
        IO.Options options = new IO.Options();
        Map<String, List<String>> headersMap = new HashMap<>();

        String credentials = SecurityUtils.getCredentials(requireActivity());
        String headers = SecurityUtils.getExtraHeaders(requireActivity());

        if (credentials != null) {
            headersMap.put("Authorization", Collections.singletonList(credentials));
        }

        if (headers != null) {
            ArrayList<ExtraHeadersModel> extraHeaders = ExtraHeadersModel.parseJSON(headers);

            if (extraHeaders != null) {
                for (ExtraHeadersModel header : extraHeaders) {
                    headersMap.put(header.getHeaderKey(),
                            Collections.singletonList(header.getHeaderValue()));
                }
            }
        }

        if (headersMap.isEmpty()) {
            createSocket(baseURl, null);
        } else {
            options.extraHeaders = headersMap;
            createSocket(baseURl, options);
        }

        socket.connect();
        socket.on(Socket.EVENT_CONNECT, args ->
                settingsUtils.requestSettingsData(socket, results -> {
                    if (!results.isEmpty()) {
                        showAlertErrorAndLog(getString(R.string.error_settings_errors, results),
                                "Error saving settings");
                    } else {
                        completeSetup(baseURl);
                    }
                }));
        socket.on(Socket.EVENT_CONNECT_ERROR, args ->
                showAlertErrorAndLog(getString(R.string.setup_socket_error),
                        "Error Connecting to Socket"));
    }

    private void completeSetup(String baseUrl) {
        if (getActivity() != null) {
            Timber.d("Socket Connected Storing URL");
            getActivity().runOnUiThread(() -> {
                setConnecting(false);
                if (Alerter.isShowing()) {
                    Alerter.hide();
                }
                Prefs.putString(getString(R.string.prefs_server_address), baseUrl);
                navController.navigate(R.id.nav_setup_push);
            });
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
            showAlertErrorAndLog(getString(R.string.setup_uri_exception), "Socket URI Error");
        }
    }

    private void showNoNetworkDialog() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                setConnecting(false);
                MaterialDialogText dialog = new MaterialDialogText.Builder(getActivity())
                        .setTitle(getString(R.string.dialog_no_network_title))
                        .setMessage(getString(R.string.dialog_dialog_no_network_title_message))
                        .setPositiveButton(getString(android.R.string.ok),
                                (dialogInterface, which) -> dialogInterface.dismiss())
                        .build();
                dialog.show();
            });
        }
    }

    private void showUnsupportedDialog(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                setConnecting(false);
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

    private void showUntestedDialog(String message, String baseUrl) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                MaterialDialogText dialog = new MaterialDialogText.Builder(getActivity())
                        .setTitle(getString(R.string.dialog_untested_app_version_title))
                        .setMessage(message)
                        .setPositiveButton(getString(android.R.string.ok),
                                (dialogInterface, which) -> {
                                    dialogInterface.dismiss();
                                    getFullSettingsData(baseUrl);
                                })
                        .build();
                dialog.show();
            });
        }
    }

    private void showCredentialsDialog(String baseUrl) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                setConnecting(false);
                new CredentialsDialog(requireActivity(),
                        new DialogAuthCallback() {
                            @Override
                            public void onAuthDialogSave(boolean success) {
                                if (success) {
                                    Prefs.putBoolean(getString(R.string.prefs_server_basic_auth),
                                            true);
                                    verifyServerUrl(baseUrl);
                                } else {
                                    showAlertError(getString(R.string.settings_credentials_error));
                                }
                            }

                            @Override
                            public void onAuthDialogCancel() {
                                setConnecting(false);
                            }
                        }).showDialog();
            });
        }
    }

    private void showSelfSignedCertDialog() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                setConnecting(false);
                new MaterialDialogText.Builder(
                        requireActivity())
                        .setTitle(getString(R.string.setup_server_self_signed_title))
                        .setMessage(getString(R.string.setup_server_self_signed))
                        .setPositiveButton(getString(android.R.string.ok),
                                (dialogInterface, which) ->
                                        dialogInterface.dismiss())
                        .build().show();
            });
        }
    }

    private void showAlertErrorAndLog(String string, String logMessage) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                setConnecting(false);
                Timber.d(logMessage);
                showAlertError(string);
            });
        }
    }

    private void showAlertError(String message) {
        if (getActivity() != null) {
            setConnecting(false);
            AlertUtils.createErrorAlert(getActivity(), message, 5000);
        }
    }

    private boolean isValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private String createUrl(String url) {
        return URLUtil.guessUrl(url).replaceAll("/$", "");
    }

    private void setConnecting(boolean isConnecting) {
        this.isConnecting = isConnecting;
        serverAddress.setEnabled(!isConnecting);
        if (progressCallback != null) progressCallback.onShowProgress(isConnecting);
    }

    public boolean isCameraAvailable() {
        return requireActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY);
    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        return status == ConnectionResult.SUCCESS;
    }

    private void openQRCodeScanner(Activity activity, SetupViewModel viewModel) {
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(activity, options);
        scanner.startScan()
                .addOnSuccessListener(barcode -> {
                    if (barcode.getValueType() == Barcode.TYPE_URL) {
                        if (barcode.getUrl() != null) {
                            viewModel.setAddress(barcode.getUrl().getUrl());
                        } else {
                            Toast.makeText(activity, getString(R.string.setup_scan_qr_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        showAlertError(getString(R.string.setup_qr_invalid_url));
                    }
                })
                .addOnCanceledListener(() -> Toast.makeText(activity,
                        getString(R.string.setup_scan_qr_canceled), Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(activity,
                        getString(R.string.setup_scan_qr_failed), Toast.LENGTH_SHORT).show());
    }
}
