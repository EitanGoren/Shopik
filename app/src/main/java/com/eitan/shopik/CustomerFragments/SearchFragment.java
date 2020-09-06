package com.eitan.shopik.CustomerFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.eitan.shopik.Adapters.RecyclerGridAdapter;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplicationActivity;
import com.eitan.shopik.ViewModels.AllItemsModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener{

    private AllItemsModel allItemsModel;
    private MainModel mainModel;
    private FloatingActionButton scroll;
    private boolean scrollUp;
    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener listener;
    private RecyclerView.OnScrollListener onScrollListener;
    private TextView header;
    private SearchView searchView;
    private Toolbar toolbar;
    String item_type,item_gender,item_sub_category;
    private int page = 0;
    private Observer<CopyOnWriteArrayList<ShoppingItem>> listObserver;
    private Observer<Integer> integerObserver;
    private Observer<Integer> longObserver;
    private ExtendedFloatingActionButton explore_items;
    private RelativeLayout relativeLayout;
    Chip price ;
    Chip all ;
    Chip sale ;
    Chip match ;
    Chip brand ;
    Chip company ;
    Chip asos_chip ;
    Chip castro_chip ;
    Chip renuar_chip ;
    Chip tx_chip ;
    Chip tfs_chip ;
    Chip aldo_chip ;
    Chip hoodies_chip ;
    Chip favorite ;

    @RequiresApi( api = Build.VERSION_CODES.N )
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allItemsModel = new ViewModelProvider(requireActivity()).get(AllItemsModel.class);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        GenderModel genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        setHasOptionsMenu(true);

        item_gender = genderModel.getGender().getValue();
        item_type = genderModel.getType().getValue();
        item_sub_category = genderModel.getSub_category().getValue();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container,false);

        appBarLayout = view.findViewById(R.id.appbar);
        mRecyclerView = view.findViewById(R.id.grid_recycler_view);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerGridAdapter = new RecyclerGridAdapter(allItemsModel.getItems().getValue(),null);

        scroll = view.findViewById(R.id.scroll_up_down);
        header = view.findViewById(R.id.text);
        price = view.findViewById(R.id.price_chip);
        all = view.findViewById(R.id.all_chip);
        sale = view.findViewById(R.id.sale_chip);
        match = view.findViewById(R.id.match_chip);
        brand = view.findViewById(R.id.brand_chip);
        company = view.findViewById(R.id.company_chip);
        asos_chip = view.findViewById(R.id.asos_chip);
        castro_chip = view.findViewById(R.id.castro_chip);
        renuar_chip = view.findViewById(R.id.renuar_chip);
        tx_chip = view.findViewById(R.id.tx_chip);
        tfs_chip = view.findViewById(R.id.tfs_chip);
        aldo_chip = view.findViewById(R.id.aldo_chip);
        hoodies_chip = view.findViewById(R.id.hoodies_chip);
        favorite = view.findViewById(R.id.favorites_chip);
        toolbar = view.findViewById(R.id.toolbar);
        explore_items = view.findViewById(R.id.explore_items);
        explore_items.setVisibility(View.VISIBLE);
        relativeLayout = view.findViewById(R.id.info_layout);

        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        company.setOnClickListener(this);
        brand.setOnClickListener(this);
        match.setOnClickListener(this);
        asos_chip.setOnClickListener(this);
        renuar_chip.setOnClickListener(this);
        tx_chip.setOnClickListener(this);
        tfs_chip.setOnClickListener(this);
        hoodies_chip.setOnClickListener(this);
        aldo_chip.setOnClickListener(this);
        castro_chip.setOnClickListener(this);
        favorite.setOnClickListener(this);
        all.setOnClickListener(this);

        listObserver = shoppingItems -> {
            allItemsModel.clearItems();
            int count_ads = 0;
            for (ShoppingItem shoppingItem : shoppingItems) {
                if (!Objects.requireNonNull(allItemsModel.getItems().getValue()).contains(shoppingItem)) {
                    int match_per = 0;
                    if (mainModel.getPreferred().getValue() != null) {
                        match_per = Objects.requireNonNull(mainModel.getPreferred().getValue()).
                                calculateMatchingPercentage(shoppingItem);
                    }
                    shoppingItem.setPercentage(match_per);

                    allItemsModel.addItem(shoppingItem);
                    recyclerGridAdapter.notifyItemInserted(allItemsModel.getItems().getValue().size());
                }
                if ((Objects.requireNonNull(allItemsModel.getItems().getValue()).size() % Macros.SEARCH_TO_AD == 0)) {
                    ShoppingItem shoppingItemAd = (ShoppingItem) ShopikApplicationActivity.getNextAd();
                    if (shoppingItemAd != null) {
                        count_ads++;
                        ShoppingItem adItem = new ShoppingItem();
                        adItem.setNativeAd(shoppingItemAd.getNativeAd());
                        adItem.setAd(true);
                        allItemsModel.addItem(adItem);
                        recyclerGridAdapter.notifyItemInserted(allItemsModel.getItems().getValue().size());
                    }
                }
            }

            String text;
            if (shoppingItems.size() > 0) {
                String cat = shoppingItems.get(0).getType();
                String sub_cat = shoppingItems.get(0).getSub_category();
                text = cat.toUpperCase() + " | " + sub_cat.toUpperCase() + " | " +
                        (recyclerGridAdapter.getItemCount() - count_ads) + " ITEMS";
            }
            else
                text = "NO ITEMS FOUND";

            header.setText(text);

            recyclerGridAdapter.setAllItems(Objects.requireNonNull(allItemsModel.getItems().getValue()));
            recyclerGridAdapter.notifyDataSetChanged();
        };
        longObserver = aLong -> page = aLong;
        listener = (appBarLayout, verticalOffset) -> {
            // Collapsed
            if (verticalOffset <= -140) {
                relativeLayout.setVisibility(View.INVISIBLE);
                toolbar.setVisibility(View.VISIBLE);
            }
            // Expanded
            else {
                toolbar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        };
        integerObserver = integer -> {
            recyclerGridAdapter.notifyDataSetChanged();
        };

        explore_items.setOnClickListener(v -> {
            int page = mainModel.getCurrent_page().getValue() == null ? 1 : mainModel.getCurrent_page().getValue() + 1;
            mainModel.setCurrent_page(page);
            explore_items.shrink();
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
                else if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scroll.setRotation(0);
                    // ask if want more items
                }
                if(!recyclerView.canScrollVertically(1)) {
                    explore_items.extend();
                }
            }
        };
        scroll.setOnClickListener(v -> {
            //scroll down
            if (!scrollUp)
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null,recyclerGridAdapter.getItemCount() - 1);
            //scroll down
            else
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);

            scrollUp = !scrollUp;
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollUp = false;

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);
        mainModel.getAll_items().observe(requireActivity(),listObserver );
        mainModel.getCurrent_page().observe(requireActivity(),longObserver);
        mRecyclerView.addOnScrollListener(onScrollListener);
        appBarLayout.addOnOffsetChangedListener(listener);
        mainModel.getCurrentItem().observe(requireActivity(),integerObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mainModel.getCurrent_page().removeObserver(longObserver);
        allItemsModel.getItems().removeObserver(listObserver);
        mainModel.getTotalItems().removeObserver(integerObserver);
        appBarLayout.removeOnOffsetChangedListener(listener);
        mLayoutManager = null;
        appBarLayout = null;
        scroll = null;
        toolbar = null;
        header = null;
        mRecyclerView.removeOnScrollListener(onScrollListener);
        mRecyclerView = null;
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
        uncheckAll();
        if(v instanceof Chip){
            ((Chip)v).setChecked(true);
        }
        switch (v.getId()){
            case R.id.match_chip:
                sortItems("match");
                break;
            case R.id.price_chip:
                sortItems("price");
                break;
            case R.id.sale_chip:
                sortItems("sale");
                break;
            case R.id.favorites_chip:
                sortItems("favorites");
                break;
            case R.id.company_chip:
                sortItems("company");
                break;
            case R.id.brand_chip:
                sortItems("brand");
                break;
            case R.id.asos_chip:
                filterItems("ASOS");
                break;
            case R.id.castro_chip:
                filterItems("Castro");
                break;
            case R.id.tx_chip:
                filterItems("Terminal X");
                break;
            case R.id.renuar_chip:
                filterItems("Renuar");
                break;
            case R.id.hoodies_chip:
                filterItems("Hoodies");
                break;
            case R.id.aldo_chip:
                filterItems("Aldo");
                break;
            case R.id.tfs_chip:
                filterItems("TwentyFourSeven");
                break;
            case R.id.all_chip:
                sortItems("clear");
        }
    }

    private void uncheckAll() {
        price.setChecked(false);
        all.setChecked(false);
        sale.setChecked(false);
        match.setChecked(false);
        brand.setChecked(false);
        company.setChecked(false);
        asos_chip.setChecked(false);
        castro_chip.setChecked(false);
        renuar_chip.setChecked(false);
        tx_chip.setChecked(false);
        tfs_chip.setChecked(false);
        aldo_chip.setChecked(false);
        hoodies_chip.setChecked(false);
        favorite.setChecked(false);
    }

    private void sortItems(String sort_by){
        recyclerGridAdapter.getSortingFilter().filter(sort_by);
    }
    private void filterItems(String filter_by){
        recyclerGridAdapter.getFilter().filter(filter_by);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.nav_search));

        String queryHint = "What's On Your Mind?";
        searchView.setQueryHint(queryHint);
        searchView.setOnClickListener(v -> searchView.onActionViewExpanded());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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