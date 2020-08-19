package com.eitan.shopik.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Pair;
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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final int TYPE_AD = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FAVORITES = 2;
    private static final int TYPE_FAVORITES_AD = 3;

    private MainModel mainModel;
    private List<ShoppingItem> AllItemsList;
    private List<ShoppingItem> ItemsList;
    Filter sorting = new Filter() {
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
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if( results.values == null ) return;
            ItemsList.clear();
            int count = 0;
            for(ShoppingItem item : (Collection<? extends ShoppingItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % Macros.SUGGESTED_TO_AD == 0) && count > 0) {
                    ItemsList.add((ShoppingItem) mainModel.getNextAd());
                }
                notifyDataSetChanged();
            }
        }
    };
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
                    if (item.getName() != null) {
                        for (String word : item.getName()) {
                            if(word.compareToIgnoreCase(constraint.toString()) == 0) {
                                filteredList.add(item);
                            }
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

            ItemsList.clear();
            int count = 0;
            for(ShoppingItem item : (Collection<? extends ShoppingItem>) results.values) {
                ItemsList.add(item);
                count++;
                if ((count % Macros.SUGGESTED_TO_AD == 0) && count > 0) {
                    ItemsList.add((ShoppingItem) mainModel.getNextAd());
                }
                notifyDataSetChanged();
            }
            notifyDataSetChanged();
        }
    };
    private List<ShoppingItem> items;
    private String type;
    private RecyclerView recyclerView;

    public RecyclerGridAdapter(CopyOnWriteArrayList<ShoppingItem> items, @Nullable String type) {
        this.items = items;
        this.ItemsList = items;
        this.AllItemsList = new CopyOnWriteArrayList<>();
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        if(type == null) {
            if (items.get(position).isAd())
                return TYPE_AD;
            else
                return TYPE_ITEM;
        }
        else if(type.equals("favorites")){
            if (items.get(position).isAd())
                return TYPE_FAVORITES_AD;
            else
                return TYPE_FAVORITES;
        }
        else
            return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        mainModel = new ViewModelProvider((ViewModelStoreOwner)parent.getContext()).get(MainModel.class);
        recyclerView = parent.findViewById(R.id.list_recycler_view);

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
    }

    @Override
    public int getItemCount() {
        return ItemsList.size();
    }

    public void setAllItems(List<ShoppingItem> allItems) {
        for(ShoppingItem item : allItems) {
            if( item != null && !item.isAd() ) {
                this.AllItemsList.add(item);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public Filter getSortingFilter() {
        return sorting;
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {

        private TextView seller_name;

        private ADViewHolder(@NonNull UnifiedNativeAdView itemView) {
            super(itemView);

            // Set the media view.
            itemView.setMediaView(itemView.findViewById(R.id.ad_media));
            itemView.setHeadlineView(itemView.findViewById(R.id.ad_brand_name));

            // Set other ad assets.
            itemView.setAdChoicesView(itemView.findViewById(R.id.ad_choice));
          //  itemView.setAdChoicesView(itemView.findViewById(R.id.ad_image));
            itemView.setBodyView(itemView.findViewById(R.id.ad_body));
            itemView.setPriceView(itemView.findViewById(R.id.ad_price));
            itemView.setStarRatingView(itemView.findViewById(R.id.ad_stars));
            itemView.setStoreView(itemView.findViewById(R.id.ad_store));
            itemView.setIconView(itemView.findViewById(R.id.ad_logo));
            itemView.setCallToActionView(itemView.findViewById(R.id.ad_action_button));
            seller_name = itemView.findViewById(R.id.ad_card_seller_name);
            itemView.setAdvertiserView(seller_name);
        }

        public Context getContext() {return itemView.getContext();}

        public void setAd(ShoppingItem item) {

            if (item.getNativeAd() != null){

                item.setAd(true);
                UnifiedNativeAd temp_ad = item.getNativeAd();

                // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
                ((TextView) ((UnifiedNativeAdView)itemView).getHeadlineView()).setText(temp_ad.getHeadline());
                ((UnifiedNativeAdView)itemView).getMediaView().setMediaContent(temp_ad.getMediaContent());

                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
                // check before trying to display them.
                if (temp_ad.getBody() == null) {
                    ((UnifiedNativeAdView)itemView).getBodyView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView)itemView).getBodyView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView)itemView).getBodyView()).setText(temp_ad.getBody());
                }

                if (temp_ad.getCallToAction() == null) {
                    ((UnifiedNativeAdView)itemView).getCallToActionView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView)itemView).getCallToActionView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView)itemView).getCallToActionView()).setText(temp_ad.getCallToAction());
                }

                if (temp_ad.getIcon() == null) {
                    ((UnifiedNativeAdView)itemView).getIconView().setVisibility(View.GONE);
                }
                else {
                    ((UnifiedNativeAdView)itemView).getIconView().setVisibility(View.VISIBLE);
                    ((CircleImageView)((UnifiedNativeAdView)itemView).
                            getIconView()).
                            setImageDrawable(temp_ad.getIcon().
                            getDrawable());
                }

                if (temp_ad.getPrice() == null || temp_ad.getPrice().equals("")) {
                    ((UnifiedNativeAdView)itemView).getPriceView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView)itemView).getPriceView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView)itemView).getPriceView()).setText(temp_ad.getPrice());
                }

                if (temp_ad.getStore() == null) {
                    ((UnifiedNativeAdView)itemView).getStoreView().setVisibility(View.GONE);
                } else {
                    ((UnifiedNativeAdView)itemView).getStoreView().setVisibility(View.VISIBLE);
                    ((TextView) ((UnifiedNativeAdView)itemView).getStoreView()).setText(temp_ad.getStore());
                }

                if (temp_ad.getStarRating() == null) {
                    ((UnifiedNativeAdView)itemView).getStarRatingView().setVisibility(View.GONE);
                } else {
                    ((RatingBar) ((UnifiedNativeAdView)itemView).getStarRatingView())
                            .setRating(temp_ad.getStarRating().floatValue());
                    ((UnifiedNativeAdView)itemView).getStarRatingView().setVisibility(View.VISIBLE);
                }

                if (temp_ad.getAdvertiser() == null) {
                    ((UnifiedNativeAdView)itemView).getAdvertiserView().setVisibility(View.GONE);
                }
                else {

                    if(temp_ad.getAdvertiser().length() > 15){
                        seller_name.setTextSize(12);
                    }
                    else
                        seller_name.setTextSize(14);

                    ((TextView) ((UnifiedNativeAdView)itemView).getAdvertiserView()).setText(temp_ad.getAdvertiser());
                    ((UnifiedNativeAdView)itemView).getAdvertiserView().setVisibility(View.VISIBLE);
                }

                // This method tells the Google Mobile Ads SDK that you have finished populating your
                // native ad view with this native ad.
                ((UnifiedNativeAdView)itemView).setNativeAd(temp_ad);
                // Get the video controller for the ad. One will always be provided, even if the ad doesn't
                // have a video asset.
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                itemView.setLayoutParams(params);
            }
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView brand_name,buy,sale,sale_percentge,
                price,old_price,description,percentage,
                percentage_header,seller_name,liked_text;
        private ViewPager viewPager;
        private Button fullscreen;
        private CircleImageView logo;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            brand_name = itemView.findViewById(R.id.brand_name);
            buy = itemView.findViewById(R.id.list_item_buy_button);
            fullscreen = itemView.findViewById(R.id.fullscreen_button);
            price = itemView.findViewById(R.id.price);
            old_price = itemView.findViewById(R.id.old_price);
            seller_name = itemView.findViewById(R.id.seller_name);
            sale = itemView.findViewById(R.id.sale);
            logo = itemView.findViewById(R.id.seller_logo);
            description = itemView.findViewById(R.id.description);
            percentage_header = itemView.findViewById(R.id.percentage_header);
            percentage = itemView.findViewById(R.id.percent);
            viewPager = itemView.findViewById(R.id.image_viewPager);
            sale_percentge = itemView.findViewById(R.id.sale_percentage);
            liked_text = itemView.findViewById(R.id.liked_item);
        }

        public void setItem(final ShoppingItem item) {

            ItemsList = items;

            itemPicsAdapter arrayAdapter = new itemPicsAdapter(item.getImages());

            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getSite_link()));

            StringBuilder item_description = new StringBuilder();
            for(String word: item.getName()) {
                item_description.append(word).append(" ");
            }

            description.setText(item_description);

            if(item.isFavorite()){
                liked_text.setText("Your Favorite");
                liked_text.setVisibility(View.VISIBLE);
            }
            else
                liked_text.setVisibility(View.GONE);

            Macros.Functions.GlidePicture(getContext(), item.getSellerLogoUrl(), logo);
            seller_name.setText(item.getSeller());

            String cur_price;
            if (item.getSeller().equals("ASOS")) {
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice()) * Macros.POUND_TO_ILS) +
                        Currency.getInstance("ILS").getSymbol();
            }
            else {
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(item.getPrice())) +
                        Currency.getInstance("ILS").getSymbol();
            }

            if (item.isOutlet() || item.isOn_sale()) {
                String new_price;
                if (item.getSeller().equals("ASOS"))
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

                price.setText(new_price);
                price.setTextColor(Color.RED);
            }
            else {
                price.setTextColor(Color.BLACK);
                old_price.setVisibility(View.INVISIBLE);
                price.setText(cur_price);
            }

            if (item.getPercentage() > 0) {
                percentage.setVisibility(View.VISIBLE);
                percentage_header.setVisibility(View.VISIBLE);
                String match_txt = "Item & You: ";
                percentage_header.setText(match_txt);
                percentage.setText(item.getPercentage() + "%");
            }
            else {
                percentage.setVisibility(View.GONE);
                percentage_header.setVisibility(View.GONE);
            }

            brand_name.setOnClickListener(v -> Macros.Functions.sellerProfile(getContext(), item.getSellerId(),null));

            if(item.isOn_sale()){

                int discount = (int) ( 100 - Math.ceil( 100 * ( Double.parseDouble(item.getReduced_price())
                        / Double.parseDouble(item.getPrice()))));
                String text = "ON SALE";
                sale.setText(text);
                sale.setTextColor(Color.RED);
                sale.setVisibility(View.VISIBLE);

                String percent =  " -" + discount + "%";
                sale_percentge.setText(percent);
                sale_percentge.setVisibility(View.VISIBLE);
                sale_percentge.setTextColor(Color.RED);
            }
            else {
                sale.setVisibility(View.GONE);
                sale_percentge.setVisibility(View.GONE);
            }
            String brand = item.getBrand();
            String seller = item.getSeller();

            if (brand != null)
                brand_name.setText(brand);
            else
                brand_name.setText(seller);

            viewPager.setAdapter(arrayAdapter);

            Pair<View, String> pair = new Pair<>(viewPager,"fullscreen");
            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(), item, pair));
        }

        public Context getContext() { return itemView.getContext(); }

        private class itemPicsAdapter extends PagerAdapter {

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
                View view = layoutInflater.inflate(R.layout.grid_images_item,container,false);
                ImageView imageView = view.findViewById(R.id.image_item);

                Macros.Functions.GlidePicture(container.getContext(),imagesUrl.get((i[0])%4), imageView);
                container.addView(view);

                Button mNext = view.findViewById(R.id.next);
                Button mPrev = view.findViewById(R.id.previous);

                mNext.setOnClickListener(v -> {
                    container.removeView(view);
                    Macros.Functions.GlidePicture(container.getContext(),imagesUrl.get((i[0] + 1)%4), imageView);
                    container.addView(view);
                    i[0]++;
                });
                mPrev.setOnClickListener(v -> {
                    container.removeView(view);
                    Macros.Functions.GlidePicture(container.getContext(),imagesUrl.get(Math.abs(i[0] + 3)%4), imageView);
                    container.addView(view);
                    --i[0];
                });

                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((RelativeLayout)object);
            }
        }
    }

    private class FavoritesViewHolder extends RecyclerView.ViewHolder {

        Button mNext,mPrev;
        ImageView imageView,favorite;
        String user_id ;
        private TextView brand_name;
        private TextView buy;
        private TextView sale;
        private TextView price;
        private TextView old_price;
        private TextView seller_name;
        private TextView likes;
        private TextView unlikes;
        private TextView description;
        private Button fullscreen;
        private ImageView mDot1,mDot2,mDot3,mDot4;
        private CircleImageView logo;

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

            mDot1.setBackground(getContext().getDrawable(R.drawable.ic_lens_black_24dp));
            mDot2.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
            mDot3.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
            mDot4.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));

            likes.setText(String.valueOf(item.getLikes()));
            unlikes.setText(String.valueOf(item.getUnlikes()));

            likes.setOnLongClickListener(v -> {
                if (item.getLikedUsers() != null) {
                    showLikesListDialog(item.getLikedUsers());
                }
                else {
                    Toast.makeText(getContext(), "No Likes yet :)", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            unlikes.setOnLongClickListener(v -> {
                if (item.getUnlikedUsers() != null) {
                    showUnlikesListDialog(item.getUnlikedUsers());
                }
                else {
                    Toast.makeText(getContext(), "No Unlikes yet :)", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            unlikes.setOnClickListener(v -> {
                String item_id = item.getId();
                String item_type = item.getType();
                String item_gender = item.getGender();
                updateCustomerDB(item_id,item_type,item_gender,item.getSub_category());
                updateItemsDB(item_id,item_type,item_gender);
                // remove(item);
                Macros.Functions.showSnackbar(recyclerView, "Removed Successfully", Objects.requireNonNull(getContext()),R.drawable.ic_thumb_down_pink);
            });

            String image = "";
            for(String img : item.getImages()){
                if(img != null && !img.equals("")) {
                    image = img;
                    break;
                }
            }
            Macros.Functions.GlidePicture(getContext(),image,imageView);

            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getSite_link()));

            Macros.Functions.GlidePicture(getContext(), item.getSellerLogoUrl(), logo);

            Animation fadein = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);

            mNext.setOnClickListener(v -> {
                if(index[0] >=0 && index[0] < 3){
                    index[0]++;
                    changeTabs(index[0]);
                    Macros.Functions.GlidePicture(getContext(), item.getImages().get(index[0]), imageView);
                    imageView.startAnimation(fadein);
                }
            });
            mPrev.setOnClickListener(v -> {
                if(index[0] > 0 && index[0] <= 3) {
                    index[0]--;
                    changeTabs(index[0]);
                    Macros.Functions.GlidePicture(getContext(), item.getImages().get(index[0]), imageView);
                    imageView.startAnimation(fadein);
                }
            });

            Pair<View, String> pair = new Pair<>(imageView,"fullscreen");
            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(), item, pair));

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

            if (item.isOutlet() || item.isOn_sale()) {
                int discount = (int) (100 - Math.ceil(100 * (Double.parseDouble(item.getReduced_price()) / Double.parseDouble(item.getPrice()))));

                if (item.isOn_sale())
                    sale.setText("SALE" + " -" + discount + "%");
                else if (item.isOutlet())
                    sale.setText("OUTLET"  + " -" + discount + "%");

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
            seller_name.setOnClickListener(v -> Macros.Functions.
                    sellerProfile(getContext(),item.getSellerId(),Pair.create(logo,"company_logo")));
            logo.setOnClickListener(v -> Macros.Functions.
                    sellerProfile(getContext(),item.getSellerId(),Pair.create(logo,"company_logo")));

            brand_name.setText(item.getBrand());
            StringBuilder desc = new StringBuilder();
            for(String word : item.getName()){
                desc.append(word).append(" ");
            }
            description.setText(desc);
        }

        public Context getContext() { return itemView.getContext(); }

        private void changeTabs(int position) {
            switch (position){
                case 0:
                    mDot1.setBackground(getContext().getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot2.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
                    break;
                case 1:
                    mDot1.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
                    mDot2.setBackground(getContext().getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot3.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
                    break;
                case 2:
                    mDot2.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
                    mDot3.setBackground(getContext().getDrawable(R.drawable.ic_lens_black_24dp));
                    mDot4.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
                    break;
                case 3:
                    mDot3.setBackground(getContext().getDrawable(R.drawable.ic_panorama_fish_eye_black_24dp));
                    mDot4.setBackground(getContext().getDrawable(R.drawable.ic_lens_black_24dp));
                    break;
            }
        }

        private void updateItemsDB(String item_id,String item_type, String item_gender) {

            Map<String,Object> unliked = new HashMap<>();
            unliked.put(user_id,null);

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

        private void updateCustomerDB(String item_id,String item_type, String item_gender, String item_sub_category) {
            Map<String,Object> map = new HashMap<>();
            map.put(item_id, null);
            // remove from liked
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(user_id).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(item_type).
                    child(item_sub_category).
                    updateChildren(map);

            map.clear();
            map.put(item_id,Macros.Items.UNLIKED);
            // add to unliked
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(user_id).
                    child(item_gender).
                    child(Macros.Items.UNLIKED).
                    child(item_type).
                    child(item_sub_category).
                    updateChildren(map);
        }

        private void showLikesListDialog(List<LikedUser> liked_items){

            Dialog dialog = new Dialog(getContext());
            LikesListAdapter likedListAdapter = new LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, liked_items);
            likedListAdapter.notifyDataSetChanged();

            dialog.setContentView(R.layout.likes_list_dialog);
            TextView header = dialog.findViewById(R.id.likes_header);
            header.setText( Macros.Items.LIKED + " this item");

            ListView listView = dialog.findViewById(R.id.likes_list);

            header.setCompoundDrawablesRelativeWithIntrinsicBounds(dialog.getContext().getDrawable(R.drawable.ic_thumb_up_seleste),null,null,null);
            header.setCompoundDrawablePadding(20);

            listView.setAdapter(likedListAdapter);

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        private void showUnlikesListDialog(List<LikedUser> unliked_items){

            Dialog dialog = new Dialog(getContext());
            LikesListAdapter unlikedListAdapter = new LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, unliked_items);
            unlikedListAdapter.notifyDataSetChanged();

            dialog.setContentView(R.layout.likes_list_dialog);
            TextView header = dialog.findViewById(R.id.likes_header);
            header.setText( Macros.Items.UNLIKED + " this item");

            ListView listView = dialog.findViewById(R.id.likes_list);

            header.setCompoundDrawablesRelativeWithIntrinsicBounds(dialog.getContext().getDrawable(R.drawable.ic_thumb_down_pink),null,null,null);
            header.setCompoundDrawablePadding(20);

            listView.setAdapter(unlikedListAdapter);

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}