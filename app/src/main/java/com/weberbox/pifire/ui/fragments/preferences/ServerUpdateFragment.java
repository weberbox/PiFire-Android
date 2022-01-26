package com.weberbox.pifire.ui.fragments.preferences;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonSyntaxException;
import com.skydoves.androidveil.VeilLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.databinding.FragmentServerUpdateBinding;
import com.weberbox.pifire.interfaces.AdminCallback;
import com.weberbox.pifire.model.remote.ServerUpdateModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.AdminActionDialog;
import com.weberbox.pifire.ui.dialogs.MessageTextDialog;
import com.weberbox.pifire.ui.dialogs.UpdaterProgressDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class ServerUpdateFragment extends Fragment {

    private FragmentServerUpdateBinding mBinding;
    private CardView mCheckingForUpdate;
    private CardView mNoUpdateAvailable;
    private CardView mUpdateCheckError;
    private CardView mUpdateAvailable;
    private TextView mUpdateAvailableText;
    private TextView mRemoteBranch;
    private TextView mRemoteAddress;
    private TextView mCurrentVersion;
    private TextView mRemoteVersion;
    private VeilLayout mInfoVeilLayout;
    private VeilLayout mBranchesVeilLayout;
    private AutoCompleteTextView mBranchesList;
    private AppCompatButton mShowLogsButton;
    private List<String> mBranches;
    private String mLogsResult;
    private String mCurrentBranch;
    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentServerUpdateBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCheckingForUpdate = mBinding.layoutStatus.serverUpdateProgress;
        mNoUpdateAvailable = mBinding.layoutStatus.serverUpToDate;
        mUpdateCheckError = mBinding.layoutStatus.serverUpdateError;
        mUpdateAvailable = mBinding.layoutStatus.serverUpdateAvailable;
        mUpdateAvailableText = mBinding.layoutStatus.serverUpdateAvailableText;
        mRemoteBranch = mBinding.serverUpdaterBranchText;
        mCurrentVersion = mBinding.serverUpdaterCvText;
        mRemoteAddress = mBinding.serverUpdaterRemoteText;
        mRemoteVersion = mBinding.serverUpdaterRvText;
        mInfoVeilLayout = mBinding.serverUpdaterInfoVeil;
        mBranchesVeilLayout = mBinding.serverUpdaterBranchVeil;
        mBranchesList = mBinding.serverUpdaterBranchSelectList;
        mShowLogsButton = mBinding.serverUpdaterLogButton;

        CardView startRemoteUpdate = mBinding.layoutStatus.serverUpdateAvailable;
        AppCompatButton checkAgainButton = mBinding.serverUpdaterCheckButton;
        AppCompatButton changeBranch = mBinding.serverUpdaterChangeBranch;

        mBranches = new ArrayList<>();

        if (getActivity() != null) {
            ArrayAdapter<String> branchesAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.item_menu_popup_updater, mBranches);
            mBranchesList.setAdapter(branchesAdapter);
        }

        checkAgainButton.setOnClickListener(v -> requestServerUpdateInfo());

        mShowLogsButton.setOnClickListener(v -> {
            MessageTextDialog dialog = new MessageTextDialog(requireActivity(),
                    getString(R.string.server_updater_logs_dialog_title), mLogsResult);
            dialog.getDialog().show();
        });

        changeBranch.setOnClickListener(v -> {
            AdminActionDialog dialog = new AdminActionDialog(requireActivity(),
                    branchChangeCallback, Constants.ACTION_ADMIN_CHANGE_BRANCH);
            dialog.showDialog();
        });

        startRemoteUpdate.setOnClickListener(v -> {
            AdminActionDialog dialog = new AdminActionDialog(requireActivity(),
                    branchChangeCallback, Constants.ACTION_ADMIN_DO_UPDATE);
            dialog.showDialog();
        });

        mBranchesList.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                mBranchesList.clearFocus();
            }
        });

        requestServerUpdateInfo();

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((PreferencesActivity) getActivity()).setActionBarTitle(R.string.settings_updater);
        }
    }

    private boolean socketConnected() {
        if (mSocket != null && mSocket.connected()) {
            return true;
        } else {
            AlertUtils.createErrorAlert(getActivity(), R.string.server_updater_error_offline, false);
            return false;
        }
    }

    private final AdminCallback branchChangeCallback = type -> {
        switch (type) {
            case Constants.ACTION_ADMIN_CHANGE_BRANCH:
                String selectedBranch = mBranchesList.getText().toString();
                if (mCurrentBranch != null && !selectedBranch.isEmpty()) {
                    if (!selectedBranch.equals(mCurrentBranch)) {
                        if (socketConnected()) {
                            changeRemoteBranch(mSocket, selectedBranch);
                        }
                    } else {
                        AlertUtils.createErrorAlert(requireActivity(),
                                getString(R.string.server_updater_change_branch_error,
                                        mCurrentBranch), false);
                    }
                }
                break;
            case Constants.ACTION_ADMIN_DO_UPDATE:
                if (mCurrentBranch != null && !mCurrentBranch.isEmpty()) {
                    if (socketConnected()) {
                        startRemoteUpdate(mSocket, mCurrentBranch);
                    }
                }
                break;
        }
    };

    public void requestServerUpdateInfo() {
        if (mSocket != null && mSocket.connected()) {
            setCheckingForUpdate();
            mSocket.emit(ServerConstants.REQUEST_UPDATER_DATA, (Ack) args -> {
                if (args.length > 0 && args[0] != null) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                updateUIWithData(args[0].toString()));
                    }
                }
            });
        } else {
            setUpdateError();
            AlertUtils.createErrorAlert(getActivity(), R.string.server_updater_error_offline, false);
        }
    }

    private void updateUIWithData(String responseData) {
        mBranches.clear();

        try {

            ServerUpdateModel updateResponse = ServerUpdateModel.parseJSON(responseData);

            boolean checkSuccess = updateResponse.getCheckSuccess();
            List<String> branches = updateResponse.getBranches();
            String currentVersion = updateResponse.getVersion();
            String branchTarget = updateResponse.getBranchTarget();
            String remoteUrl = updateResponse.getRemoteUrl();
            String remoteVersion = updateResponse.getRemoteVersion();
            Integer commitsBehind = updateResponse.getCommitsBehind();
            List<String> logsResult = updateResponse.getLogsResult();
            String errorMessage = updateResponse.getErrorMessage();


            if (checkSuccess) {
                if (commitsBehind != null) {
                    if (commitsBehind > 0) {
                        setUpdateAvailable(String.valueOf(commitsBehind));
                    } else {
                        setNoUpdateAvailable();
                    }
                }
            } else {
                setUpdateError();
                if (errorMessage != null) {
                    AlertUtils.createErrorAlert(getActivity(), errorMessage,
                            false);
                }
            }

            if (branches != null) {
                mBranches.addAll(branches);
            }

            if (currentVersion != null) {
                String version = "v" + currentVersion;
                mCurrentVersion.setText(version);
            }

            if (branchTarget != null) {
                mRemoteBranch.setText(branchTarget);
                mBranchesList.setText(branchTarget, false);
                mCurrentBranch = branchTarget;
            }

            if (remoteUrl != null) {
                mRemoteAddress.setText(remoteUrl);
            }

            if (remoteVersion != null) {
                mRemoteVersion.setText(remoteVersion);
            }

            if (logsResult != null) {
                AnimUtils.fadeViewGone(mShowLogsButton, 300, Constants.FADE_IN);
                StringBuilder logs = new StringBuilder();
                for (String log : logsResult) {
                    logs.append(log);
                }
                mLogsResult = logs.toString();
            } else {
                AnimUtils.fadeViewGone(mShowLogsButton, 300, Constants.FADE_OUT);
            }

            mInfoVeilLayout.unVeil();
            mBranchesVeilLayout.unVeil();


        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e, "Updater JSON Error");
            AlertUtils.createErrorAlert(getActivity(), R.string.json_error_info, false);
        }
    }

    private void changeRemoteBranch(Socket socket, String targetBranch) {
        UpdaterProgressDialog dialog = new UpdaterProgressDialog(requireActivity(),
                R.string.server_updater_change_dialog_title);
        dialog.createDialog().show();
        socket.emit(ServerConstants.REQUEST_UPDATER_ACTION,
                JSONUtils.encodeJSON(
                        ServerConstants.UPDATER_CHANGE_BRANCH,
                        ServerConstants.UPDATER_BRANCH_TARGET, targetBranch),
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null) {
                        try {
                            JSONArray array = new JSONArray(args[0].toString());
                            StringBuilder output = new StringBuilder();
                            for (int i = 0 ; i < array.length() ; i++) {
                                output.append(array.get(i));
                            }
                            requireActivity().runOnUiThread(() ->
                                    dialog.setOutputMessage(output.toString()));
                        } catch (JSONException e) {
                            Timber.w(e, "Updater JSON Error");
                        }
                    }
                });
    }

    private void startRemoteUpdate(Socket socket, String targetBranch) {
        UpdaterProgressDialog dialog = new UpdaterProgressDialog(requireActivity(),
                R.string.server_updater_update_dialog_title);
        dialog.createDialog().show();
        socket.emit(ServerConstants.REQUEST_UPDATER_ACTION,
                JSONUtils.encodeJSON(
                        ServerConstants.UPDATER_START_UPDATE,
                        ServerConstants.UPDATER_BRANCH_TARGET, targetBranch),
                (Ack) args -> {
                    if (args.length > 0 && args[0] != null) {
                        try {
                            JSONArray array = new JSONArray(args[0].toString());
                            StringBuilder output = new StringBuilder();
                            for (int i = 0 ; i < array.length() ; i++) {
                                output.append(array.get(i));
                            }
                            requireActivity().runOnUiThread(() ->
                                    dialog.setOutputMessage(output.toString()));
                        } catch (JSONException e) {
                            Timber.w(e, "Updater JSON Error");
                        }
                    }
                });
    }

    private void setCheckingForUpdate() {
        AnimUtils.fadeOutAnimation(mUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(mNoUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(mUpdateCheckError, 300);
        AnimUtils.fadeInAnimation(mCheckingForUpdate, 300);
    }

    private void setNoUpdateAvailable() {
        AnimUtils.fadeOutAnimation(mCheckingForUpdate, 300);
        AnimUtils.fadeOutAnimation(mUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(mUpdateCheckError, 300);
        AnimUtils.fadeInAnimation(mNoUpdateAvailable, 300);
    }

    private void setUpdateAvailable(String commits) {
        AnimUtils.fadeOutAnimation(mCheckingForUpdate, 300);
        AnimUtils.fadeOutAnimation(mNoUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(mUpdateCheckError, 300);
        mUpdateAvailableText.setText(getString(R.string.server_updater_update_available_text,
                commits));
        AnimUtils.fadeInAnimation(mUpdateAvailable, 300);
    }

    private void setUpdateError() {
        AnimUtils.fadeOutAnimation(mCheckingForUpdate, 300);
        AnimUtils.fadeOutAnimation(mUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(mNoUpdateAvailable, 300);
        AnimUtils.fadeInAnimation(mUpdateCheckError, 300);
    }
}
