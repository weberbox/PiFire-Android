package com.weberbox.pifire.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.ui.fragments.DashboardFragment;
import com.weberbox.pifire.ui.fragments.EventsFragment;
import com.weberbox.pifire.ui.fragments.HistoryFragment;
import com.weberbox.pifire.ui.fragments.PelletsFragment;
import com.weberbox.pifire.ui.fragments.RecipesFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 5;

    public MainPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case Constants.FRAG_RECIPES:
                return new RecipesFragment();
            case Constants.FRAG_PELLETS:
                return new PelletsFragment();
            case Constants.FRAG_DASHBOARD:
                return new DashboardFragment();
            case Constants.FRAG_HISTORY:
                return new HistoryFragment();
            case Constants.FRAG_EVENTS:
                return new EventsFragment();
        }
        return new DashboardFragment();
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}