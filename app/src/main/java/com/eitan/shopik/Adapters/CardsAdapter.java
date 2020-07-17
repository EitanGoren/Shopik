package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eitan.shopik.Company.CompanyProfileActivity;
import com.eitan.shopik.Customer.FullscreenImageActivity;
import com.eitan.shopik.CustomerFragments.CustomerHomeFragment;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardsAdapter extends ArrayAdapter<ShoppingItem> {

    private final boolean[] isFavorite = new boolean[1];

    @Override
    public int getPosition(@Nullable ShoppingItem shoppingItem) {
        return super.getPosition(shoppingItem);
    }

    public CardsAdapter(Context context, int resourceId, List<ShoppingItem> bags){
        super(context,resourceId,bags);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {

        final ShoppingItem shoppingItem = getItem(position);

        assert shoppingItem != null;
        if(shoppingItem.isAd() && shoppingItem.getNativeAd() != null) {
            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.native_card_ad_template,null);
            // Set the media view.
            adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

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
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,pixels, Gravity.CENTER );
            convertView.setLayoutParams(params);
        }
        else if(!shoppingItem.isAd()) {

            final ArrayList<String> imagesUrl = new ArrayList<>();
            setImagesArray(shoppingItem,imagesUrl);

            isFavorite[0] = false;

            SwipeFlingAdapterView swipeFlingAdapterView = parent.findViewById(R.id.frame);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_item, parent, false);

            final ImageView favorite = convertView.findViewById(R.id.swipe_favorite_button);
            final ImageView image = convertView.findViewById(R.id.swipe_image);
            final ImageButton fullscreen = convertView.findViewById(R.id.fullscreen_button);
            TextView sale = convertView.findViewById(R.id.swipe_discount);
            TextView price = convertView.findViewById(R.id.updated_price);
            TextView old_price = convertView.findViewById(R.id.old_price);
            CircleImageView seller_logo = convertView.findViewById(R.id.card_seller_logo);
            TextView exclusive = convertView.findViewById(R.id.exclusive);
            TextView brand_name = convertView.findViewById(R.id.brand_name);

            final androidx.appcompat.widget.Toolbar toolbar = convertView.findViewById(R.id.customer_toolbar);
            androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener = item -> {
                switch (item.getItemId()) {
                    case R.id.card_cart:
                        //TODO Make cart page & icon in mainpage
                        Toast.makeText(getContext(), "cart", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.card_shop:
                        if ( shoppingItem.getSite_link() != null) {
                            if (!shoppingItem.getSite_link().startsWith("http://") && !shoppingItem.getSite_link().startsWith("https://"))
                                shoppingItem.setSite_link("http://" + shoppingItem.getSite_link());
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shoppingItem.getSite_link()));
                            getContext().startActivity(browserIntent);
                        }
                        break;
                    case R.id.card_info:
                        //TODO Make an item info page
                        Toast.makeText(getContext(), "info", Toast.LENGTH_SHORT).show();
                       break;
                    case R.id.card_seller:
                        Intent intent = new Intent(getContext(), CompanyProfileActivity.class);
                        intent.putExtra("id",shoppingItem.getSellerId());
                        intent.putExtra("customer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                        getContext().startActivity(intent);
                        break;
                    case R.id.card_favorites:
                        if(!isFavorite[0]) {
                            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.like_swing);
                            favorite.startAnimation(animation);
                            isFavorite[0] = !isFavorite[0];
                        //    final Handler handler = new Handler();
                        //    final Runnable r = () -> swipeFlingAdapterView.getTopCardListener().selectRight();
                         //   handler.postDelayed(r, 1050);
                            swipeFlingAdapterView.getTopCardListener().selectRight();
                        }
                        break;
                }
                return true;
            };
            toolbar.setOnMenuItemClickListener(topNavListener);
            Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            toolbar.setSoundEffectsEnabled(true);

            final TextView likes = convertView.findViewById(R.id.like);
            final TextView unlikes = convertView.findViewById(R.id.unlike);
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
                mp.start();
                likes.startAnimation(animation);
                likes.setText(String.valueOf(shoppingItem.getLikes() + 1));
                final Handler handler = new Handler();
                final Runnable r = () -> swipeFlingAdapterView.getTopCardListener().selectRight();
                handler.postDelayed(r, 1050);

            });
            unlikes.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.zoomout);
                final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fblike1);
                mp.start();
                unlikes.startAnimation(animation);
                unlikes.setText(String.valueOf(shoppingItem.getUnlikes() + 1));
                final Handler handler = new Handler();
                final Runnable r = () -> swipeFlingAdapterView.getTopCardListener().selectLeft();
                handler.postDelayed(r, 1050);
            });

            seller_logo.setOnClickListener(v -> Macros.Functions.sellerProfile(getContext(),shoppingItem));

            String cur_price = new DecimalFormat("##.##").
                    format(Double.parseDouble(shoppingItem.getPrice())*Macros.POUND_TO_ILS) +
                    Currency.getInstance("ILS").getSymbol();

            if(shoppingItem.isOutlet() || shoppingItem.isOn_sale()) {
                int discount = (int) (100 - Math.ceil(100*(Double.parseDouble(shoppingItem.getReduced_price())/Double.parseDouble(shoppingItem.getPrice()))));

                if(shoppingItem.isOn_sale())
                    sale.setText( "SALE" + System.lineSeparator() + "-" + discount + "%" );
                else if(shoppingItem.isOutlet())
                    sale.setText( "OUTLET" + System.lineSeparator() + "-" + discount + "%" );

                sale.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                sale.startAnimation(animation);

                String new_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(shoppingItem.getReduced_price())*Macros.POUND_TO_ILS) +
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

            Glide.with(getContext()).load(shoppingItem.getSellerLogoUrl()).into(seller_logo);

            setBrandName(shoppingItem, brand_name);

            favorite.setOnClickListener(v -> {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.like_swing);
                favorite.startAnimation(animation);
                isFavorite[0] = true;
                final Handler handler = new Handler();
                final Runnable r = () -> swipeFlingAdapterView.getTopCardListener().selectRight();
                handler.postDelayed(r, 1000);
            });

            shoppingItem.setImages(imagesUrl);
            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(),shoppingItem));

            Glide.with(getContext()).load(imagesUrl.get(0)).into(image);
            final RelativeLayout[] card_info = {convertView.findViewById(R.id.card_info)};
            Glide.with(getContext()).asBitmap().load(imagesUrl.get(0)).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    image.setImageBitmap(resource);
                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();

                    checkToolbarColor(bitmap,width,toolbar);
                    checkFullscreenColor(bitmap,width,height,fullscreen);
                    checkBottomLayoutColor(bitmap,height, card_info[0],seller_logo);
                }
                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }

                @Override
                public void onDestroy() {
                    super.onDestroy();
                    card_info[0] = null;
                }
            });
        }
        return convertView;
    }

    private void setBrandName(ShoppingItem shoppingItem, TextView brand_name) {
        brand_name.setText(shoppingItem.getBrand());
    }

    private void setImagesArray(ShoppingItem shoppingItem, ArrayList<String> imagesUrl) {

        Database connection = new Database();

        imagesUrl.add(connection.getASOSimageUrl(1,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
        imagesUrl.add(connection.getASOSimageUrl(2,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
        imagesUrl.add(connection.getASOSimageUrl(3,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
        imagesUrl.add(connection.getASOSimageUrl(4,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
    }

    private void checkToolbarColor(Bitmap bitmap, int width, Toolbar toolbar){

        int pixel_top_right = bitmap.getPixel(width - 15,70 );
        int red = Color.red(pixel_top_right);
        int blue = Color.blue(pixel_top_right);
        int green = Color.green(pixel_top_right);

        if( (red < 115 && blue < 115) || (green < 115 && red < 115)  || (green < 115 && blue < 115) )
            Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.rgb(255, 255, 255));
        else
            Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(Color.rgb(0, 0, 0));
    }

    private void checkFullscreenColor(Bitmap bitmap, int width, int height, ImageButton fullscreen){
        int pixel_bottom_right = bitmap.getPixel(width - 15,height - 50 );
        int red_bottom_right = Color.red(pixel_bottom_right);
        int blue_bottom_right  = Color.blue(pixel_bottom_right);
        int green_bottom_right  = Color.green(pixel_bottom_right);

        if( (red_bottom_right < 115 && blue_bottom_right < 115) || (green_bottom_right < 115 && red_bottom_right < 115) || (green_bottom_right < 115 && blue_bottom_right < 115) )
            fullscreen.getBackground().setTint(Color.rgb(255, 255, 255));
        else
            fullscreen.getBackground().setTint(Color.rgb(0, 0, 0));
    }

    private void checkBottomLayoutColor(Bitmap bitmap, int height, RelativeLayout card_info, CircleImageView seller_logo){

        int pixel_bottom_left = bitmap.getPixel(15,height - 50 );
        int red_bottom = Color.red(pixel_bottom_left);
        int blue_bottom  = Color.blue(pixel_bottom_left);
        int green_bottom  = Color.green(pixel_bottom_left);

        if( (red_bottom < 115 && blue_bottom < 115) || (green_bottom < 115 && red_bottom < 115) || (green_bottom < 115 && blue_bottom < 115) ) {
            card_info.setBackground(getContext().getDrawable(R.drawable.white_border_background));
            seller_logo.setBorderColor(Color.WHITE);
        }
        else {
            card_info.setBackground(getContext().getDrawable(R.drawable.bottom_info_border_line));
            seller_logo.setBorderColor(Color.BLACK);
        }
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

        header.setCompoundDrawablesRelativeWithIntrinsicBounds(dialog.getContext().getDrawable(R.drawable.ic_thumb_up_seleste),null,null,null);
        header.setCompoundDrawablePadding(20);

        listView.setAdapter(likedListAdapter);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

