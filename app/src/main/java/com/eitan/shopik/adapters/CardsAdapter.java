package com.eitan.shopik.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.customer.FullscreenImageActivity;
import com.eitan.shopik.items.ShoppingItem;
import com.facebook.CallbackManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardsAdapter extends ArrayAdapter<ShoppingItem> {

    private final boolean[] isFavorite = new boolean[1];
    private SwipeFlingAdapterView flingCardContainer;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    public int getPosition(@Nullable ShoppingItem shoppingItem) {
        return super.getPosition(shoppingItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CardsAdapter(Context context, int resourceId, List<ShoppingItem> items){
        super(context,resourceId,items);
    }

    public void setFlingContainer(SwipeFlingAdapterView swipeFlingAdapterView){
        this.flingCardContainer = swipeFlingAdapterView;
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {

        final ShoppingItem shoppingItem = getItem(position);

        assert shoppingItem != null;
        if(shoppingItem.isAd() && shoppingItem.getNativeAd() != null) {
            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.
                    from(getContext()).inflate(R.layout.native_card_ad_template, parent,false);
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

            UnifiedNativeAd nativeAd = shoppingItem.getNativeAd();

            // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.GONE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.GONE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((CircleImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.GONE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.GONE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.GONE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd);

            convertView = adView;
            final float scale = getContext().getResources().getDisplayMetrics().density;
            int pixels = (int) (570 * scale + 0.5f);
            FrameLayout.LayoutParams params = new FrameLayout.
                    LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, pixels, Gravity.CENTER
            );
            convertView.setLayoutParams(params);
        }
        else if(!shoppingItem.isAd()) {

            isFavorite[0] = false;
            callbackManager = CallbackManager.Factory.create();
            shareDialog = new ShareDialog((Activity) getContext());

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_item, parent,false);

            ImageView favorite = convertView.findViewById(R.id.swipe_favorite_button);

            ImageView imageView = convertView.findViewById(R.id.swipe_image);
            ImageButton fullscreen = convertView.findViewById(R.id.fullscreen_button);
            TextView sale = convertView.findViewById(R.id.swipe_discount);
            TextView price = convertView.findViewById(R.id.updated_price);
            TextView old_price = convertView.findViewById(R.id.old_price);
            CircleImageView seller_logo = convertView.findViewById(R.id.card_seller_logo);
            TextView exclusive = convertView.findViewById(R.id.exclusive);
            TextView brand_name = convertView.findViewById(R.id.brand_name);

            androidx.appcompat.widget.Toolbar toolbar = convertView.findViewById(R.id.customer_toolbar);
            androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener = item -> {
                switch (item.getItemId()) {
                    case R.id.card_shop:
                        Macros.Functions.buy(getContext(),shoppingItem.getSite_link());
                        break;
                    case R.id.card_seller:
                        Macros.Functions.sellerProfile(getContext(), shoppingItem.getSellerId(),null);
                        break;
                }
                return true;
            };
            toolbar.setOnMenuItemClickListener(topNavListener);
            Objects.requireNonNull(toolbar.getOverflowIcon()).
                    setTint(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            toolbar.setSoundEffectsEnabled(true);

            TextView likes = convertView.findViewById(R.id.like);

            StringBuilder description = new StringBuilder();
            for(String word : shoppingItem.getName()){
                description.append(word);
            }

            YoYo.with(Techniques.Shake).duration(1000).playOn(likes);

            TextView unlikes = convertView.findViewById(R.id.unlike);
            likes.setText(String.valueOf(shoppingItem.getLikes()));
            unlikes.setText(String.valueOf(shoppingItem.getUnlikes()));

            likes.setOnLongClickListener(v -> {
                if (shoppingItem.getLikedUsers() != null)
                    showLikesListDialog(shoppingItem.getLikedUsers());
                else
                    Toast.makeText(getContext(), "No Likes yet :)", Toast.LENGTH_SHORT).show();
                return true;
            });
            unlikes.setOnLongClickListener(v -> {
                if (shoppingItem.getUnlikedUsers() != null)
                    showUnlikesListDialog(shoppingItem.getUnlikedUsers());
                else
                    Toast.makeText(getContext(), "No Unlikes yet :)", Toast.LENGTH_SHORT).show();
                return true;
            });

            likes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.zoomin);
                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fblike1);

                likes.startAnimation(animation);
                likes.setText(String.valueOf(shoppingItem.getLikes() + 1));

                mp.start();
                mp.setOnCompletionListener(mp1 -> flingCardContainer.getTopCardListener().selectRight());

            });
            unlikes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.zoomout);
                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fblike2);

                unlikes.startAnimation(animation);
                unlikes.setText(String.valueOf(shoppingItem.getUnlikes() + 1));

                mp.start();
                mp.setOnCompletionListener(mp1 -> flingCardContainer.getTopCardListener().selectLeft());

            });

            ArrayList<Pair<View,String>> pairs = new ArrayList<>();
            pairs.add(Pair.create(seller_logo,"company_logo"));
            pairs.add(Pair.create(brand_name,"company_name"));

            seller_logo.setOnClickListener(v -> Macros.Functions.
                    sellerProfile(getContext(), shoppingItem.getSellerId(), pairs));

            String cur_price;
            try {
                if (shoppingItem.getSeller().equals("ASOS")) {
                    cur_price = new DecimalFormat("##.##").
                            format(Double.parseDouble(shoppingItem.getPrice()) * Macros.POUND_TO_ILS) +
                            Currency.getInstance("ILS").getSymbol();
                }
                else {
                    cur_price = new DecimalFormat("##.##").
                            format(Double.parseDouble(shoppingItem.getPrice())) +
                            Currency.getInstance("ILS").getSymbol();
                }
            }
            catch (NumberFormatException ex){
                cur_price = "--";
            }

            if( shoppingItem.isOn_sale()) {
                int discount = (int) (100 - Math.ceil(100*(Double.parseDouble(shoppingItem.
                        getReduced_price())/Double.parseDouble(shoppingItem.getPrice()))));

                if(shoppingItem.isOn_sale())
                    sale.setText( "SALE" + System.lineSeparator() + "-" + discount + "%" );

                sale.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                sale.startAnimation(animation);

                String new_price;
                if(shoppingItem.getSeller().equals("ASOS"))
                    new_price = new DecimalFormat("##.##").
                            format(Double.parseDouble(shoppingItem.getReduced_price()) * Macros.POUND_TO_ILS) +
                            Currency.getInstance("ILS").getSymbol();
                else
                    new_price = new DecimalFormat("##.##").
                            format(Double.parseDouble(shoppingItem.getReduced_price())) +
                            Currency.getInstance("ILS").getSymbol();

                old_price.setVisibility(View.VISIBLE);
                old_price.setText(cur_price);
                old_price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                price.setText(new_price);
                price.setTextColor(Color.parseColor("#FEA8FF"));
            }
            else {
                sale.setVisibility(View.INVISIBLE);
                old_price.setVisibility(View.INVISIBLE);
                price.setText(cur_price);
                price.setTextColor(Color.WHITE);
            }
            if(shoppingItem.isExclusive()) {
                exclusive.setVisibility(View.VISIBLE);
                exclusive.setText("EXCLUSIVE");
            }
            else
                exclusive.setVisibility(View.INVISIBLE);

            Glide.with(getContext()).load(shoppingItem.getSellerLogoUrl()).into(seller_logo);

            brand_name.setText(shoppingItem.getBrand());

            favorite.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.like_swing);
                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.ding);
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

            fullscreen.setOnClickListener(v -> {

                Intent intent = new Intent(getContext(), FullscreenImageActivity.class);
                intent.putExtra("isFav", shoppingItem.isFavorite());
                intent.putExtra("brand", shoppingItem.getBrand());
                intent.putExtra("id", shoppingItem.getId());
                for(int i=0;i<shoppingItem.getImages().size(); i++) {
                    intent.putExtra("img"+(i+1), shoppingItem.getImages().get(i));
                }
                if(shoppingItem.getImages().size() < 4){
                    for(int i=shoppingItem.getImages().size(); i<4; i++) {
                        intent.putExtra("img"+(i), shoppingItem.getImages().get(0));
                    }
                }
                intent.putExtra("seller_logo", shoppingItem.getSellerLogoUrl());
                intent.putExtra("description", description.toString());
                intent.putExtra("seller", shoppingItem.getSeller());

                Macros.Functions.fullscreen( getContext(), intent, pairs);
            });

            Macros.Functions.GlidePicture(getContext(), shoppingItem.getImages().get(0), imageView);
        }

        return convertView;
    }

    public boolean isFavorite(){
        return isFavorite[0];
    }

    private void showLikesListDialog(ArrayList<LikedUser> liked_items){

        Dialog dialog = new Dialog(getContext());
        LikesListAdapter likedListAdapter = new
                LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, liked_items);
        likedListAdapter.notifyDataSetChanged();

        dialog.setContentView(R.layout.likes_list_dialog);
        TextView header = dialog.findViewById(R.id.likes_header);
        String _header_text = Macros.Items.LIKED + " this item";
        header.setText(_header_text);

        ListView listView = dialog.findViewById(R.id.likes_list);

        header.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.
                getDrawable(dialog.getContext(), R.drawable.ic_thumb_up_seleste),null,null,null);
        header.setCompoundDrawablePadding(20);

        listView.setAdapter(likedListAdapter);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setElevation(25);
        dialog.getWindow().setAllowEnterTransitionOverlap(true);
        dialog.getWindow().setAllowReturnTransitionOverlap(true);
        dialog.show();
    }

    private void showUnlikesListDialog(ArrayList<LikedUser> unliked_items){

        Dialog dialog = new Dialog(getContext());
        LikesListAdapter unlikedListAdapter = new LikesListAdapter(dialog.getContext(),
                R.layout.likes_list_item, unliked_items);
        unlikedListAdapter.notifyDataSetChanged();

        dialog.setContentView(R.layout.likes_list_dialog);
        TextView header = dialog.findViewById(R.id.likes_header);
        String _header_text = Macros.Items.UNLIKED + " this item";
        header.setText(_header_text);

        ListView listView = dialog.findViewById(R.id.likes_list);

        header.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.
                getDrawable(dialog.getContext(),
                        R.drawable.ic_thumb_down_pink),null,null,null);
        header.setCompoundDrawablePadding(20);

        listView.setAdapter(unlikedListAdapter);

        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}

