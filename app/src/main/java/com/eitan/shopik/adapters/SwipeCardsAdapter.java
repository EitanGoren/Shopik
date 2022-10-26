package com.eitan.shopik.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import com.eitan.shopik.customer.FullscreenImageActivity;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.viewHolders.ShopikViewHolder;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class SwipeCardsAdapter extends ArrayAdapter<ShoppingItem> {

    private SwipeFlingAdapterView mSwipeFlingAdapterView;
    private SwipeCardViewHolder holder;

    private static class SwipeCardViewHolder extends ShopikViewHolder {
        public MaterialButton favorite;
        public ImageView imageView;
        public TextView sale;
        public TextView price;
        public TextView old_price;
        public TextView brand_name;
        public TextView comp_name;
        public MaterialButton likes;
        public MaterialButton unlikes;
        public TextView description;
        public ShimmerFrameLayout shimmerFrameLayout;

        private final Context context;
        public ArrayList<Pair<View,String>> pairs;
        public boolean[] isFavorite = new boolean[1];

        private SwipeFlingAdapterView flingCardContainer;

        public SwipeCardViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            pairs = new ArrayList<>();
        }

        public void renderNewView(ShoppingItem shoppingItem) {
            SetToolbar(shoppingItem);
            SetDescription(shoppingItem);
            SetUserInteraction(shoppingItem);
            Macros.Functions.getRemasteredPrice(context, shoppingItem, price, old_price, sale, "#FFFFFF");
            SetBrandName(shoppingItem);
            favorite.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.like_swing);
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.ding);
                favorite.startAnimation(animation);
                isFavorite[0] = !isFavorite[0];
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mp.start();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        flingCardContainer.getTopCardListener().selectRight();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            });
            if (!shoppingItem.getConvertedImage().isEmpty())
                Glide.with(context).load(shoppingItem.getConvertedImage().get(0)).apply(new RequestOptions().
                                override(Target.SIZE_ORIGINAL).
                                format(DecodeFormat.PREFER_ARGB_8888)).
                        transition(withCrossFade(350)).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                shimmerFrameLayout.hideShimmer();
                                return false;
                            }
                        }).into(imageView);
            comp_name.setText(shoppingItem.getSeller());
        }
        public void setFlingCardContainer(SwipeFlingAdapterView flingCardContainer) {
            this.flingCardContainer = flingCardContainer;
        }
        public boolean getIsFavorite() {
            return isFavorite[0];
        }
        private void SetUserInteraction(ShoppingItem item) {
            likes.setText(String.valueOf(item.getLikes()));
            unlikes.setText(String.valueOf(item.getUnlikes()));
            likes.setOnLongClickListener(view -> {
                if (!item.getConvertedLikedUsersIds().isEmpty()) {
                    Set<PublicUser> liked = item.getConvertedInteractedUsers().stream().filter(publicUser ->
                                    item.getConvertedLikedUsersIds().contains(publicUser.getId())).
                            collect(Collectors.toSet());

                    Macros.Functions.showInteractionsDialog(itemView.getContext(), liked, true);
                }
                else Toast.makeText(itemView.getContext(), "No Likes yet :)", Toast.LENGTH_SHORT).show();
                return true;
            });
            unlikes.setOnLongClickListener(v -> {
                if (!item.getConvertedUnlikedUsersIds().isEmpty()) {
                    Set<PublicUser> unliked = item.getConvertedInteractedUsers().stream().filter(publicUser ->
                                    item.getConvertedUnlikedUsersIds().contains(publicUser.getId())).
                            collect(Collectors.toSet());

                    Macros.Functions.showInteractionsDialog(itemView.getContext(), unliked, false);
                }
                else Toast.makeText(itemView.getContext(), "No Unlikes yet :)", Toast.LENGTH_SHORT).show();
                return false;
            });
            likes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.zoomin);
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.fblike1);

                likes.startAnimation(animation);
                likes.setText(String.valueOf(item.getLikes() + 1));

                mp.start();
                mp.setOnCompletionListener(mp1 -> flingCardContainer.getTopCardListener().selectRight());

            });
            unlikes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(context,R.anim.zoomout);
                final MediaPlayer mp = MediaPlayer.create(context, R.raw.fblike2);

                unlikes.startAnimation(animation);
                unlikes.setText(String.valueOf(item.getUnlikes() + 1));

                mp.start();
                mp.setOnCompletionListener(mp1 -> flingCardContainer.getTopCardListener().selectLeft());

            });
            YoYo.with(Techniques.Shake).duration(1000).playOn(likes);
        }
        private void SetToolbar(ShoppingItem shoppingItem) {
            Toolbar toolbar = itemView.findViewById(R.id.customer_toolbar);
            Toolbar.OnMenuItemClickListener topNavListener = item -> {
                switch (item.getItemId()) {
                    case R.id.card_shop:
                        Macros.Functions.buy(context,shoppingItem.getSite_link());
                        break;
                    case R.id.card_seller:
                        Macros.Functions.sellerProfile(context, shoppingItem.getSellerId(),null);
                        break;
                }
                return true;
            };
            toolbar.setOnMenuItemClickListener(topNavListener);
            Objects.requireNonNull(toolbar.getOverflowIcon()).
                    setTint(ContextCompat.getColor(context, R.color.colorPrimary));
            toolbar.setSoundEffectsEnabled(true);
        }
        private void SetBrandName(ShoppingItem item) {
            String brand = item.getBrand() == null ? item.getSeller() : item.getBrand();
            if (brand != null && brand.length() <= 45) {
                brand_name.setText(brand);
            }
            else {
                String _brand = Objects.requireNonNull(brand).substring(0,40) + "...";
                brand_name.setText(_brand);
            }
        }
        private void SetDescription(ShoppingItem item) {
            StringBuilder item_description = new StringBuilder();
            for(String word: item.getConvertedName()) {
                item_description.append(word).append(" ");
            }
            description.setText(item_description);
        }
    }
    private static class SwipeAdCardViewHolder extends ShopikViewHolder{
        public SwipeAdCardViewHolder(@NonNull View itemView) {
            super(itemView);
        }
//            NativeAd nativeAd = mAdMobNativeAd.getNativeAd();
//            if(nativeAd != null) {
//                NativeAdView adView = (NativeAdView) LayoutInflater.
//                        from(getContext()).inflate(R.layout.native_card_ad_template, parent, false);
//
//                // Set the media view.
//                adView.setMediaView(adView.findViewById(R.id.ad_media));
//
//                // Set other ad assets.
//                adView.setHeadlineView(adView.findViewById(R.id.ad_brand_name));
//                adView.setBodyView(adView.findViewById(R.id.ad_body));
//                adView.setCallToActionView(adView.findViewById(R.id.ad_action_button));
//                adView.setIconView(adView.findViewById(R.id.ad_card_seller_logo));
//                adView.setPriceView(adView.findViewById(R.id.ad_price));
//                adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
//                adView.setStoreView(adView.findViewById(R.id.ad_store));
//                adView.setAdvertiserView(adView.findViewById(R.id.ad_card_seller_name));
//
//                // The headline and mediaContent are guaranteed to be in every NativeAd.
//                ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
//                Objects.requireNonNull(adView.getMediaView()).setMediaContent(Objects.requireNonNull(nativeAd.getMediaContent()));
//
//                // These assets aren't guaranteed to be in every NativeAd, so it's important to
//                // check before trying to display them.
//                if (nativeAd.getBody() == null) {
//                    adView.getBodyView().setVisibility(View.GONE);
//                } else {
//                    adView.getBodyView().setVisibility(View.VISIBLE);
//                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
//                }
//
//                if (nativeAd.getCallToAction() == null) {
//                    adView.getCallToActionView().setVisibility(View.GONE);
//                } else {
//                    adView.getCallToActionView().setVisibility(View.VISIBLE);
//                    ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
//                }
//
//                if (nativeAd.getIcon() == null) {
//                    adView.getIconView().setVisibility(View.GONE);
//                } else {
//                    ((CircleImageView) adView.getIconView()).setImageDrawable(
//                            nativeAd.getIcon().getDrawable());
//                    adView.getIconView().setVisibility(View.VISIBLE);
//                }
//
//                if (nativeAd.getPrice() == null) {
//                    adView.getPriceView().setVisibility(View.GONE);
//                } else {
//                    adView.getPriceView().setVisibility(View.VISIBLE);
//                    ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
//                }
//
//                if (nativeAd.getStore() == null) {
//                    adView.getStoreView().setVisibility(View.GONE);
//                } else {
//                    adView.getStoreView().setVisibility(View.VISIBLE);
//                    ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
//                }
//
//                if (nativeAd.getStarRating() == null) {
//                    adView.getStarRatingView().setVisibility(View.GONE);
//                } else {
//                    ((RatingBar) adView.getStarRatingView())
//                            .setRating(nativeAd.getStarRating().floatValue());
//                    adView.getStarRatingView().setVisibility(View.VISIBLE);
//                }
//
//                if (nativeAd.getAdvertiser() == null) {
//                    adView.getAdvertiserView().setVisibility(View.GONE);
//                } else {
//                    ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
//                    adView.getAdvertiserView().setVisibility(View.VISIBLE);
//                }
//
//                // This method tells the Google Mobile Ads SDK that you have finished populating your
//                // native ad view with this native ad.
//                adView.setNativeAd(nativeAd);
//
//                convertView = adView;
//                final float scale = getContext().getResources().getDisplayMetrics().density;
//                int pixels = (int) (570 * scale + 0.5f);
//                FrameLayout.LayoutParams params = new FrameLayout.
//                        LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, pixels, Gravity.CENTER
//                );
//                convertView.setLayoutParams(params);
//            }
//        }
    }

    public SwipeCardsAdapter(Context context, int resourceId, Set<ShoppingItem> items){
        super(context,resourceId, new ArrayList(items));
    }

    @Override
    public int getPosition(@Nullable ShoppingItem shoppingItem) {
        return super.getPosition(shoppingItem);
    }

    @NonNull
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_item, parent,false);
            holder = new SwipeCardViewHolder(convertView, getContext());
            holder.setFlingCardContainer(mSwipeFlingAdapterView);

            holder.shimmerFrameLayout = convertView.findViewById(R.id.shimmer_view_container);
            holder.comp_name = convertView.findViewById(R.id.company_name);
            holder.brand_name = convertView.findViewById(R.id.brand_name);
            holder.favorite = convertView.findViewById(R.id.swipe_favorite_button);
            holder.imageView = convertView.findViewById(R.id.swipe_image);
            holder.description = convertView.findViewById(R.id.swipe_description);
            holder.likes = convertView.findViewById(R.id.like);
            holder.old_price = convertView.findViewById(R.id.old_price);
            holder.price = convertView.findViewById(R.id.updated_price);
            holder.sale = convertView.findViewById(R.id.swipe_discount);
            holder.unlikes = convertView.findViewById(R.id.unlike);

            convertView.setTag(holder);
        }
        else
            holder = (SwipeCardViewHolder) convertView.getTag();

        holder.isFavorite[0] = false;
        holder.pairs.clear();
        holder.pairs.add(Pair.create(holder.brand_name,"company_name"));

        final ShoppingItem shoppingItem = getItem(position);
        holder.shimmerFrameLayout.startShimmer();
        holder.renderNewView(shoppingItem);
        return convertView;
    }

    public void setFlingContainer(SwipeFlingAdapterView swipeFlingAdapterView){
        mSwipeFlingAdapterView = swipeFlingAdapterView;
    }

    public boolean isFavorite(){
        return holder.getIsFavorite();
    }
}

