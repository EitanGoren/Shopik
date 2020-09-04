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
import com.eitan.shopik.ViewModels.AllItemsModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener{

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
    private SearchView searchView;
    private Toolbar toolbar;
    String item_type,item_gender,item_sub_category;
    private long page = 0;
    private Observer<CopyOnWriteArrayList<ShoppingItem>> listObserver;
    private Observer<Integer> integerObserver;
    private Observer<Long> longObserver;

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container,false);

        appBarLayout = view.findViewById(R.id.appbar);

        mRecyclerView = view.findViewById(R.id.grid_recycler_view);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        scroll = view.findViewById(R.id.scroll_up_down);
        header = view.findViewById(R.id.text);
        price = view.findViewById(R.id.price_chip);
        sale = view.findViewById(R.id.sale_chip);
        match = view.findViewById(R.id.match_chip);
        brand = view.findViewById(R.id.brand_chip);
        company = view.findViewById(R.id.company_chip);
        toolbar = view.findViewById(R.id.toolbar);

        Chip favorite = view.findViewById(R.id.favorites_chip);
        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        company.setOnClickListener(this);
        brand.setOnClickListener(this);
        match.setOnClickListener(this);
        favorite.setOnClickListener(this);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollUp = false;

        recyclerGridAdapter = new RecyclerGridAdapter(allItemsModel.getItems().getValue(),null);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);

        listObserver = shoppingItems -> {

            allItemsModel.clearItems();
            recyclerGridAdapter.notifyDataSetChanged();

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

            String text;
            if (shoppingItems.size() > 0) {
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
        };
        mainModel.getAll_items().observe(requireActivity(),listObserver );

        scroll.setOnClickListener(v -> {

            //scroll down
            if (!scrollUp)
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null,
                        Objects.requireNonNull(allItemsModel.getItems().getValue()).size() - 1);

                //scroll down
            else
                mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);

            scrollUp = !scrollUp;

        });

        longObserver = aLong -> page = aLong;
        mainModel.getCurrent_page().observe(requireActivity(),longObserver);

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
            }
        };

        mRecyclerView.addOnScrollListener(onScrollListener);

        listener = (appBarLayout, verticalOffset) -> {

            RelativeLayout relativeLayout = requireView().findViewById(R.id.info_layout);
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
        appBarLayout.addOnOffsetChangedListener(listener);

        integerObserver = integer -> {
            if (integer == 100)
                recyclerGridAdapter.notifyDataSetChanged();
        };
        mainModel.getTotalItems().observe(requireActivity(),integerObserver );
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
        price = null;
        sale = null;
        match = null;
        brand = null;
        company = null;
        scroll = null;
        toolbar = null;
        header = null;
        mRecyclerView.removeOnScrollListener(onScrollListener);
        mRecyclerView.removeAllViewsInLayout();
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