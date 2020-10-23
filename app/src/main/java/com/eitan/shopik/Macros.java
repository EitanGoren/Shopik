package com.eitan.shopik;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.eitan.shopik.adapters.LikesListAdapter;
import com.eitan.shopik.company.CompanyProfileActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
@Keep
public class Macros {

    public static final String TAG = "com.eitan.shopik";
    public static final String COMPANIES = "Companies";
    public static final String DEFAULT_DESCRIPTION = "Write something here...";
    public static final String DEFAULT_COVER_PHOTO = "https://st2.depositphotos.com/1747568/8686/v/950/depositphotos_86864998-stock-illustration-abstract-background-consisting-of-small.jpg";
    public static final String DEFAULT_PROFILE_IMAGE = "https://clipartart.com/images/account-profile-clipart-4.png";
    public static final String CUSTOMERS = "Customers";
    public static final String ITEMS = "items";
    public static final String TEST_DEVICE_ID = "7255B1C36174A2C33091060576730302";
    public static final String PAGE_NUM = "page_num";
    public static final String NATIVE_ADVANCED_AD = "ca-app-pub-5843605953860340/3737677950";
    public static final String FB_PLACEMENT_ID = "555191575133910_605556963430704";
    public static final String BAG = "Bag";
    public static final String SHOES = "Shoes";
    public static final String JEANS = "Jeans";
    public static final String ACCESSORIES = "Accessories";
    public static final String LINGERIE = "Lingerie";
    public static final String DRESS = "Dress";
    public static final String SHIRT = "Shirt";
    public static final String SUNGLASSES = "Sunglasses";
    public static final String SWIMWEAR = "Swimwear";
    public static final String JACKETS = "Jackets";
    public static final String JEWELLERY = "Jewellery";
    public static final String WATCH = "Watch";
    public static final String FIRST_PIC = "https://lh3.googleusercontent.com/6zlRdjaz9XWvbywzvyctdRFFIwSUBmnkvsd3wLJ8w3X-u5auwtZSl418Vb8VPTIZqkEA";
    public static final String SECOND_PIC = "https://www.renanim.co.il/wp-content/uploads/2018/03/cube-550-550.jpg";
    public static final String THIRD_PIC = "https://lh3.googleusercontent.com/AOIrDWZ7ckxFyiQBNVuzN1CwKb7_O07euOgxMzqLs4nPpbJCztWz9-8tvy3vfICWi7k";
    public static final String FOURTH_PIC = "https://thejewishvoice.com/wp-content/uploads/2020/05/New-York-Canadian-Retailer-article.jpg";
    public static final String FIFTH_PIC = "https://www.shaar-hatsafon.co.il/wp-content/uploads/2018/12/%D7%A8%D7%A0%D7%95%D7%90%D7%A8-1.jpg";
    public static final String SIXTH_PIC = "https://www.daitd.com/wp-content/uploads/2020/04/retminla_X_9.jpg";
    public static final String SEVENTH_PIC = "https://seamall.co.il/wp-content/uploads/2019/05/%D7%91%D7%90%D7%A0%D7%A8-%D7%98%D7%95%D7%95%D7%A0%D7%98%D7%99-%D7%A4%D7%95%D7%A8-%D7%A1%D7%91%D7%9F.jpg";
    public static final String EIGHTH_PIC = "https://www.alon-ein-shemer.co.il/wp-content/uploads/2016/10/%D7%94%D7%95%D7%93%D7%99%D7%A1-%D7%A2%D7%99%D7%9F-%D7%A9%D7%9E%D7%A8.jpg";
    public static final String YOUTUBE_IC = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/YouTube_full-color_icon_%282017%29.svg/1200px-YouTube_full-color_icon_%282017%29.svg.png";
    public static final String TWITTER_IC = "https://www.aps.edu/sapr/images/pnglot.comtwitterbirdlogopng139932.png/image";
    public static final String FACEBOOK_IC = "https://upload.wikimedia.org/wikipedia/commons/0/05/Facebook_Logo_%282019%29.png";
    public static final String WEB_IC = "https://www.freepnglogos.com/uploads/logo-website-png/logo-website-world-wide-web-svg-png-icon-download-10.png";
    public static final String INSTAGRAM_IC = "https://www.kartisyarok.co.il/wp-content/uploads/2018/09/instagram-logo-png-7.png";

    public static final double POUND_TO_ILS = 5.2484377;
    public static final int ITEMS_TO_AD = 16;

    public static class Providers {
        public static final String FACEBOOK = "facebook.com";
        public static final String GOOGLE = "google.com";
        public static final String PASSWORD = "password";
    }

    public static final String[] CompanyNames = {
            "Castro", "Asos", "Terminal X", "TwentyFourSeven", "Renuar", "Aldo", "Hoodies", "Shein"
    };

    public static class CustomerMacros {
        public static final String MEN = "Men";
        public static final String WOMEN = "Women";
        public static final String LIKED = "Liked";
        public static final String FAVOURITE = "favorite";
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
            return new String[]{BAG,SHOES,JEANS,DRESS,SHIRT,SUNGLASSES,SWIMWEAR,JACKETS,JEWELLERY,WATCH,ACCESSORIES,LINGERIE};
        }

        public static final String MEN_JACKET_IC = "https://ae01.alicdn.com/kf/HTB1_6q2NXXXXXbGXVXXq6xXFXXXv/Men-s-suit-new-men-denim-jacket-spring-small-men-s-cultivate-one-s-morality-leisure.jpg";
        public static final String WOMEN_JACKET_IC = "https://www.dhresource.com/0x0/f2/albu/g9/M00/7A/63/rBVaWFwsnfSAGBQJAACzSfzciJg907.jpg";
        public static final String BAGS_IC = "https://berlinbpictureblob.blob.core.windows.net/pictures/0016206_small-bag-womens-fashion-printed-letters-women-backpack-portable-old-flower-pu-female-bag-mummy-bag-_550.jpeg";
        public static final String WOMENS_SHOES_IC = "https://cdn.shopify.com/s/files/1/0252/2313/5306/files/petitfour_shoes_magnolia_1600x.jpg?v=1582827967";
        public static final String MENS_SHOES_IC = "https://5.imimg.com/data5/LW/DR/JH/ANDROID-80862476/product-jpeg-500x500.jpg";
        public static final String WOMEN_GLASSES_IC = "https://image.ebdcdn.com/image/upload/c_fill,e_sharpen:70,f_auto,h_600,q_auto:good,w_400/v1/product/model/portrait/pl4962_w1.jpg";
        public static final String MEN_GLASSES_IC = "https://image.made-in-china.com/2f0j00YUifWqyrscbG/2019-New-Arrival-Acetate-Sun-Glasses-Fashion-Sunglasses-for-Men.jpg";
        public static final String WOMEN_JEWELLERY_IC = "https://nichesubshop.com/wp-content/uploads/2019/12/2019-New-Fashion-Pearl-Ear-Cuff-Bohemia-Stackable-C-Shaped-CZ-Rhinestone-Small-Earcuffs-Clip-Earrings-for-Women-Wedding-1-2.jpg";
        public static final String MEN_JEWELLERY_IC = "https://ae01.alicdn.com/kf/Hb5f72d8e114e48959202a9cc04e28baaf/Explosion-model-3D-sand-gold-beads-bracelet-obsidian-sand-gold-bracelets-men-and-women-small-jewelry.jpg_q50.jpg";
        public static final String DRESS_IC = "https://cdn.shopify.com/s/files/1/0047/9418/7847/products/71nGc4UfLBL.jpg?v=1583280772";
        public static final String JEANS_IC = "https://www.nirofashion.com/images/giorgio-armani-mens-j15-regular-fit-blue-stretch-jeans-with-zip-fly-and-small-metal-logo-tab-on-ticket-pocket-p3252-27405_zoom.jpg";
        public static final String MEN_WATCH_IC = "https://cdn.shopify.com/s/files/1/0020/2116/3107/products/Men-Watches-Top-Brand-Luxury-YAZOLE-Watch-Male-Clock-Business-Mens-Wrist-Watch-Small-Second-Dial_d6933845-5231-4582-b518-ba5013381c17.jpg?v=1552372539";
        public static final String WOMEN_WATCH_IC = "https://myer-media.com.au/wcsstore/MyerCatalogAssetStore/images/10/105/1511/402/1/593353990/593353990_1_720x928.jpg";
        public static final String WOMENS_SHIRTS_IC = "https://cdn.shopify.com/s/files/1/0075/2415/5507/products/t-shirts-light-pink-x-small-strong-af-women-s-xc-tee-6214657081459_2000x.png?v=1550271116";
        public static final String MENS_SHIRTS_IC = "https://cdn3.volusion.com/ba3tg.hs2vg/v/vspfiles/photos/m-10602-100-2.jpg?v-cache=1570606546";
        public static final String WOMENS_SWIM_IC = "https://jhonpeters.com/wp-content/uploads/2019/05/Untitled-6-33.jpg";
        public static final String MENS_SWIM_IC = "https://images-na.ssl-images-amazon.com/images/I/71xMusbhIkL._UL1200_.jpg";
        public static final String WOMEN_ACCESSORIES_IC = "https://review.chinabrands.com/chinabrands/seo/image/20180912/wholesale%20fashion%20accessories.jpg";
        public static final String MEN_ACCESSORIES_IC = "https://ae01.alicdn.com/kf/HTB1ZvyEOhnaK1RjSZFBq6AW7VXa8/Stone-Beads-Bracelet-Men-Accessories-Crown-Braclets-Handmade-2-Piece-Set-Erkek-Bileklik-Skull-Jewelry-Mens.jpg" ;
        public static final String WOMEN_LINGERIE_IC = "https://ae01.alicdn.com/kf/HTB1854TexrI8KJjy0Fpq6z5hVXaU/Christmas-Sexy-Red-Women-Lingerie-Sleepwear-Underwear-Bowknot-Bodysuit.jpg_960x960.jpg";
        public static final String MEN_UNDERWEAR_IC = "https://i1.wp.com/jp.smpclippingpath.com/wp-content/uploads/2020/01/lkh0.png?fit=450%2C450&ssl=1";

