package com.weberbox.pifire.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.fragments.DashboardFragment;
import com.weberbox.pifire.ui.fragments.EventsFragment;
import com.weberbox.pifire.ui.fragments.PelletsFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    public MainPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case Constants.FRAG_PELLETS -> new PelletsFragment();
            case Constants.FRAG_EVENTS -> new EventsFragment();
            default -> new DashboardFragment();
        };
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}