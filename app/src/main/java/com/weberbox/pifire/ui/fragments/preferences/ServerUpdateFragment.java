package com.weberbox.pifire.ui.fragments.preferences;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.ServerUpdateModel;
import com.weberbox.pifire.ui.activities.PreferencesActivity;
import com.weberbox.pifire.ui.dialogs.BottomButtonDialog;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.ui.dialogs.ProgressDialog;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.utils.AlertUtils;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class ServerUpdateFragment extends Fragment {

    private FragmentServerUpdateBinding binding;
    private CardView checkingForUpdate;
    private CardView noUpdateAvailable;
    private CardView updateCheckError;
    private CardView updateAvailable;
    private TextView updateAvailableText;
    private TextView remoteBranch;
    private TextView remoteAddress;
    private TextView currentVersion;
    private TextView remoteVersion;
    private VeilLayout infoVeilLayout;
    private VeilLayout branchesVeilLayout;
    private AutoCompleteTextView branchesList;
    private AppCompatButton showLogsButton;
    private ProgressDialog progressDialog;
    private List<String> branches;
    private String logsResult;
    private String currentBranch;
    private Socket socket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            socket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServerUpdateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        checkingForUpdate = binding.layoutStatus.serverUpdateProgress;
        noUpdateAvailable = binding.layoutStatus.serverUpToDate;
        updateCheckError = binding.layoutStatus.serverUpdateError;
        updateAvailable = binding.layoutStatus.serverUpdateAvailable;
        updateAvailableText = binding.layoutStatus.serverUpdateAvailableText;
        remoteBranch = binding.serverUpdaterBranchText;
        currentVersion = binding.serverUpdaterCvText;
        remoteAddress = binding.serverUpdaterRemoteText;
        remoteVersion = binding.serverUpdaterRvText;
        infoVeilLayout = binding.serverUpdaterInfoVeil;
        branchesVeilLayout = binding.serverUpdaterBranchVeil;
        branchesList = binding.serverUpdaterBranchSelectList;
        showLogsButton = binding.serverUpdaterLogButton;

        CardView startRemoteUpdate = binding.layoutStatus.serverUpdateAvailable;
        AppCompatButton checkAgainButton = binding.serverUpdaterCheckButton;
        AppCompatButton changeBranch = binding.serverUpdaterChangeBranch;
        AppCompatButton updateBranches = binding.serverUpdaterUpdateBranches;

        branches = new ArrayList<>();

        if (getActivity() != null) {
            ArrayAdapter<String> branchesAdapter = new ArrayAdapter<>(getActivity(),
                    R.layout.item_menu_popup_updater, branches);
            branchesList.setAdapter(branchesAdapter);
        }

        checkAgainButton.setOnClickListener(v -> requestServerUpdateInfo());

        showLogsButton.setOnClickListener(v -> {
            MaterialDialogText dialog = new MaterialDialogText.Builder(requireActivity())
                    .setTitle(getString(R.string.server_updater_logs_dialog_title))
                    .setMessage(logsResult)
                    .setPositiveButton(getString(R.string.close), (dialogInterface, which) ->
                            dialogInterface.dismiss())
                    .build();
            dialog.show();
        });

        changeBranch.setOnClickListener(v -> {
            if (socketConnected()) {
                BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                        .setAutoDismiss(true)
                        .setTitle(getString(R.string.dialog_confirm_action))
                        .setMessage(getString(R.string.server_updater_change_dialog_message))
                        .setNegativeButton(getString(R.string.cancel),
                                (dialogInterface, which) -> {
                                })
                        .setPositiveButton(getString(R.string.server_updater_change_branch),
                                (dialogInterface, which) -> {
                                    String selectedBranch = branchesList.getText().toString();
                                    if (currentBranch != null && !selectedBranch.isEmpty()) {
                                        if (!selectedBranch.equals(currentBranch)) {
                                            changeRemoteBranch(socket, selectedBranch);
                                        } else {
                                            AlertUtils.createErrorAlert(requireActivity(),
                                                    getString(R.string.server_updater_change_branch_error,
                                                            currentBranch), false);
                                        }
                                    }
                                })
                        .build();
                dialog.show();
            }
        });

        updateBranches.setOnClickListener(v -> {
            if (socketConnected()) {
                updateRemoteBranches(socket);
            }
        });

        startRemoteUpdate.setOnClickListener(v -> {
            if (socketConnected()) {
                BottomButtonDialog dialog = new BottomButtonDialog.Builder(requireActivity())
                        .setAutoDismiss(true)
                        .setTitle(getString(R.string.dialog_confirm_action))
                        .setMessage(getString(R.string.server_updater_update_dialog_message))
                        .setNegativeButton(getString(R.string.cancel),
                                (dialogInterface, which) -> {
                                })
                        .setPositiveButton(getString(R.string.update_button),
                                (dialogInterface, which) -> {
                                    if (currentBranch != null && !currentBranch.isEmpty()) {
                                        startRemoteUpdate(socket, currentBranch);
                                    }
                                })
                        .build();
                dialog.show();
            }
        });

        branchesList.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                branchesList.clearFocus();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        socket = null;
    }

    private boolean socketConnected() {
        if (socket != null && socket.connected()) {
            return true;
        } else {
            AlertUtils.createErrorAlert(getActivity(), R.string.server_updater_error_offline, false);
            return false;
        }
    }

    public void requestServerUpdateInfo() {
        if (socket != null && socket.connected()) {
            setCheckingForUpdate();
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_UPDATER_DATA,
                    (Ack) args -> {
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

    public void requestUpdatedBranchInfo(ProgressDialog progressDialog, String message) {
        if (socket != null && socket.connected()) {
            setCheckingForUpdate();
            socket.emit(ServerConstants.GE_GET_APP_DATA, ServerConstants.GA_UPDATER_DATA,
                    (Ack) args -> {
                        if (args.length > 0 && args[0] != null) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> {
                                    progressDialog.dismiss();
                                    AlertUtils.createAlert(getActivity(), message, 3000);
                                    updateUIWithData(args[0].toString());
                                });
                            }
                        }
                    });
        } else {
            setUpdateError();
            AlertUtils.createErrorAlert(getActivity(), R.string.server_updater_error_offline, false);
        }
    }

    private void updateUIWithData(String responseData) {
        branches.clear();

        try {

            ServerUpdateModel updateResponse = ServerUpdateModel.parseJSON(responseData);

            if (updateResponse.getResponse() != null) {
                String result = updateResponse.getResponse().getResult();
                String message = updateResponse.getResponse().getMessage();

                if (result != null && result.equals("error")) {
                    setUpdateError();
                    if (message != null) {
                        AlertUtils.createErrorAlert(getActivity(), message,
                                false);
                    }
                }
            }

            Boolean checkSuccess = updateResponse.getCheckSuccess();
            List<String> branches = updateResponse.getBranches();
            String currentVersion = updateResponse.getVersion();
            String branchTarget = updateResponse.getBranchTarget();
            String remoteUrl = updateResponse.getRemoteUrl();
            String remoteVersion = updateResponse.getRemoteVersion();
            Integer commitsBehind = updateResponse.getCommitsBehind();
            List<String> logsResult = updateResponse.getLogsResult();
            String errorMessage = updateResponse.getErrorMessage();


            if (checkSuccess != null && checkSuccess) {
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
                this.branches.addAll(branches);
            }

            if (currentVersion != null) {
                String version = "v" + currentVersion;
                this.currentVersion.setText(version);
            }

            if (branchTarget != null) {
                remoteBranch.setText(branchTarget);
                branchesList.setText(branchTarget, false);
                currentBranch = branchTarget;
            }

            if (remoteUrl != null) {
                remoteAddress.setText(remoteUrl);
            }

            if (remoteVersion != null) {
                this.remoteVersion.setText(remoteVersion);
            }

            if (logsResult != null) {
                AnimUtils.fadeViewGone(showLogsButton, 300, Constants.FADE_IN);
                StringBuilder logs = new StringBuilder();
                for (String log : logsResult) {
                    String logClean = log.replaceAll("((?i)<br*/?>)", "\n");
                    logs.append(logClean);
                }
                this.logsResult = logs.toString();
            } else {
                AnimUtils.fadeViewGone(showLogsButton, 300, Constants.FADE_OUT);
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e, "Updater JSON Error");
            AlertUtils.createErrorAlert(getActivity(), getString(R.string.json_parsing_error,
                    getString(R.string.menu_info)), false);
            setUpdateError();
        }

        infoVeilLayout.unVeil();
        branchesVeilLayout.unVeil();
    }

    private void changeRemoteBranch(Socket socket, String targetBranch) {
        final Handler handler = new Handler(Looper.getMainLooper());
        progressDialog = new ProgressDialog.Builder(requireActivity())
                .setTitle(getString(R.string.server_updater_change_dialog_title))
                .setMessage("")
                .setCancelable(false)
                .build();
        progressDialog.getProgressIndicator().setIndeterminate(true);
        progressDialog.setOnShowListener(dialogInterface -> handler.postDelayed(runnable, 30000));
        progressDialog.setOnDismissListener(dialogInterface -> {
            handler.removeCallbacks(runnable);
            requireActivity().finish();
        });
        progressDialog.show();
        socket.emit(ServerConstants.PE_POST_UPDATER_DATA, ServerConstants.PT_CHANGE_BRANCH,
                targetBranch, (Ack) args -> {
                    if (args.length > 0 && args[0] != null) {
                        ServerResponseModel response =
                                ServerResponseModel.parseJSON(args[0].toString());
                        String result = response.getResult();
                        String message = response.getMessage();

                        requireActivity().runOnUiThread(() -> {
                            if (result.equals("success")) {
                                String cleanMessage = message.replaceAll("((?i)<br*/?>)", "\n");
                                progressDialog.getProgressMessage().setText(cleanMessage);
                            } else {
                                AlertUtils.createErrorAlert(getActivity(), message, false);
                            }
                        });
                    }
                });
    }

    private void updateRemoteBranches(Socket socket) {
        progressDialog = new ProgressDialog.Builder(requireActivity())
                .setTitle(getString(R.string.server_updater_updating_branches))
                .setMessage("")
                .build();
        progressDialog.getProgressIndicator().setIndeterminate(true);
        progressDialog.show();
        socket.emit(ServerConstants.PE_POST_UPDATER_DATA, ServerConstants.PT_UPDATE_BRANCHES,
                (Ack) args -> {
            if (args.length > 0 && args[0] != null) {
                ServerResponseModel response =
                        ServerResponseModel.parseJSON(args[0].toString());
                String result = response.getResult();
                String message = response.getMessage();

                requireActivity().runOnUiThread(() -> {
                    if (result.equals("success")) {
                        requestUpdatedBranchInfo(progressDialog, message);
                    } else {
                        if (progressDialog != null) progressDialog.dismiss();
                        AlertUtils.createErrorAlert(getActivity(), message, false);
                    }
                });
            }
        });
    }

    private void startRemoteUpdate(Socket socket, String targetBranch) {
        final Handler handler = new Handler(Looper.getMainLooper());
        progressDialog = new ProgressDialog.Builder(requireActivity())
                .setTitle(getString(R.string.server_updater_update_dialog_title))
                .setCancelable(false)
                .setMessage("")
                .build();
        progressDialog.getProgressIndicator().setIndeterminate(true);
        progressDialog.setOnShowListener(dialogInterface -> handler.postDelayed(runnable, 30000));
        progressDialog.setOnDismissListener(dialogInterface -> {
            handler.removeCallbacks(runnable);
            requireActivity().finish();
        });
        progressDialog.show();
        socket.emit(ServerConstants.PE_POST_UPDATER_DATA, ServerConstants.PT_DO_UPDATE,
                targetBranch, (Ack) args -> {
                    if (args.length > 0 && args[0] != null) {
                        ServerResponseModel response =
                                ServerResponseModel.parseJSON(args[0].toString());
                        String result = response.getResult();
                        String message = response.getMessage();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (result.equals("success")) {
                                    String cleanMessage = message.replaceAll("((?i)<br*/?>)", "\n");
                                    progressDialog.getProgressMessage().setText(cleanMessage);
                                } else {
                                    AlertUtils.createErrorAlert(getActivity(), message, false);
                                }
                            });
                        }
                    }
                });
    }

    private void setCheckingForUpdate() {
        AnimUtils.fadeOutAnimation(updateAvailable, 300);
        AnimUtils.fadeOutAnimation(noUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(updateCheckError, 300);
        AnimUtils.fadeInAnimation(checkingForUpdate, 300);
    }

    private void setNoUpdateAvailable() {
        AnimUtils.fadeOutAnimation(checkingForUpdate, 300);
        AnimUtils.fadeOutAnimation(updateAvailable, 300);
        AnimUtils.fadeOutAnimation(updateCheckError, 300);
        AnimUtils.fadeInAnimation(noUpdateAvailable, 300);
    }

    private void setUpdateAvailable(String commits) {
        AnimUtils.fadeOutAnimation(checkingForUpdate, 300);
        AnimUtils.fadeOutAnimation(noUpdateAvailable, 300);
        AnimUtils.fadeOutAnimation(updateCheckError, 300);
        updateAvailableText.setText(getString(R.string.server_updater_update_available_text,
                commits));
        AnimUtils.fadeInAnimation(updateAvailable, 300);
    }

    private void setUpdateError() {
        AnimUtils.fadeOutAnimation(checkingForUpdate, 300);
        AnimUtils.fadeOutAnimation(updateAvailable, 300);
        AnimUtils.fadeOutAnimation(noUpdateAvailable, 300);
        AnimUtils.fadeInAnimation(updateCheckError, 300);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    };

}