        public static final String TOTE_RES = "https://cdn.shopify.com/s/files/1/0166/6656/products/Oh_honey_tote022.jpg?v=1563184080";
        public static final String CROSS_RES = "https://cdn-com.thelittlegreenbag.com/upload/artikelen/Matt-Nat/ss20/bee-dwell-crossbody-bag/Matt-Nat-Bee-Dwell-Crossbody-bag-SS20-crossbody-tas-mojito-crossbody-bag-model-600.jpg";
        public static final String BACKPACK_RES = "https://i.pinimg.com/236x/67/4f/a0/674fa0e41bcc893f8a4112c288ca9442.jpg";
        public static final String BUM_RES = "https://www.mohito.com/media/catalog/product/cache/1200/a4e40ebdc3e371adff845072e1c73f37/X/Y/XY082-99X-020_1.jpg";
        public static final String CLUTCH_RES = "https://ae01.alicdn.com/kf/HTB14qFGLpXXXXX.aXXXq6xXFXXXb/women-genuine-leather-evening-clutch-bag-stone-pattern-flap-handbag-for-female-wristlet-purse-fashion-shoulder.jpg";
        public static final String WALLETS_RES = "https://pipiluxury.com/wp-content/uploads/2019/12/Fashion-Brand-Long-Wallets-Women-Leather-Women-Wallets-Wristlet-Handbags-Long-Money-Bag-Zipper-Coin-Purse.jpg";

        public static final String WOMEN_BELTS = "https://ae01.alicdn.com/kf/H4d419aefdf194531ab0bfdd7ed5c9051v/Fashion-PU-Leather-Belts-Simple-Round-Leopard-Print-Buckle-Women-Belts-Vintage-Jeans-Dress-Decor-Waistbands.jpg";
        public static final String WOMEN_SCARVES = "https://image.made-in-china.com/43f34j00JUYfpSuGbmoK/New-Winter-Scarf-Fashion-Women-Scarf-Luxury-Plaid-Cashmere-Scarves-Women-Triangle-Wholesale-Scarf.jpg";
        public static final String WOMEN_HATS = "https://images-na.ssl-images-amazon.com/images/I/61IxpKIPAgL._AC_UX385_.jpg";
        public static final String WOMEN_SPORT = "https://cdn.shopify.com/s/files/1/0028/8102/8131/products/0_600x.jpg?v=1576219316";

        public static final String MEN_BELTS = "https://5.imimg.com/data5/UN/ED/MY-35870101/tb148lm2b-men-belts-250x250.png";
        public static final String MEN_HATS = "https://ae01.alicdn.com/kf/HTB1skoVm22H8KJjy0Fcq6yDlFXap.jpg_q50.jpg";
        public static final String MEN_TIES = "https://contestimg.wish.com/api/webimage/59d1c9867526fb2fbf6f0aec-large.jpg?cache_buster=f536d0e7dc88b92a2cc7611bcb5d3556";
        public static final String MEN_SCARVES = "https://images-na.ssl-images-amazon.com/images/I/61HolmRyimL._AC_UY1000_.jpg";

        public static final String MEN_UNDERWEAR = "https://cdn.pricearchive.org/images/aliexpress.com/32918630258/Men-Underwear-Boxers-Plaid-Loose-Shorts-Men-Panties-Cotton-Soft-Large-Arrow-Pants-At-Home-Underwear.jpg";
        public static final String MEN_SOCKS = "https://billybelt.com/5988-large_default/cotton-socks-khaki-striped.jpg";

        public static final String WOMEN_UNDERWEAR = "https://img.joomcdn.net/96f1579fa8a9573dc13b3324e1edb4824ab9d7cb_400_400.jpeg";
        public static final String WOMEN_SOCKS = "https://ae01.alicdn.com/kf/HTB1SrWzPQvoK1RjSZFDq6xY3pXa6/Solid-Color-No-Show-Socks-Women-Boat-Invisible-Girls-Cotton-Women-Socks-Slippers-1-Pair.jpg_q50.jpg";
        public static final String WOMEN_BRAS = "https://img.joomcdn.net/517caacbae366bd288a824d31a35e36aa33e7390_400_400.jpeg";
        public static final String WOMEN_SHAPEWEAR = "https://i5.walmartimages.com/asr/c52237fe-5bb5-4349-8217-5d782a4b63aa_1.4413efef7927471436bbc3ddd4d47a78.jpeg";
        public static final String WOMEN_PYJAMAS = "https://sc02.alicdn.com/kf/HTB1Ya2WKFXXXXbIXFXXq6xXFXXX4.jpg_350x350.jpg";

        public static final String WOMEN_LEATHER_JACKETS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/000-012-blk-5/black-leather-moto-jacket-product.jpg?1546447623";
        public static final String WOMEN_PUFFER_JACKETS_RES = "https://i.pinimg.com/564x/9d/95/9a/9d959a0c5a3ec05c572769613011565c.jpg";
        public static final String WOMEN_TEDDY_JACKETS_RES = "https://cdn.shopify.com/s/files/1/2033/1473/products/shopify_9f083e8613ffd5621849c28434d1935e_teddy-jacket-tan_1230x1230.jpg?v=1571594952";
        public static final String WOMEN_JACKET_JACKETS_RES = "https://sc01.alicdn.com/kf/HTB1BpMGOFXXXXcDXpXXq6xXFXXXM.jpg";
        public static final String WOMEN_COAT_JACKETS_RES ="https://i.pinimg.com/736x/69/fd/9e/69fd9e9fefe519f0a6dafeadb43b4d95.jpg";

        public static final String MEN_BIKER_JACKETS_RES = "https://redbridgejeans.de/media/image/product/65309/lg/m6013_red-bridge-mens-faux-leather-jacket-biker-jacket-biker-quilted-jacket_5.jpg";
        public static final String MEN_LEATHER_JACKETS_RES = "https://images-na.ssl-images-amazon.com/images/I/81gPumH6e1L._AC_UL1500_.jpg";
        public static final String MEN_PUFFER_JACKETS_RES = "https://theinscribermag.com/wp-content/uploads/2018/10/puffer-coat-mens-puffer-padded-jacket-winter-style-puffer-coat-mens-river-island.jpg";
        public static final String MEN_DENIM_JACKETS_RES = "https://www.thefashionisto.com/wp-content/uploads/2019/09/HM-Men-Skinny-Jeans.jpg";
        public static final String MEN_TRENCH_JACKETS_RES = "https://i.pinimg.com/originals/41/b1/7a/41b17ad4c3d86eb07c038d8d0e4c6d3a.png";
        public static final String MEN_OVERCOAT_JACKETS_RES = "https://cdn.shopify.com/s/files/1/1083/6796/products/product-image-1237256389_1024x1024.jpg?v=1578625267";

