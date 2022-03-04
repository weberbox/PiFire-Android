package com.weberbox.pifire.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentFeedbackBinding;
import com.weberbox.pifire.ui.dialogs.MaterialDialogText;
import com.weberbox.pifire.utils.CrashUtils;

import io.sentry.Sentry;
import io.sentry.UserFeedback;
import io.sentry.protocol.SentryId;

public class FeedbackFragment extends Fragment {

    private FragmentFeedbackBinding binding;
    private String sentryId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            sentryId = bundle.getString(Constants.INTENT_CRASHED_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!Prefs.getBoolean(getString(R.string.prefs_crash_enable))) {
            showCrashDisabledDialog();
        }

        TextView actionBarText = binding.feedbackToolbar.actionBarText;
        ImageView navButton = binding.feedbackToolbar.actionBarNavButton;
        ImageView configButton = binding.feedbackToolbar.actionBarConfigButton;

        TextView feedbackMessage = binding.feedbackMessage;
        TextInputEditText emailAddress = binding.feedbackEmailAddress;
        TextInputLayout commentsLayout = binding.feedbackCommentsLayout;
        TextInputEditText comments = binding.feedbackComments;
        FloatingActionButton fab = binding.fabFeedback;

        actionBarText.setText(R.string.feedback);
        navButton.setImageResource(R.drawable.ic_nav_back);
        navButton.setOnClickListener(v -> requireActivity().onBackPressed());
        configButton.setVisibility(View.GONE);

        String existingEmail = Prefs.getString(getString(R.string.prefs_crash_user_email));

        if (!existingEmail.isEmpty()) {
            emailAddress.setText(existingEmail);
        }

        if (sentryId != null) {
            feedbackMessage.setText(R.string.feedback_crash_message);
        }

        comments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    commentsLayout.setError(getString(R.string.text_blank_error));
                } else {
                    commentsLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        fab.setOnClickListener(v -> {
            Editable userComments = comments.getText();
            if (userComments != null) {
                if (userComments.length() == 0) {
                    commentsLayout.setError(getString(R.string.text_blank_error));
                } else {
                    if (sentryId == null) {
                        sentryId = Sentry.captureMessage(AppConfig.SENTRY_FEEDBACK).toString();
                    }
                    UserFeedback userFeedback = new UserFeedback(new SentryId(sentryId));
                    Editable userEmail = emailAddress.getText();
                    if (userEmail != null && !userEmail.toString().isEmpty()) {
                        userFeedback.setEmail(userEmail.toString());
                        Prefs.putString(getString(R.string.prefs_crash_user_email),
                                userEmail.toString());
                    }
                    userFeedback.setComments(comments.getText().toString());
                    Sentry.captureUserFeedback(userFeedback);
                    Toast.makeText(requireActivity(), R.string.feedback_toast,
                            Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                }
            }
        });
    }

    private void showCrashDisabledDialog() {
        Activity activity = requireActivity();
        MaterialDialogText dialog = new MaterialDialogText.Builder(activity)
                .setCancelable(false)
                .setTitle(activity.getString(R.string.dialog_crash_disabled_title))
                .setMessage(activity.getString(R.string.dialog_crash_disabled_message))
                .setNegativeButton(activity.getString(R.string.exit),
                        (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            requireActivity().onBackPressed();
                        })
                .setPositiveButton(activity.getString(R.string.sure),
                        (dialogInterface, which) -> {
                            Prefs.putBoolean(getString(R.string.prefs_crash_enable), true);
                            if (!Sentry.isEnabled()) {
                                CrashUtils.initCrashReporting(
                                        requireActivity().getApplicationContext());
                            }
                            dialogInterface.dismiss();
                        })
                .build();
        dialog.show();
    }
}
