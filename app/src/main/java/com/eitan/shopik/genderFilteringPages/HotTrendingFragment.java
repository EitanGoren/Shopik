package com.eitan.shopik.genderFilteringPages;

import android.animation.LayoutTransition;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.CustomItemAnimator;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.RecyclerAdapter;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.items.RecyclerItem;
import com.eitan.shopik.viewModels.EntranceViewModel;
import com.eitan.shopik.viewModels.GenderModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class HotTrendingFragment extends Fragment {

    private Dialog dialog;
    private TextView liked_counter;
    private RecyclerView recyclerView;
    private RelativeLayout layout;

    private static EntranceViewModel entranceViewModel;
    private RecyclerAdapter recyclerAdapter;
    private GenderModel model;
    private Observer<String> observer;
    private Observer<ArrayList<RecyclerItem>> recentObserver;
    private ShopikUser mShopikUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        entranceViewModel = new ViewModelProvider(requireActivity()).get(EntranceViewModel.class);
        recyclerAdapter = new RecyclerAdapter(entranceViewModel.getRecentLikedItems().getValue(), RecyclerAdapter.ViewHolderType.ITEM);
        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        mShopikUser = ShopikUser.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_e1, container, false);

        liked_counter = view.findViewById(R.id.best_sellers_count);
        layout = view.findViewById(R.id.layout1);
        recyclerView = view.findViewById(R.id.recycler);

        observer = s -> {
            if(mShopikUser.getGender() != null) {
                if (!mShopikUser.getGender().equals(s)) {
                    mShopikUser.setGender(s);
                    recyclerAdapter.notifyDataSetChanged();
                    liked_counter.setText(R.string.loading);
                }
            }
        };

        recentObserver = recyclerItems -> {
            if(recyclerItems.isEmpty())
                layout.setVisibility(View.GONE);
            else {
                layout.setVisibility(View.VISIBLE);
                String text = "(" + recyclerItems.size() + " items)";
                liked_counter.setText(text);
                recyclerAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(recyclerAdapter);
            }
        };

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        model.getGender().observe(getViewLifecycleOwner(), observer);
        entranceViewModel.getRecentLikedItems().observe(requireActivity(), recentObserver);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setScrollbarFadingEnabled(true);
        recyclerView.setItemAnimator(new CustomItemAnimator());
    }

    private void init() {
        dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.new_items_grid_dialog);
        CardView cardSheet = dialog.findViewById(R.id.card_sheet);
        BottomSheetBehavior<CardView> bottomSheetBehavior = BottomSheetBehavior.from(cardSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        recyclerView.setItemAnimator(new CustomItemAnimator());

        ((ViewGroup) requireView().findViewById(R.id.root)).
                getLayoutTransition().
                enableTransitionType(LayoutTransition.APPEARING);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObserver(observer);
        entranceViewModel.getRecentLikedItems().removeObserver(recentObserver);
        layout = null;
    }
}