        public static final String FLAT_SHOES_RES = "https://shoedazed.com/image/cache/catalog/AIMEIGAO1//AIMEIGAO-2019-New-Summer-Sandals-Women-Casual-Flat-Sandals-Comfortable-Sandals-For-Women-Large-Size--32972219136-1711-550x550.jpeg";
        public static final String WOMEN_BOOTS_RES = "https://c4.wallpaperflare.com/wallpaper/53/168/878/kendall-jenner-women-model-brunette-boots-hd-wallpaper-preview.jpg";
        public static final String WOMEN_HEELS_RES = "https://cdn.shopify.com/s/files/1/0727/7899/products/Fashion-Summer-High-Heels-Shoes-Women-s-Sandals-Strap-Ladies-Ankle-High-Heels-Size-35-43.jpg_640x640_35a7641d-8003-4da0-a38d-a1bad21dd539_1024x1024.jpg?v=1571438898";
        public static final String WOMEN_SLIDERS_RES = "https://www.twinset.com/on/demandware.static/-/Sites-master-catalog/default/dwaab16f5b/images/TwinSet/Images/Catalog/201TCT022-00519-0S.JPG";
        public static final String WOMEN_SLIPPERS_RES = "https://ae01.alicdn.com/kf/H5a82937964f2483e8344658dee390223O/Woman-Slippers-Lady-Platform-Flower-Casual-Beach-Flip-Flops-Sandals-Women-Sandals-Summer-Sexy-High-Heel.jpg";
        public static final String WOMEN_TRAINERS_RES = "https://contestimg.wish.com/api/webimage/5d8c3111cb2046355e5cc88f-large.jpg?cache_buster=29bf7df2cdb7e6335ee8661a73efc9a4";
        public static final String WOMEN_SANDALS_RES = "https://sc02.alicdn.com/kf/HTB1EUgNPpXXXXc7XVXXq6xXFXXXS.jpg";

        public static final String MINI_DRESSES_RES = "https://i.pinimg.com/originals/c6/4a/2b/c64a2b4c4daa4a48b3a9f787353b1953.jpg";
        public static final String MIDI_DRESS_RES = "https://images.asos-media.com/products/liquorish-a-line-lace-detail-midi-dress/8881608-4?$XXL$&wid=513&fit=constrain";
        public static final String PARTY_DRESS_RES = "https://cdn.iclothing.com/media/catalog/product/cache/1/thumbnail/1345x1992/9df78eab33525d08d6e5fb8d27136e95/g/r/green_sequin_dress_1.jpg";
        public static final String EVENING_DRESS_RES = "https://www.mrslali.com/wp-content/uploads/2019/03/lookbook1904.jpg";
        public static final String JUMPER_DRESS_RES = "https://dimg.dillards.com/is/image/DillardsZoom/mainProduct/love--piece-plaid-button-front-jumper-dress/05858227_zi_black_red_white.jpg";
        public static final String MAXI_DRESS_RES = "https://is4.revolveassets.com/images/p4/n/d/PBTO-WD14_V1.jpg";
        public static final String WEDDING_DRESS_RES = "https://cdn.alysonhaley.com/wp-content/uploads/2018/07/31154340/amalfi-church-instagram-spot-italy-alyson-haley-80.jpg";

        public static final String MEN_SLIM_JEANS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/sks-009-blu-2-1547859752085/skinny-slim-jeans-in-dark-worn-wash-product.jpg?1547859758";
        public static final String MEN_SKINNY_JEANS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/mens-skinny-jeans-in-faded-black-3/mens-faded-black-skinny-jeans-1-product.jpg?1527794179";
        public static final String MEN_STRAIGHT_JEANS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/straight-jeans-in-black-worn-05/straight-jeans-in-black-worn-product.jpg?1537903204";

        public static final String WOMEN_SLIM_JEANS_RES = "https://cdn11.bigcommerce.com/s-pkla4xn3/images/stencil/1280x1280/products/21185/187304/Skinny-Jeans-Woman-Autumn-New-2018-High-waist-Women-Fashion-Slim-Jeans-Female-washed-casual-skinny__35509.1546333221.jpg?c=2?imbypass=on";
        public static final String WOMEN_SKINNY_JEANS_RES = "https://www.argo-holidays.com/images/a/skinny%20jeans%20for%20women-942nkp.jpg";
        public static final String WOMEN_STRAIGHT_JEANS_RES = "https://www1.assets-gap.com/webcontent/0017/347/802/cn17347802.jpg";
        public static final String WOMEN_RIPPED_JEANS_RES = "https://i.pinimg.com/originals/85/35/02/8535027a20707a4c3ae0f4e2ee2b6b51.jpg";
        public static final String WOMEN_JEGGINGS = "https://cottonon.com/dw/image/v2/BBDS_PRD/on/demandware.static/-/Sites-catalog-master-women/default/dwd3c519fd/241163/241163-207-2.jpg?sw=566&sh=849&sm=fit";
        public static final String WOMEN_HIGH_WAIST = "https://media.missguided.com/s/missguided/G1806691_set/1/blue-sinner-distressed-knee-high-waisted-jeans";

        public static final String MEN_NECKLACE_RES = "https://images-na.ssl-images-amazon.com/images/I/91sUTxBPQlL._SL1500_.jpg";
        public static final String MEN_RING_RES = "https://www.anitolia.com/plain-model-black-stone-silver-ring-mens-ring-ani-yuzuk-749-14-B.jpg";
        public static final String MEN_BRACELET_RES = "https://www.ties.com/blog/wp-content/uploads/2015/10/Your_Guide_to_Men%E2%80%99s_Jewelry_01.jpg";
        public static final String MEN_EARRING_RES = "https://miro.medium.com/max/870/0*gigjbEBGJQ_0PIUQ.jpeg";

        public static final String WOMEN_FACE_MASK = "https://cdn.shopify.com/s/files/1/0098/8990/6788/products/SAFE0046_3.jpg?v=1590787101";
        public static final String MEN_FACE_MASK = "https://www.headcovers.com/media/catalog/product/cache/ba642c93a0efc71830935b1d4e0de39d/m/e/medical-surgical-face-mask-for-men-solid-black.jpg";

        public static final String WOMEN_NECKLACE_RES = "https://ae01.alicdn.com/kf/HLB1TVEBaizxK1RkSnaVq6xn9VXaT/Choker-Necklace-Heart-New-Design-Punk-Gold-Necklace-Boho-Gothic-Fashion-Jewelry-For-Women-Wicca-Chain.jpg";
        public static final String WOMEN_RING_RES = "https://cdn.shopify.com/s/files/1/0259/0713/products/DOC-R8121-M_Designer_Platinum_Ring_with_Diamonds_with_Partly_Rose_Gold_Polish_JL_PT_566_Model_View_showing_how_the_ring_looks_when_worn_in_hand_of_a_woman_grande.jpg?v=1520407031";
        public static final String WOMEN_BRACELET_RES = "https://images.neimanmarcus.com/ca/1/product_assets/Y/5/0/X/U/NMY50XU_la.jpg";
        public static final String WOMEN_EARRING_RES = "https://www.besttohave.com/images/925-sterling-silver-white-freshwater-pearl-drop-earrings-p183-2477_image.jpg";
        public static final String WOMEN_ANKLET_RES = "https://cdn.lisaangel.co.uk/image/cache/data/product-images/ss19/su-ss19/layered-beaded-anklet-in-grey-and-rose-gold-4x3a1249-515x515.jpg";

