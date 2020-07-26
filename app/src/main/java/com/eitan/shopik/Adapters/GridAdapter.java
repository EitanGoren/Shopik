package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GridAdapter extends ArrayAdapter<ShoppingItem> implements Filterable {

    private List<ShoppingItem> AllItemsList;
    private List<ShoppingItem> ItemsList;
    private List<ShoppingItem> items;

    public GridAdapter(@NonNull Context context, int resource, List<ShoppingItem> items){
        super(context, resource,items);
        this.items = items;
        this.ItemsList = items;
        this.AllItemsList = new CopyOnWriteArrayList<>();
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final ShoppingItem item = getItem(position);

        assert item != null;
        if ( item.isAd() && item.getNativeAd() != null){

            item.setAd(true);
            UnifiedNativeAd temp_ad = item.getNativeAd();

            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.native_grid_ad_template, null);
            // Set the media view.
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_brand_name));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));

            adView.setAdvertiserView(adView.findViewById(R.id.ad_card_seller_name));

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(temp_ad.getHeadline());
            adView.getMediaView().setMediaContent(temp_ad.getMediaContent());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (temp_ad.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(temp_ad.getBody());
            }

            if (temp_ad.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(temp_ad.getPrice());
            }

            if (temp_ad.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(temp_ad.getStore());
            }

            if (temp_ad.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(temp_ad.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (temp_ad.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(temp_ad.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(temp_ad);
            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.

            convertView = adView;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(params);

        }
        else if(!item.isAd()){

            ItemsList = items;
            ArrayList<String> images = new ArrayList<>();

            if(item.getSeller().equals("ASOS")) {
                final Database connection = new Database();
                images.add(connection.getASOSimageUrl(3, item.getColor(), item.getId_in_seller()));
                images.add(connection.getASOSimageUrl(2, item.getColor(), item.getId_in_seller()));
                images.add(connection.getASOSimageUrl(1, item.getColor(), item.getId_in_seller()));
                images.add(connection.getASOSimageUrl(4, item.getColor(), item.getId_in_seller()));
            }
            else
                images = item.getImages();

            itemPicsAdapter arrayAdapter = new itemPicsAdapter(images);

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);

            TextView seller_name = convertView.findViewById(R.id.seller_name);
            TextView buy = convertView.findViewById(R.id.list_item_buy_button);
            final Button fullscreen = convertView.findViewById(R.id.fullscreen_button);

            item.setImages(images);
            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(),item));

            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(),item.getSite_link()));

            Currency shekel = Currency.getInstance("ILS");
            String currency_symbol = shekel.getSymbol();
            double current;
            if(item.getSeller().equals("ASOS"))
                current = Double.parseDouble(item.getPrice()) * Macros.POUND_TO_ILS;
            else
                current = Double.parseDouble(item.getPrice());

            TextView price = convertView.findViewById(R.id.price);
            price.setText(new DecimalFormat("##.##").format(current) + currency_symbol);

            TextView counter = convertView.findViewById(R.id.item_count);
            counter.setText((position + 1) + "/" + getCount());

            TextView percentage = convertView.findViewById(R.id.percentage);

            if(item.getPercentage() > 0) {
                percentage.setVisibility(View.VISIBLE);
                percentage.setText(item.getPercentage() + "%");
            }
            else
                percentage.setVisibility(View.INVISIBLE);

            seller_name.setOnClickListener(v -> Macros.Functions.sellerProfile(getContext(),item.getSellerId()));

            String brand = item.getBrand();
            String seller = item.getSeller();

            if(brand != null)
                seller_name.setText(brand);
            else
                seller_name.setText(seller);

            ViewPager viewPager = convertView.findViewById(R.id.image_viewPager);
            viewPager.setAdapter(arrayAdapter);

        }
        return convertView;
    }

    @Nullable
    @Override
    public ShoppingItem getItem(int position) {
        return ItemsList.get(position);
    }

    @Override
    public int getCount() {
        return ItemsList.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter getSortingFilter() {
        return sorting;
    }

    public void setAllItems(List<ShoppingItem> allItems) {
        for(ShoppingItem item : allItems) {
            if( item != null && !item.isAd() ) {
                this.AllItemsList.add(item);
            }
        }
    }

    Filter filter = new Filter() {
        //runs in background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ShoppingItem> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
               filteredList.addAll(AllItemsList);
            }
            else {
                for (ShoppingItem item : AllItemsList) {
                    StringBuilder description = new StringBuilder();
                    if (item.getName() != null) {
                        for (String word : item.getName()) {
                            description.append(word).append(" ");
                        }
                        if (description.toString().contains(constraint)) {
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
            MainModel mainModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(MainModel.class);

            ItemsList.clear();
            int count = 0;
            for(ShoppingItem item : (Collection<? extends ShoppingItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % 20 == 0) && count > 0) {
                    ItemsList.add((ShoppingItem) mainModel.getNextAd());
                }
                notifyDataSetChanged();
            }
        }
    };

    Filter sorting = new Filter() {
        //runs in background thread
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ShoppingItem> filteredList = new ArrayList<>(AllItemsList);

            if(constraint.toString().isEmpty()){
                filteredList.addAll(AllItemsList);
            }

            if(constraint.equals("price")) {
                filteredList.sort((o1, o2) -> Double.compare(Double.parseDouble(o2.getPrice()),Double.parseDouble(o1.getPrice())));
            }
            else if(constraint.equals("match")) {
                filteredList.sort((o1, o2) -> o2.getPercentage() - o1.getPercentage());
            }
            else if(constraint.equals("sale")) {
                filteredList.sort((o1, o2) -> Boolean.compare(o1.isOn_sale(),o2.isOn_sale()));
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
            MainModel mainModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(MainModel.class);

            ItemsList.clear();
            int count = 0;
            for(ShoppingItem item : (Collection<? extends ShoppingItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % 20 == 0) && count > 0) {
                    ItemsList.add((ShoppingItem) mainModel.getNextAd());
                }
                notifyDataSetChanged();
            }
        }
    };

    private static class itemPicsAdapter extends PagerAdapter {

        private ArrayList<String> imagesUrl;

        public itemPicsAdapter(ArrayList<String> images){
            this.imagesUrl = images;
        }

        @Override
        public int getCount() {
            return imagesUrl.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {

            LayoutInflater layoutInflater = (LayoutInflater) container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final int[] i = {position};
            assert layoutInflater != null;
            final View view = layoutInflater.inflate(R.layout.grid_images_item,container,false);
            final ImageView imageView = view.findViewById(R.id.image_item);

            Glide.with(container.getContext()).load(imagesUrl.get((i[0])%4)).into(imageView);
            container.addView(view);

            Button mNext = view.findViewById(R.id.next);
            Button mPrev = view.findViewById(R.id.previous);

            mNext.setOnClickListener(v -> {
                container.removeView(view);
                Glide.with(container.getContext()).load(imagesUrl.get((i[0] + 1)%4)).into(imageView);
                container.addView(view);
                i[0]++;
            });

            mPrev.setOnClickListener(v -> {
                container.removeView(view);
                Glide.with(container.getContext()).load(imagesUrl.get((i[0] + 3)%4)).into(imageView);
                container.addView(view);
                --i[0];
            });

            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((RelativeLayout)object);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
        }

    }
}
