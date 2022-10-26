package com.eitan.shopik.customerFragments;

import static com.eitan.shopik.database.Database.FAVORITE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eitan.shopik.CustomItemAnimator;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.RecyclerGridAdapter;
import com.eitan.shopik.customer.CustomerMainActivity;
import com.eitan.shopik.database.ShopikRepository;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.viewModels.MainModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FavoritesFragment extends Fragment {

    private MainModel mainModel;
    private DividerItemDecoration dividerItemDecoration;
    private RecyclerGridAdapter recyclerGridAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView searchView;
    private SearchView.OnQueryTextListener queryTextListener;
    private FavViewHolder viewHolder;
    private Observer<Pair<Integer,Integer>> progressObserver;
    private Observer<Set<ShoppingItem>> favoritesObserver;
    private final Set<ShoppingItem> items = new HashSet<>();

    private static class FavViewHolder extends RecyclerView.ViewHolder{

        protected TextView mHeader;
        protected RecyclerView mRecyclerView;
        protected VerticalSpaceItemDecoration verticalSpaceItemDecoration;
        protected View itemView;

        public FavViewHolder(@NonNull View itemView) {
            super(itemView);
            mHeader = itemView.findViewById(R.id.header_text);
            mRecyclerView = itemView.findViewById(R.id.list_recycler_view);
            this.itemView = itemView;

            verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(0);
            mRecyclerView.addItemDecoration(verticalSpaceItemDecoration);
            DefaultItemAnimator defaultItemAnimator = new CustomItemAnimator();
            mRecyclerView.setItemAnimator(defaultItemAnimator);
        }

        public void setHeader(String header){
            this.mHeader.setText(header);
        }
        public RecyclerView getRecyclerView() {
            return mRecyclerView;
        }
        public void setRecyclerView(RecyclerGridAdapter recyclerGridAdapter, RecyclerView.LayoutManager mLayoutManager, DividerItemDecoration dividerItemDecoration){
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(recyclerGridAdapter);
            mRecyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainModel = new ViewModelProvider(requireActivity()).get(MainModel.class);
        dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireContext(), R.drawable.recycler_divider)));
        mLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(viewHolder == null) {
            View view = inflater.inflate(R.layout.fragment_favorites, container, false);
            viewHolder = new FavViewHolder(view);
        }

        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //NOT MOVING
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    EventBus.getDefault().post(new CustomerMainActivity.NotScrollingEvent());
                    //MOVING
                else
                    EventBus.getDefault().post(new CustomerMainActivity.ScrollingEvent());
            }
        };
        viewHolder.mRecyclerView.setOnScrollListener(onScrollListener);

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

        ShopikRepository repository = new ShopikRepository(requireActivity().getApplication());
        repository.getAllInteractedSwimwearMen().observe(requireActivity(), shoppingItems -> {
            String text;
            if (!shoppingItems.isEmpty()) {
                items.clear();
                items.addAll(shoppingItems);
                if (recyclerGridAdapter == null) {
                    recyclerGridAdapter = new RecyclerGridAdapter(items, FAVORITE);
                    recyclerGridAdapter.setHeaderView(viewHolder.mHeader);
                    viewHolder.setRecyclerView(recyclerGridAdapter, mLayoutManager, dividerItemDecoration);
                }
                recyclerGridAdapter.setAllItems(Objects.requireNonNull(items));
                recyclerGridAdapter.notifyDataSetChanged();
                try {
                    String cat = ((ShoppingItem) shoppingItems.toArray()[0]).getType();
                    text = cat.toUpperCase() + " | " + shoppingItems.size() + " ITEMS";
                }
                catch(Exception e) {
                    Log.w(getClass().getName(), Objects.requireNonNull(e.getMessage()));
                    text = "NO FAVORITES FOUND";
                }
            }
            else{
                text = "NO FAVORITES FOUND";
            }
            viewHolder.setHeader(text);
        });
//        favoritesObserver = shoppingItems -> {
//            String text;
//            if (!shoppingItems.isEmpty()) {
//                items.clear();
//                items.addAll(shoppingItems);
//                if (recyclerGridAdapter == null) {
//                    recyclerGridAdapter = new RecyclerGridAdapter(items, FAVORITE);
//                }
//                recyclerGridAdapter.setHeaderView(viewHolder.mHeader);
//                viewHolder.setRecyclerView(recyclerGridAdapter, mLayoutManager, dividerItemDecoration);
//                recyclerGridAdapter.setAllItems(Objects.requireNonNull(items));
//                recyclerGridAdapter.notifyDataSetChanged();
//                try {
//                    String cat = ((ShoppingItem) shoppingItems.toArray()[0]).getType();
//                    text = cat.toUpperCase() + " | " + shoppingItems.size() + " ITEMS";
//                }
//                catch(Exception e) {
//                    Log.w(getClass().getName(), Objects.requireNonNull(e.getMessage()));
//                    text = "NO FAVORITES FOUND";
//                }
//            }
//            else{
//                text = "NO FAVORITES FOUND";
//            }
//            viewHolder.setHeader(text);
//        };
        progressObserver = pair -> {
            int progress = (int) (((float) pair.first / (float) pair.second) * 100);
            if (progress >= 100 && recyclerGridAdapter != null) {
                recyclerGridAdapter.setFinishedFetchingData(true);
            }
        };

        return viewHolder.itemView;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        mainModel.getCurrentItem().observe(requireActivity(), progressObserver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mainModel.getCurrentItem().removeObserver(progressObserver);
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
        MenuItem search = menu.findItem(R.id.nav_search);
        searchView = (SearchView) search.getActionView();

        String queryHint = "Talk To Me...";
        searchView.setQueryHint(queryHint);
        searchView.setOnClickListener(v -> searchView.onActionViewExpanded());
        searchView.setOnQueryTextListener(queryTextListener);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Subscribe
    public void onScrollUpEvent(CustomerMainActivity.ScrollUpEvent event){
        if (recyclerGridAdapter.getItemCount() > 100)
            mLayoutManager.scrollToPosition(recyclerGridAdapter.getItemCount() - 1);
        else if (recyclerGridAdapter.getItemCount() > 0)
            mLayoutManager.smoothScrollToPosition(viewHolder.getRecyclerView(), null, recyclerGridAdapter.getItemCount() - 1);
    }

    @Subscribe
    public void onTopWordsEvent(CustomerMainActivity.ScrollDownEvent event){
        if (recyclerGridAdapter.getItemCount() > 100)
            mLayoutManager.scrollToPosition(0);
        else if (recyclerGridAdapter.getItemCount() > 0)
            mLayoutManager.smoothScrollToPosition(viewHolder.getRecyclerView(), null, 0);
    }
}