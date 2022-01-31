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

    private FragmentSetupPushBinding binding;
    private NavController navController;
    private boolean isAccepted;

    private boolean skipConsent = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetupPushBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        SwitchCompat consent = binding.setupPushConsent;

        isAccepted = Prefs.getBoolean(getString(R.string.prefs_notif_onesignal_accepted));

        consent.setChecked(isAccepted);
        consent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAccepted = isChecked;
            Prefs.putBoolean(getString(R.string.prefs_notif_onesignal_accepted), isChecked);
            OneSignalUtils.provideUserConsent(isChecked);
        });

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity())
                .get(SetupViewModel.class);
        setupViewModel.getFabEvent().observe(getViewLifecycleOwner(), unused -> {
            if (isAccepted || skipConsent) {
                navController.navigate(R.id.nav_setup_finish);
            } else {
                MessageTextDialog skipDialog = new MessageTextDialog(getActivity(),
                        R.string.setup_push_dialog_declined_title,
                        R.string.setup_push_dialog_declined_message);
                skipDialog.getDialog()
                        .setOnDismissListener(dialog -> skipConsent = true)
                        .show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
