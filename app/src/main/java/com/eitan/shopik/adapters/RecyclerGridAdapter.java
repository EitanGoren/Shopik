package com.eitan.shopik.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.eitan.shopik.adapters.RecyclerGridAdapter.AdType.TYPE_AD;
import static com.eitan.shopik.database.Database.FAVORITE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Macros;
import com.eitan.shopik.PublicUser;
import com.eitan.shopik.R;
import com.eitan.shopik.ads.AdMobNativeAd;
import com.eitan.shopik.customer.FullscreenImageActivity;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.viewHolders.ShopikViewHolder;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerGridAdapter extends RecyclerView.Adapter<ShopikViewHolder>
        implements Filterable {

    public enum AdType{
        TYPE_AD,
        TYPE_ITEM,
        TYPE_FAVORITES,
        TYPE_FAVORITES_AD,
        TYPE_NEW_ITEM,
    }

    private final ArrayList<ShoppingItem> AllItemsList;
    private final ArrayList<ShoppingItem> ItemsList;

    private static class FavAdCardViewHolder extends ShopikViewHolder {

        public FavAdCardViewHolder(@NonNull NativeAdView itemView) {
            super(itemView);
            // Set the media view.
            itemView.setMediaView(itemView.findViewById(R.id.ad_media));
            Objects.requireNonNull(itemView.getMediaView()).setImageScaleType(ImageView.ScaleType.CENTER_CROP);
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
//            if (item.getNativeAd() != null) {
//
//                item.setAd(true);
//                NativeAd temp_ad = item.getNativeAd();
//
//                // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
//                ((TextView) ((NativeAdView) itemView).getHeadlineView()).setText(temp_ad.getHeadline());
//                Objects.requireNonNull(((NativeAdView) itemView).getMediaView()).setMediaContent(Objects.requireNonNull(temp_ad.getMediaContent()));
//
//                // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
//                // check before trying to display them.
//                if (temp_ad.getBody() == null) {
//                    ((NativeAdView) itemView).getBodyView().setVisibility(View.GONE);
//                } else {
//                    ((NativeAdView) itemView).getBodyView().setVisibility(View.VISIBLE);
//                    ((TextView) ((NativeAdView) itemView).getBodyView()).setText(temp_ad.getBody());
//                }
//
//                if (temp_ad.getCallToAction() == null) {
//                    ((NativeAdView) itemView).getCallToActionView().setVisibility(View.GONE);
//                } else {
//                    ((NativeAdView) itemView).getCallToActionView().setVisibility(View.VISIBLE);
//                    ((TextView) ((NativeAdView) itemView).getCallToActionView()).setText(temp_ad.getCallToAction());
//                }
//
//                if (temp_ad.getIcon() == null) {
//                    ((NativeAdView) itemView).getIconView().setVisibility(View.GONE);
//                } else {
//                    ((NativeAdView) itemView).getIconView().setVisibility(View.VISIBLE);
//                    ((CircleImageView) ((NativeAdView) itemView).
//                            getIconView()).
//                            setImageDrawable(temp_ad.getIcon().
//                                    getDrawable());
//                }
//
//                if (temp_ad.getPrice() == null || temp_ad.getPrice().equals("")) {
//                    ((NativeAdView) itemView).getPriceView().setVisibility(View.GONE);
//                } else {
//                    ((NativeAdView) itemView).getPriceView().setVisibility(View.VISIBLE);
//                    ((TextView) ((NativeAdView) itemView).getPriceView()).setText(temp_ad.getPrice());
//                }
//
//                if (temp_ad.getStore() == null) {
//                    ((NativeAdView) itemView).getStoreView().setVisibility(View.GONE);
//                } else {
//                    ((NativeAdView) itemView).getStoreView().setVisibility(View.VISIBLE);
//                    ((TextView) ((NativeAdView) itemView).getStoreView()).setText(temp_ad.getStore());
//                }
//
//                if (temp_ad.getStarRating() == null) {
//                    ((NativeAdView) itemView).getStarRatingView().setVisibility(View.GONE);
//                } else {
//                    ((RatingBar) ((NativeAdView) itemView).getStarRatingView())
//                            .setRating(temp_ad.getStarRating().floatValue());
//                    ((NativeAdView) itemView).getStarRatingView().setVisibility(View.VISIBLE);
//                }
//
//                if (temp_ad.getAdvertiser() == null) {
//                    ((NativeAdView) itemView).getAdvertiserView().setVisibility(View.GONE);
//                } else {
//                    ((TextView) ((NativeAdView) itemView).getAdvertiserView()).setText(temp_ad.getAdvertiser());
//                    ((NativeAdView) itemView).getAdvertiserView().setVisibility(View.VISIBLE);
//                }
//
//                // This method tells the Google Mobile Ads SDK that you have finished populating your
//                // native ad view with this native ad.
//                ((NativeAdView) itemView).setNativeAd(temp_ad);
//                // Get the video controller for the ad. One will always be provided, even if the ad doesn't
//                // have a video asset.
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                itemView.setLayoutParams(params);
//            }
        }
    }
    private static class GridCardViewHolder extends ShopikViewHolder {

        private final TextView brand_name;
        private final TextView buy;
        private final TextView sale;
        private final TextView price;
        private final TextView old_price;
        private final TextView description;
        private final TextView percentage;
        private final TextView percentage_header;
        private final TextView liked_text;
        private final CircleImageView logo;
        private final Button mPrev,mNext,fullscreen;
        private final ImageView imageView;
        private final LinearLayout mDots;
        private final ShimmerFrameLayout mShimmerImage;
        private final RequestListener<Drawable> listener;

        private ShoppingItem item;

        public GridCardViewHolder(@NonNull View itemView) {
            super(itemView);
            brand_name = itemView.findViewById(R.id.shimmer_brand);
            buy = itemView.findViewById(R.id.list_item_buy_button);
            price = itemView.findViewById(R.id.price);
            old_price = itemView.findViewById(R.id.old_price);
            sale = itemView.findViewById(R.id.sale);
            logo = itemView.findViewById(R.id.seller_logo);
            description = itemView.findViewById(R.id.description);
            percentage_header = itemView.findViewById(R.id.percentage_header);
            percentage = itemView.findViewById(R.id.percent);
            liked_text = itemView.findViewById(R.id.liked_item);
            imageView = itemView.findViewById(R.id.image_item);
            mNext = itemView.findViewById(R.id.next);
            mPrev = itemView.findViewById(R.id.previous);
            fullscreen = itemView.findViewById(R.id.fullscreen);
            mDots = itemView.findViewById(R.id.dots_layouts);
            mShimmerImage = itemView.findViewById(R.id.shimmer_image);
            listener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mShimmerImage.hideShimmer();
                    return false;
                }
            };
        }
        public void setItem(final ShoppingItem item) {
            this.item = item;

            SetDescription();
            SetFavoriteSign();

            Glide.with(getContext()).load(item.getSellerLogoUrl()).apply(new RequestOptions().
                            override(Target.SIZE_ORIGINAL)).
                            transition(withCrossFade(0)).
                            listener(listener).into(logo);

            Macros.Functions.getRemasteredPrice(getContext(), item, price, old_price, sale, "#FF57D8");

            SetMatchPercentage();
            SetBrandName();
            TabsScrollingHandler(getContext(), item.getConvertedImage(), mNext, mPrev, mDots, imageView, mShimmerImage);

            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getSite_link()));
            logo.setOnClickListener(v ->
                    Macros.Functions.sellerProfile(getContext(), item.getSellerId(), null));
            fullscreen.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("isFav", item.isFavorite());
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("images", item.getConvertedImage());
                bundle.putStringArrayList("description", item.getConvertedName());
                bundle.putString("seller", item.getSeller());
                bundle.putString("brand", item.getBrand());
                bundle.putString("price", item.getPrice());
                bundle.putString("old_price", item.getReducedPrice());
                intent.putExtra("package", bundle);
                Macros.Functions.fullscreen(getContext(), intent, null);
            });
        }
        public Context getContext() {
            return itemView.getContext();
        }

        private void SetDescription() {
            StringBuilder item_description = new StringBuilder();
            for(String word: item.getConvertedName()) {
                item_description.append(word).append(" ");
            }
            description.setText(item_description);
        }
        private void SetFavoriteSign() {
            if(item.isFavorite()){
                String text = "Your Favorite";
                liked_text.setText(text);
                liked_text.setVisibility(View.VISIBLE);
            }
            else liked_text.setVisibility(View.GONE);
        }
        private void SetBrandName() {
            String brand = item.getBrand() == null ? item.getSeller() : item.getBrand();
            if (brand != null && brand.length() <= 45) {
                brand_name.setText(brand);
            }
            else {
                String _brand = Objects.requireNonNull(brand).substring(0,40) + "...";
                brand_name.setText(_brand);
            }
        }
        private void SetMatchPercentage() {
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
        }
    }
    private static class FavCardViewHolder extends ShopikViewHolder {
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
        private final TextView likes_num;
        private final TextView likes_txt;
        private final CircleImageView img1;
        private final CircleImageView img2;
        private final MaterialButton unlikes;
        private final MaterialButton share;
        private final MaterialButton info;
        private final MaterialButton comment;
        private final LinearLayout info_layout;
        private final RelativeLayout likes_layout;
        private final TextView description;
        private final CircleImageView logo;
        private final LinearLayout mDots;
        private final Handler handler;
        private final RequestListener<Drawable> listener;
        private final ShimmerFrameLayout mShimmerImage;
        private final ArrayList<Pair<View,String>> pairs;
        private ShoppingItem item;

        public FavCardViewHolder(@NonNull View itemView) {
            super(itemView);

            brand_name = itemView.findViewById(R.id.brand_name);
            favorite = itemView.findViewById(R.id.list_item_favourite_icon);
            mNext = itemView.findViewById(R.id.next);
            mPrev = itemView.findViewById(R.id.previous);
            imageView = itemView.findViewById(R.id.image_item);
            buy = itemView.findViewById(R.id.list_item_buy_button);
            price = itemView.findViewById(R.id.updated_price);
            old_price = itemView.findViewById(R.id.old_price);
            seller_name = itemView.findViewById(R.id.seller_name);
            sale = itemView.findViewById(R.id.discount);
            logo = itemView.findViewById(R.id.seller_logo2);
            description = itemView.findViewById(R.id.description);
            user_id = Objects.requireNonNull(ShopikUser.getInstance().getId());
            mDots = itemView.findViewById(R.id.dots_layouts2);
            mShimmerImage = itemView.findViewById(R.id.shimmer_image);
            unlikes = itemView.findViewById(R.id.unlike);
            share = itemView.findViewById(R.id.share);
            likes_num = itemView.findViewById(R.id.likes_num);
            likes_txt = itemView.findViewById(R.id.likes_txt);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            likes_layout = itemView.findViewById(R.id.likes_layout);
            info = itemView.findViewById(R.id.info);
            comment = itemView.findViewById(R.id.comment);
            info_layout = itemView.findViewById(R.id.info1);
            AdMobNativeAd adMobNativeAd = AdMobNativeAd.getInstance();
            adMobNativeAd.initialize(itemView.getContext());

            handler = new Handler();
            pairs = new ArrayList<>();

            listener = new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    mShimmerImage.hideShimmer();
                    return false;
                }
            };

            comment.setOnClickListener(view -> {

            });
            info.setOnClickListener(view -> info_layout.setVisibility(View.VISIBLE));
            info_layout.setOnClickListener(view -> info_layout.setVisibility(View.GONE));
        }

        public void setItem(final ShoppingItem item) {
            this.item = item;
            pairs.clear();
            pairs.add(Pair.create(logo,"company_logo"));
            pairs.add(Pair.create(brand_name,"company_name"));

            SetUserInteraction();

            String image = "";
            for(String img : item.getConvertedImage()){
                if(img != null && !img.equals("")) {
                    image = img;
                    break;
                }
            }

            if(image.equals("")) return;

            mShimmerImage.startShimmer();
            Glide.with(itemView.getContext()).
                    load(image).
                    apply(new RequestOptions().
                            override(Target.SIZE_ORIGINAL).
                            format(DecodeFormat.PREFER_ARGB_8888)
                    ).transition(withCrossFade(650)).
                    listener(listener).
                    into(imageView);

            SetDescription();

            SetFavoriteSign();

            Macros.Functions.GlidePicture(itemView.getContext(), image, imageView, 350);
            Macros.Functions.GlidePicture(itemView.getContext(), item.getSellerLogoUrl(), logo,0);

            Macros.Functions.getRemasteredPrice(itemView.getContext(), item, price, old_price, sale, "#FF57D8"); // pink

            SetBrandName();

            TabsScrollingHandler(itemView.getContext(), item.getConvertedImage(), mNext, mPrev, mDots, imageView, mShimmerImage);

            buy.setOnClickListener(v -> Macros.Functions.buy(itemView.getContext(), item.getSite_link()));

            logo.setOnClickListener(v -> Macros.Functions.sellerProfile(itemView.getContext(), item.getSellerId(), null));

            if(item.getLikes() > 0) {
                likes_txt.setVisibility(View.VISIBLE);
                String[] liked = item.getConvertedLikedUsersIds().toArray(new String[0]);
                List<PublicUser> first = item.getConvertedInteractedUsers().stream().
                        filter(publicUser -> publicUser.getId().equals(liked[0])).collect(Collectors.toList());

                likes_num.setText(String.valueOf(item.getLikes()));
                Macros.Functions.GlidePicture(itemView.getContext(), first.get(0).getProfile_image(), img1, 350);
                img1.setVisibility(View.VISIBLE);
                if (item.getLikes() > 1){
                    List<PublicUser> second = item.getConvertedInteractedUsers().stream().
                            filter(publicUser -> publicUser.getId().equals(liked[1])).collect(Collectors.toList());
                    Macros.Functions.GlidePicture(itemView.getContext(), second.get(0).getProfile_image(), img2, 350);
                    img2.setVisibility(View.VISIBLE);
                }
            }

            seller_name.setText(item.getSeller());
        }
        private void SetUserInteraction() {
            likes_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Set<PublicUser> liked = item.getConvertedInteractedUsers().stream().filter(publicUser ->
                                    item.getConvertedLikedUsersIds().contains(publicUser.getId())).collect(Collectors.toSet());
                    Macros.Functions.showInteractionsDialog(itemView.getContext(), liked, false);
                }
            });
            //unlikes.setText(String.valueOf(item.getUnlikes()));
            unlikes.setOnClickListener(v -> {
                if (item.getConvertedUnlikedUsersIds() != null) {
                    Set<PublicUser> unliked = item.getConvertedInteractedUsers().stream().filter(publicUser ->
                                    item.getConvertedUnlikedUsersIds().contains(publicUser.getId())).
                            collect(Collectors.toSet());
                    Macros.Functions.showInteractionsDialog(itemView.getContext(), unliked, false);
                }
                else Toast.makeText(itemView.getContext(), "No Unlikes yet :)", Toast.LENGTH_SHORT).show();
            });
            unlikes.setOnLongClickListener(v -> {
                if (isFinishedFetchingData) setAlertDialog();
                else Toast.makeText(itemView.getContext(), "Please wait for all items to be updated", Toast.LENGTH_LONG).show();
                return true;
            });
            share.setOnClickListener(view -> {
                // TODO: SHARE THIS ITEM WITH OTHER APPS
            });
        }
        private void setAlertDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
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
                String company = item.getSeller();

                //   SHOULD MOVE TO DATABASE MODULE
                Database.getInstance().updateItemsDB(item_id,item_type,item_gender,company);
                removeItem();

                ImageView checkIcon = alertDialog.findViewById(R.id.check_sign);
                TextView textView = alertDialog.findViewById(R.id.item_removed);

                recursiveCirculate(progressBar, checkIcon, textView);

                mProgress = 0;
                YoYo.with(Techniques.RollIn).delay(550).duration(3000).
                        onEnd(animator -> alertDialog.dismiss()).
                        playOn(checkIcon);
            });

            Objects.requireNonNull(alertDialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.getWindow().setGravity(Gravity.CENTER);
        }
        private void removeItem() {

        }
        private void recursiveCirculate(ProgressBar progressBar, ImageView checkIcon, TextView textView) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgress += 1;
                    progressBar.setProgress(mProgress);
                    if (mProgress < 100)
                        handler.postDelayed(this, 5);
                    else if (mProgress == 100) {
                        checkIcon.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                    }
                }
            }, 5);
        }
        private void SetDescription() {
            StringBuilder item_description = new StringBuilder();
            for(String word: item.getConvertedName()) {
                item_description.append(word).append(" ");
            }
            description.setText(item_description);
        }
        private void SetFavoriteSign() {
            if(item.isFavorite()){
                favorite.setVisibility(View.VISIBLE);
            }
            else favorite.setVisibility(View.GONE);
        }
        private void SetBrandName() {
            String brand = item.getBrand() == null ? item.getSeller() : item.getBrand();
            if (brand != null && brand.length() <= 45) {
                brand_name.setText(brand);
            }
            else {
                String _brand = Objects.requireNonNull(brand).substring(0,40) + "...";
                brand_name.setText(_brand);
            }
        }
    }

    final Filter sorting = new Filter() {
        private int sortByPrice(ShoppingItem o1, ShoppingItem o2){
            double price1 = o1.getReducedPrice() != null ? Double.parseDouble(o1.getReducedPrice().replaceAll(",", "")) :
                    Double.parseDouble(o1.getPrice().replaceAll(",", ""));
            double price2 = o2.getReducedPrice() != null ? Double.parseDouble(o2.getReducedPrice().replaceAll(",", "")) :
                    Double.parseDouble(o2.getPrice().replaceAll(",", ""));
            return (int) Math.ceil((price2 - price1));
        }
        //runs in background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ShoppingItem> filteredList = new ArrayList<>(AllItemsList);
            try {
                if (constraint.equals("price")) {
                    filteredList.sort(this::sortByPrice);
                } else if (constraint.equals("match")) {
                    filteredList.sort((o1, o2) -> o2.getPercentage() - o1.getPercentage());
                } else if (constraint.equals("sale")) {
                    filteredList.sort((o1, o2) -> Boolean.compare(o2.isOn_sale(), o1.isOn_sale()));
                } else if (constraint.equals("favorites")) {
                    filteredList.sort((o1, o2) -> Boolean.compare(o2.isFavorite(), o1.isFavorite()));
                } else if (constraint.equals("company")) {
                    filteredList.sort(Comparator.comparing(ShoppingItem::getSeller));
                } else if (constraint.equals("brand")) {
                    filteredList.sort(Comparator.comparing(ShoppingItem::getBrand));
                }
            }
            catch (NumberFormatException nfe){
                System.out.println(nfe.getMessage());
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();

            return filterResults;
        }

        //runs in UI thread
        @SuppressLint("NotifyDataSetChanged")
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
                   // ItemsList.add((ShoppingItem) ShopikApplicationActivity.getNextAd());
                }
                notifyItemInserted(ItemsList.indexOf(item));
            }
            notifyDataSetChanged();
        }
    };
    final Filter filter = new Filter() {
        //runs in background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ShoppingItem> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty() || constraint.equals("clear")){
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
                    if (item.getConvertedName() != null) {
                        StringBuilder description = new StringBuilder();
                        for (String word : item.getConvertedName()) {
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
        @SuppressLint("NotifyDataSetChanged")
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
                    //ItemsList.add((ShoppingItem) ShopikApplicationActivity.getNextAd());
                }
                notifyDataSetChanged();
            }
            notifyDataSetChanged();
        }
    };
    final Filter sub_cat_filter = new Filter() {
        //runs in background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ShoppingItem> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(AllItemsList);
            }
            else {
                for (ShoppingItem item : AllItemsList) {
                    if (item.getConvertedName() != null) {
                        StringBuilder description = new StringBuilder();
                        for (String word : item.getConvertedName()) {
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
        @SuppressLint("NotifyDataSetChanged")
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
                   // ItemsList.add((ShoppingItem) ShopikApplicationActivity.getNextAd());
                }
                notifyDataSetChanged();
            }
            notifyDataSetChanged();
        }
    };

    private final ArrayList<ShoppingItem> items;
    private final String type;
    private boolean isFinishedFetchingData = false;
    private TextView header;

    public RecyclerGridAdapter(Set<ShoppingItem> items, @Nullable String type) {
        this.items = new ArrayList<>(items);
        this.ItemsList = this.items;
        this.AllItemsList = new ArrayList<>();
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        if( type == null && items.get(position) != null ) {
            return AdType.TYPE_ITEM.ordinal();
        }
        else {
            if(type != null && type.equals(FAVORITE)){
                return AdType.TYPE_FAVORITES.ordinal();
            }
            else if(type != null && type.equals("outlets")){
                return AdType.TYPE_NEW_ITEM.ordinal();
            }
            else
                return AdType.TYPE_ITEM.ordinal();
        }
    }

    @NonNull
    @Override
    public ShopikViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ShopikViewHolder selected;

        if(viewType == TYPE_AD.ordinal()) {
            NativeAdView view = (NativeAdView) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.native_grid_ad_template, parent, false);
            selected = new FavAdCardViewHolder(view);
        }
        else if(viewType == AdType.TYPE_ITEM.ordinal() || viewType == AdType.TYPE_NEW_ITEM.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.grid_clean_item, parent, false);
            selected = new GridCardViewHolder(view);
        }
        else if(viewType == AdType.TYPE_FAVORITES.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.list_item_new, parent, false);
            selected = new FavCardViewHolder(view);
        }
        else {
            NativeAdView view = (NativeAdView) LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.favorites_ad_item, parent, false);
            selected = new FavAdCardViewHolder(view);
        }

        return selected;
    }

    @Override
    public void onBindViewHolder(@NonNull ShopikViewHolder holder, int position) {
        if(items.get(position) != null && !items.get(position).getConvertedImage().isEmpty()){
            if (getItemViewType(position) == TYPE_AD.ordinal() || getItemViewType(position) == AdType.TYPE_FAVORITES_AD.ordinal())
                ((FavAdCardViewHolder) holder).setAd(items.get(position));
            else if (getItemViewType(position) == AdType.TYPE_NEW_ITEM.ordinal() || getItemViewType(position) == AdType.TYPE_ITEM.ordinal())
                ((GridCardViewHolder) holder).setItem(items.get(position));
            else if (getItemViewType(position) == AdType.TYPE_FAVORITES.ordinal())
                ((FavCardViewHolder) holder).setItem(items.get(position));
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ShopikViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.setFinishedFetchingData(isFinishedFetchingData);
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
    public Filter getSubCatFilter() {
        return sub_cat_filter;
    }

    public void setHeaderView(TextView headerView) {
        header = headerView;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAllItems(Set<ShoppingItem> allItems) {
        this.AllItemsList.clear();
        this.AllItemsList.addAll(allItems);
    }
    public void setFinishedFetchingData(boolean finishedFetchingData) {
        isFinishedFetchingData = finishedFetchingData;
    }
}
