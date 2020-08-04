package com.eitan.shopik.CustomerFragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eitan.shopik.Adapters.RecyclerGridAdapter;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.MainModel;
import com.eitan.shopik.ViewModels.SuggestedModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SuggestedFragment extends Fragment implements View.OnClickListener{

    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView mRecyclerView;
    private SuggestedModel suggestedModel;
    private MainModel mainModel;
    private FloatingActionButton price,sale,match;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_all_items, container,false);
    }

    private void initOnCreate() {
        suggestedModel = new ViewModelProvider(requireActivity()).get(SuggestedModel.class);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        price = null;
        sale = null;
        match = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize views
        init();

        mainModel.getAll_items_ids().observe(requireActivity(), pairs -> {
            suggestedModel.clearItems();
            int count = 0;
            int items_num = 0;
            String sub_category = "";
            String category = "";
            for (Pair<String, ShoppingItem> item : pairs) {
                int match = 0;
                assert item.second != null;

                sub_category = item.second.getSub_category();
                category = item.second.getType();

                if(suggestedModel.getPreferred().getValue() != null){
                    match = Objects.requireNonNull(suggestedModel.
                            getPreferred().
                            getValue()).
                            calculateMatchingPercentage(item.second);
                }
                item.second.setPercentage(match);
                if(match >= Macros.CustomerMacros.SUGGESTION_PERCENTAGE){
                    mRecyclerView.setAdapter(recyclerGridAdapter);
                    suggestedModel.addToAllItems(item.second);
                    count++;
                    items_num++;
                    if( (count % Macros.SUGGESTED_TO_AD == 0) && count > 0 ){
                        suggestedModel.addToAllItems((ShoppingItem) mainModel.getNextAd());
                        count++;
                    }
                    recyclerGridAdapter.notifyDataSetChanged();
                }

                String text = "We found " + items_num + " " + sub_category + " " + category.toLowerCase() + " that you may like," +
                        System.lineSeparator() + "based on your swipes ";

                TextView text_view = requireView().findViewById(R.id.text_view);
                text_view.setText(text);
            }
            recyclerGridAdapter.setAllItems(Objects.requireNonNull(suggestedModel.getAllItems().getValue()));

        });
    }

    private void init() {

        mRecyclerView = requireView().findViewById(R.id.grid_recycler_view);
        recyclerGridAdapter = new RecyclerGridAdapter(suggestedModel.getAllItems().getValue());
        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);
        CollapsingToolbarLayout collapsing_toolbar = requireView().findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setExpandedTitleColor(Color.WHITE);
        collapsing_toolbar.setCollapsedTitleTextColor(Color.WHITE);

        initFab();
    }

    private void initFab(){

        price = requireView().findViewById(R.id._price_icon);
        sale = requireView().findViewById(R.id._sale_icon);
        match = requireView().findViewById(R.id._match_icon);

        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        match.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id._match_icon:
                sortItems("match");
                break;
            case R.id._price_icon:
                sortItems("price");
                break;
            case R.id._sale_icon:
                sortItems("sale");
                break;
            default:
                sortItems("clear");
                break;
        }
    }

    private void sortItems(String sort_by){
        recyclerGridAdapter.getSortingFilter().filter(sort_by);
    }
}