package com.eitan.shopik.CustomerFragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SuggestedFragment extends Fragment implements View.OnClickListener{

    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView mRecyclerView;
    private TextView text_view;
    private SuggestedModel suggestedModel;
    private MainModel mainModel;
    private Chip price,sale,match,favorite,toolbar_price,toolbar_sale,toolbar_match,toolbar_favorite;
    private FloatingActionButton scroll;
    private boolean scrollUp;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppBarLayout appBarLayout;

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

        scrollUp = false;
        //initialize views
        init();

        mainModel.getAll_items().observe(requireActivity(), shoppingItems -> {
            suggestedModel.clearItems();
            int count = 0;
            int items_num = 0;
            String sub_category;
            String category;
            for (ShoppingItem shoppingItem : shoppingItems) {
                int match = 0;
                sub_category = shoppingItem.getSub_category();
                category = shoppingItem.getType();

                if(suggestedModel.getPreferred().getValue() != null){
                    match = Objects.requireNonNull(suggestedModel.
                            getPreferred().
                            getValue()).
                            calculateMatchingPercentage(shoppingItem);
                }
                shoppingItem.setPercentage(match);
                if(match >= Macros.CustomerMacros.SUGGESTION_PERCENTAGE){
                    mRecyclerView.setAdapter(recyclerGridAdapter);
                    suggestedModel.addToAllItems(shoppingItem);
                    count++;
                    items_num++;
                    if( (count % Macros.SUGGESTED_TO_AD == 0) && count > 0 ){
                        ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                        shoppingItemAd.setAd(true);
                        suggestedModel.addToAllItems(shoppingItemAd);
                        count++;
                    }
                    recyclerGridAdapter.notifyDataSetChanged();
                }

                String text = "We found " + items_num + " " + sub_category + " " + category.toLowerCase() + " that you may like";
                text_view.setText(text);
            }
            recyclerGridAdapter.setAllItems(Objects.requireNonNull(suggestedModel.getAllItems().getValue()));
        });

        scroll.setOnClickListener(v -> {

            //scroll down
            if(!scrollUp) {
                scroll.hide();
                mLayoutManager.smoothScrollToPosition(mRecyclerView,
                        null, Objects.requireNonNull(suggestedModel.getAllItems().getValue()).size() - 1);
            } //scroll down
            else {
                scroll.hide();
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
            }
            scrollUp = !scrollUp;
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // ON BOTTOM
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scroll.show();
                    scroll.setRotation(180);
                }
                // ON TOP
                else if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    scroll.show();
                    scroll.setRotation(0);
                }
            }
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            RelativeLayout relativeLayout = requireView().findViewById(R.id.info_layout);
            Toolbar toolbar = requireView().findViewById(R.id.toolbar);

            // Collapsed
            if(verticalOffset <= -270) {
                relativeLayout.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.VISIBLE);
            }
            // Expanded
            else{
                toolbar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        });

    }

    private void init() {

        appBarLayout = requireView().findViewById(R.id.appbar);
        mRecyclerView = requireView().findViewById(R.id.grid_recycler_view);
        text_view = requireView().findViewById(R.id.text_view);
        scroll = requireView().findViewById(R.id.scroll_up_down);
        recyclerGridAdapter = new RecyclerGridAdapter(suggestedModel.getAllItems().getValue());
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);

        initChips();
    }

    private void initChips(){

        price = requireView().findViewById(R.id.price_chip);
        sale = requireView().findViewById(R.id.sale_chip);
        match = requireView().findViewById(R.id.match_chip);
        favorite = requireView().findViewById(R.id.favorites_chip);

        toolbar_price = requireView().findViewById(R.id.price_chip2);
        toolbar_sale = requireView().findViewById(R.id.sale_chip2);
        toolbar_match = requireView().findViewById(R.id.match_chip2);
        toolbar_favorite = requireView().findViewById(R.id.favorites_chip2);

        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        match.setOnClickListener(this);
        favorite.setOnClickListener(this);

        toolbar_favorite.setOnClickListener(this);
        toolbar_match.setOnClickListener(this);
        toolbar_sale.setOnClickListener(this);
        toolbar_price.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.match_chip2:
            case R.id.match_chip:
                sortItems("match");
                break;
            case R.id.price_chip:
            case R.id.price_chip2:
                sortItems("price");
                break;
            case R.id.sale_chip2:
            case R.id.sale_chip:
                sortItems("sale");
                break;
            case R.id.favorites_chip:
            case R.id.favorites_chip2:
                sortItems("favorites");
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