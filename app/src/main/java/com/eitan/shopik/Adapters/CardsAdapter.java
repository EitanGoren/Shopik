package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
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

import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardsAdapter extends ArrayAdapter<ShoppingItem> {

    private final boolean[] isFavorite = new boolean[1];

    @Override
    public int getPosition(@Nullable ShoppingItem shoppingItem) {
        return super.getPosition(shoppingItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CardsAdapter(Context context, int resourceId, List<ShoppingItem> items){
        super(context,resourceId,items);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {

        final ShoppingItem shoppingItem = getItem(position);

        assert shoppingItem != null;
        if(shoppingItem.isAd() && shoppingItem.getNativeAd() != null && !shoppingItem.isDummyLastItem()) {
            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.native_card_ad_template,null);
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
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
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
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getVideoController();

            if(vc.hasVideoContent()) {
                vc.mute(false);
                vc.play();
            }

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

            SwipeFlingAdapterView swipeFlingAdapterView = parent.findViewById(R.id.frame);
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
                    case R.id.card_cart:
                        // TODO Make cart page & icon in Main Page
                        Toast.makeText(getContext(),"Cart", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.card_shop:
                        Macros.Functions.buy(getContext(),shoppingItem.getSite_link());
                        break;
                    case R.id.card_info:
                        // TODO Make an item info page
                        Toast.makeText(getContext(),"Info", Toast.LENGTH_SHORT).show();
                       break;
                    case R.id.card_seller:
                        Macros.Functions.sellerProfile(getContext(), shoppingItem.getSellerId(),null);
                        break;
                    case R.id.card_favorites:
                        if(!isFavorite[0]) {
                            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.like_swing);
                            favorite.startAnimation(animation);
                            isFavorite[0] = !isFavorite[0];
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    swipeFlingAdapterView.getTopCardListener().selectRight();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }
                        break;
                }
                return true;
            };
            toolbar.setOnMenuItemClickListener(topNavListener);
            Objects.requireNonNull(toolbar.getOverflowIcon()).
                    setTint(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            toolbar.setSoundEffectsEnabled(true);

            TextView likes = convertView.findViewById(R.id.like);
            TextView unlikes = convertView.findViewById(R.id.unlike);
            likes.setText(String.valueOf(shoppingItem.getLikes()));
            unlikes.setText(String.valueOf(shoppingItem.getUnlikes()));

            likes.setOnLongClickListener(v -> {
                if (shoppingItem.getLikedUsers() != null) {
                    showLikesListDialog(shoppingItem.getLikedUsers());
                }
                else {
                    Toast.makeText(getContext(), "No Likes yet :)", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            unlikes.setOnLongClickListener(v -> {
                if (shoppingItem.getUnlikedUsers() != null) {
                    showUnlikesListDialog(shoppingItem.getUnlikedUsers());
                }
                else {
                    Toast.makeText(getContext(), "No Unlikes yet :)", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            likes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.zoomin);
                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fblike1);
                likes.startAnimation(animation);
                likes.setText(String.valueOf(shoppingItem.getLikes() + 1));

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mp.start();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mp.setOnCompletionListener(mp1 -> {
                    swipeFlingAdapterView.getTopCardListener().selectRight();
                });

            });
            unlikes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.zoomout);
                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fblike1);

                unlikes.startAnimation(animation);
                unlikes.setText(String.valueOf(shoppingItem.getUnlikes() + 1));

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mp.start();
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mp.setOnCompletionListener(mp1 -> {
                    swipeFlingAdapterView.getTopCardListener().selectLeft();
                });

            });

            seller_logo.setOnClickListener(v -> Macros.Functions.
                    sellerProfile(getContext(),shoppingItem.getSellerId(), Pair.create(seller_logo,"company_logo")));

            String cur_price;
            if(shoppingItem.getSeller().equals("ASOS")) {
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(shoppingItem.getPrice())*Macros.POUND_TO_ILS) +
                        Currency.getInstance("ILS").getSymbol();
            }
            else {
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(shoppingItem.getPrice())) +
                        Currency.getInstance("ILS").getSymbol();
            }

            if(shoppingItem.isOutlet() || shoppingItem.isOn_sale()) {
                int discount = (int) (100 - Math.ceil(100*(Double.parseDouble(shoppingItem.getReduced_price())/Double.parseDouble(shoppingItem.getPrice()))));

                if(shoppingItem.isOn_sale())
                    sale.setText( "SALE" + System.lineSeparator() + "-" + discount + "%" );
                else if(shoppingItem.isOutlet())
                    sale.setText( "OUTLET" + System.lineSeparator() + "-" + discount + "%" );

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
                price.setTextColor(Color.RED);
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

            Macros.Functions.GlidePicture(getContext(),shoppingItem.getSellerLogoUrl(),seller_logo);

            brand_name.setText(shoppingItem.getBrand());

            favorite.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.like_swing);
                favorite.startAnimation(animation);
                isFavorite[0] = true;
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        swipeFlingAdapterView.getTopCardListener().selectRight();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            });

            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(), shoppingItem));

            String image = "";
            for(String img : shoppingItem.getImages()){
                if(img != null && !img.equals("")) {
                    image = img;
                    break;
                }
            }
            Macros.Functions.GlidePicture(getContext(),image,imageView);
        }
        return convertView;
    }

    public boolean isFavorite(){
        return isFavorite[0];
    }

    private void showLikesListDialog(ArrayList<LikedUser> liked_items){

        Dialog dialog = new Dialog(getContext());
        LikesListAdapter likedListAdapter = new LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, liked_items);
        likedListAdapter.notifyDataSetChanged();

        dialog.setContentView(R.layout.likes_list_dialog);
        TextView header = dialog.findViewById(R.id.likes_header);
        String _header_text = Macros.Items.LIKED + " this item";
        header.setText(_header_text);

        ListView listView = dialog.findViewById(R.id.likes_list);

        header.setCompoundDrawablesRelativeWithIntrinsicBounds(dialog.getContext().
                getDrawable(R.drawable.ic_thumb_up_seleste),null,null,null);
        header.setCompoundDrawablePadding(20);

        listView.setAdapter(likedListAdapter);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle("POOK");
        dialog.getWindow().setElevation(25);
        dialog.getWindow().setAllowEnterTransitionOverlap(true);
        dialog.getWindow().setAllowReturnTransitionOverlap(true);
        dialog.show();
    }

    private void showUnlikesListDialog(ArrayList<LikedUser> unliked_items){

        Dialog dialog = new Dialog(getContext());
        LikesListAdapter unlikedListAdapter = new LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, unliked_items);
        unlikedListAdapter.notifyDataSetChanged();

        dialog.setContentView(R.layout.likes_list_dialog);
        TextView header = dialog.findViewById(R.id.likes_header);
        String _header_text = Macros.Items.UNLIKED + " this item";
        header.setText(_header_text);

        ListView listView = dialog.findViewById(R.id.likes_list);

        header.setCompoundDrawablesRelativeWithIntrinsicBounds(dialog.getContext().getDrawable(R.drawable.ic_thumb_down_pink),null,null,null);
        header.setCompoundDrawablePadding(20);

        listView.setAdapter(unlikedListAdapter);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

}