        public static final String WOMEN_SQUARE_GLASSES_RES = "https://ae01.alicdn.com/kf/H661c51c6971345b58d2c0fb61b7607c82/GIFANSEE-women-square-sunglasses-oversized-uxury-brand-glasses-vintage-design-eyewear-uv400.jpg";
        public static final String WOMEN_AVIATOR_GLASSES_RES = "https://cdn.shopify.com/s/files/1/0166/6656/products/Skinnydip_London_Rainbow_Aviator_Sunglasses_Model_Image_1_600x.jpg?v=1579790986";
        public static final String WOMEN_CATEYE_GLASSES_RES = "https://ae01.alicdn.com/kf/HLB1YKs2LHvpK1RjSZFqq6AXUVXah.jpg";
        public static final String WOMEN_ROUND_GLASSES_RES = "https://ae01.alicdn.com/kf/HTB1q60jdLjM8KJjSZFyq6xdzVXaC/DOKLY-New-Polarized-Mirror-Round-Sunglasses-women-Vintage-RetroWomen-Designer-Eyewear-Green-Color-UV400-Glasses-oculos.jpg";

        public static final String MEN_ROUND_GLASSES_RES = "https://images.51microshop.com/4602/product/20190807/New_Fashion_Mens_Driving_Sunglasses_Polarized_Retro_Men_Round_Popular_Sunglasses_1565161878828_0.jpg";
        public static final String MEN_SQUARE_GLASSES_RES = "https://pipiluxury.com/wp-content/uploads/2020/02/VIVIBEE-Color-Change-Grey-Frame-Photochromic-Polarized-Sunglasses-Men-Square-Classic-Chameleon-Glaases-Transition-Lens-Eyewear-1.jpg";

        public static final String MEN_LOAFERS_RES = "https://cf.shopee.ph/file/906adc125c1045c1dd079ed0cdd8afb9";
        public static final String MEN_BOOTS_RES = "https://clarks.scene7.com/is/image/Pangaea2Build/db-winter-boots-2-854-wk31-aw19?wid=854&fmt=pjpeg";
        public static final String MEN_SLIDERS_RES = "https://2.bp.blogspot.com/-6PQM-tOmFfY/W2YXjmi95yI/AAAAAAAAcpE/uy0q7iOHaI8XsHlBO0CU05hFJ5ir8TUPwCLcBGAs/s640/top-5-men-sliders-under%252430.jpg";
        public static final String MEN_SANDALS_RES = "https://ae01.alicdn.com/kf/HTB1Bmw.cWQoBKNjSZJnq6yw9VXaC/2018-New-Men-s-Sandals-Explosive-models-Half-Slippers-Couple-Mesh-Sandals-Lazy-Bird-Nest-Slippers.jpg";
        public static final String MEN_TRAINERS_RES = "https://5.imimg.com/data5/AP/NV/BK/SELLER-9615630/big-fox-men-s-running-training-walking-555-mesh-sports-shoes-500x500.jpg";

        public static final String WOMEN_TSHIRT_RES = "https://cdn.shopify.com/s/files/1/0814/7533/products/Women_s-V-Neck-T-shirt-White-Front.jpg?v=1521696280";
        public static final String WOMEN_LEOTARDS_RES = "https://i.pinimg.com/236x/fc/5e/a3/fc5ea3c7de82420781be536e7a76f800.jpg";
        public static final String WOMEN_TANKS_RES = "https://www.vientoclothing.com/14264-thickbox_default/women-tank-top-black-dark-captain.jpg";
        public static final String WOMEN_CAMIS_RES = "https://ae01.alicdn.com/kf/HTB1.UQpbo_rK1Rjy0Fcq6zEvVXaK/Women-Camis-Silk-Top-Women-Camisole-2020-Summer-Style-Sexy-Sleeveless-Vest-Slim-White-Tank-Top.jpg_q50.jpg";
        public static final String WOMEN_EVENING_RES = "https://i.pinimg.com/originals/c8/56/1b/c8561bb9944669fdef60e4a489710094.jpg";
        public static final String WOMEN_SUMMER_TOP_RES = "https://cartling.com/wp-content/uploads/2019/06/Plus-Size-5XL-Women-Tops-Blouse-Long-Sleeve-Solid-V-neck-Loose-Chiffon-Blouses-2019-Summer-10.jpg_640x640-10.jpg";
        public static final String WOMEN_BLOUSES_RES = "https://ae01.alicdn.com/kf/HTB13y9scfWG3KVjSZFgq6zTspXaQ/Women-Blouses-Fashion-Long-Sleeve-Turn-Down-Collar-Office-Shirt-Chiffon-Blouse-Shirt-Casual-Tops-Plus.jpg_640x640.jpg";
        public static final String WOMEN_HOODIES_RES = "https://i.pinimg.com/originals/8c/e1/37/8ce137b4560bb55d88e226c79372f178.jpg";
        public static final String WOMEN_SWEATSHIRTS_RES = "https://i.pinimg.com/564x/06/25/d2/0625d21e19fffa33c03e9b01fdbe5434.jpg";

        public static final String MEN_TSHIRT_RES = "https://sc01.alicdn.com/kf/HT1IltqFOxbXXagOFbXg/220128135/HT1IltqFOxbXXagOFbXg.jpg";
        public static final String MEN_DENIM_RES = "https://images-na.ssl-images-amazon.com/images/I/91LSbpHdSYL._AC_UY550_.jpg";
        public static final String MEN_CHECK_RES = "https://cdn.anscommerce.com/image/tr:h-690,w-520,cm-pad_resize/data/celio/2d-images/NAFARM2_ROUGE_1.jpg";
        public static final String MEN_TANK_RES = "https://img1.g-star.com/product/c_fill,f_auto,h_630,q_80/v1586457099/D07206-124-110-Z01/g-star-raw-basic-tanktop-2-pack-white-model-front.jpg";
        public static final String MEN_OXFORD_RES = "https://sc01.alicdn.com/kf/HTB1Jd5iffjM8KJjSZFNq6zQjFXa7.jpg_350x350.jpg";
        public static final String MEN_SLIM_RES = "https://images-na.ssl-images-amazon.com/images/I/51Cap%2BrawjL._AC_UL1000_.jpg";

        public static final String MEN_SMART_WATCH_RES = "https://ae01.alicdn.com/kf/HTB1UJqQLxTpK1RjSZR0q6zEwXXaI/Q9-Sport-Smart-Watch-15-Days-Work-Message-Display-Multi-Sport-Model-Heart-Rate-Waterproof-Women.jpg";
        public static final String MEN_DIGITAL_RES = "https://www.micahtec.com/wp-content/uploads/2020/02/1815-b88b6f.jpg";
         public static final String MEN_WATCHES_RES = "https://ae01.alicdn.com/kf/HTB17d9TmQ7mBKNjSZFyq6zydFXag/2019-Mens-Watches-AGELOCER-Swiss-Brand-Luxury-Saphire-Power-Reserve-Wristwatch-Leather-Strap-Male-Clock-watch.jpg_q50.jpg";

        public static final String WOMEN_DIGITAL_RES = "https://images-na.ssl-images-amazon.com/images/I/71LVIC%2B2kGL._AC_UX522_.jpg";
        public static final String WOMEN_SMART_RES = "https://forthesmarthome.com/wp-content/uploads/2017/10/Michael-Kors-Sofie-Best-Smartwatches-for-Women.jpg";
        public static final String WOMEN_WATCHES_RES = "https://www.stuhrling.com/wp-content/uploads/womens-watch-banner.jpg";

        public static final String MEN_SHORTS_SWIM_RES = "https://www.iciw.com/bilder/artiklar/zoom/10397-092_1.jpg";
        public static final String MEN_CO_ORD_SWIM_RES = "https://images.asos-media.com/products/asos-design-co-ord-swim-shorts-in-multi-coloured-stripe-short-length/14028780-1-multi?$n_480w$&wid=476&fit=constrain";
        public static final String MEN_PLUS_SWIM_RES = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTqBJ_k6Dg4LBHbXufOQ-6hsXS9Gjwo-DBCH-PP18fcA3wHM0Tc&usqp=CAU";

        public static final String WOMEN_BIKINI_RES = "https://ae01.alicdn.com/kf/HTB1GFvBRgHqK1RjSZFPq6AwapXaW/Trend-Plus-Size-2XL-Swimsuit-Women-Sexy-Bikini-Swimwear-Women-Bikini-Push-Up-Brazilian-Beach-Bathing.jpg";
        public static final String WOMEN_ONE_PIECE_RES = "https://i.pinimg.com/736x/93/61/6a/93616a54b232233777faf3fa70b1356b.jpg";
        public static final String WOMEN_SWIMMWEAR_RES = "https://i.pinimg.com/originals/97/f5/d0/97f5d0d7d08686f8d5b278b2d048a125.jpg";

