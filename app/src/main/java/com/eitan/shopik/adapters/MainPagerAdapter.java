package com.eitan.shopik.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.eitan.shopik.customerFragments.CustomerHomeFragment;
import com.eitan.shopik.customerFragments.FavoritesFragment;
import com.eitan.shopik.customerFragments.NewFragment;
import com.eitan.shopik.customerFragments.SearchFragment;

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
                selectedFragment = new NewFragment();
                break;
            case 2:
                selectedFragment = new FavoritesFragment();
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
