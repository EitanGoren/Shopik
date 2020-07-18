package com.eitan.shopik.explanation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.OutletsModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class E3Fragment extends Fragment implements View.OnClickListener {

    private static final int BULK = 5;
    private static final int ITEMS_PER_PAGE = 72;
    private GridView gridContainer;
    private boolean isOpened = false;
    private boolean isSearchOpened = false;
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private ExtendedFloatingActionButton main;
    private FloatingActionButton search,clear,more;
    private float translationY = 100f;
    private LinearLayout options;
    private CardView search_card;
    private SearchView searchView2;
    private String gender;
    private E3GridAdapter gridAdapter;
    private GenderModel model;
    private OutletsModel outletsModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_e3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(GenderModel.class);
        outletsModel = new ViewModelProvider(requireActivity()).get(OutletsModel.class);
        gender = model.getGender().getValue();
        TextView header = requireView().findViewById(R.id.best_sellers2);
        TextView count = requireView().findViewById(R.id.items_count);

        String header_text = "Save on Outlet";
        header.setText(header_text);

        model.getGender().observe(getViewLifecycleOwner(), s -> {
            if(!gender.equals(s)) {
                gender = s;
                outletsModel.clearAllOutlets();
                gridAdapter.clearItems();
                gridAdapter.notifyDataSetChanged();
            }
        });

        gridAdapter = new E3GridAdapter(requireActivity(), R.layout.grid_item, outletsModel.getOutlets().getValue());
        gridContainer = requireView().findViewById(R.id.grid_view);
        outletsModel.getOutlets().observe(requireActivity(), recyclerItems -> {
            for(int i=1; i<recyclerItems.size()+1; ++i){
                String text = "(" + i + " items)";
                count.setText(text);
            }
            gridContainer.setAdapter(gridAdapter);
            gridAdapter.setAllItems(recyclerItems);
            gridAdapter.notifyDataSetChanged();
        });

        initFab();

        gridContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // not moving
                if(scrollState == SCROLL_STATE_IDLE && !isOpened && !isSearchOpened)
                    main.extend();
                // scrolling
                else
                    main.shrink();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });

        search_card = requireView().findViewById(R.id.search_card);
        searchView2 = requireView().findViewById(R.id.search_bar);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        model.getGender().removeObservers(getViewLifecycleOwner());
        outletsModel.getOutlets().removeObservers(getViewLifecycleOwner());
        search_card = null;
        searchView2 = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initFab(){

        main = requireView().findViewById(R.id.main_fab);
        search = requireView().findViewById(R.id.search_icon);
        more = requireView().findViewById(R.id.more_icon);
        clear = requireView().findViewById(R.id.clear_search);
        options = requireView().findViewById(R.id.more_options_layout);

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

        TextView count = requireView().findViewById(R.id.items_count);
        //open search view
        if (!isSearchOpened) {

            switchFab();
            searchView2.setVisibility(View.VISIBLE);
            search_card.setVisibility(View.VISIBLE);
            String queryHint = "Search something...";
            searchView2.setQueryHint(queryHint);

            search_card.startAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.fui_slide_in_right));
            gridContainer.setAdapter(gridAdapter);

            searchView2.setOnClickListener(v -> searchView2.onActionViewExpanded());
            searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    closeKeyboard();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    gridAdapter.getFilter().filter(newText, size -> {
                        String text = "(" + size + " items)";
                        count.setText(text);
                    });
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
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_fab:
                switchFab();
                break;
            case R.id.search_icon:
                setSearch();
                break;
            case R.id.clear_search:
                searchView2.setQuery("",true);
                closeKeyboard();
                break;
            case R.id.more_icon:
                //TODO ADD MORE ITEMS
                Toast.makeText(getContext(), "Adding " + BULK*ITEMS_PER_PAGE + " items", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class E3GridAdapter extends ArrayAdapter<RecyclerItem> implements Serializable {

        private List<RecyclerItem> AllItemsList;
        private List<RecyclerItem> ItemsList;

        public E3GridAdapter(@NonNull Context context, int resource, List<RecyclerItem> items){
            super(context, resource, items);
            this.ItemsList = items;
            this.AllItemsList = new ArrayList<>();
        }

        @NonNull
        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {

            final RecyclerItem item = getItem(position);

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.e3_grid_item, parent, false);
            }

            ImageView imageView = convertView.findViewById(R.id.image_slider);
            Button link = convertView.findViewById(R.id.store_link);
            final Button fullscreen = convertView.findViewById(R.id.fullscreen_button);
            TextView price = convertView.findViewById(R.id.slider_brand);
            TextView reduced_price = convertView.findViewById(R.id.reduced_price);
            TextView sale = convertView.findViewById(R.id.sale);

            link.setOnClickListener(v -> {
                assert item != null;
                Macros.Functions.buy(getContext(),item.getLink());
            });

            assert item != null;
            final ArrayList<String> imagesUrl = item.getImages();
            Glide.with(getContext()).load(imagesUrl.get(0)).into(imageView);

            if(item.isSale())
                sale.setVisibility(View.VISIBLE);
            else
                sale.setVisibility(View.INVISIBLE);

            if(item.isOutlet() || item.isSale()){
                Currency shekel = Currency.getInstance("ILS");
                String currency_symbol = shekel.getSymbol();
                Double old = Double.parseDouble(item.getReduced_price()) * Macros.POUND_TO_ILS;
                String cur_price = new DecimalFormat("##.##").format(old) + currency_symbol;

                reduced_price.setVisibility(View.VISIBLE);
                reduced_price.setText(cur_price);
                reduced_price.setTextColor(Color.RED);
                reduced_price.setTextSize(16);

                price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                price.setText(item.getPrice());
                price.setTextSize(12);
            }
            else {
                reduced_price.setVisibility(View.INVISIBLE);
                price.setText(item.getPrice());
                price.setTextSize(16);
            }

            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(),item));

            return convertView;
        }

        @Override
        public int getCount() {
            return ItemsList.size();
        }

        @Nullable
        @Override
        public RecyclerItem getItem(int position) {
            return ItemsList.get(position);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return filter;
        }

        public void setAllItems(CopyOnWriteArrayList<RecyclerItem> allItems){
            this.AllItemsList.addAll(allItems);
        }

        public void clearItems(){
            this.AllItemsList.clear();
            this.ItemsList.clear();
        }

        Filter filter = new Filter() {
            //runs in background thread
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Set<RecyclerItem> filteredList = new ArraySet<>();

                if(constraint.toString().isEmpty()){
                    filteredList.addAll(AllItemsList);
                }
                else {
                    for(RecyclerItem item : AllItemsList){
                        if(item.getDescription() != null) {
                            String description = "";
                            for(String word : item.getDescription()) {
                                description = description.concat(word.toLowerCase());
                            }
                            if(description.contains(constraint.toString().toLowerCase())){
                                filteredList.add(item);
                            }
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                filterResults.count = filteredList.size();

                return filterResults;
            }

            //runs in UI thread
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.values == null )
                    return;
                ItemsList.clear();
                ItemsList.addAll((Collection<? extends RecyclerItem>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}