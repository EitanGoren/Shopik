package com.eitan.shopik.CustomerFragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.eitan.shopik.Adapters.GridAdapter;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.MainModel;
import com.eitan.shopik.ViewModels.SuggestedModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class SuggestedFragment extends Fragment implements View.OnClickListener{

    private GridAdapter gridAdapter;
    private SuggestedModel suggestedModel;
    private MainModel mainModel;
    private GridView gridContainer;
    private ImageView down_arrow;
    private boolean isOpened = false;
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private ExtendedFloatingActionButton main;
    private FloatingActionButton price,clear,sale,match;
    private float translationY = 100f;
    private LinearLayout options;
    private TextView clear_txt,price_txt,sale_txt,match_txt;
    private CardView clear_card,price_card,sale_card,match_card;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initOnCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_items, container, false);
    }

    private void initOnCreate() {
        suggestedModel = new ViewModelProvider(requireActivity()).get(SuggestedModel.class);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
       // while (mainModel.getAdsContainerSize() !=  Macros.NUM_OF_ADS);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gridAdapter = null;
        gridContainer = null;
        mainModel.getAll_items().removeObservers(getViewLifecycleOwner());
        down_arrow = null;
        main = null;
        price = null;
        clear = null;
        sale = null;
        match = null;
        options = null;
        clear_txt = null;
        price_txt = null;
        sale_txt = null;
        match_txt = null;
        clear_card = null;
        match_card = null;
        sale_card = null;
        price_card = null;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialize views
        init();

        mainModel.getAll_items_ids().observe(requireActivity(), pairs -> {
            suggestedModel.clearItems();
            int count = 0;
            for (Pair<String,ShoppingItem> item : pairs) {
                int match = 0;
                if(suggestedModel.getPreferred().getValue() != null ) {
                    match = Objects.requireNonNull(suggestedModel.getPreferred().getValue()).calculateMatchingPercentage(item.second);
                }
                assert item.second != null;
                item.second.setPercentage(match);
                if(match >= Macros.CustomerMacros.SUGGESTION_PERCENTAGE) {
                    gridContainer.setAdapter(gridAdapter);
                    suggestedModel.addToAllItems(item.second);
                    count++;
                    if( (count % Macros.SUGGESTED_TO_AD == 0) && count > 0 ) {
                        suggestedModel.addToAllItems((ShoppingItem) mainModel.getNextAd());
                        count++;
                    }
                    gridAdapter.notifyDataSetChanged();
                }
            }
            gridAdapter.setAllItems(Objects.requireNonNull(suggestedModel.getAllItems().getValue()));
        });
                gridContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        if (view.canScrollList(View.SCROLL_AXIS_VERTICAL) && (scrollState == SCROLL_STATE_IDLE)) {
                            showArrow();
                        } else if ((scrollState != SCROLL_STATE_IDLE) && view.canScrollList(View.SCROLL_AXIS_VERTICAL)) {
                            hideArrow();
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                    }
                });
    }

    private void init() {
        gridContainer = requireView().findViewById(R.id.grid_view);
        gridAdapter = new GridAdapter(requireContext(),R.layout.grid_item,suggestedModel.getAllItems().getValue());
        gridContainer.setAdapter(gridAdapter);
        down_arrow = requireView().findViewById(R.id.see_items_below);

        initFab();
    }

    private void initFab(){

        main = requireView().findViewById(R.id._main_icon);
        price = requireView().findViewById(R.id._price_icon);
        sale = requireView().findViewById(R.id._sale_icon);
        match = requireView().findViewById(R.id._match_icon);
        clear = requireView().findViewById(R.id._clear);
        options = requireView().findViewById(R.id._more_options_layout);
        clear_txt = requireView().findViewById(R.id.clear_text);
        price_txt = requireView().findViewById(R.id.price_text);
        sale_txt = requireView().findViewById(R.id.sale_text);
        match_txt = requireView().findViewById(R.id.match_text);
        clear_card = requireView().findViewById(R.id.clear_card);
        price_card = requireView().findViewById(R.id.price_card);
        sale_card = requireView().findViewById(R.id.sale_card);
        match_card = requireView().findViewById(R.id.match_card);

        options.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.floating));
        main.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.floating));

        price.setTranslationY(translationY);
        sale.setTranslationY(translationY);
        match.setTranslationY(translationY);
        clear.setTranslationY(translationY);
        match_txt.setTranslationY(translationY);
        sale_txt.setTranslationY(translationY);
        price_txt.setTranslationY(translationY);
        clear_txt.setTranslationY(translationY);
        match_card.setTranslationY(translationY);
        sale_card.setTranslationY(translationY);
        price_card.setTranslationY(translationY);
        clear_card.setTranslationY(translationY);

        price.setAlpha(0f);
        sale.setAlpha(0f);
        match.setAlpha(0f);
        clear.setAlpha(0f);
        price_txt.setAlpha(0f);
        sale_txt.setAlpha(0f);
        match_txt.setAlpha(0f);
        clear_txt.setAlpha(0f);
        price_card.setAlpha(0f);
        sale_card.setAlpha(0f);
        match_card.setAlpha(0f);
        clear_card.setAlpha(0f);

        main.setOnClickListener(this);
        price.setOnClickListener(this);
        sale.setOnClickListener(this);
        match.setOnClickListener(this);
        clear.setOnClickListener(this);
    }

    private void switchFab(){

        main.shrink();
        //open
        if(!isOpened) {
            options.setVisibility(View.VISIBLE);
            price.setVisibility(View.VISIBLE);
            match.setVisibility(View.VISIBLE);
            sale.setVisibility(View.VISIBLE);
            clear.setVisibility(View.VISIBLE);
            price_txt.setVisibility(View.VISIBLE);
            match_txt.setVisibility(View.VISIBLE);
            sale_txt.setVisibility(View.VISIBLE);
            clear_txt.setVisibility(View.VISIBLE);
            price_card.setVisibility(View.VISIBLE);
            match_card.setVisibility(View.VISIBLE);
            sale_card.setVisibility(View.VISIBLE);
            clear_card.setVisibility(View.VISIBLE);

            main.animate().setInterpolator(overshootInterpolator).rotationBy(180f).setDuration(300).start();
            sale.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            price.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            match.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            sale_txt.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            price_txt.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            match_txt.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear_txt.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            sale_card.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            price_card.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            match_card.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear_card.animate().translationY(0f).alpha(1f).setInterpolator(overshootInterpolator).setDuration(300).start();
        }
        //close
        else {
            main.animate().setInterpolator(overshootInterpolator).rotationBy(180f).setDuration(300).start();
            price.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            sale.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            match.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            price_txt.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            sale_txt.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            match_txt.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear_txt.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            price_card.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            sale_card.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            match_card.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();
            clear_card.animate().translationY(translationY).alpha(0f).setInterpolator(overshootInterpolator).setDuration(300).start();

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                options.setVisibility(View.GONE);
                price.setVisibility(View.GONE);
                sale.setVisibility(View.GONE);
                match.setVisibility(View.GONE);
                clear.setVisibility(View.GONE);
                price_txt.setVisibility(View.GONE);
                match_txt.setVisibility(View.GONE);
                sale_txt.setVisibility(View.GONE);
                clear_txt.setVisibility(View.GONE);
                price_card.setVisibility(View.GONE);
                match_card.setVisibility(View.GONE);
                sale_card.setVisibility(View.GONE);
                clear_card.setVisibility(View.GONE);
            },500);
        }
        isOpened = !isOpened;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id._main_icon:
                switchFab();
                break;
            case R.id._match_icon:
                sortItems("match");
                break;
            case R.id._price_icon:
                sortItems("price");
                break;
            case R.id._sale_icon:
                sortItems("sale");
                break;
            case R.id._clear:
                sortItems("clear");
                break;
        }
    }

    private void sortItems(String sort_by) {
        gridAdapter.getSortingFilter().filter(sort_by);
    }
}