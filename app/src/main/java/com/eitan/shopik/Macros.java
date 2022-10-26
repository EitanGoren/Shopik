package com.eitan.shopik;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.eitan.shopik.adapters.LikesListAdapter;
import com.eitan.shopik.company.CompanyProfileActivity;
import com.eitan.shopik.customer.ShopikUser;
import com.eitan.shopik.database.models.ShoppingItem;
import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
@Keep
public class Macros {

    private static final String TAG = "Macros";

    public static final String WEBSITE_URL = "https://eitangoren.com";
    public static final String GOOGLE_PLAY_APP = "https://play.google.com/store/apps/details?id=com.eitan.shopik";
    public static final String COMPANIES = "Companies";
    public static final String DEFAULT_DESCRIPTION = "Write something here...";
    public static final String DEFAULT_COVER_PHOTO = "https://st2.depositphotos.com/1747568/8686/v/950/depositphotos_86864998-stock-illustration-abstract-background-consisting-of-small.jpg";
    public static final String DEFAULT_PROFILE_IMAGE = "https://clipartart.com/images/account-profile-clipart-4.png";
    public static final String CUSTOMERS = "Customers";
    public static final String MESSAGES = "Messages";
    public static final String ITEMS = "Items";
    //"ca-app-pub-5843605953860340/3737677950";
    public static final String FB_PLACEMENT_ID = "555191575133910_605556963430704";
    public static final String BAG = "Bags";
    public static final String SHOES = "Shoes";
    public static final String JEANS = "Jeans";
    public static final String ACCESSORIES = "Accessories";
    public static final String UNDERWEAR = "Underwear";
    public static final String DRESS = "Dresses";
    public static final String SHIRT = "Shirts";
    public static final String SUNGLASSES = "Sunglasses";
    public static final String SWIMWEAR = "Swimwear";
    public static final String JACKETS = "Jackets";
    public static final String JEWELLERY = "Jewellery";
    public static final String WATCH = "Watches";
    public static final String YOUTUBE_IC = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/YouTube_full-color_icon_%282017%29.svg/1200px-YouTube_full-color_icon_%282017%29.svg.png";
    public static final String TWITTER_IC = "https://www.aps.edu/sapr/images/pnglot.comtwitterbirdlogopng139932.png/image";
    public static final String FACEBOOK_IC = "https://upload.wikimedia.org/wikipedia/commons/0/05/Facebook_Logo_%282019%29.png";
    public static final String WEB_IC = "https://www.freepnglogos.com/uploads/logo-website-png/logo-website-world-wide-web-svg-png-icon-download-10.png";
    public static final String INSTAGRAM_IC = "https://www.kartisyarok.co.il/wp-content/uploads/2018/09/instagram-logo-png-7.png";
    public static final String WOMEN_CATEGORY_BACKGROUND_COLOR = "FF3E80";
    public static final String MEN_CATEGORY_BACKGROUND_COLOR = "83CED8";

    public static final int ITEMS_TO_AD = 16;

    public static class SnackbarGravity {
        public static final int TOP = Gravity.TOP;
        public static final int BOTTOM = Gravity.BOTTOM;
        public static final int CENTER = Gravity.CENTER;
    }

    public static class Providers {
        public static final String FACEBOOK = "facebook.com";
        public static final String GOOGLE = "google.com";
        public static final String FIREBASE = "firebase";
    }

    public static final String[] CompanyNames = {
            "Castro", "Asos", "Terminal X", "TwentyFourSeven", "Renuar", "Aldo", "Hoodies", "Shein"
    };

    public static class CustomerMacros {
        public static final String MEN = "Men";
        public static final String WOMEN = "Women";
        public static final String LIKED = "Liked";
        public static final String FAVORITE = "favorite";
        public static final String PREFERRED_ITEMS = "Preferred items";
    }

    public static class Arrays {
        public static final String[] PRICES = {"between 50 to 100", "above 100" ,"less than 50"};
    }

