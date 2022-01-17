package com.weberbox.pifire.ui.fragments.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSetupFinishBinding;

import org.jetbrains.annotations.NotNull;

public class SetupFinishFragment extends Fragment {

    private FragmentSetupFinishBinding mBinding;

    public static SetupFinishFragment getInstance() {
        return new SetupFinishFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentSetupFinishBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            app.disconnectSocket();
        }

        Button finishSetup = mBinding.setupFinishButton;
        finishSetup.setOnClickListener(view1 -> {
            if (getActivity() != null) {
                Prefs.putBoolean(getString(R.string.prefs_first_app_start), false);
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(Constants.INTENT_SETUP_RESTART, true);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}