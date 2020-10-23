package com.eitan.shopik.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplicationActivity;
import com.eitan.shopik.customer.FullscreenImageActivity;
import com.eitan.shopik.items.ShoppingItem;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int TYPE_AD = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FAVORITES = 2;
    private static final int TYPE_FAVORITES_AD = 3;
    private static final int TYPE_NEW_ITEM = 4;

    private final List<ShoppingItem> AllItemsList;
    private List<ShoppingItem> ItemsList;
    final Filter sorting = new Filter() {
        //runs in background thread
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ShoppingItem> filteredList = new ArrayList<>(AllItemsList);

            if(constraint.toString().isEmpty()){
                filteredList.addAll(AllItemsList);
            }
            else if(constraint.equals("price")) {
                filteredList.sort((o1, o2) -> {

                    double price1 = o1.getReduced_price() != null ? Double.parseDouble(o1.getReduced_price()) :
                            Double.parseDouble(o1.getPrice());

                    double price2 = o2.getReduced_price() != null ? Double.parseDouble(o2.getReduced_price()) :
                            Double.parseDouble(o2.getPrice());

                    if(o1.getSeller().equals("ASOS")) {
                        price1 *= Macros.POUND_TO_ILS;
                    }
                    if(o2.getSeller().equals("ASOS")) {
                        price2 *= Macros.POUND_TO_ILS;
                    }

                    return (int) Math.ceil((price2 - price1));

                });
            }
            else if(constraint.equals("match")) {
                filteredList.sort((o1, o2) -> o2.getPercentage() - o1.getPercentage());
            }
            else if(constraint.equals("sale")) {
                filteredList.sort((o1, o2) -> Boolean.compare(o2.isOn_sale(),o1.isOn_sale()));
            }
            else if(constraint.equals("favorites")) {
                filteredList.sort((o1, o2) -> Boolean.compare(o2.isFavorite(), o1.isFavorite()));
            }
            else if(constraint.equals("company")) {
                filteredList.sort((o1, o2) -> o1.getSeller().compareTo(o2.getSeller()));
            }
            else if(constraint.equals("brand")) {
                filteredList.sort((o1, o2) -> o1.getBrand().compareTo(o2.getBrand()));
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();

            return filterResults;
        }

        //runs in UI thread
        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if( results.values == null ) return;
            ItemsList.clear();
            int count = 0;
            for(ShoppingItem item : (Collection<? extends ShoppingItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % Macros.ITEMS_TO_AD == 0) && count > 0) {
                    ItemsList.add((ShoppingItem) ShopikApplicationActivity.getNextAd());
                }
                notifyDataSetChanged();
            }
        }
    };
    final Filter filter = new Filter() {
        //runs in background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ShoppingItem> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(AllItemsList);
            }
            else if(Arrays.asList(Macros.CompanyNames).contains(constraint.toString())){
                for (ShoppingItem item : AllItemsList) {
                    if (item.getSeller() != null) {
                        if( item.getSeller().toLowerCase().contentEquals(constraint.toString().toLowerCase())) {
                            filteredList.add(item);
                        }
                    }
                }
            }
            else {
                for (ShoppingItem item : AllItemsList) {
                    if (item.getName() != null) {
                        StringBuilder description = new StringBuilder();
                        for (String word : item.getName()) {
                            description.append(word.toLowerCase()).append(" ");
                        }
                        description.append(item.getBrand().toLowerCase()).append(" ").append(item.getSeller().toLowerCase());
                        if( description.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
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
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.values == null ) return;

            ItemsList.clear();
            int count = 0;
            for(ShoppingItem item : (Collection<? extends ShoppingItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % Macros.ITEMS_TO_AD == 0) && count > 0) {
                    ItemsList.add((ShoppingItem) ShopikApplicationActivity.getNextAd());
                }
                notifyDataSetChanged();
            }
            notifyDataSetChanged();
        }
    };
    private final List<ShoppingItem> items;
    private final String type;
    private int prog = 0;
    private boolean isFinishedFetchingData = false;
    private TextView header;

    public RecyclerGridAdapter(CopyOnWriteArrayList<ShoppingItem> items, @Nullable String type) {
        this.items = items;
        this.ItemsList = items;
        this.AllItemsList = new CopyOnWriteArrayList<>();
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        if( type == null && items.get(position) != null ) {
            if (items.get(position).isAd())
                return TYPE_AD;
            else
                return TYPE_ITEM;
        }
        else {
            if(type != null && type.equals("favorites")){
                if (items.get(position).isAd())
                    return TYPE_FAVORITES_AD;
                else
                    return TYPE_FAVORITES;
            }
            else if(type != null && type.equals("outlets")){
                return TYPE_NEW_ITEM;
            }
            else
                return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == TYPE_AD) {
            UnifiedNativeAdView view = (UnifiedNativeAdView) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.native_grid_ad_template, parent,false);

            return new ADViewHolder(view);
        }
        else if(viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.grid_clean_item, parent,false);

            return new ItemViewHolder(view);
        }
        else if(viewType == TYPE_FAVORITES) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_new, parent,false);
            return new FavoritesViewHolder(view);
        }
        else if( viewType == TYPE_NEW_ITEM){
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.grid_clean_item, parent, false);

            return new ItemViewHolder(view);
        }
        else {
            UnifiedNativeAdView view = (UnifiedNativeAdView) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.favorites_ad_item, parent,false);

            return new ADViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_AD )
            ((ADViewHolder) holder).setAd(items.get(position));
        else if (getItemViewType(position) == TYPE_ITEM)
            ((ItemViewHolder) holder).setItem(items.get(position));
        else if (getItemViewType(position) == TYPE_FAVORITES)
            ((FavoritesViewHolder) holder).setItem(items.get(position));
        else if (getItemViewType(position) == TYPE_FAVORITES_AD)
            ((ADViewHolder) holder).setAd(items.get(position));
        else if (getItemViewType(position) == TYPE_NEW_ITEM)
            ((ItemViewHolder) holder).setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return ItemsList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    public Filter getSortingFilter() {
        return sorting;
    }

    public void setAllItems(List<ShoppingItem> allItems) {
        this.AllItemsList.clear();
        for(ShoppingItem item : allItems) {
            if( item != null && !item.isAd() ) {
                this.AllItemsList.add(item);
            }
        }
    }
    private void changeTabs(int position, ImageView[] imageViews, Context context) {
        switch (position) {
            case 0:
                imageViews[0].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));
                imageViews[1].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                break;
            case 1:
                imageViews[0].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                imageViews[1].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));
                imageViews[2].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                break;
            case 2:
                imageViews[1].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                imageViews[2].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));
                imageViews[3].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                break;
            case 3:
                imageViews[2].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                imageViews[3].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));
                break;
        }
    }

    public void setFinishedFetchingData(boolean finishedFetchingData) {
        isFinishedFetchingData = finishedFetchingData;
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {

        private ADViewHolder(@NonNull UnifiedNativeAdView itemView) {
            super(itemView);
            // Set the media view.
            itemView.setMediaView(itemView.findViewById(R.id.ad_media));
            itemView.getMediaView().setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            itemView.setHeadlineView(itemView.findViewById(R.id.ad_brand_name));
            itemView.setBodyView(itemView.findViewById(R.id.ad_body));
            itemView.setPriceView(itemView.findViewById(R.id.ad_price));
            itemView.setStarRatingView(itemView.findViewById(R.id.ad_stars));
            itemView.setStoreView(itemView.findViewById(R.id.ad_store));
            itemView.setIconView(itemView.findViewById(R.id.ad_logo));
            itemView.setCallToActionView(itemView.findViewById(R.id.ad_action_button));
            itemView.setAdvertiserView(itemView.findViewById(R.id.ad_card_seller_name));
        }

        public void setAd(ShoppingItem item) {

            if (item.getNativeAd() != null) {

                item.setAd(true);
                UnifiedNativeAd temp_ad = item.getNativeAd();

                // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
                ((TextView) ((UnifiedNativeAdView) itemView).getHeadlineView()).setText(temp_ad.getHeadline());
                ((UnifiedNativeAdView) itemView).getMediaView().setMediaContent(temp_ad.getMediaContent());

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                if (temp_ad.getBody() == null) {
                    ((UnifiedNativeAdView) itemView).getBodyView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView) itemView).getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView) itemView).getBodyView()).setText(temp_ad.getBody());
                }

                if (temp_ad.getCallToAction() == null) {
                    ((UnifiedNativeAdView) itemView).getCallToActionView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView) itemView).getCallToActionView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView) itemView).getCallToActionView()).setText(temp_ad.getCallToAction());
                }

                if (temp_ad.getIcon() == null) {
                    ((UnifiedNativeAdView) itemView).getIconView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView) itemView).getIconView().setVisibility(View.VISIBLE);
                    ((CircleImageView) ((UnifiedNativeAdView) itemView).
                            getIconView()).
                            setImageDrawable(temp_ad.getIcon().
                                    getDrawable());
                }

                if (temp_ad.getPrice() == null || temp_ad.getPrice().equals("")) {
                    ((UnifiedNativeAdView) itemView).getPriceView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView) itemView).getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView) itemView).getPriceView()).setText(temp_ad.getPrice());
                }

                if (temp_ad.getStore() == null) {
                    ((UnifiedNativeAdView) itemView).getStoreView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView) itemView).getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView) itemView).getStoreView()).setText(temp_ad.getStore());
                }

                if (temp_ad.getStarRating() == null) {
                    ((UnifiedNativeAdView) itemView).getStarRatingView().setVisibility(View.GONE);
                } else {
                    ((RatingBar) ((UnifiedNativeAdView) itemView).getStarRatingView())
                            .setRating(temp_ad.getStarRating().floatValue());
                    ((UnifiedNativeAdView) itemView).getStarRatingView().setVisibility(View.VISIBLE);
                }

                if (temp_ad.getAdvertiser() == null) {
                    ((UnifiedNativeAdView) itemView).getAdvertiserView().setVisibility(View.GONE);
                } else {
                    ((TextView) ((UnifiedNativeAdView) itemView).getAdvertiserView()).setText(temp_ad.getAdvertiser());
                    ((UnifiedNativeAdView) itemView).getAdvertiserView().setVisibility(View.VISIBLE);
                }

                // This method tells the Google Mobile Ads SDK that you have finished populating your
                // native ad view with this native ad.
                ((UnifiedNativeAdView) itemView).setNativeAd(temp_ad);
                // Get the video controller for the ad. One will always be provided, even if the ad doesn't
                // have a video asset.
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(params);
            }
        }
    }
    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final TextView brand_name;
        private final TextView buy;
        private final TextView sale;
        private final TextView sale_percentage;
        private final TextView price;
        private final TextView old_price;
        private final TextView description;
        private final TextView percentage;
        private final TextView percentage_header;
        private final TextView liked_text;
        private final CircleImageView logo;
        private final Button mPrev,mNext,fullscreen;
        private final ImageView imageView;
        private final ImageView mDot1;
        private final ImageView mDot2;
        private final ImageView mDot3;
        private final ImageView mDot4;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            brand_name = itemView.findViewById(R.id.brand_name);
            buy = itemView.findViewById(R.id.list_item_buy_button);
            price = itemView.findViewById(R.id.price);
            old_price = itemView.findViewById(R.id.old_price);
            sale = itemView.findViewById(R.id.sale);
            logo = itemView.findViewById(R.id.seller_logo);
            description = itemView.findViewById(R.id.description);
            percentage_header = itemView.findViewById(R.id.percentage_header);
            percentage = itemView.findViewById(R.id.percent);
            sale_percentage = itemView.findViewById(R.id.sale_percentage);
            liked_text = itemView.findViewById(R.id.liked_item);
            imageView = itemView.findViewById(R.id.image_item);
            mNext = itemView.findViewById(R.id.next);
            mPrev = itemView.findViewById(R.id.previous);
            fullscreen = itemView.findViewById(R.id.fullscreen);
            mDot1 = itemView.findViewById(R.id.fullscreen_dot_1);
            mDot2 = itemView.findViewById(R.id.fullscreen_dot_2);
            mDot3 = itemView.findViewById(R.id.fullscreen_dot_3);
            mDot4 = itemView.findViewById(R.id.fullscreen_dot_4);
        }

        public void setItem(final ShoppingItem item) {

            ItemsList = items;

            mDot1.setBackground(ContextCompat.
                    getDrawable(getContext(),R.drawable.ic_lens_black));
            mDot2.setBackground(ContextCompat.
                    getDrawable(getContext(),R.drawable.ic_panorama_fish_eye_black_24dp));
            mDot3.setBackground(ContextCompat.
                    getDrawable(getContext(),R.drawable.ic_panorama_fish_eye_black_24dp));
            mDot4.setBackground(ContextCompat.
                    getDrawable(getContext(),R.drawable.ic_panorama_fish_eye_black_24dp));

            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getSite_link()));

            StringBuilder item_description = new StringBuilder();
            for(String word: item.getName()) {
                item_description.append(word).append(" ");
            }
            description.setText(item_description);

            if(item.isFavorite()){
                String text = "Your Favorite";
                liked_text.setText(text);
                liked_text.setVisibility(View.VISIBLE);
            }
            else
                liked_text.setVisibility(View.GONE);

            Glide.with(getContext()).load(item.getSellerLogoUrl()).into(logo);

            String cur_price;
            try {
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice())) +
                        Currency.getInstance("ILS").getSymbol();
            } catch (NumberFormatException ex) {
                cur_price = "Unknown";
            }

            if (item.isOn_sale()) {
                String new_price;
                new_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getReduced_price())) +
                        Currency.getInstance("ILS").getSymbol();

                old_price.setVisibility(View.VISIBLE);
                old_price.setText(cur_price);
                old_price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                price.setText(new_price);
                price.setTextColor(Color.RED);
            }
            else {
                price.setTextColor(Color.BLACK);
                old_price.setVisibility(View.GONE);
                price.setText(cur_price);
            }

            if (item.getPercentage() > 0) {
                percentage.setVisibility(View.VISIBLE);
                percentage_header.setVisibility(View.VISIBLE);
                String match_txt = "Item & You: ";
                percentage_header.setText(match_txt);
                String text = item.getPercentage() + "%";
                percentage.setText(text);
            }
            else {
                percentage.setVisibility(View.GONE);
                percentage_header.setVisibility(View.GONE);
            }

            if(item.isOn_sale()){
                int discount = (int) ( 100 - Math.ceil( 100 * ( Double.parseDouble(item.getReduced_price())
                        / Double.parseDouble(item.getPrice()))));
                String text = "ON SALE";
                sale.setText(text);
                sale.setTextColor(Color.RED);
                sale.setVisibility(View.VISIBLE);

                String percent = " -" + discount + "%";
                sale_percentage.setText(percent);
                sale_percentage.setVisibility(View.VISIBLE);
                sale_percentage.setTextColor(Color.RED);
            } else {
                sale.setVisibility(View.GONE);
                sale_percentage.setVisibility(View.GONE);
            }

            logo.setOnClickListener(v ->
                    Macros.Functions.sellerProfile(getContext(), item.getSellerId(), null));

            String brand = item.getBrand() == null ? item.getSeller() : item.getBrand();
            String seller = item.getSeller();

            if (brand != null && brand.length() <= 45) {
                brand_name.setText(brand);
            } else
                brand_name.setText(seller);

            final int[] index = {0};

            Glide.with(getContext()).load(item.getImages().get(index[0])).
                    transition(withCrossFade(650)).
                    into(imageView);

            ImageView[] imageViews = {mDot1, mDot2, mDot3, mDot4};

            mNext.setOnClickListener(v -> {
                if (index[0] % 4 < 3) {
                    index[0]++;
                    Glide.with(getContext()).
                            load(item.getImages().get(index[0] % 4)).
                            transition(withCrossFade(500)).
                            into(imageView);
                    changeTabs(index[0] % 4, imageViews, itemView.getContext());
                }
            });
            mPrev.setOnClickListener(v -> {
                if(index[0]%4 > 0) {
                    index[0]--;
                    Glide.with(getContext()).
                            load(item.getImages().get(index[0] % 4)).
                            transition(withCrossFade(500)).
                            into(imageView);
                    changeTabs(index[0] % 4, imageViews, itemView.getContext());
                }
            });

            fullscreen.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("isFav", item.isFavorite());
                intent.putExtra("brand", item.getBrand());
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
                intent.putExtra("description", item_description.toString());
                intent.putExtra("seller", item.getSeller());

                Macros.Functions.fullscreen(getContext(), intent, null);
            });
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }

    public void setHeaderView(TextView headerView) {
        header = headerView;
    }

    private class FavoritesViewHolder extends RecyclerView.ViewHolder {

        final Button mNext;
        final Button mPrev;
        final ImageView imageView;
        final ImageView favorite;
        final String user_id;
        private final TextView brand_name;
        private final TextView buy;
        private final TextView sale;
        private final TextView price;
        private final TextView old_price;
        private final TextView seller_name;
        private final TextView likes;
        private final TextView unlikes;
        private final TextView description;
        private final Button fullscreen;
        private final ImageView mDot1;
        private final ImageView mDot2;
        private final ImageView mDot3;
        private final ImageView mDot4;
        private final CircleImageView logo;

        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);

            brand_name = itemView.findViewById(R.id.brand_name);
            favorite = itemView.findViewById(R.id.list_item_favourite_icon);
            mNext = itemView.findViewById(R.id.next);
            mPrev = itemView.findViewById(R.id.previous);
            imageView = itemView.findViewById(R.id.image_item);
            unlikes = itemView.findViewById(R.id.list_unlike);
            likes = itemView.findViewById(R.id.list_like);
            buy = itemView.findViewById(R.id.list_item_buy_button);
            fullscreen = itemView.findViewById(R.id.fullscreen_button);
            price = itemView.findViewById(R.id.updated_price);
            old_price = itemView.findViewById(R.id.old_price);
            seller_name = itemView.findViewById(R.id.seller_name);
            sale = itemView.findViewById(R.id.discount);
            logo = itemView.findViewById(R.id.seller_logo2);
            description = itemView.findViewById(R.id.description);
            user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            mDot1 = itemView.findViewById(R.id.fullscreen_dot_1);
            mDot2 = itemView.findViewById(R.id.fullscreen_dot_2);
            mDot3 = itemView.findViewById(R.id.fullscreen_dot_3);
            mDot4 = itemView.findViewById(R.id.fullscreen_dot_4);
        }

        public void setItem(final ShoppingItem item) {

            ItemsList = items;

            final int[] index = {0};

            mDot1.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_lens_black));
            mDot2.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_panorama_fish_eye_black_24dp));
            mDot3.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_panorama_fish_eye_black_24dp));
            mDot4.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.ic_panorama_fish_eye_black_24dp));

            likes.setText(String.valueOf(item.getLikes()));
            unlikes.setText(String.valueOf(item.getUnlikes()));

            likes.setOnClickListener(v -> {
                if (item.getLikedUsers() != null) {
                    Macros.Functions.showLikesListDialog(getContext(), item.getLikedUsers());
                }
                else {
                    Toast.makeText(getContext(), "No Likes yet :)", Toast.LENGTH_SHORT).show();
                }
            });
            unlikes.setOnClickListener(v -> {
                if (item.getUnlikedUsers() != null) {
                    Macros.Functions.showUnlikesListDialog(getContext(), item.getUnlikedUsers());
                }
                else {
                    Toast.makeText(getContext(), "No Unlikes yet :)", Toast.LENGTH_SHORT).show();
                }
            });
            unlikes.setOnLongClickListener(v -> {
                if (isFinishedFetchingData)
                    setAlertDialog(item);
                else
                    Toast.makeText(getContext(),
                            "Please wait for all items to be updated",
                            Toast.LENGTH_LONG).show();
                return true;
            });

            String image = "";
            for(String img : item.getImages()){
                if(img != null && !img.equals("")) {
                    image = img;
                    break;
                }
            }
            Macros.Functions.GlidePicture(getContext(), image, imageView);

            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getSite_link()));
            Glide.with(getContext()).load(item.getSellerLogoUrl()).into(logo);

            mNext.setOnClickListener(v -> {
                if (index[0] >= 0 && index[0] < 3) {
                    index[0]++;
                    changeImage(item.getImages().get(index[0]), index[0]);
                }
            });
            mPrev.setOnClickListener(v -> {
                if(index[0] > 0 && index[0] <= 3) {
                    index[0]--;
                    changeImage(item.getImages().get(index[0]), index[0]);
                }
            });

            StringBuilder desc = new StringBuilder();
            for(String word : item.getName()){
                desc.append(word).append(" ");
            }

            fullscreen.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("isFav", item.isFavorite());
                intent.putExtra("brand", item.getBrand());
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
                intent.putExtra("description", desc.toString());
                intent.putExtra("seller", item.getSeller());

                ArrayList<Pair<View,String>> pairs = new ArrayList<>();
                pairs.add(Pair.create(logo,"company_logo"));
                pairs.add(Pair.create(brand_name,"company_name"));

                Macros.Functions.fullscreen(getContext(), intent, pairs);
            });

            if (item.isFavorite()) {
                favorite.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                favorite.startAnimation(animation);
            }
            else {
                favorite.setVisibility(View.INVISIBLE);
            }

            String cur_price;
            if(item.getSeller().equals("ASOS"))
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice()) * Macros.POUND_TO_ILS) +
                        Currency.getInstance("ILS").getSymbol();
            else
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice())) +
                        Currency.getInstance("ILS").getSymbol();

            if (item.isOn_sale()) {
                int discount = (int) (100 - Math.ceil(100 *
                        (Double.parseDouble(item.getReduced_price()) / Double.parseDouble(item.getPrice()))));

                String text = "SALE" + " -" + discount + "%";
                sale.setText(text);

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
                sale.setVisibility(View.GONE);
                old_price.setVisibility(View.INVISIBLE);
                price.setText(cur_price);
                price.setTextColor(Color.BLACK);
            }

            seller_name.setText(item.getSeller());

            ArrayList<Pair<View,String>> pairs = new ArrayList<>();
            pairs.add(Pair.create(logo,"company_logo"));
            pairs.add(Pair.create(brand_name,"company_name"));

            logo.setOnClickListener(v -> Macros.Functions.
                    sellerProfile(getContext(), item.getSellerId(), pairs));

            if (item.getBrand() != null && item.getBrand().length() <= 45)
                brand_name.setText(item.getBrand());
            else
                brand_name.setText(item.getSeller());

            description.setText(desc);
        }

        public Context getContext() {
            return itemView.getContext();
        }

        private void changeImage(String image, int idx) {
            ImageView[] imageViews = {mDot1, mDot2, mDot3, mDot4};
            Context context = getContext();
            changeTabs(idx, imageViews, itemView.getContext());
            Glide.with(context).
                    load(image).
                    transition(withCrossFade(900)).
                    into(imageView);
        }

        private void updateItemsDB(String item_id, String item_type, String item_gender) {

            Map<String, Object> unliked = new HashMap<>();
            unliked.put(user_id, null);

            // remove user_id from liked list
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.ITEMS).
                    child(item_gender).
                    child(item_type).
                    child(item_id).
                    child(Macros.Items.LIKED).
                    updateChildren(unliked);

            unliked.clear();
            unliked.put(user_id, Macros.Items.UNLIKED);

            // add user_id to unliked list
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.ITEMS).
                    child(item_gender).
                    child(item_type).
                    child(item_id).
                    child(Macros.Items.UNLIKED).
                    updateChildren(unliked);
        }

        private void updateCustomerDB(String item_id,String item_type, String item_gender,
                                      String item_sub_category,String seller) {

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(user_id).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(seller).
                    child(item_type).
                    child(item_sub_category).
                    child(item_id).removeValue();

            Map<String,Object> map = new HashMap<>();
            map.put(item_id,Macros.Items.UNLIKED);
            // add to unliked
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(user_id).
                    child(item_gender).
                    child(Macros.Items.UNLIKED).
                    child(seller).
                    child(item_type).
                    child(item_sub_category).
                    updateChildren(map);
        }

        private void removeItem() {
            int position = getLayoutPosition();
            try {
                AllItemsList.remove(position == AllItemsList.size() ? position - 1 : position);
                ItemsList.remove(position == ItemsList.size() ? position - 1 : position);
                notifyDataSetChanged();
                updateHeader();
            } catch (NullPointerException | IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong ):"
                        + System.lineSeparator()
                        + "Please try again later", Toast.LENGTH_SHORT).show();
            }
        }

        private void updateHeader() {
            String text;
            if (ItemsList.size() > 0) {
                String cat = ItemsList.get(0).getType();
                String sub_cat = ItemsList.get(0).getSub_category();
                text = cat.toUpperCase() + " | " + sub_cat.toUpperCase() + " | "
                        + ItemsList.size() + " ITEMS";
            } else
                text = "NO FAVORITES FOUND";

            if (header != null)
                header.setText(text);
        }

        private void setAlertDialog(ShoppingItem item) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(R.layout.alert_dialog);

            final AlertDialog alertDialog = builder.create();
            alertDialog.show();

            alertDialog.findViewById(R.id.dismiss_btn).setOnClickListener(v -> alertDialog.dismiss());
            alertDialog.findViewById(R.id.remove_btn).setOnClickListener(v -> {

                alertDialog.findViewById(R.id.text).setVisibility(View.GONE);
                alertDialog.findViewById(R.id.sub_text).setVisibility(View.GONE);
                alertDialog.findViewById(R.id.dismiss_btn).setVisibility(View.GONE);
                alertDialog.findViewById(R.id.remove_btn).setVisibility(View.GONE);

                ProgressBar progressBar = alertDialog.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                String item_id = item.getId();
                String item_type = item.getType();
                String item_gender = item.getGender();

                updateCustomerDB(item_id, item_type, item_gender, item.getSub_category(), item.getSeller());
                updateItemsDB(item_id, item_type, item_gender);

                removeItem();

                ImageView checkIcon = alertDialog.findViewById(R.id.check_sign);
                TextView textView = alertDialog.findViewById(R.id.item_removed);

                recursiveCirculate(progressBar, checkIcon, textView);

                prog = 0;
                YoYo.with(Techniques.RollIn).delay(850).duration(3400).
                        onEnd(animator -> alertDialog.dismiss()).
                        playOn(checkIcon);
            });

            Objects.requireNonNull(alertDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.CENTER);
        }

        private void recursiveCirculate(ProgressBar progressBar, ImageView checkIcon, TextView textView) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    prog += 1;
                    progressBar.setProgress(prog);
                    if (prog < 100)
                        handler.postDelayed(this, 8);
                    else if (prog == 100) {
                        checkIcon.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
            }, 8);
        }
    }
}