    public static class Items {
        public static final String[] brands = {
                //WOMENS BRANDS
                "A Star Is Born", "A.kjaerbede", "Abercrombie and Fitch", "Abrand", "Accessorize",
                "adidas", "Adolescent Clothing", "Aerie", "AJ Morgan", "Akasa", "Aldo", "AllSaints",
                "Alpha H", "Amelia Rose", "Ameliorate", "America & Beyond", "American Eagle",
                "Amuse Society", "Amy Lynn", "Anatomicals", "Anaya With Love", "Anmol", "Ann Summers",
                "Another Reason", "Ardell", "Aria Cove", "Ariana Grande", "Aroma Home", "Ash", "Ashiana",
                "ASOS 4505", "ASOS Collection", "ASOS DESIGN", "ASOS EDITION", "ASOS MADE IN KENYA",
                "ASOS WHITE", "ASRA", "Astrid & Miyu", "Atoir", "Aveda", "AX Paris", "AYM Studio",
                "b.Young", "Babyliss", "Band of Gypsies", "Band Of Stars", "Bandia", "Barbour", "Bardot",
                "bareMinerals", "Bariano", "Barneys Originals", "Barry M", "BCBG", "BCBGeneration",
                "O'Neill", "Oasis", "Obey", "Object", "Office", "Oh K!", "Olaplex", "Olivia Burton",
                "One Above Another", "One Teaspoon", "Only", "Opening Ceremony", "Opulence England",
                "Orelia", "Origins", "Oskia", "Ouai", "Outrageous Fortune", "Paddywax", "Palladium",
                "Paper Dolls", "Parallel Lines", "Parisian", "Park Lane", "Peek & Beau", "People Tree",
                "Pepe Jeans", "Peter Werth", "Philip Kingsley", "Pieces", "Pilgrim", "Pimkie",
                "Pink Clove", "Pistol Panties", "Pixi", "Playful Promises", "Polaroid", "Police",
                "Polo Ralph Lauren", "PopSockets", "Pour Moi", "Power 2 The Flower", "Pretty Lavish",
                "Teva", "TFNC", "The Couture Club", "The East Order", "The Fox Tan", "The Girlcode",
                "THE INKEY LIST", "The North Face", "The O Dolls Collection", "The Ordinary",
                "The Ragged Priest", "The Shrine", "THINX", "This Works", "Three Floor", "Thumbs Up",
                "Tiger Mist", "Timberland", "Tommy Hilfiger", "Toms", "Tonymoly", "Topshop", "True Decadence",
                "True Religion", "True Violet", "Truffle Collection", "Tutti Rouge", "Twiin", "Twisted Wunder",
                "Typo", "Ugg", "Ultrasun", "Under Armour", "Unique 21", "UOMA", "Urban Bliss", "Urban Decay",
                "Urbancode", "Vagabond", "Valentino by Mario Valentino", "Vans", "Vera Wang", "Vero Moda",
                "Verona", "Versace Jeans Couture", "Vesper", "Vestire", "Vila", "Vintage Supply",
                "Virgos Lounge", "Vita Liberata", "Vix", "Volcom", "Von Dutch", "Warehouse", "Waven",
                "We Are Kindred", "We Are Paradoxx", "We Are We Wear", "Wednesday's Girl", "Weekday",
                "WeWoreWhat", "Whistles", "Who What Wear", "Wild Honey", "Without You", "Wolf & Whistle",
                "Women'secret", "Wonderbra", "Wrangler", "Y.A.S", "Yaura", "Yes To", "Yours", "Z_Code_Z",
                "Zulu & Zephyr", "ZYA"
        };

        public static final String[] shit_words = {
                "fit","size","up",".", "#", "$", "[", "]" ,"with", "&" ,
                "design","in","t","and","'farleigh'","waist","waisted","jean","jeans",
                "at","x","to","by","pu","the","tea","sunglasses","sandals","shoes","sandal","shirt","t-shirt",
                "bag","watch","swim","boots","jacket","dress","swimsuit",
        };

        public static String[] getAllItemTypes(){
            return new String[]{BAG, SHOES, JEANS, DRESS, SHIRT, SWIMWEAR, JACKETS, ACCESSORIES, UNDERWEAR};
        }

