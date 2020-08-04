package com.eitan.shopik.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.eitan.shopik.CustomerFragments.CustomerHomeFragment;
import com.eitan.shopik.CustomerFragments.FavoritesFragment;
import com.eitan.shopik.CustomerFragments.SearchFragment;
import com.eitan.shopik.CustomerFragments.SuggestedFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;
        switch (position) {
            case 1:
                selectedFragment = new FavoritesFragment();
                break;
            case 2:
                selectedFragment = new SuggestedFragment();
                break;
            case 3:
                selectedFragment = new SearchFragment();
                break;
            default:
                selectedFragment = new CustomerHomeFragment();
        }
        return selectedFragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
