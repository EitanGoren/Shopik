package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends ArrayAdapter<RecyclerItem> {

    final String user_id;
    private List<RecyclerItem> AllItemsList;
    private List<RecyclerItem> ItemsList;
    private List<RecyclerItem> items;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public SearchAdapter(@NonNull Context context, int resource, CopyOnWriteArrayList<RecyclerItem> items){
        super(context, resource,items);
        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.items = items;
        this.ItemsList = items;
        this.AllItemsList = new CopyOnWriteArrayList<>();
    }
    Filter filter = new Filter() {
        //runs in background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            Set<RecyclerItem> filteredList = new ArraySet<>();

            if(constraint.equals("")){
                filteredList.addAll(AllItemsList);
            }
            else {
                for(RecyclerItem item : AllItemsList){
                    if(item.getDescription() != null) {
                        StringBuilder description = new StringBuilder();
                        for(String word : item.getDescription()) {
                            description.append(word.toLowerCase().concat(" "));
                        }
                        description.append(item.getId().toLowerCase().concat(" ")).
                                append(item.getItem_sub_category().toLowerCase().concat(" ")).
                                append(item.getBrand().toLowerCase().concat(" ")).
                                append(item.getSeller().toLowerCase().concat(" "));

                        if(description.toString().contains(constraint.toString().toLowerCase())){
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
            if(results.values == null ) return;
            MainModel mainModel = new ViewModelProvider((ViewModelStoreOwner) getContext()).get(MainModel.class);

            ItemsList.clear();
            int count = 0;
            for(RecyclerItem item : (Collection<? extends RecyclerItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % Macros.SEARCH_TO_AD == 0) && count > 0) {
                    ShoppingItem shoppingItem = (ShoppingItem) mainModel.getNextAd();
                    RecyclerItem recyclerItem = new RecyclerItem(null,null);
                    recyclerItem.setAd(true);
                    recyclerItem.setNativeAd(shoppingItem.getNativeAd());
                    ItemsList.add(recyclerItem);
                }
                notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public RecyclerItem getItem(int position) {
        return ItemsList.get(position);
    }

    @Override
    public int getCount() {
        return ItemsList.size();
    }

    public void setAllItems(CopyOnWriteArrayList<RecyclerItem> allItems){
        for(RecyclerItem item : allItems) {
            if( item != null && !item.isAd() ) {
                this.AllItemsList.add(item);
            }
        }
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }
    public Filter getSortingFilter() {
        return sorting;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final RecyclerItem item = getItem(position);

        assert item != null;
        if ( item.isAd() && item.getNativeAd() != null) {

            item.setAd(true);
            UnifiedNativeAd temp_Ad = item.getNativeAd();

            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.favorites_ad_item, null);
            // Set the media view.
            adView.setMediaView(adView.findViewById(R.id.ad_media));

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_brand_name));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_action_button));
            adView.setIconView(adView.findViewById(R.id.ad_card_seller_logo));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_card_seller_name));

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(temp_Ad.getHeadline());
            adView.getMediaView().setMediaContent(temp_Ad.getMediaContent());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (temp_Ad.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(temp_Ad.getBody());
            }

            if (temp_Ad.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(temp_Ad.getCallToAction());
            }

            if (temp_Ad.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((CircleImageView) adView.getIconView()).setImageDrawable(temp_Ad.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (temp_Ad.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(temp_Ad.getPrice());
            }

            if (temp_Ad.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(temp_Ad.getStore());
            }

            if (temp_Ad.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(temp_Ad.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (temp_Ad.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                if(adView.getIconView().getVisibility() == View.GONE) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    adView.getAdvertiserView().setLayoutParams(params);
                    adView.getAdvertiserView().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                ((TextView) adView.getAdvertiserView()).setText(temp_Ad.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(temp_Ad);
            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.

            convertView = adView;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(params);

        }
        else if(!item.isAd()) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_item_list, parent, false);

            ItemsList = items;

            final ArrayList<String> images = item.getImages();

            TextView counter = convertView.findViewById(R.id.item_count);
            counter.setText((position + 1) + "/" + getCount());
            TextView sale = convertView.findViewById(R.id.discount);
            TextView price = convertView.findViewById(R.id.updated_price);
            TextView old_price = convertView.findViewById(R.id.old_price);
            TextView seller_name = convertView.findViewById(R.id.seller_name);

            String cur_price;
            if(item.getSeller().equals("ASOS"))
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice()) * Macros.POUND_TO_ILS) +
                        Currency.getInstance("ILS").getSymbol();
            else
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice())) +
                        Currency.getInstance("ILS").getSymbol();

            if (item.isOutlet() || item.isSale()) {
                int discount = (int) (100 - Math.ceil(100 * (Double.parseDouble(item.getReduced_price()) / Double.parseDouble(item.getPrice()))));

                if (item.isSale())
                    sale.setText("SALE" + System.lineSeparator() + "-" + discount + "%");
                else if (item.isOutlet())
                    sale.setText("OUTLET" + System.lineSeparator() + "-" + discount + "%");

                sale.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                sale.startAnimation(animation);

                String new_price;
                if(item.getSeller().equals("ASOS"))
                    new_price = new DecimalFormat("##.##").
                            format(Double.parseDouble(item.getReduced_price()) * Macros.POUND_TO_ILS) +
                            Currency.getInstance("ILS").getSymbol();
                else
                    new_price = new DecimalFormat("##.##").
                            format(Double.parseDouble(item.getReduced_price())) +
                            Currency.getInstance("ILS").getSymbol();

                old_price.setVisibility(View.VISIBLE);
                old_price.setText(cur_price);
                old_price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                old_price.setTextColor(Color.BLACK);

                price.setText(new_price);
                price.setTextColor(Color.RED);
            }
            else {
                sale.setVisibility(View.INVISIBLE);
                old_price.setVisibility(View.INVISIBLE);
                price.setText(cur_price);
                price.setTextColor(Color.BLACK);
            }

            final ImageView imageView = convertView.findViewById(R.id.image_item);
            final int[] i = {0};

            String image = "";
            for(String img : images){
                if(img != null && !img.equals("")) {
                    image = img;
                    break;
                }
            }
            Glide.with(getContext()).load(image).into(imageView);

            Button mNext = convertView.findViewById(R.id.next);
            Button mPrev = convertView.findViewById(R.id.previous);

            mNext.setOnClickListener(v -> {
                Animation fadeout = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
                Animation fadein = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
                imageView.startAnimation(fadeout);
                Glide.with(getContext()).load(images.get(((Math.abs(i[0]) + 1) % 4))).into(imageView);
                i[0]++;
                imageView.startAnimation(fadein);
            });
            mPrev.setOnClickListener(v -> {
                Animation fadeout = AnimationUtils.loadAnimation(getContext(), R.anim.fadeout);
                Animation fadein = AnimationUtils.loadAnimation(getContext(), R.anim.fadein);
                imageView.startAnimation(fadeout);
                Glide.with(getContext()).load((images.get((Math.abs(i[0]) + 1) % 4))).into(imageView);
                --i[0];
                imageView.startAnimation(fadein);
            });

            final ImageButton fullscreen = convertView.findViewById(R.id.fullscreen_button);
            item.setImages(images);
            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(), item));

            TextView buy = convertView.findViewById(R.id.list_item_buy_button);
            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getLink()));

            CircleImageView seller_logo = convertView.findViewById(R.id.seller_logo2);
            seller_logo.setOnClickListener(v -> Macros.Functions.sellerProfile(getContext(), item.getSeller_id()));
            seller_name.setText(item.getText());
            Glide.with(getContext()).load(item.getSellerLogoUrl()).into(seller_logo);
        }

        return convertView;
    }
    Filter sorting = new Filter() {
        //runs in background thread
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<RecyclerItem> filteredList = new ArrayList<>(AllItemsList);

            if(constraint.equals("price")) {
                filteredList.sort((o1, o2) -> Double.compare(Double.parseDouble(o2.getPrice()),Double.parseDouble(o1.getPrice())));
            }
            else if(constraint.equals("sale")) {
                filteredList.sort((o1, o2) -> Boolean.compare(o1.isSale(),o2.isSale()));
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
            for(RecyclerItem item : (Collection<? extends RecyclerItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % Macros.SEARCH_TO_AD == 0) && count > 0) {
                    ShoppingItem shoppingItem = (ShoppingItem) mainModel.getNextAd();
                    RecyclerItem recyclerItem = new RecyclerItem(null,null);
                    recyclerItem.setAd(true);
                    recyclerItem.setNativeAd(shoppingItem.getNativeAd());
                    ItemsList.add(recyclerItem);
                }
                notifyDataSetChanged();
            }
        }
    };
}