        public static final String WOMEN_JACKET_IC = "https://www.dhresource.com/0x0/f2/albu/g9/M00/7A/63/rBVaWFwsnfSAGBQJAACzSfzciJg907.jpg";
        public static final String MENS_SHOES_IC = "https://5.imimg.com/data5/LW/DR/JH/ANDROID-80862476/product-jpeg-500x500.jpg";
        public static final String WOMEN_GLASSES_IC = "https://image.ebdcdn.com/image/upload/c_fill,e_sharpen:70,f_auto,h_600,q_auto:good,w_400/v1/product/model/portrait/pl4962_w1.jpg";
        public static final String MEN_JEWELLERY_IC = "https://ae01.alicdn.com/kf/Hb5f72d8e114e48959202a9cc04e28baaf/Explosion-model-3D-sand-gold-beads-bracelet-obsidian-sand-gold-bracelets-men-and-women-small-jewelry.jpg_q50.jpg";
        public static final String JEANS_IC = "https://www.nirofashion.com/images/giorgio-armani-mens-j15-regular-fit-blue-stretch-jeans-with-zip-fly-and-small-metal-logo-tab-on-ticket-pocket-p3252-27405_zoom.jpg";
        public static final String WOMEN_WATCH_IC = "https://myer-media.com.au/wcsstore/MyerCatalogAssetStore/images/10/105/1511/402/1/593353990/593353990_1_720x928.jpg";
        public static final String WOMENS_SHIRTS_IC = "https://cdn.shopify.com/s/files/1/0075/2415/5507/products/t-shirts-light-pink-x-small-strong-af-women-s-xc-tee-6214657081459_2000x.png?v=1550271116";
        public static final String MENS_SWIM_IC = "https://images-na.ssl-images-amazon.com/images/I/71xMusbhIkL._UL1200_.jpg";
        public static final String WOMEN_ACCESSORIES_IC = "https://review.chinabrands.com/chinabrands/seo/image/20180912/wholesale%20fashion%20accessories.jpg";
        public static final String WOMEN_LINGERIE_IC = "https://ae01.alicdn.com/kf/HTB1854TexrI8KJjy0Fpq6z5hVXaU/Christmas-Sexy-Red-Women-Lingerie-Sleepwear-Underwear-Bowknot-Bodysuit.jpg_960x960.jpg";
        public static final String LIKED = "Liked";
        public static final String UNLIKED = "Unliked";
    }

    public static class Functions {

        public static void showInteractionsDialog(Context context, Set<PublicUser> users, boolean isLiked) {
            Dialog dialog = new Dialog(context);
            LikesListAdapter adapter =
                    new LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, users);
            adapter.notifyDataSetChanged();

            dialog.setContentView(R.layout.likes_list_dialog);
            TextView header = dialog.findViewById(R.id.likes_header);
            String _header_text = isLiked ? "Like It" : "Didn't like it";
            header.setText(_header_text);

            ListView listView = dialog.findViewById(R.id.likes_list);

            header.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ContextCompat.
                    getDrawable(dialog.getContext(),
                            isLiked ? R.drawable.ic_thumb_up_seleste : R.drawable.ic_thumb_down_pink),
                    null, null, null
            );

            header.setCompoundDrawablePadding(20);

            listView.setAdapter(adapter);

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setElevation(25);
            dialog.getWindow().setAllowEnterTransitionOverlap(true);
            dialog.getWindow().setAllowReturnTransitionOverlap(true);
            dialog.show();
        }

