package com.weberbox.pifire.ui.fragments.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentSetupFinishBinding;

import org.jetbrains.annotations.NotNull;

public class SetupFinishFragment extends Fragment {
    public static final String TAG = SetupFinishFragment.class.getSimpleName();

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

        Button finishSetup = mBinding.setupFinishButton;
        finishSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra(Constants.INTENT_SETUP_RESTART, true);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}