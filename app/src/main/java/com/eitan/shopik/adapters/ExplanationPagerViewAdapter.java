package com.eitan.shopik.adapters;

import android.database.DataSetObserver;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.eitan.shopik.R;
import com.eitan.shopik.genderFilteringPages.CategoryPickFragment;
import com.eitan.shopik.genderFilteringPages.HotTrendingFragment;
import com.eitan.shopik.genderFilteringPages.OutletsFragment;

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
                selectedFragment = new HotTrendingFragment(); //WatchedItemsFragment();
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