//        public static void showUnlikesListDialog(Context context, ArrayList<PublicUser> unliked_items) {
//            Dialog dialog = new Dialog(context);
//            LikesListAdapter unlikedListAdapter = new LikesListAdapter(dialog.getContext(),
//                    R.layout.likes_list_item, unliked_items);
//            unlikedListAdapter.notifyDataSetChanged();
//
//            dialog.setContentView(R.layout.likes_list_dialog);
//            TextView header = dialog.findViewById(R.id.likes_header);
//            String _header_text = "Didn't like it";
//            header.setText(_header_text);
//
//            ListView listView = dialog.findViewById(R.id.likes_list);
//
//            header.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.
//                    getDrawable(dialog.getContext(),
//                            R.drawable.ic_thumb_down_pink), null, null, null);
//            header.setCompoundDrawablePadding(20);
//
//            listView.setAdapter(unlikedListAdapter);
//
//            Objects.requireNonNull(dialog.getWindow()).
//                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            dialog.show();
//        }

        public static void showSnackbar(View view, String text, Context context, int drawableRes, int gravity) {
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
            View snackbarLayout = snackbar.getView();

            TextView textView = snackbarLayout.findViewById(R.id.snackbar_text);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0);
            textView.setAllCaps(true);
            textView.setBackgroundColor(Color.TRANSPARENT);
            snackbarLayout.setBackgroundColor(context.getColor(R.color.SnackbarBackground));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);
            snackbar.show();
        }

        public static void GlidePicture(Context context, Object imageUrl, View view, int cros_fade) {
            try {
                if (view instanceof CircleImageView) {
                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.image_placeholder)
                            .into((CircleImageView) view);
                }
                else{
                    Glide.with(context).
                            load(imageUrl).
                            apply(new RequestOptions().
                                    override(Target.SIZE_ORIGINAL).
                                    format(DecodeFormat.PREFER_ARGB_8888)).
                            placeholder(R.drawable.image_placeholder).
                            transition(withCrossFade(cros_fade)).
                            into((ImageView) view);
                }
            }
            catch (Exception e){
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }

        public static void buy(Context context, String site_link) {
            if (site_link == null) return;
            if (!site_link.startsWith("http://") && !site_link.startsWith("https://"))
                site_link = ("http://" + site_link);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site_link));
            context.startActivity(browserIntent);
        }

        public static void fullscreen(Context context, Intent intent, ArrayList<Pair<View, String>> pairs) {

            if (pairs != null && pairs.size() > 1) {
                ActivityOptions options = ActivityOptions.
                        makeSceneTransitionAnimation((Activity) context, pairs.get(0), pairs.get(1));
                context.startActivity(intent, options.toBundle());
            }
            else
                context.startActivity(intent);

            if(context instanceof Activity)
                ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        public static void sellerProfile(Context context, String sellerId, ArrayList<Pair<View, String>> pairs) {
            Intent intent = new Intent(context, CompanyProfileActivity.class);
            intent.putExtra("id", sellerId);
            intent.putExtra("customer_id", ShopikUser.getInstance().getId());

            if (pairs != null && pairs.size() > 0) {
                ActivityOptions options = ActivityOptions.
                        makeSceneTransitionAnimation((Activity) context, pairs.get(0), pairs.get(1));
                context.startActivity(intent, options.toBundle());
            }
            else
                context.startActivity(intent);

            if(context instanceof Activity)
                ((Activity) context).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        public static void getRemasteredPrice(Context context, ShoppingItem shoppingItem,
                                              TextView price,
                                              TextView old_price,
                                              @Nullable TextView sale,
                                              String color_string) {
            String cur_price;
            try {
                cur_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(shoppingItem.getPrice())) +
                        Currency.getInstance("ILS").getSymbol();
            }
            catch (NumberFormatException ex){
                cur_price = "--";
            }
            catch (NullPointerException npe){
                cur_price = "Unknown";
            }

            if(shoppingItem.isOn_sale()) {
                int discount = (int) (100 - Math.ceil(100*(Double.parseDouble(shoppingItem.
                        getReducedPrice())/Double.parseDouble(shoppingItem.getPrice()))));

                if(sale != null) {
                    String sale_str = "SALE" + " -" + discount + "%";
                    sale.setText(sale_str);
                    sale.setTextColor(Color.RED);
                    sale.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.blink_anim);
                    sale.startAnimation(animation);
                }

                String new_price;
                new_price = new DecimalFormat("##.##").
                        format(Double.parseDouble(shoppingItem.getReducedPrice())) +
                        Currency.getInstance("ILS").getSymbol();

                old_price.setVisibility(View.VISIBLE);
                old_price.setText(cur_price);
                old_price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                price.setTextColor(Color.parseColor(color_string));
                price.setText(new_price);
            }
            else {
                if(sale != null) sale.setVisibility(View.GONE);
                old_price.setVisibility(View.INVISIBLE);
                price.setText(cur_price);
                price.setTextColor(Color.BLACK);
            }
        }
    }
}
