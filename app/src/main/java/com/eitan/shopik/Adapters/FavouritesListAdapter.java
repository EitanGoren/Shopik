package com.eitan.shopik.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavouritesListAdapter extends ArrayAdapter<ShoppingItem> {

    final String user_id;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public FavouritesListAdapter(@NonNull Context context, int resource, List<ShoppingItem> items){
        super(context, resource,items);
        this.user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ShoppingItem item = getItem(position);

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

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            final int[] i = {0};

            CircleImageView seller_logo2 = convertView.findViewById(R.id.seller_logo2);
            TextView seller_name = convertView.findViewById(R.id.seller_name);
            TextView counter = convertView.findViewById(R.id.item_count);
            counter.setText((position + 1) + "/" + getCount());
            final TextView likes = convertView.findViewById(R.id.list_like);
            likes.setText(String.valueOf(item.getLikes()));
            final TextView unlikes = convertView.findViewById(R.id.list_unlike);
            unlikes.setText(String.valueOf(item.getUnlikes()));
            TextView sale = convertView.findViewById(R.id.swipe_discount);
            TextView price = convertView.findViewById(R.id.list_item_price);
            TextView old_price = convertView.findViewById(R.id.old_price_text);

            final ImageView imageView = convertView.findViewById(R.id.image_item);
            String image = "";
            for(String img : item.getImages()){
                if(img != null && !img.equals("")) {
                    image = img;
                    break;
                }
            }
            Glide.with(getContext()).load(image).into(imageView);

            Button mNext = convertView.findViewById(R.id.next);
            Button mPrev = convertView.findViewById(R.id.previous);

            mNext.setOnClickListener(v -> {
                Animation fadeout = AnimationUtils.loadAnimation(getContext(),R.anim.fadeout);
                Animation fadein = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);
                imageView.startAnimation(fadeout);
                Glide.with(getContext()).load(item.getImages().get(((Math.abs(i[0]) + 1) % 4))).into(imageView);
                i[0]++;
                imageView.startAnimation(fadein);
            });
            mPrev.setOnClickListener(v -> {
                Animation fadeout = AnimationUtils.loadAnimation(getContext(),R.anim.fadeout);
                Animation fadein = AnimationUtils.loadAnimation(getContext(),R.anim.fadein);
                imageView.startAnimation(fadeout);
                Glide.with(getContext()).load((item.getImages().get((Math.abs(i[0]) + 1) % 4))).into(imageView);
                --i[0];
                imageView.startAnimation(fadein);
            });

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

            seller_name.setText(item.getBrand());
            Glide.with(getContext()).load(item.getSellerLogoUrl()).into(seller_logo2);

            ImageView favorite = convertView.findViewById(R.id.list_item_favourite_icon);

            final ImageButton fullscreen = convertView.findViewById(R.id.fullscreen_button);
            fullscreen.setOnClickListener(v -> Macros.Functions.fullscreen(getContext(), item));

            if (item.isFavorite()) {
                favorite.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
                favorite.startAnimation(animation);
            }
            else {
                favorite.setVisibility(View.INVISIBLE);
            }

            ListView listContainer = parent.findViewById(R.id.favorites_list);
            unlikes.setOnClickListener(v -> {
                String item_id = item.getId();
                String item_type = item.getType();
                String item_gender = item.getGender();
                updateCustomerDB(item_id,item_type,item_gender,item.getSub_category());
                updateItemsDB(item_id,item_type,item_gender);
                remove(item);
                Macros.Functions.showSnackbar(listContainer, "Removed Successfully", Objects.requireNonNull(getContext()),R.drawable.ic_thumb_down_pink);
            });

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


            TextView buy = convertView.findViewById(R.id.list_item_buy_button);
            buy.setOnClickListener(v -> Macros.Functions.buy(getContext(), item.getSite_link()));

            seller_logo2.setOnClickListener(v -> Macros.Functions.sellerProfile(getContext(),item.getSellerId()));
            seller_name.setOnClickListener(v -> Macros.Functions.sellerProfile(getContext(),item.getSellerId()));
        }

        return convertView;
    }

    @Nullable
    @Override
    public ShoppingItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void remove(@Nullable ShoppingItem object) {
        super.remove(object);
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