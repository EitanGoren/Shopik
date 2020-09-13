package com.eitan.shopik.CustomerFragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.Adapters.RecyclerGridAdapter;
import com.eitan.shopik.CustomItemAnimator;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplicationActivity;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class FavoritesFragment extends Fragment {

    private GenderModel genderModel;
    private MainModel mainModel;
    private FloatingActionButton scroll;
    private boolean scrollUp;
    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView header;
    private CopyOnWriteArrayList<ShoppingItem> fav_list;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        genderModel = new ViewModelProvider(requireActivity()).get(GenderModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        header = view.findViewById(R.id.header_text);
        mRecyclerView = view.findViewById(R.id.list_recycler_view);
        scroll = view.findViewById(R.id.scroll_up_down);

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

        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(0);
        mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());

        fav_list = new CopyOnWriteArrayList<>();
        recyclerGridAdapter = new RecyclerGridAdapter(fav_list,"favorites");

        scroll.setOnClickListener(v -> {
            int size = Objects.requireNonNull(mainModel.getFavorite().getValue()).size();
            //scroll down
            if(!scrollUp && size > 0) {
                scroll.hide();
                mLayoutManager.smoothScrollToPosition(mRecyclerView,null, (size - 1));
            } //scroll down
            else {
                scroll.hide();
                mLayoutManager.smoothScrollToPosition(mRecyclerView,null,0);
            }
            scrollUp = !scrollUp;
        });
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scrollUp = false;

        init();

        queryTextListener = new SearchView.OnQueryTextListener() {
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
        };
        mainModel.getFavorite().observe(requireActivity(), shoppingItems -> {
            fav_list.clear();
            int count_ads = 0;
            for (ShoppingItem shoppingItem : shoppingItems) {
                fav_list.add(shoppingItem);
                recyclerGridAdapter.notifyItemInserted(0);
                if(( fav_list.size() % Macros.FAV_TO_AD == 0 ) && fav_list.size() > 0 ) {
                    ShoppingItem shoppingItemAd = (ShoppingItem) ShopikApplicationActivity.getNextAd();
                    if(shoppingItemAd != null) {
                        count_ads++;
                        fav_list.add(shoppingItemAd);
                        recyclerGridAdapter.notifyItemInserted(0);
                    }
                }
            }
            String text;
            if(shoppingItems.size() > 0) {
                String cat = shoppingItems.get(0).getType();
                String sub_cat = shoppingItems.get(0).getSub_category();
                text = cat.toUpperCase() + " | " + sub_cat.toUpperCase() + " | " + (fav_list.size() - count_ads) + " ITEMS";
            }
            else
                text = "NO FAVORITES YET";

            header.setText(text);
            recyclerGridAdapter.setAllItems(fav_list);
            recyclerGridAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        genderModel.getType().removeObservers(getViewLifecycleOwner());
        genderModel.getSub_category().removeObservers(getViewLifecycleOwner());
        mRecyclerView = null;
        scroll = null;
        scrollUp = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init(){

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(),
                DividerItemDecoration.VERTICAL);

        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.
                getDrawable(requireContext(),R.drawable.recycler_divider)));

        mLayoutManager = new LinearLayoutManager(requireActivity(),
                LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerGridAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
    }

    @Override
    public void onPause() {
        super.onPause();
        scroll.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        scroll.setVisibility(View.VISIBLE);
    }

    public static class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
            outRect.top = verticalSpaceHeight;
        }
    }

    private void closeKeyboard(){
        View view = requireActivity().getCurrentFocus();
        if( view != null ){
            InputMethodManager imm = (InputMethodManager) requireActivity().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        // Retrieve the SearchView and plug it into SearchManager
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.nav_search));

        String queryHint = "What's On Your Mind?";
        searchView.setQueryHint(queryHint);
        searchView.setOnClickListener(v -> searchView.onActionViewExpanded());
        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu,inflater);
    }
}