        public static final String LIKED = "Liked";
        public static final String UNLIKED = "Unliked";
    }

    public static class Functions {

        public static void showLikesListDialog(Context context, ArrayList<LikedUser> liked_items) {

            Dialog dialog = new Dialog(context);
            LikesListAdapter likedListAdapter =
                    new LikesListAdapter(dialog.getContext(), R.layout.likes_list_item, liked_items);
            likedListAdapter.notifyDataSetChanged();

            dialog.setContentView(R.layout.likes_list_dialog);
            TextView header = dialog.findViewById(R.id.likes_header);
            String _header_text = "People who liked this item";
            header.setText(_header_text);

            ListView listView = dialog.findViewById(R.id.likes_list);

            header.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.
                    getDrawable(dialog.getContext(), R.drawable.ic_thumb_up_seleste), null, null, null);
            header.setCompoundDrawablePadding(20);

            listView.setAdapter(likedListAdapter);

            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setElevation(25);
            dialog.getWindow().setAllowEnterTransitionOverlap(true);
            dialog.getWindow().setAllowReturnTransitionOverlap(true);
            dialog.show();
        }

        public static void showUnlikesListDialog(Context context, ArrayList<LikedUser> unliked_items) {

            Dialog dialog = new Dialog(context);
            LikesListAdapter unlikedListAdapter = new LikesListAdapter(dialog.getContext(),
                    R.layout.likes_list_item, unliked_items);
            unlikedListAdapter.notifyDataSetChanged();

            dialog.setContentView(R.layout.likes_list_dialog);
            TextView header = dialog.findViewById(R.id.likes_header);
            String _header_text = "People who disliked this item";
            header.setText(_header_text);

            ListView listView = dialog.findViewById(R.id.likes_list);

            header.setCompoundDrawablesRelativeWithIntrinsicBounds(ContextCompat.
                    getDrawable(dialog.getContext(),
                            R.drawable.ic_thumb_down_pink), null, null, null);
            header.setCompoundDrawablePadding(20);

            listView.setAdapter(unlikedListAdapter);

            Objects.requireNonNull(dialog.getWindow()).
                    setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        public static void showSnackbar(View view, String text, Context context, int drawableRes) {
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
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

        public static void GlidePicture(Context context, String imageUrl, View view) {

            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
            circularProgressDrawable.setArrowDimensions(10,10);
            circularProgressDrawable.setCenterRadius(50);
            circularProgressDrawable.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
            circularProgressDrawable.setStrokeWidth(7);
            circularProgressDrawable.start();

            if (view instanceof CircleImageView) Glide.with(context).
                    load(imageUrl).placeholder(circularProgressDrawable).
                    transition(withCrossFade(350)).
                    into((CircleImageView) view);
            else
                Glide.with(context).load(imageUrl).placeholder(circularProgressDrawable).
                        transition(withCrossFade(350)).
                        into((ImageView) view);
        }

        public static void buy(Context context, String site_link) {
            if (site_link == null) return;
            if (!site_link.startsWith("http://") && !site_link.startsWith("https://"))
                site_link = ("http://" + site_link);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site_link));
            context.startActivity(browserIntent);
        }

        public static void fullscreen(Context context, Intent intent, ArrayList<Pair<View, String>> pairs) {

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

        public static void sellerProfile(Context context, String sellerId, ArrayList<Pair<View, String>> pairs) {
            Intent intent = new Intent(context, CompanyProfileActivity.class);
            intent.putExtra("id", sellerId);
            intent.putExtra("customer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

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

        public static Pair<Integer, Integer> getCategoryNum(String gender, String sub_cat, String item_type) {
            switch (item_type) {
                case BAG:
                    return getBag(sub_cat);
                case JEANS:
                    return getJeans(gender, sub_cat);
                case SHOES:
                    return getShoes(gender, sub_cat);
                case DRESS:
                    return getDress(sub_cat);
                case SHIRT:
                    return getShirt(gender, sub_cat);
                case WATCH:
                    return getWatch(gender, sub_cat);
                case SUNGLASSES:
                    return getSunglasses(gender, sub_cat);
                case JACKETS:
                    return getJackets(gender, sub_cat);
                case JEWELLERY:
                    return getJewellery(gender, sub_cat);
                case ACCESSORIES:
                    return getAccessories(gender, sub_cat);
                case LINGERIE:
                    return getLingerie(gender, sub_cat);
                case SWIMWEAR:
                    return getSwimwear(sub_cat);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getJeans(String gender, String sub_cat) {
            switch (sub_cat) {
                case "jeggings":
                    return new Pair<>(19057, 1);
                case "slim":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(15157, 2) : new Pair<>(5054, 1);
                case "straight":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(3630, 1) : new Pair<>(5052, 1);
                case "ripped":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(19823, 3) : new Pair<>(16463, 1);
                case "high-waist":
                    return new Pair<>(15159, 6);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(10769, 1) : new Pair<>(5403, 1);
            }
        }

        private static Pair<Integer, Integer> getShoes(String gender, String sub_cat) {
            switch (sub_cat) {
                case "boots":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(6455, 1) : new Pair<>(5774, 1);
                case "heels":
                    return new Pair<>(6461, 1);
                case "sliders":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17170, 1) : new Pair<>(17514, 1);
                case "loafers":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(13692, 1) : new Pair<>(11247, 1);
                case "sandals":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17170, 1) : new Pair<>(6593, 1);
                case "trainers":
                    return Pair.create(6456, 12);
                case "flat-shoes":
                    return Pair.create(6459, 3);
                case "slippers":
                    return Pair.create(6457, 3);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getDress(String sub_cat) {
            switch (sub_cat) {
                case "evening":
                    return new Pair<>(8857, 25);
                case "jumper":
                    return new Pair<>(12686, 2);
                case "party":
                    return new Pair<>(11057, 25);
                case "maxi":
                    return new Pair<>(9979, 16);
                case "midi":
                    return new Pair<>(12899, 22);
                case "wedding":
                    return new Pair<>(13934, 22);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getShirt(String gender, String sub_cat) {
            switch (sub_cat) {
                case "hoodies":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17426, 3) : new Pair<>(15427, 10);
                case "sweatshirts":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17427, 3) : new Pair<>(15426, 10);
                case "blouses":
                    return new Pair<>(11318, 13);
                case "t-shirts":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(4718, 22) : new Pair<>(20778, 20);
                case "check":
                    return new Pair<>(12299, 1);
                case "denim":
                    return new Pair<>(13024, 2);
                case "oxford":
                    return new Pair<>(14478, 2);
                case "camis":
                    return new Pair<>(15202, 3);
                case "summer-top":
                    return new Pair<>(28018, 6);
                case "slim-fit":
                    return new Pair<>(21884, 4);
                case "evening":
                    return new Pair<>(11320, 3);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getWatch(String gender, String sub_cat) {
            switch (sub_cat) {
                case "smart":
                    return new Pair<>(26217, 1);
                case "digital":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11328, 1) : new Pair<>(29087, 1);
                case "stylish":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(5088, 2) : new Pair<>(19855, 3);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getSunglasses(String gender, String sub_cat) {
            switch (sub_cat) {
                case "round":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17433, 2) : new Pair<>(19271, 2);
                case "cat-eye":
                    return new Pair<>(17432,1);
                case "square":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(15192, 2) : new Pair<>(21043, 2);
                case "aviator":
                    return gender.equals(CustomerMacros.MEN) ? new Pair<>(15191, 1) : null;
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getJackets(String gender, String sub_cat) {
            switch (sub_cat) {
                case "jacket":
                    return new Pair<>(11894, 7);
                case "coat":
                    return new Pair<>(11893, 3);
                case "teddy":
                    return new Pair<>(29374, 1);
                case "leather":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11895, 1) : new Pair<>(11760, 1);
                case "biker":
                    return new Pair<>(12340, 1);
                case "trench":
                    return new Pair<>(11902, 1);
                case "denim":
                    return new Pair<>(11908, 3);
                case "winter":
                    return new Pair<>(29375, 1);
                case "overcoat":
                    return new Pair<>(20990, 1);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(28643, 1) : new Pair<>(28642, 1);
                //puffer
            }
        }

        private static Pair<Integer, Integer> getJewellery(String gender, String sub_cat) {
            switch (sub_cat) {
                case "ring":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11407, 2) : new Pair<>(13834, 3);
                case "anklet":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(29193, 1) : new Pair<>(29194, 1);
                case "earrings":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11409, 6) : new Pair<>(13837, 1);
                case "necklace":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11408, 4) : new Pair<>(13836, 3);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11410, 1) : new Pair<>(13835, 1);
            }
        }

        private static Pair<Integer, Integer> getSwimwear(String sub_cat) {
            switch (sub_cat) {
                case "bikini":
                    return new Pair<>(10117, 20);
                case "one-piece":
                case "swimsuit":
                case "swimwear":
                    return new Pair<>(10118, 4);
                default:
                    return new Pair<>(13210, 10);
                //mens
            }
        }

        private static Pair<Integer, Integer> getBag(String sub_cat) {
            switch (sub_cat) {

                case "wallets":
                    return new Pair<>(11304, 1);
                case "clutches":
                    return new Pair<>(11305, 1);
                case "shoulder":
                    return new Pair<>(11307, 1);
                case "rucksacks":
                    return new Pair<>(12496, 2);
                case "crossbody":
                    return new Pair<>(15121, 3);
                case "beach":
                    return new Pair<>(15122, 1);
                case "bum":
                    return new Pair<>(21804, 1);
                case "tote":
                    return new Pair<>(15142, 1);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getAccessories(String gender, String sub_cat) {
            switch (sub_cat) {
                case "belts":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(6448, 2) : new Pair<>(6474, 3);
                case "hats":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(6449, 3) : Pair.create(6517, 1);
                case "scarves":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(6452, 1) : Pair.create(6518, 1);
                case "sport":
                    return new Pair<>(27162, 1);
                case "ties":
                    return Pair.create(6520, 1);
                case "face masks":
                    return gender.equals(CustomerMacros.WOMEN) ? Pair.create(50035, 1) : Pair.create(50036, 1);
                default:
                    return null;
            }
        }

        private static Pair<Integer, Integer> getLingerie(String gender, String sub_cat) {
            switch (sub_cat) {
                case "underwear":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(7724, 1) : Pair.create(20317, 5);
                case "socks":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(7657, 3) : Pair.create(16329, 4);
                case "bras":
                    return new Pair<>(6576, 5);
                case "shapewear":
                    return new Pair<>(6579, 1);
                case "pyjamas":
                    return new Pair<>(18099, 3);
                case "thongs":
                    return Pair.create(26392, 5);
                default:
                    return null;
            }
        }

        public static String translateSubCategoryToTerminalX(String item_sub_category) {
            switch (item_sub_category) {
                case "mini":
                    return "mini";
                case "maxi":
                    return "maxi";
                case "midi":
                    return "midi";
                case "bikini":
                    return "bikini";
                case "one-piece":
                    return "one-piece";
                case "swimwear":
                    return "swimwear";
                case "hoodies":
                    return "shirts";
                case "t-shirts":
                    return "tshirts";
                case "tanks":
                    return "tank-tops";
                case "oxford":
                    return "dress-shirts";
                case "sweatshirts":
                    return "sport-tops";
                case "leotards":
                    return "leotard";
                case "trainers":
                    return "sneakers";
                case "flat shoes":
                    return "flat-shoes";
                case "slippers":
                    return "נעלי-בית";
                case "boots":
                    return "boots";
                case "heels":
                    return "heels";
                case "sandals":
                case "sliders":
                    return "sandals";
                case "belts":
                    return "belts";
                case "hats":
                    return "hats";
                case "scarves":
                    return "scarves";
                case "sport":
                    return "sports";
                case "jacket":
                    return "jackets";
                case "coat":
                    return "coats";
                case "face masks":
                case "underwear":
                    return "underwear";
                case "bras":
                    return "bras";
                case "pyjamas":
                    return "pyjamas";
                case "shapewear":
                    return "shapewear";
                case "socks":
                    return "tights-socks";
                default:
                    return null;
            }
        }

        public static String translateCategoryTo247(String item_type, String gender) {

            if (gender.equals(CustomerMacros.WOMEN)) {
                switch (item_type) {
                    case JEANS:
                        return "bgdim/gi-nsim";
                    case BAG:
                        return "tiqim";
                    case SHOES:
                        return "neliim";
                    case SHIRT:
                    case DRESS:
                        return "bgdim";
                    case SWIMWEAR:
                        return "bgdim/bgdi-im";
                    case JACKETS:
                        return "bgdim/covers";
                    case JEWELLERY:
                        return "accessories/tkwitim";
                    case ACCESSORIES:
                        return "accessories";
                    default:
                        return null;
                }
            } else {
                switch (item_type) {
                    case JEANS:
                        return "bgdim/gi-nsim";
                    case BAG:
                        return "tiqim";
                    case SHOES:
                        return "neliim";
                    case SHIRT:
                        return "bgdim";
                    case SWIMWEAR:
                        return "bgdim/bgdi-im";
                    case JACKETS:
                        return "bgdim/covers";
                    case JEWELLERY:
                        return "accessories/tkwitim";
                    case ACCESSORIES:
                        return "aqssvriz";
                    case LINGERIE:
                        return "bgdim/grbiim-vthtvnim";
                    default:
                        return null;
                }
            }
        }

        public static String translateCategoryToCastro(String category, String sub_cat, String gender) {
            switch (category) {
                case BAG:
                    return "bags_and_wallets";
                case DRESS:
                    return "dresses";
                case SHIRT:
                    return "tops";
                case JEANS:
                    return "jeans";
                case SWIMWEAR:
                    return "swimwear";
                case SHOES:
                    return "shoes";
                case JACKETS:
                    return "jackets_and_coats";
                case JEWELLERY:
                    return "accessories";
                case ACCESSORIES:
                     if(gender.equals(CustomerMacros.WOMEN)) {
                         return sub_cat.equals("face masks") ? "face_masks" : "accessories";
                     }
                     else{
                         return sub_cat.equals("face masks") ? "categories/face_mask" : "accessories";
                     }
                case LINGERIE:
                    return gender.equals(CustomerMacros.WOMEN) ? "langerie" : "underwear_and_socks";
                default:
                    return null;
            }
        }

        public static String translateCategoryToTerminalX(String gender, String category) {
            switch (category) {
                case BAG:
                    return "accessories/bags";
                case DRESS:
                    return "dresses";
                case SHIRT:
                    return "tops";
                case JEANS:
                    return "pants-skirts/jeans";
                case SWIMWEAR:
                    return "swimsuit";
                case SHOES:
                    return "shoes";
                case JACKETS:
                    return "jackets-coats";
                case JEWELLERY:
                    return "accessories/jewellery";
                case SUNGLASSES:
                    return "accessories/sun-glasses";
                case ACCESSORIES:
                    return "accessories";
                case LINGERIE:
                    return gender.equals(CustomerMacros.WOMEN) ? "lingerie" : "underwear";
                default:
                    return null;
            }
        }

        public static Integer translateSubCategoryToCastro(String item_sub_category) {
            switch (item_sub_category) {
                case "mini":
                    return 2039;
                case "maxi":
                    return 2041;
                case "midi":
                    return 2040;
                case "jumper":
                    return 1976;
                case "slim":
                case "straight":
                case "skinny":
                case "high-waist":
                    return 2272;
                case "t-shirts":
                    return 1980;
                case "oxford":
                    return 1964;
                case "summer-top":
                    return 1965;
                case "jacket":
                    return 1961;
                case "heels":
                    return 2011;
                case "flat-shoes":
                    return 2017;
                case "sandals":
                case "sliders":
                    return 2252;
                case "trainers":
                    return 2018;
                case "bikini":
                    return 2002;
                case "one-piece":
                    return 2003;
                case "swimwear":
                    return 1991;
                case "tote":
                    return 1998;
                case "backpack":
                    return 2001;
                case "underwear":
                    return 2022;
                case "bras":
                    return 2021;
                case "belts":
                    return 1996;
                case "socks":
                    return 2019;
                case "hats":
                    return 1994;
                case "scarves":
                    return 1995;
                case "ring":
                case "bracelet":
                case "necklace":
                case "earrings":
                case "anklet":
                    return 2042;
                default:
                    return null;
            }
        }

        public static String translateSubCategoryTo247(String item_sub_category, String gender) {
            if (gender.equals(CustomerMacros.WOMEN)) {
                switch (item_sub_category) {
                    case "heels":
                        return "neliim2";
                    case "trainers":
                        return "neliim1";
                    case "sandals":
                    case "slippers":
                    case "sliders":
                        return "kpkpim";
                    case "socks":
                        return "grbivnim";
                    case "belts":
                        return "hgvrvt";
                    case "backpack":
                        return "tiqim2";
                    case "clutch":
                    case "bum":
                    case "cross body":
                        return "pavc-im";
                    case "t-shirts":
                        return "t-shirts1";
                    case "summer-top":
                        return "t-shirts";
                    case "hoodies":
                    case "camis":
                    case "evening":
                        return "hvlcvt-arig";
                    case "pyjamas":
                        return "pig-mvt";
                    case "mini":
                    case "midi":
                    case "maxi":
                        return "wmlvt";
                    default:
                        return null;
                }
            }
            else {
                switch (item_sub_category) {
                    case "trainers":
                        return "sniqrs";
                    case "tank":
                        return "gvpivt";
                    default:
                        return "";
                }
            }
        }

        public static String translateCategoryToAldo(String item_gender, String category) {
            if (item_gender.equals(CustomerMacros.WOMEN)) {
                switch (category) {
                    case BAG:
                        return "handbag";
                    case SHOES:
                        return "footwear";
                    case WATCH:
                        return "accessories/watches";
                    case ACCESSORIES:
                    case JEWELLERY:
                        return "accessories";
                    case SUNGLASSES:
                        return "accessories/sunglasses";
                    default:
                        return null;
                }
            } else {
                switch (category) {
                    case SHOES:
                        return "footwear";
                    case WATCH:
                        return "handbags-and-accessories/watches";
                    case ACCESSORIES:
                    case JEWELLERY:
                        return "handbags-and-accessories";
                    case SUNGLASSES:
                        return "handbags-and-accessories/sunglasses";
                    default:
                        return null;
                }
            }
        }

        public static String translateSubCategoryToAldo(String item_gender, String item_sub_category) {
            if (item_gender.equals(CustomerMacros.WOMEN)) {
                switch (item_sub_category) {
                    case "heels":
                        return "shoes/mid-heels";
                    case "trainers":
                        return "shoes/sneakers";
                    case "sandals":
                    case "slippers":
                        return "sandals";
                    case "boots":
                        return "boots";
                    case "flat shoes":
                    case "sliders":
                        return "shoes/low-heel-flat-shoes";
                    case "socks":
                        return "socks-legwear";
                    case "bum":
                    case "tote":
                    case "leather":
                        return "shoulder-bags-totes";
                    case "clutch":
                    case "cross body":
                        return "clutches-evening-bags";
                    case "wallets":
                        return "wallets";
                    case "ring":
                        return "rings";
                    case "bracelet":
                        return "bracelets";
                    case "earrings":
                        return "earrings";
                    case "necklace":
                        return "necklaces";
                    case "belts":
                        return "belts";
                    default:
                        return "";
                }
            } else {
                switch (item_sub_category) {
                    case "trainers":
                        return "shoes/sneakers";
                    case "sandals":
                    case "sliders":
                    case "slippers":
                        return "sandals";
                    case "loafers":
                        return "shoes/casual-shoes";
                    case "elegant":
                        return "shoes/dress-shoes";
                    case "boots":
                        return "boots";
                    case "socks":
                        return "socks";
                    case "ring":
                        return "rings";
                    case "bracelet":
                        return "bracelets";
                    case "earrings":
                        return "earrings";
                    case "necklace":
                        return "necklaces";
                    case "belts":
                        return "belts";
                    default:
                        return "";
                }
            }
        }

        public static String translateCategoryToRenuar(String category) {
            switch (category) {
                case BAG:
                    return "accessories/bags";
                case DRESS:
                    return "bgdim/dresses";
                case SHIRT:
                    return "bgdim/";
                case JEANS:
                    return "bgdim/jeans";
                case SWIMWEAR:
                    return "bgdim/swimwear";
                case SHOES:
                    return "shoes";
                case JACKETS:
                    return "bgdim/coatsandjackets";
                case ACCESSORIES:
                    return "accessories";
                default:
                    return null;
            }
        }

        public static String translateSubCategoryToRenuar(String item_gender, String item_sub_category) {
            if (item_gender.equals(CustomerMacros.WOMEN)) {
                switch (item_sub_category) {
                    case "sandals":
                        return "sndlim";
                    case "trainers":
                        return "filter/sniqrs";
                    case "socks":
                        return "grbivnim";
                    case "backpack":
                        return "filter/tiq_gb";
                    case "clutch":
                    case "cross body":
                        return "filter/tiq_qtn";
                    case "wallets":
                        return "filter/arnq";
                    case "underwear":
                        return "zvg-hbilvt-thtvnim-b-49-90";
                    case "midi":
                        return "filter/midi_";
                    case "maxi":
                        return "filter/maxi_";
                    case "mini":
                        return "filter/mini_";
                    case "tank":
                        return "shirts/filter/gvpih";
                    case "sweatshirts":
                    case "blouses":
                        return "shirts/filter/tiwirt";
                    case "camis":
                        return "shirts/filter/qrvp";
                    case "t-shirts":
                        return "shirts1/filter/tiwirt";
                    case "straight":
                        return "filter/iwr";
                    case "super-skini":
                    case "skini":
                        return "filter/sqini";
                    case "slim":
                        return "filter/bviprnd";
                    case "heels":
                        return "filter/nel_eqb";
                    case "flat shoes":
                        return "filter/sndl_wtvh";
                    case "sliders":
                        return "filter/kpkp";
                    default:
                        return "";
                }
            } else {
                switch (item_sub_category) {
                    case "trainers":
                        return "shoes/sneakers";
                    case "sandals":
                    case "sliders":
                    case "slippers":
                        return "sandals";
                    case "loafers":
                        return "shoes/casual-shoes";
                    case "elegant":
                        return "shoes/dress-shoes";
                    case "boots":
                        return "boots";
                    case "socks":
                        return "socks";
                    case "ring":
                        return "rings";
                    case "bracelet":
                        return "bracelets";
                    case "earrings":
                        return "earrings";
                    case "necklace":
                        return "necklaces";
                    case "belts":
                        return "belts";
                    default:
                        return "";
                }
            }
        }

        public static String translateCategoryToHoodies(String item_gender, String item_type, String sub_cat) {
            if(item_gender.equals(CustomerMacros.WOMEN)) {
                switch (item_type) {
                    case BAG:
                        return "accessories/bags";
                    case DRESS:
                        return "dresses";
                    case SHIRT:
                        return sub_cat.equals("sweatshirts") ? "sweatshirts" : "shirts";
                    case JEANS:
                        return "jeans";
                    case SWIMWEAR:
                        return "swimsuit";
                    case ACCESSORIES:
                        return "accessories";
                    case LINGERIE:
                        return "underwear";
                    default:
                        return null;
                }
            }
            else{
                switch (item_type) {
                    case SHIRT:
                        return "shirts";
                    case SWIMWEAR:
                        return "swimwear";
                    case ACCESSORIES:
                        return "accessories";
                    case LINGERIE:
                        return "underwear";
                    default:
                        return null;
                }
            }
        }

        public static String translateSubCategoryToHoodies(String item_gender, String item_sub_category) {
            if (item_gender.equals(CustomerMacros.WOMEN)) {
                switch (item_sub_category) {
                    case "socks":
                        return "socks";
                    case "hats":
                        return "hats";
                    case "backpack":
                        return "bags";
                    case "underwear":
                        return "underpants";
                    case "bras":
                        return "geese";
                    case "midi":
                        return "midi";
                    case "maxi":
                        return "maxi";
                    case "mini":
                        return "mini";
                    case "tank":
                        return "top_thank";
                    case "t-shirts":
                        return "short";
                    case "leotards":
                        return "leotards";
                    case "straight":
                    case "slim":
                        return "long";
                    case "face masks":
                        return "מסכות-פנים";
                    default:
                        return "";
                }
            }
            else {
                switch (item_sub_category) {
                    case "socks":
                        return "socks";
                    case "tank":
                        return "tank_tops";
                    case "t-shirt":
                        return "short_shirts";
                    default:
                        return "";
                }
            }
        }

        public static String translateCategoryToShein(String item_gender, String item_type, String item_sub_category) {
            if(item_gender.equals(CustomerMacros.WOMEN)) {
                switch (item_type) {
                    case BAG:
                        switch (item_sub_category){
                            case "bum":
                                return "Bum-Bags-c-2156";
                            case "clutch":
                                return "Evening-Clutch-c-2153";
                            case "backpack":
                                return "Backpack-c-2151";
                            case "wallet":
                                return "Purses-c-2154";
                            case "cross body":
                                return "Crossbody-c-2152";
                            case "tote":
                                return "Shoulder-Tote-Bag-c-1764";
                        }
                    case DRESS:
                        switch (item_sub_category){
                            case "midi":
                                return "Midi-Dresses-sc-00100629";
                            case "maxi":
                                return "Maxi-Dresses-sc-00100346";
                            case "mini":
                                return "Short-Dresses-sc-00100608";
                            default:
                                return null;
                        }
                    case SHIRT:
                        switch (item_sub_category){
                            case "tanks":
                                return "Tank-Tops-Camis-c-1779";
                            case "t-shirts":
                                return "T-Shirts-c-1738";
                            case "hoodies":
                                return "Hooded-Sweatshirts-sc-00104082";
                            case "sweatshirts":
                                return "Sweatshirts-c-1773";
                            default:
                                return null;
                        }
                    case JEANS:
                        return "Jeans-c-1934";
                    case SWIMWEAR:
                        return "Beachwear-c-2039";
                    case SHOES:
                        switch (item_sub_category) {
                            case "trainers":
                                return "Sneakers-c-1913";
                            case "flat shoes":
                                return "Flats-c-1881";
                            case "slippers":
                                return "Slippers-c-1929";
                            case "boots":
                                return "Boots-c-1748";
                            case "heels":
                                return "Pumps-c-1750";
                            case "sandals":
                                return "Sandals-c-1751";
                            default:
                                return null;
                        }
                    case JACKETS:
                        if ("jacket".equals(item_sub_category)) {
                            return "Jackets-c-1776";
                        }
                        return "Coats-Jackets-c-2037";
                    case JEWELLERY:
                        switch (item_sub_category) {
                            case "earrings":
                                return "Earrings-c-1757";
                            case "anklet":
                                   return  "Body-Jewelry-c-1908.html?&tag_ids=400075";
                            case "necklace":
                                return "Necklaces-c-1755";
                            case "ring":
                                return "Rings-c-1759";
                            case "bracelet":
                                return "Bracelets-c-1758";
                            default:
                                return null;
                        }
                    case SUNGLASSES:
                        return "Sunglasses-c-1770";
                    case ACCESSORIES:
                        switch (item_sub_category) {
                            case "socks":
                                return "Socks-Tights-c-1899";
                            case "hats":
                                return "Hats-c-1772";
                            case "scarves":
                                return "Scarves-c-1872";
                            case "belts":
                                return "Belts-c-1875";
                            default:
                                return null;
                        }
                    case LINGERIE:
                        switch (item_sub_category){
                            case "underwear":
                                return "Panties-c-2205";
                            case "bras":
                                return "Bras-Bralettes-c-2203";
                            case "bra & panty":
                                return"Bra-Panty-Sets-c-2270";
                            default:
                                return null;
                        }
                    default:
                        return null;
                }
            }
            else{
                switch (item_type) {
                    case SHIRT:
                        switch (item_sub_category) {
                            case "denim":
                                return  "Denim-Shirts-c-1987";
                            case "sweatshirts":
                                return "Sweatshirts-c-1773";
                            default:
                                return null;
                        }
                    case JEANS:
                        switch (item_sub_category){
                            case "skinny":
                                return "Skinny-Jeans-sc-00100497";
                            case "straight":
                                return "Straight-Jeans-sc-00126916";
                            case "slim":
                                    return "Jeans-c-1989";
                            default:
                                return null;
                        }
                    case SWIMWEAR:
                    if (item_sub_category.equals("shorts"))
                        return "Swimwear-sc-00810098";
                    else
                        return null;
                    case SHOES:
                        return "Shoes-c-2089";
                    case JACKETS:
                        return item_sub_category.equals("denim") ? "Denim-Jackets-c-1986" : "Coats-Jackets-c-1983";
                    case JEWELLERY:
                        switch (item_sub_category){
                            case "trainers":
                                return "Sneakers-c-2093";
                            case "slippers":
                                return "Slippers-c-2330";
                            case "boots":
                                return "Boots-c-1748";
                            case "loafers":
                                return "Loafers-c-2092";
                            case "sliders":
                                return "Flip-Flops-Slides-c-2331";
                            case "sandals":
                                return "Sandals-c-2095";
                            default:
                                return null;
                        }
                    case SUNGLASSES:
                        return "Sunglasses-c-2137";
                    case ACCESSORIES:
                        switch (item_sub_category){
                            case "socks":
                                return "Socks-c-2145";
                            case "hats":
                                return "Hats-Gloves-c-2136";
                            case "scarves":
                                return "Scarves-c-2139";
                            case "belts":
                                return "Belts-Suspenders-c-2138";
                            default:
                                return null;
                        }
                    case LINGERIE:
                        if ("underwear".equals(item_sub_category)) {
                            return "Underwear-c-1985";
                        }
                        else
                            return null;
                    default:
                        return null;
                }
            }
        }

        public static Pair<String,Boolean> translateSubCategoryToShein(String item_gender, String item_sub_category) {
            if (item_gender.equals(CustomerMacros.WOMEN)) {
                switch (item_sub_category) {
                    case "bikini":
                        return Pair.create("pdsearch/bikini",true);
                    case "one-piece":
                        return Pair.create("?&tag_ids=30002193",true);
                    case "swimwear":
                        return Pair.create("?child_id=2192",true);
                    case "teddy":
                        return Pair.create("?&tag_ids=4000584",true);
                    case "puffer":
                        return Pair.create("?&tag_ids=40001774",true);
                    case "face masks":
                        return Pair.create("pdsearch/face%20mask",true);
                    case "coat":
                        return Pair.create("?&tag_ids=70007011",true);
                    case "shapewear":
                        return Pair.create("pdsearch/shapewear/",true);
                    case "round":
                        return Pair.create("?&tag_ids=4000559",true);
                    case "square":
                        return Pair.create("?&tag_ids=40001691",true);
                    case "cat-eye":
                        return Pair.create("?&tag_ids=4000168",true);
                    case "high-waist":
                        return Pair.create("?&tag_ids=4000357",true);
                    case "ripped":
                        return Pair.create("?&tag_ids=4000554",true);
                    case "skinny":
                        return Pair.create("?&attr_ids=109_607",true);
                    case "straight":
                        return Pair.create("?&attr_ids=109_643",true);
                    default:
                        return Pair.create("",false);
                }
            }
            else {
                switch (item_sub_category) {
                    case "tank":
                        return Pair.create("?&tag_ids=4000612",true);
                    case "t-shirts":
                        return Pair.create("?&tag_ids=4000592",true);
                    case "earrings":
                        return Pair.create("?&child_id=2142",true);
                    case "necklace":
                        return Pair.create("?&child_id=2141",true);
                    case "ring":
                        return Pair.create("?&child_id=2140",true);
                    case "bracelet":
                        return Pair.create("?&child_id=2029",true);
                    case "round":
                        return Pair.create("?&tag_ids=4000559",true);
                    case "square":
                        return Pair.create("?&tag_ids=40001691",true);
                    case "biker":
                        return Pair.create("?&attr_ids=109_109", true);
                    case "puffer":
                        return Pair.create("?&attr_ids=109_1774",true);
                    case "trench":
                        return Pair.create("?&attr_ids=109_690", true);
                    default:
                        return Pair.create("",false);
                }
            }
        }
    }
}
