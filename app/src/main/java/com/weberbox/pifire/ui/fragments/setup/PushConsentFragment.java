package com.weberbox.pifire.ui.fragments.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupPushBinding;
import com.weberbox.pifire.model.view.SetupViewModel;
import com.weberbox.pifire.ui.dialogs.MessageTextDialog;
import com.weberbox.pifire.utils.OneSignalUtils;

import org.jetbrains.annotations.NotNull;

public class PushConsentFragment extends Fragment {

    private FragmentSetupPushBinding mBinding;
    private NavController mNavController;
    private boolean mIsAccepted;

    private boolean mSkipConsent = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupPushBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);

        SwitchCompat consent = mBinding.setupPushConsent;

        mIsAccepted = Prefs.getBoolean(getString(R.string.prefs_notif_onesignal_accepted));

        consent.setChecked(mIsAccepted);
        consent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mIsAccepted = isChecked;
            Prefs.putBoolean(getString(R.string.prefs_notif_onesignal_accepted), isChecked);
            OneSignalUtils.provideUserConsent(isChecked);
        });

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity())
                .get(SetupViewModel.class);
        setupViewModel.getFab().observe(getViewLifecycleOwner(), setupFab ->
                setupFab.setOnClickListener(v -> onFabClicked()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    public void onFabClicked() {
        if (mIsAccepted || mSkipConsent) {
            mNavController.navigate(R.id.nav_setup_finish);
        } else {
            MessageTextDialog skipDialog = new MessageTextDialog(getActivity(),
                    R.string.setup_push_dialog_declined_title,
                    R.string.setup_push_dialog_declined_message);
            skipDialog.getDialog()
                    .setOnDismissListener(dialog -> mSkipConsent = true)
                    .show();
        }
    }
}
