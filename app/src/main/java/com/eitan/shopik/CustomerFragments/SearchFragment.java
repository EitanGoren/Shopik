package com.eitan.shopik.CustomerFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eitan.shopik.Adapters.RecyclerGridAdapter;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.AllItemsModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SearchFragment extends Fragment implements View.OnClickListener{

   // private CardView search_card;
    private SearchView searchView2;
    private AllItemsModel allItemsModel;
    private MainModel mainModel;
    private Chip price;
    private Chip sale;
    private Chip match;
    private Chip company;
    private Chip brand;
    private FloatingActionButton scroll;
    private boolean scrollUp;
    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener listener;
    private RecyclerView.OnScrollListener onScrollListener;
    private TextView header;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
        MobileAds.initialize(getContext());
    }

    private void initOnCreate() {
        allItemsModel = new ViewModelProvider(requireActivity()).get(AllItemsModel.class);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        appBarLayout = view.findViewById(R.id.appbar);
        mRecyclerView = view.findViewById(R.id.grid_recycler_view);
        scroll = view.findViewById(R.id.scroll_up_down);
        header = view.findViewById(R.id.text);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollUp = false;

        //initialize views
        initViews();

        mainModel.getAll_items().observe(requireActivity(), shoppingItems -> {

            allItemsModel.clearItems();
            recyclerGridAdapter.notifyDataSetChanged();

            int count_ads = 0;
            for (ShoppingItem shoppingItem : shoppingItems) {

                if(!Objects.requireNonNull(allItemsModel.getItems().getValue()).contains(shoppingItem)) {
                    int match_per = 0;
                    if (mainModel.getPreferred().getValue() != null) {
                        match_per = Objects.requireNonNull(mainModel.getPreferred().getValue()).
                                calculateMatchingPercentage(shoppingItem);
                    }
                    shoppingItem.setPercentage(match_per);

                    allItemsModel.addItem(shoppingItem);
                    recyclerGridAdapter.notifyDataSetChanged();
                }
                if ((Objects.requireNonNull(allItemsModel.getItems().getValue()).size() % Macros.SEARCH_TO_AD == 0)) {
                    ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                    if (shoppingItemAd != null) {
                        count_ads++;
                        ShoppingItem adItem = new ShoppingItem();
                        adItem.setNativeAd(shoppingItemAd.getNativeAd());
                        adItem.setAd(true);
                        allItemsModel.addItem(adItem);
                        recyclerGridAdapter.notifyDataSetChanged();
                    }
                }
            }

            String text= "";
            if(shoppingItems.size() > 0) {
                String cat = shoppingItems.get(0).getType();
                String sub_cat = shoppingItems.get(0).getSub_category();
                text = cat.toUpperCase() + " | " + sub_cat.toUpperCase() + " | " +
                        (allItemsModel.getItems().getValue().size() - count_ads) + " ITEMS";
            }
            else
                text = "NO ITEMS FOUND";

            header.setText(text);
            recyclerGridAdapter.setAllItems(Objects.requireNonNull(allItemsModel.getItems().getValue()));
            recyclerGridAdapter.notifyDataSetChanged();
        });

        scroll.setOnClickListener(v -> {
            //scroll down
            if(!scrollUp) {
                mLayoutManager.smoothScrollToPosition(mRecyclerView,
                        null, Objects.requireNonNull(allItemsModel.getItems().getValue()).size() - 1);
                scrollUp = !scrollUp;
            } //scroll down
            else {
                mLayoutManager.smoothScrollToPosition(mRecyclerView,null,0);
                scrollUp = !scrollUp;
            }
        });

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // ON BOTTOM
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scroll.setRotation(180);
                }
                // ON TOP
                else if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    scroll.setRotation(0);
                }
            }
        };

        mRecyclerView.addOnScrollListener(onScrollListener);

        listener = (appBarLayout, verticalOffset) -> {

            RelativeLayout relativeLayout = requireView().findViewById(R.id.info_layout);
            Toolbar toolbar = requireView().findViewById(R.id.toolbar);

            System.out.println(verticalOffset);

            // Collapsed
            if (verticalOffset <= -40) {
                relativeLayout.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.VISIBLE);
            }
            // Expanded
            else {
                toolbar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        };
        appBarLayout.addOnOffsetChangedListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        allItemsModel.getItems().removeObservers(getViewLifecycleOwner());
        appBarLayout.removeOnOffsetChangedListener(listener);
        appBarLayout = null;
        searchView2 = null;
        price = null;
        sale = null;
        match = null;
        brand = null;
        company = null;
        mRecyclerView.removeOnScrollListener(onScrollListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViews() {

        recyclerGridAdapter = new RecyclerGridAdapter(allItemsModel.getItems().getValue(),null);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);

        initChips();
    }

    private void initChips(){

        price = requireView().findViewById(R.id.price_chip);
        sale = requireView().findViewById(R.id.sale_chip);
        match = requireView().findViewById(R.id.match_chip);
        brand = requireView().findViewById(R.id.brand_chip);
        company = requireView().findViewById(R.id.company_chip);
        Chip favorite = requireView().findViewById(R.id.favorites_chip);

        Chip toolbar_price = requireView().findViewById(R.id.price_chip2);
        Chip toolbar_sale = requireView().findViewById(R.id.sale_chip2);
        Chip toolbar_match = requireView().findViewById(R.id.match_chip2);
        Chip toolbar_favorite = requireView().findViewById(R.id.favorites_chip2);
        Chip toolbar_company = requireView().findViewById(R.id.company_chip2);
        Chip toolbar_brand = requireView().findViewById(R.id.brand_chip2);

        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        company.setOnClickListener(this);
        brand.setOnClickListener(this);
        match.setOnClickListener(this);
        favorite.setOnClickListener(this);

        toolbar_favorite.setOnClickListener(this);
        toolbar_match.setOnClickListener(this);
        toolbar_brand.setOnClickListener(this);
        toolbar_company.setOnClickListener(this);
        toolbar_sale.setOnClickListener(this);
        toolbar_price.setOnClickListener(this);
    }

    private void setSearch() {

        String queryHint = "Search something...";
        searchView2.setQueryHint(queryHint);

        searchView2.setOnClickListener(v -> searchView2.onActionViewExpanded());
        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerGridAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void closeKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if( view != null ){
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
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
            case R.id.company_chip:
            case R.id.company_chip2:
                sortItems("company");
                break;
            case R.id.brand_chip:
            case R.id.brand_chip2:
                sortItems("brand");
                break;
            default:
                sortItems("clear");
                break;
        }
    }

    private void sortItems(String sort_by){
        recyclerGridAdapter.getSortingFilter().filter(sort_by);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem getItem = menu.findItem(R.id.nav_search);
        searchView2 = (SearchView) getItem.getActionView();

        String queryHint = "Search something...";
        searchView2.setQueryHint(queryHint);
        searchView2.setOnClickListener(v -> searchView2.onActionViewExpanded());
        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                closeKeyboard();
                recyclerGridAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerGridAdapter.getFilter().filter(newText);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }
}