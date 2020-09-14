package com.eitan.shopik.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.eitan.shopik.GenderFilteringPages.CategoryPickFragment;
import com.eitan.shopik.GenderFilteringPages.HotTrendingFragment;
import com.eitan.shopik.GenderFilteringPages.OutletsFragment;

public class ExplanationPagerViewAdapter extends FragmentPagerAdapter {

    public ExplanationPagerViewAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment selectedFragment;
        switch (position) {
            case 1:
                selectedFragment = new CategoryPickFragment();
                break;
            case 2:
                selectedFragment = new OutletsFragment();
                break;
            default:
                selectedFragment = new HotTrendingFragment();//WatchedItemsFragment();
        }
        return selectedFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }
}
