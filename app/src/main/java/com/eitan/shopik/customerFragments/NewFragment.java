package com.eitan.shopik.customerFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.eitan.shopik.R;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.viewModels.MainModel;

public class NewFragment extends Fragment {

    private MainModel mainModel;
    private ShopikUser mShopikUser;
    private Database mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        mShopikUser = ShopikUser.getInstance();
        mDatabase = Database.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new, container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
