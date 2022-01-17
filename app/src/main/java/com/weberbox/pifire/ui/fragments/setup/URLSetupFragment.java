package com.weberbox.pifire.ui.fragments.setup;

import android.app.Activity;
import android.content.res.ColorStateList;
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
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSetupUrlBinding;
import com.weberbox.pifire.interfaces.AuthDialogCallback;
import com.weberbox.pifire.ui.dialogs.MessageTextDialog;
import com.weberbox.pifire.ui.dialogs.SetupUserPassDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
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

    public static URLSetupFragment getInstance(){
        return new URLSetupFragment();
    }

    private FragmentSetupUrlBinding mBinding;
    private Button mSkipButton;
    private Snackbar mErrorSnack;
    private TextInputEditText mServerAddress;
    private TextInputLayout mServerURLLayout;
    private AutoCompleteTextView mServerScheme;
    private ProgressBar mConnectProgress;
    private ArrayAdapter<String> mSchemes;
    private Socket mSocket;
    private String mValidURL;
    private String mSecure;
    private String mUnSecure;
    private Boolean mIsConnecting = false;

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

        mServerAddress = mBinding.serverAddress;
        mServerURLLayout = mBinding.serverAddressLayout;
        mConnectProgress = mBinding.connectProgressbar;
        mErrorSnack = Snackbar.make(view, R.string.setup_error, Snackbar.LENGTH_LONG);

        mServerScheme = mBinding.serverAddressSchemeTv;

        mSecure = getString(R.string.https_scheme);
        mUnSecure = getString(R.string.http_scheme);

        if (getActivity() != null) {
            mSchemes = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1);
            mSchemes.add(mUnSecure);
            mSchemes.add(mSecure);
            mServerScheme.setAdapter(mSchemes);
        }

        String storedURL = Prefs.getString(getString(R.string.prefs_server_address), "");
        if (!storedURL.isEmpty()) {
            try {
                URI uri = new URI(storedURL);
                mServerAddress.setText(uri.getHost());
                if (storedURL.startsWith(mSecure)) {
                    mServerScheme.setText(mSchemes.getItem(1), false);
                }
            } catch (URISyntaxException e) {
                Timber.w(e, "Invalid URI");
            }
        }

        mServerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    mServerURLLayout.setError(getString(R.string.setup_blank_url));
                } else {
                    mServerURLLayout.setError(null);
                }
            }
        });

        Button continueButton = mBinding.continueUrlButton;
        continueButton.setOnClickListener(view1 -> {
            if(mErrorSnack.isShown()) {
                mErrorSnack.dismiss();
            }
            if(mServerAddress.getText() != null && !mIsConnecting) {
                verifyURLAndTestConnect(mServerAddress.getText().toString());
            }
        });

        mSkipButton = mBinding.skipUrlButton;
        mSkipButton.setOnClickListener(view12 -> {
            if(mErrorSnack.isShown()) {
                mErrorSnack.dismiss();
            }
            if(getActivity() != null) {
                SetupFinishFragment setupCompeteFragment = SetupFinishFragment.getInstance();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.server_setup_fragment, setupCompeteFragment,
                                SetupFinishFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mSocket != null) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        }
    }

    @Override
    public void onAuthDialogSave(boolean success) {
        if (success) {
            Prefs.putBoolean(getString(R.string.prefs_server_basic_auth), true);
            if(mServerAddress.getText() != null && !mIsConnecting) {
                verifyURLAndTestConnect(mServerAddress.getText().toString());
            }
        } else {
            showSnackBarMessage(getActivity(), getString(R.string.setup_error));
        }
    }

    @Override
    public void onAuthDialogCancel() {
        if (mConnectProgress.isShown()) mConnectProgress.setVisibility(View.GONE);
    }

    private void verifyURLAndTestConnect(String address) {
        if(address.length() !=0) {
            if(isValidUrl(address)) {
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
                                            getActivity(), URLSetupFragment.this);
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
            showSnackBarMessage(getActivity(), getString(R.string.setup_error));
        }
    }

    private void connectSocket(String serverURL, IO.Options options) {
        if (options != null) {
            try {
                mSocket = IO.socket(serverURL, options);
            } catch (URISyntaxException e) {
                Timber.w(e,"Socket URI Error");
                mServerURLLayout.setError(getString(R.string.setup_error));
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Timber.w(e,"Socket URI Error");
                mServerURLLayout.setError(getString(R.string.setup_error));
            }
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null) {
                Timber.d("Socket Connected Storing URL");
                getActivity().runOnUiThread(() -> {
                    mConnectProgress.setVisibility(View.GONE);
                    mIsConnecting = false;
                    if(!mErrorSnack.isShown()) {
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
                    showSnackBarMessage(getActivity(), getString(R.string.setup_cannot_connect));
                });
            }
        }
    };

    private boolean isValidUrl(String url) {
        if (url.startsWith(mSecure) | url.startsWith(mUnSecure)) {
            return false;
        }
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    private void showSnackBarMessage(Activity activity, String message) {
        if (mSkipButton.getVisibility() != View.VISIBLE) {
            AnimUtils.fadeView(mSkipButton, 300, Constants.FADE_IN);
        }
        if(!mErrorSnack.isShown() && activity != null) {
            if (mConnectProgress.isShown()) mConnectProgress.setVisibility(View.GONE);
            mErrorSnack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
            mErrorSnack.setTextColor(activity.getColor(R.color.colorWhite));
            mErrorSnack.setText(message);
            mErrorSnack.show();
        }
    }
}
