package com.eitan.shopik.CustomerFragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.eitan.shopik.Adapters.SearchAdapter;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.AllItemsModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class SearchFragment extends Fragment implements View.OnClickListener{

    private SearchAdapter searchAdapter;
    private ListView listContainer;
    private boolean isOpened = false;
    private boolean isSearchOpened = false;
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private ExtendedFloatingActionButton main;
    private FloatingActionButton search,clear,more;
    private float translationY = 100f;
    private LinearLayout options;
    private ImageView down_arrow;
    private CardView search_card;
    private SearchView searchView2;
    private AllItemsModel allItemsModel;
    private MainModel mainModel;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
        MobileAds.initialize(getContext());
    }

    private void initOnCreate() {
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        allItemsModel = new ViewModelProvider(requireActivity()).get(AllItemsModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();

        mainModel.getAll_items_ids().observe(getViewLifecycleOwner(), pairs -> {
            allItemsModel.clearItems();
            int count = 0;
            for (Pair<String,ShoppingItem> item : pairs) {
                final ArrayList<String> imagesUrl = new ArrayList<>();
                Database connection = new Database();
                assert item.second != null;
                imagesUrl.add(connection.getASOSimageUrl(1,item.second.getColor(),item.second.getId_in_seller()));
                imagesUrl.add(connection.getASOSimageUrl(2,item.second.getColor(),item.second.getId_in_seller()));
                imagesUrl.add(connection.getASOSimageUrl(3,item.second.getColor(),item.second.getId_in_seller()));
                imagesUrl.add(connection.getASOSimageUrl(4,item.second.getColor(),item.second.getId_in_seller()));

                RecyclerItem recyclerItem = new RecyclerItem(item.second.getBrand(), item.second.getSite_link());
                recyclerItem.setPrice(item.second.getPrice());
                recyclerItem.setLink(item.second.getSite_link());
                recyclerItem.setDescription(item.second.getName());
                recyclerItem.setType(item.second.getType());
                recyclerItem.setId(item.second.getId());
                recyclerItem.setSale(item.second.isOn_sale());
                recyclerItem.setVideo_link(item.second.getVideo_link());
                recyclerItem.setOutlet(item.second.isOutlet());
                recyclerItem.setReduced_price(item.second.getReduced_price());
                recyclerItem.setImages(imagesUrl);
                recyclerItem.setSellerImageUrl(item.second.getSellerLogoUrl());

                allItemsModel.addItem(recyclerItem);
                count++;
                if( (count% Macros.SEARCH_TO_AD == 0) && count > 0 ) {
                    ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                    if(shoppingItemAd != null) {
                        RecyclerItem adItem = new RecyclerItem(null, null);
                        adItem.setNativeAd(shoppingItemAd.getNativeAd());
                        adItem.setAd(true);
                        allItemsModel.addItem(adItem);
                        count++;
                    }
                }
                searchAdapter.notifyDataSetChanged();
            }
            searchAdapter.setAllItems(Objects.requireNonNull(allItemsModel.getItems().getValue()));
        });
        listContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(view.canScrollList(View.SCROLL_AXIS_VERTICAL) && (scrollState == SCROLL_STATE_IDLE)){
                    showArrow();
                }
                else if( (scrollState != SCROLL_STATE_IDLE) && view.canScrollList(View.SCROLL_AXIS_VERTICAL)){
                    hideArrow();
                }
                // were mot moving
                if(scrollState == SCROLL_STATE_IDLE && !isOpened && !isSearchOpened)
                    main.extend();
                    // were scrolling
                else
                    main.shrink();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        searchAdapter = null;
        listContainer = null;
        allItemsModel.getItems().removeObservers(getViewLifecycleOwner());
        options = null;
        search_card = null;
        searchView2 = null;
        main = null;
        search = null;
        clear = null;
        more = null;
        down_arrow = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initViews() {
        listContainer = requireView().findViewById(R.id.search_list);
        search_card = requireView().findViewById(R.id.search_card);
        searchView2 = getView().findViewById(R.id.search_bar);
        down_arrow = requireView().findViewById(R.id.see_items_below);
        searchAdapter = new SearchAdapter(requireActivity(), R.layout.list_item, allItemsModel.getItems().getValue());
        listContainer.setAdapter(searchAdapter);

        initFab();
    }

    private void hideArrow(){
        down_arrow.clearAnimation();
        down_arrow.setVisibility(View.GONE);
    }

    private void showArrow(){
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.blink_anim);
        down_arrow.startAnimation(animation);
        down_arrow.setVisibility(View.VISIBLE);
    }

    private void initFab(){

        main = requireView().findViewById(R.id._main_fab);
        search = getView().findViewById(R.id._search_icon);
        more = getView().findViewById(R.id._more_icon);
        clear = getView().findViewById(R.id._clear_search);
        options = getView().findViewById(R.id._more_options_layout);

        options.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.floating));
        main.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.floating));

        search.setTranslationY(translationY);
        more.setTranslationY(translationY);
        clear.setTranslationY(translationY);

        search.setAlpha(0f);
        more.setAlpha(0f);
        clear.setAlpha(0f);

        main.setOnClickListener(this);
        more.setOnClickListener(this);
        search.setOnClickListener(this);
        clear.setOnClickListener(this);
    }

    private void switchFab(){

        main.shrink();
        //open
        if(!isOpened) {
            options.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            more.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);

            main.animate().setInterpolator(overshootInterpolator).rotationBy(180f).setDuration(300).start();
            search.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            more.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
        }
        //close
        else {
            main.animate().setInterpolator(overshootInterpolator).rotationBy(180f).setDuration(300).start();
            search.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            more.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                options.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
                more.setVisibility(View.GONE);
                clear.setVisibility(View.GONE);
            },500);
        }
        isOpened = !isOpened;
    }

    private void setSearch() {

        //open search view
        if (!isSearchOpened) {

            switchFab();
            searchView2.setVisibility(View.VISIBLE);
            search_card.setVisibility(View.VISIBLE);
            String queryHint = "Search something...";
            searchView2.setQueryHint(queryHint);

            search_card.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fui_slide_in_right));
            listContainer.setAdapter(searchAdapter);

            searchView2.setOnClickListener(v -> searchView2.onActionViewExpanded());
            searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    closeKeyboard();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }
        else {
            search_card.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fui_slide_out_left));
            search_card.setVisibility(View.GONE);
            switchFab();
        }
        isSearchOpened = !isSearchOpened;
    }

    private void closeKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if( view != null ){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id._main_fab:
                switchFab();
                break;
            case R.id._search_icon:
                setSearch();
                break;
            case R.id._clear_search:
                searchView2.setQuery("",true);
                closeKeyboard();
                break;
            case R.id.more_icon:
                searchAdapter.getSortingFilter().filter("price");
        }
    }

}