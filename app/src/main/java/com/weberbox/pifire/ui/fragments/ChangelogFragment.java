package com.weberbox.pifire.ui.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.weberbox.changelibs.library.view.ChangeLogRecyclerView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.FragmentChangelogBinding;

public class ChangelogFragment extends Fragment {

    private FragmentChangelogBinding binding;
    private LottieAnimationView animationView;
    private ChangeLogRecyclerView changeLogView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChangelogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView actionBarText = binding.changelogToolbar.actionBarText;
        ImageView navButton = binding.changelogToolbar.actionBarNavButton;
        ImageView configButton = binding.changelogToolbar.actionBarConfigButton;
        changeLogView = binding.changelog;
        animationView = binding.animationView;

        changeLogView.setAlpha(0.0f);
        animationView.addAnimatorListener(listener);

        actionBarText.setText(R.string.changelog_title);
        navButton.setImageResource(R.drawable.ic_nav_back);
        navButton.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());
        configButton.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private final Animator.AnimatorListener listener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(@NonNull Animator animation) {
            changeLogView.animate()
                    .alpha(1.0f)
                    .setInterpolator(new DecelerateInterpolator())
                    .setDuration(400L)
                    .setStartDelay(1200L);
            animationView.animate()
                    .alpha(0.0f).scaleX(9.0f)
                    .scaleY(9.0f)
                    .setInterpolator(new AccelerateInterpolator())
                    .setDuration(200L)
                    .setStartDelay(1100L);
        }

        @Override
        public void onAnimationEnd(@NonNull Animator animation) {
        }

        @Override
        public void onAnimationCancel(@NonNull Animator animation) {
        }

        @Override
        public void onAnimationRepeat(@NonNull Animator animation) {
        }
    };
}
