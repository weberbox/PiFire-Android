package com.weberbox.pifire.ui.fragments.setup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentSetupBeginBinding;
import com.weberbox.pifire.model.view.SetupViewModel;

import org.jetbrains.annotations.NotNull;

public class SetupBeginFragment extends Fragment {

    private FragmentSetupBeginBinding binding;
    private NavController navController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSetupBeginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity()).get(
                SetupViewModel.class);

        setupViewModel.getFabEvent().observe(getViewLifecycleOwner(), unused ->
                navController.navigate(R.id.nav_setup_address));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
