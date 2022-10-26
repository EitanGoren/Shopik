package com.eitan.shopik.customerFragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.CustomItemAnimator;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.RecyclerGridAdapter;
import com.eitan.shopik.customer.CustomerMainActivity;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.viewModels.DatabaseViewModel;
import com.eitan.shopik.viewModels.GenderModel;
import com.eitan.shopik.viewModels.MainModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.shape.ShapeAppearanceModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SearchFragment extends Fragment implements View.OnClickListener{

    private MainModel mainModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerGridAdapter recyclerGridAdapter;
    private AppBarLayout appBarLayout;
    private AppBarLayout.OnOffsetChangedListener listener;
    private RecyclerView.OnScrollListener onScrollListener;
    private TextView header;
    private SearchView searchView;
    private Toolbar toolbar;
    private String item_type;
    private Observer<Set<ShoppingItem>> listObserver;
    private ExtendedFloatingActionButton explore_items;
    private RelativeLayout relativeLayout;
    private Observer<Pair<Integer,Integer>> total_items_observer;
    private ChipGroup chip_group_sub_cat;
    private ChipGroup chip_group_filter;
    private Set<String> all_sellers;
    private Set<ShoppingItem> items = new HashSet<>();
    private HorizontalScrollView fhsv, schsv;
    private boolean isFinishedFetching = false;

    public SearchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenderModel genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        setHasOptionsMenu(true);
        item_type = genderModel.getType().getValue();
        all_sellers = new HashSet<>();//ShopikApplication.sellers;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        appBarLayout = view.findViewById(R.id.appbar);
        mRecyclerView = view.findViewById(R.id.grid_recycler_view);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());

        fhsv = view.findViewById(R.id.filter_scroll);
        schsv = view.findViewById(R.id.chip_group_sub_cat_layout);
        header = view.findViewById(R.id.text);

        Chip price = view.findViewById(R.id.price_chip);
        Chip all = view.findViewById(R.id.all_chip);
        Chip sale = view.findViewById(R.id.sale_chip);
        Chip match = view.findViewById(R.id.match_chip);
        Chip brand = view.findViewById(R.id.brand_chip);
        Chip company1 = view.findViewById(R.id.company_chip);

        chip_group_sub_cat = view.findViewById(R.id.chip_group_sub_cat);
        chip_group_sub_cat.setSingleSelection(true);
        ChipGroup chip_group_sort = view.findViewById(R.id.chip_group_sort);
        chip_group_sort.setSingleSelection(true);
        chip_group_filter = view.findViewById(R.id.chip_group_filter);
        chip_group_filter.setSingleSelection(true);

        Chip favorite = view.findViewById(R.id.favorites_chip);
        toolbar = view.findViewById(R.id.toolbar);
        explore_items = view.findViewById(R.id.explore_items);
        explore_items.setVisibility(View.GONE);
        relativeLayout = view.findViewById(R.id.info_layout);

        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        company1.setOnClickListener(this);
        brand.setOnClickListener(this);
        match.setOnClickListener(this);
        favorite.setOnClickListener(this);
        all.setOnClickListener(this);

        DatabaseViewModel databaseViewModel = DatabaseViewModel.getInstance();
        ShopikUser shopikUser = ShopikUser.getInstance();
        databaseViewModel.updateTopWords(item_type, shopikUser.getGender());

        listObserver = shoppingItems -> {
            if(!shoppingItems.isEmpty()) {
                items.clear();
                items.addAll(shoppingItems);
                if (recyclerGridAdapter == null) {
                    recyclerGridAdapter = new RecyclerGridAdapter(items, null);
                    mRecyclerView.setAdapter(recyclerGridAdapter);
                }
                all_sellers.clear();

                for (ShoppingItem shoppingItem : shoppingItems) {
                    if (!items.contains(shoppingItem)) {
                        int match_per = 0;
                        if (mainModel.getPreferred().getValue() != null) {
                            match_per = Objects.requireNonNull(mainModel.getPreferred().getValue()).
                                    calculateMatchingPercentage(shoppingItem);
                        }
                        shoppingItem.setPercentage(match_per);
                    }
                    all_sellers.add(shoppingItem.getSeller());
                }

                chip_group_filter.removeAllViews();
                createChipByName(all_sellers);
                recyclerGridAdapter.setAllItems(Objects.requireNonNull(items));
                recyclerGridAdapter.notifyDataSetChanged();
            }
        };
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
        total_items_observer = pair -> {
            String text;
            if (pair.first > 0)
                text = item_type.toUpperCase() + " | " + pair.first + " ITEMS";
            else
                text = "NO ITEMS FOUND";

            header.setText(text);

            isFinishedFetching = (pair.first.equals(pair.second));
        };
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //NOT MOVING
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    EventBus.getDefault().post(new CustomerMainActivity.NotScrollingEvent());
                    if(!recyclerView.canScrollVertically(1) && isFinishedFetching) {
                        explore_items.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeIn).playOn(explore_items);
                    }
                }
                //MOVING
                else {
                    EventBus.getDefault().post(new CustomerMainActivity.ScrollingEvent());
                }
                if(explore_items.getVisibility() == View.VISIBLE){
                    Handler handler = new Handler();
                    handler.postDelayed(() -> YoYo.with(Techniques.FadeOut).
                            onEnd(animator -> explore_items.setVisibility(View.GONE)).
                            playOn(explore_items),1000 * 5);
                }
            }
        };
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mainModel.getAllItems().observe(requireActivity(), listObserver);
        mainModel.getCurrentItem().observe(requireActivity(), total_items_observer);
        mRecyclerView.addOnScrollListener(onScrollListener);
        appBarLayout.addOnOffsetChangedListener(listener);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EventBus.getDefault().unregister(this);
        mainModel.getAllItems().removeObserver(listObserver);
        mainModel.getCurrentItem().removeObserver(total_items_observer);
        appBarLayout.removeOnOffsetChangedListener(listener);
        mLayoutManager = null;
        appBarLayout = null;
        toolbar = null;
        header = null;
        mRecyclerView.removeOnScrollListener(onScrollListener);
        mRecyclerView = null;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        chip_group_sub_cat.clearCheck();
        chip_group_filter.clearCheck();

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
            case R.id.all_chip:
                sortItems("clear");
            default:
                filterSubCat(String.valueOf(v.getId()));
        }
    }
    private void sortItems(String sort_by){
        recyclerGridAdapter.getSortingFilter().filter(sort_by, count -> updateHeader(count,false));
    }
    private void filterItems(String filter_by){
        recyclerGridAdapter.getFilter().filter(filter_by, count -> updateHeader(count,true));
    }
    private void filterSubCat(String sub_cat){
        recyclerGridAdapter.getSubCatFilter().filter(sub_cat, count -> updateHeader(count,true));
    }
    private void updateHeader(int count, boolean isFilter) {
        String text;
        if(isFilter) {
            text = count + " ITEMS ";
        }
        else{
            text = item_type.toUpperCase() + " | " + count + " ITEMS";
        }
        header.setText(text);
    }
    private void closeKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if( view != null ){
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @SuppressLint("SetTextI18n")
    private void CreateTopWordsChips(Set<String> words) {
        if(words == null) {
            schsv.setVisibility(View.GONE);
            return;
        }
        for (String word: words) {
            Chip chipy = new Chip(getContext());
            chipy.setText(word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase());
            chipy.setOnClickListener(view -> filterSubCat(word));
            chipy.setTextColor(getResources().getColor(R.color.LightThemeColor));
            chipy.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            ShapeAppearanceModel sam = new ShapeAppearanceModel().withCornerSize(70);
            chipy.setShapeAppearanceModel(sam);
            chipy.setTextSize(16);
            chipy.setChipStartPadding(10);
            chipy.setChipEndPadding(10);
            chipy.setCheckable(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMarginStart(15);
            layoutParams.setMarginEnd(15);
            chipy.setLayoutParams(layoutParams);
            chip_group_sub_cat.addView(chipy);
        }
        schsv.setVisibility(View.VISIBLE);
    }
    private void createChipByName(Set<String> sellers) {

        if(sellers.isEmpty()){
            fhsv.setVisibility(View.GONE);
            return;
        }
        for (String seller: sellers) {
            String _name = seller.substring(0, 1).toUpperCase() + seller.substring(1).toLowerCase();
            Chip chipy = new Chip(getContext());
            chipy.setText(_name);
            chipy.setOnClickListener(view -> filterItems(_name));
            chipy.setTextColor(getResources().getColor(R.color.LightThemeColor));
            chipy.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            ShapeAppearanceModel sam = new ShapeAppearanceModel().withCornerSize(80);
            chipy.setShapeAppearanceModel(sam);
            chipy.setTextSize(15);
            chipy.setChipStartPadding(10);
            chipy.setChipEndPadding(10);
            chipy.setCheckable(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMarginStart(12);
            layoutParams.setMarginEnd(12);
            chipy.setLayoutParams(layoutParams);
            chip_group_filter.addView(chipy);
        }
        fhsv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) menu.findItem(R.id.nav_search).getActionView();

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

    @Subscribe
    public void onTopWordsEvent(Database.TopWordsEvent event){
        CreateTopWordsChips(Objects.requireNonNull(event.topWords));
    }

    @Subscribe
    public void onScrollUpEvent(CustomerMainActivity.ScrollUpEvent event){
        if (recyclerGridAdapter.getItemCount() > 100)
            mLayoutManager.scrollToPosition(recyclerGridAdapter.getItemCount() - 1);
        else if (recyclerGridAdapter.getItemCount() > 0)
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, recyclerGridAdapter.getItemCount() - 1);
    }

    @Subscribe
    public void onTopWordsEvent(CustomerMainActivity.ScrollDownEvent event){
        if (recyclerGridAdapter.getItemCount() > 100)
            mLayoutManager.scrollToPosition(0);
        else if (recyclerGridAdapter.getItemCount() > 0)
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
    }
}