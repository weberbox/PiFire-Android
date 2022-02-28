package com.weberbox.pifire.ui.fragments.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSetupFinishBinding;
import com.weberbox.pifire.model.view.SetupViewModel;

import org.jetbrains.annotations.NotNull;

public class SetupFinishFragment extends Fragment {

    private FragmentSetupFinishBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetupFinishBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity())
                .get(SetupViewModel.class);
        setupViewModel.getFabEvent().observe(getViewLifecycleOwner(), unused -> {
            if (getActivity() != null) {
                Prefs.putBoolean(getString(R.string.prefs_first_app_start), false);
                Prefs.putInt(getString(R.string.prefs_installed_version), BuildConfig.VERSION_CODE);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(Constants.INTENT_SETUP_RESTART, true);
                startActivity(intent);
                getActivity().finish();
            }
        });

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            app.disconnectSocket();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}