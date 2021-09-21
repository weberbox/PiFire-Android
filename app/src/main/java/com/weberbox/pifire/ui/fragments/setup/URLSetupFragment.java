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
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupUrlBinding;
import com.weberbox.pifire.utils.Log;
import com.weberbox.pifire.utils.SSLSocketUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.jetbrains.annotations.NotNull;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Credentials;

public class URLSetupFragment extends Fragment {
    public static final String TAG = URLSetupFragment.class.getSimpleName();

    public static URLSetupFragment getInstance(){
        return new URLSetupFragment();
    }

    private FragmentSetupUrlBinding mBinding;
    private Button mContinueButton;
    private Button mSkipButton;
    private Snackbar mErrorSnack;
    private TextInputEditText mServerURL;
    private TextInputLayout mServerURLLayout;
    private ProgressBar mConnectProgress;
    private Socket mSocket;
    private String mValidURL;
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

        mServerURL = mBinding.serverAddress;
        mServerURLLayout = mBinding.serverAddressLayout;
        mConnectProgress = mBinding.connectProgressbar;
        mErrorSnack = Snackbar.make(view, R.string.setup_error, Snackbar.LENGTH_LONG);

        String storedURL = Prefs.getString(getString(R.string.prefs_server_address), "");
        if (!storedURL.equals("")) {
            mServerURL.setText(storedURL);
        }

        mServerURL.addTextChangedListener(new TextWatcher() {

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

        mContinueButton = mBinding.continueUrlButton;
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mErrorSnack.isShown()) {
                    mErrorSnack.dismiss();
                }
                if(mServerURL.getText() != null && !mIsConnecting) {
                    verifyURLAndTestConnect(mServerURL.getText().toString());
                }
            }
        });

        mSkipButton = mBinding.skipUrlButton;
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mErrorSnack.isShown()) {
                    mErrorSnack.dismiss();
                }
                if(getActivity() != null) {
                    SetupFinishFragment setupCompeteFragment = SetupFinishFragment.getInstance();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.server_setup_fragment, setupCompeteFragment, SetupFinishFragment.TAG)
                            .addToBackStack(null)
                            .commit();
                }
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

    private void verifyURLAndTestConnect(String url) {
        if(url.length() !=0) {
            if(isValidUrl(url)) {
                if(url.startsWith("http://") || url.startsWith("https://")) {
                    mServerURL.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    testConnection(url);
                } else {
                    mServerURL.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    testConnection("http://" + url);
                }
            } else {
                mServerURLLayout.setError(getString(R.string.setup_invalid_url));
            }
        } else {
            mServerURLLayout.setError(getString(R.string.setup_blank_url));
        }
    }

    private void testConnection(String Url) {
        if(!mIsConnecting) {
            IO.Options options = new IO.Options();

            if (Prefs.getBoolean(getString(R.string.prefs_server_basic_auth), false)) {
                String username = SecurityUtils.decrypt(getActivity(), R.string.prefs_server_basic_auth_user);
                String password = SecurityUtils.decrypt(getActivity(), R.string.prefs_server_basic_auth_password);

                String credentials = Credentials.basic(username, password);

                options.extraHeaders = Collections.singletonMap("Authorization",
                        Collections.singletonList(credentials));

                if (Url.startsWith("https://")) {
                    SSLSocketUtils.set(options);
                }

                connectSocket(Url, options);
            } else {
                if (Url.startsWith("https://")) {
                    SSLSocketUtils.set(options);
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
            if(!mErrorSnack.isShown() && getActivity() != null) {
                showSnackBarMessage(getActivity(), R.string.setup_error);
            }
        }
    }

    private void connectSocket(String serverURL, IO.Options options) {
        if (options != null) {
            try {
                mSocket = IO.socket(serverURL, options);
            } catch (URISyntaxException e) {
                Log.e("Socket URI Error", e.toString());
                mServerURLLayout.setError(getString(R.string.setup_error));
            }
        } else {
            try {
                mSocket = IO.socket(serverURL);
            } catch (URISyntaxException e) {
                Log.e("Socket URI Error", e.toString());
                mServerURLLayout.setError(getString(R.string.setup_error));
            }
        }
    }

    private final Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null) {
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
                    Log.e(TAG, "Error connecting");
                    mConnectProgress.setVisibility(View.GONE);
                    mSocket.disconnect();
                    mIsConnecting = false;
                    if(!mErrorSnack.isShown() && getActivity() != null) {
                        showSnackBarMessage(getActivity(), R.string.setup_cannot_connect);
                    }
                    mSkipButton.setVisibility(View.VISIBLE);
                });
            }
        }
    };

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    private void showSnackBarMessage(Activity activity, int message) {
        mErrorSnack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
        mErrorSnack.setTextColor(activity.getColor(R.color.colorWhite));
        mErrorSnack.setText(message);
        mErrorSnack.show();
    }
}
