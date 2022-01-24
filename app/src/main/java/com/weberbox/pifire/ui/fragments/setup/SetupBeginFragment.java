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

    private FragmentSetupBeginBinding mBinding;
    private NavController mNavController;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupBeginBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(view);

        SetupViewModel setupViewModel = new ViewModelProvider(requireActivity()).get(
                SetupViewModel.class);

        setupViewModel.getFab().observe(getViewLifecycleOwner(), dashData ->
                dashData.setOnClickListener(v ->
                        mNavController.navigate(R.id.nav_setup_address)));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}
