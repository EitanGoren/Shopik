package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.ArraySet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eitan.shopik.Customer.FullscreenImageActivity;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class OutletsGridAdapter extends ArrayAdapter<RecyclerItem> implements Serializable {

        private List<RecyclerItem> AllItemsList;
        private List<RecyclerItem> ItemsList;

        public OutletsGridAdapter(@NonNull Context context, int resource, List<RecyclerItem> items){
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

            Macros.Functions.GlidePicture(getContext(), imagesUrl.get(0), imageView);

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

            ArrayList<Pair<View,String>> pairs = new ArrayList<>();
            pairs.add(Pair.create(imageView,"image_item"));

            fullscreen.setOnClickListener(v -> {

                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("isFav", false);
                intent.putExtra("brand", item.getSeller());
                intent.putExtra("id", item.getId());
                for(int i=0;i<item.getImages().size(); i++) {
                    intent.putExtra("img"+(i+1), item.getImages().get(i));
                }
                if(item.getImages().size() < 4){
                    for(int i=item.getImages().size(); i<4; i++) {
                        intent.putExtra("img"+(i), item.getImages().get(0));
                    }
                }
                intent.putExtra("seller_logo", item.getSellerLogoUrl());
                intent.putExtra("description", item.toString());
                intent.putExtra("seller", item.getSeller());

                Macros.Functions.fullscreen( getContext(), intent, pairs);
            });

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
