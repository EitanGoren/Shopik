package com.eitan.shopik;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.eitan.shopik.Company.CompanyProfileActivity;
import com.eitan.shopik.Customer.CustomerMainActivity;
import com.eitan.shopik.Customer.FullscreenImageActivity;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Macros {

    public static final String TAG = "com.eitan.shopik";
    public static final String COMPANIES = "Companies";
    public static final String DEFAULT_DESCRIPTION = "Write something here...";
    public static final String DEFAULT_YOUTUBE_VIDEO = "https://www.youtube.com/watch?v=oQbh5Kvet04";
    public static final String DEFAULT_COVER_PHOTO = "https://i.pinimg.com/originals/10/50/61/1050612a07b764970e4df8227caf8e1c.jpg";
    public static final String DEFAULT_PROFILE_IMAGE = "https://clipartart.com/images/account-profile-clipart-4.png";
    public static final String CUSTOMERS = "Customers";
    public static final String COMPANY = "Company";
    public static final String CUSTOMER = "Customer";
    public static final String ITEMS = "Items";
    public static final String API_KEY = "AIzaSyA-NCcR_U7gBNm1BN3lXKTS8wC1W-VC9fE";
    public static final String AD_MOB_INTERSTITIAL_AD_DEBUG_CODE = "ca-app-pub-3940256099942544/8691691433";
    public static final String TEST_DEVICE_ID = "7255B1C36174A2C33091060576730302";
    public static final String NATIVE_VIDEO_TEST_AD = "ca-app-pub-3940256099942544/1044960115";
    public static final String NATIVE_ADVANCED_AD = "ca-app-pub-3940256099942544/2247696110";
    public static final String FB_PLACEMENT_ID = "555191575133910_605556963430704";
    public static final String YOUTUBE_API = "https://www.googleapis.com/youtube/v3/videos?part=id%2C+snippet";
    public static final String VIDEO_LINK = "https://video.asos-media.com/products/ASOS/_media_";
    public static final String BAG = "Bag";
    public static final String SHOES = "Shoes";
    public static final String JEANS = "Jeans";
    public static final String DRESS = "Dress";
    public static final String SHIRT = "Shirt";
    public static final String SUNGLASSES = "Sunglasses";
    public static final String SWIMWEAR = "Swimwear";
    public static final String JACKETS = "Jackets";
    public static final String JEWELLERY = "Jewellery";
    public static final String WATCH = "Watch";
    public static final String NEW_SHOES = "Shoes";
    public static final String NEW_TRENDING = "TrendingNow";
    public static final String NEW_CLOTHING = "Clothing";
    public static final String WOMEN_FIRST_PIC = "https://img.freepik.com/free-photo/portrait-beautiful-caucasian-sunbathed-woman-model-transparent-white-blouse-sitting-summer-beach-blue-ocean-background_158538-9730.jpg?size=626&ext=jpg";
    public static final String WOMEN_SECOND_PIC = "https://media.gettyimages.com/photos/female-hands-lacing-running-shoes-closeup-picture-id531912454?b=1&k=6&m=531912454&s=612x612&w=0&h=U0OPbVWYvR4XkM_0uOslKgx8yg3TWGUP_99j-KOMUHk=";
    public static final String WOMEN_THIRD_PIC = "https://d356cpcjxoolwe.cloudfront.net/media/catalog/product/cache/3/image/533x704/72b4a3c89279b6295f5413414e9ad668/7/1/7110822.01.0500_d2_187982e.jpg";
    public static final String MEN_FIRST_PIC = "https://www.thefashionisto.com/wp-content/uploads/2015/07/Matt-Trethe-Matches-Fashion-Summer-Swimwear-Shoot-2015-Mens-Style-007.jpg";
    public static final String MEN_SECOND_PIC = "https://cdn.hiconsumption.com/wp-content/uploads/2019/08/Best-Mens-Sneakers-For-Summer-0-Hero.jpg";
    public static final String MEN_THIRD_PIC = "https://www.rankandstyle.com/media/products/e/eddie-bauer-2-mens-clothing-websites.jpg";
    public static final String YOUTUBE_IC = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/09/YouTube_full-color_icon_%282017%29.svg/1200px-YouTube_full-color_icon_%282017%29.svg.png";
    public static final String TWITTER_IC = "https://www.aps.edu/sapr/images/pnglot.comtwitterbirdlogopng139932.png/image";
    public static final String FACEBOOK_IC = "https://upload.wikimedia.org/wikipedia/commons/0/05/Facebook_Logo_%282019%29.png";
    public static final String WEB_IC = "https://www.freepnglogos.com/uploads/logo-website-png/logo-website-world-wide-web-svg-png-icon-download-10.png";
    public static final String INSTAGRAM_IC = "https://www.kartisyarok.co.il/wp-content/uploads/2018/09/instagram-logo-png-7.png";

    public static final double POUND_TO_ILS = 5.2482;
    public static final int SWIPES_TO_AD = 8;
    public static final int FAV_TO_AD = 15;
    public static final int SEARCH_TO_AD = 15;
    public static final int SUGGESTED_TO_AD = 15;
    public static final int NUM_OF_ADS = 15;

    public static class Providers {

        public static final String FACEBOOK = "facebook.com";
        public static final String GOOGLE = "google.com";
        public static final String PASSWORD = "password";
    }

    public static class Items {

        public static final String[] brands = {
              /*  "ASOS","ASOS Design","American Eagle","Pull&Bear","In the style","The O Dolls Collection","& Other Stories",
                "Stradivarius","Bershka","Collusion","Mango","Valentino","Birkenstock","The North Face",
                "French Connection","Topshop","Urban Bliss","River Island","Calvin Klein","FashionKilla",
                "Tommy Jeans","New Look","AllSaints","Selected Femme","G-Star","Levi's","Sinead Gorey","New Girl Order",
                "Monkey","Freddy WR.UP","Ridley","Only","Vero Moda","Weekday","Miss Selfridge","Converse",
                "Reclaimed Vintage","Mamalicious","Spanx","Liquor N Poker","Noisy May","Rivington","Simply Be",
                "Pimkie","Farleigh","PrettyLittleThing","Pieces","Adidas","Club L London","Femme Luxe","Abrand '94",
                "Oasis","Camper","Raid Damien","Simmi London","Kurt Geiger","Genuins Sara","Glamourous","Free People",
                "Warehouse","Nike","SilkSilk","Jack & Jones","boohooMAM","Burton","Armani Exchange","Replay","Dr Martens",
                "Lacoste","WALK LONDON","Mad But Magic","TFNC","STYLENNANDA","Maya","Lasula","Forever New","Wednesday's Girl",
                "Boohoo","Paper Dolls","Whistles Elis","JDY","The Ragged Priest","Keepsake Think Twice","Monki","First Base",
                "E.F.L.A","Every Cloud","Foxiedox","Motel","Chi Chi London","Love Triangle","Misha Collection","Jaded London",
                "Puma","My Accessories","Mi-Pac","Love Moschino","Santa Cruz","HUGO","Ted Baker","Ralph Lauren","Barbour Eldon",
                "Palladium","Koi","Timberland","Clarks","Jeffery West","House of Hounds","Teva","Fadey","Columbia",
                "Foxglove","Fossil","Limit","Sekonda","Marc Jacobs","Olivia Burton","Tommy Hilfiger","Versus Versace","Versace",
                "Missguided","Hollister","Napapijri Box","DKNY","FitBit","The Couture Club","Diesel","Fiorucci",
                "Rip Curl","Volcom Stone","Ellesse","Dr Denim","Public Desire","Champion","Chinatown Market",
                "Fred Perry","Abercrombie & Fitch","Daisy Street","South Beach","NA-KD","Reebok","Barbour",
                "Crooked Tongues","Casio","Rolex","Swatch","Michael Kors","Herschel","Dooper","Goorin","Vans",*/
              //MENS BRANDS
                "ASOS EDITION", "Chained & Able", "Classics 77", "DesignB London", "Icon Brand",
                "Topman", "Uncommon Souls", "WFTW","AAPE BY A BATHING APE", "Abercrombie and Fitch",
                "adidas", "AJ Morgan","Serge DeNimes", "Seven London",
                "Albam Utility", "AllSaints", "American Crew", "American Eagle", "Anatomicals",
                "Armani", "Armani Exchange", "Arnette", "Asics", "ASOS 4505", "ASOS Collection",
                "ASOS DESIGN", "ASOS EDITION", "Bench", "Berghaus","Barbour", "Apothecary 87",
                "ASOS MADE IN KENYA", "ASOS WHITE", "Avail London", "Aveda", "Babyliss", "Barber Pro",
                "Barneys Originals", "Base London", "Beauty Kitchen", "Bellfield", "Ben Sherman",
                "Bershka", "Billabong", "Birkenstock", "Bjorn Borg", "Bleach London", "Blood Brother",
                "BOSS", "Bowlcut", "Bronx", "Brooklyn Supply Co.", "Buffalo", "Bulldog", "Bumble and bumble",
                "Burton Menswear London", "Call it Spring", "Calvin Klein", "Camper", "Cantu",
                "Carhartt WIP", "Carrots", "Casio", "Caudalie", "Celio", "CeraVe", "Chained & Able",
                "Champion","Clinique","Criminal Damage","Bolongaro Trevor", "Edwin", "Element",
                "Cheap Monday", "Chinatown Market", "Choose Love", "Clarks Originals", "Classics 77",
                "COLLUSION", "Columbia", "Common People", "Converse","Creative Recreation", "Crep Protect",
                "Crocs", "Crooked Tongues", "David Beckham", "DesignB", "Deus Ex Machina", "Dickies",
                "Diesel", "DKNY", "Doers of London", "Dr Denim", "Dr Martens", "Dr. Jart+", "Dr. Oracle",
                "Due Diligence", "Duke", "Dune London", "Dunlop", "DUSK TO DAWN", "EA7", "Eastpak",
                "Elemis", "ellesse", "Emporio Armani", "Entente", "Esprit", "Estee Lauder", "Farah",
                "Fila", "Fiorucci", "Fitbit", "Fossil", "Fred Perry", "French Connection","Friend Or Faux",
                "G Star", "G.H. Bass & Co.", "Garnier", "Gianni Feraud", "Gift Republic", "Gillette",
                "Grenson", "H by Hudson", "Hanz de Fuko", "Harry Brown", "Havaianas", "Heart & Dagger",
                "Helly Hansen", "Hermano", "Herschel", "Hi-Tec", "HIIT", "Holika Holika", "Hollister",
                "House 99","Hype", "Icon Brand", "Mennace", "Good For Nothing", "Aprex Supersoft",
                "House of Hounds", "Hudson", "Huf", "HUGO", "Hugo Boss", "Hunter", "HXTN Supply",
                "J.Crew Mercantile", "Jacamo", "Jack & Jones","Jack Wills", "Jaded London", "Jeepers Peepers",
                "Jeffery West", "Johnnys Chop Shop", "Jordan", "Kangol", "Kappa", "Karl Kani",
                "Karl Lagerfeld", "Kent Brushes", "Kickers", "Koi Footwear", "Kurt Geiger", "Lab Series",
                "Lacoste", "Lambretta", "Laura Mercier", "LDN DNM", "Lee", "Lefrik", "Les (Art)ists",
                "Levi's", "Limit", "Liquor N Poker", "Lock Stock", "Love Moschino","Lyle & Scott",
                "L’Oreal Men Expert", "M.A.D Beauty", "MAC", "MasqueBAR", "matt & nat", "Mauvais",
                "Mi-Pac", "Michael Kors", "Milk It", "Mitchell & Ness", "Moss London", "Mossimo", "Mr Natty",
                "Murdock London", "Napapijri", "Native Youth", "NEOM", "New Balance", "New Era", "New Look",
                "New Love Club", "Nicce", "Nike", "Noak", "Nokwol", "Nothing Is Sacred", "Nudie Jeans Co",
                "Obey", "Office", "Oh K!", "Olaplex", "One Above Another", "Only & Sons", "Original Penguin",
                "Origins", "Paddywax", "Palladium", "Parlez", "Paul Smith", "Paul Smith Jeans","Pepe Jeans",
                "Peter Werth", "Pier One", "Polo Ralph Lauren", "PopSockets", "Pretty Green", "Produkt",
                "PS Paul Smith", "Pull&Bear", "Puma", "Quiksilver", "RAINS", "Ray-Ban", "Reclaimed Vintage",
                "Revolution", "Ringspun", "Rip N Dip", "River Island", "Rudie", "Russell Athletic",
                "Sacred Hawk", "Santa Cruz", "Reebok", "Religion", "Replay","Protest", "Oakley",
                "Schmidt's", "Schott", "Scotch & Soda", "Sekonda", "Selected Homme", "Serge DeNimes",
                "Seven Jewellery", "Sex Skateboards", "Shay and Blue", "Shelby & Sons",
                "Shiseido", "Sik Silk", "Sixth June", "Skechers", "skyn ICELAND", "Slydes", "Solid",
                "Stan Ray", "Sunday Rain", "Superdry", "Superga", "Taka Original", "Ted Baker", "Teva",
                "THE INKEY LIST", "The North Face", "The Ordinary", "The Ragged Priest", "Thumbs Up",
                "Timberland", "Tom Tailor", "Tommy Hilfiger","The Couture Club", "The Fox Tan",
                "Toms", "Tonymoly", "Topman", "True Religion", "Tux Till Dawn", "Twisted Tailor", "Typo",
                "Ugg", "Uncommon Souls","Vans", "Versace Jeans Couture", "South Beach", "Spiral", "Spitfire",
                "Under Armour", "United Colors of Benetton", "Uppercut Deluxe", "Valentino by Mario Valentino",
                "Vibe + Carter", "Viggo", "Vintage Supply", "Voi", "Volcom", "Wahl", "Walk London",
                "Weekday", "WeSC", "WFTW", "World Projects", "Wrangler", "31st State", "Castro","TERMINAL X",


                //WOMENS BRANDS

    };


        public static final String[] cuts = {
                "Petite","Maternity","Curve","Tall","Plus","plus","tall","curve","maternity","petite","Super","super",
        };

        public static final String[] styles = {
                "square","neck","off","shoulder","mini","short","sleeve","button","front","smock","tie","back",
                "drawstring","waist","kimono","polka","dot","blouson","Shoulder","Lace","Satin","pleated",
                "cami","cut","out","bodycon","wedding","shoulder","stripe","work","casual","oxford","revere",
                "cord","smart","check","floral","print","polo","Polo","mocassin","cuban","heel","loafer","edpadrilles",
                "brogue s","foldover","cross","body","coin","pouch","chain","arm","bum","quilted","purse","shoulder",
                "backpack","phone","zip","snake","ring","strap","asymetric","chunky","jelly","flatform","snake","sport","t-shirt",
                "tee","short","long","SHAHD","shahd","Shahd"
        };

        public static final String[] jeans_fit = {
                "super","comfort","skinny","mini","Baroque","slim","knee","spray","cropped","stretch",
                "classic","rips","washed","midi","maxi","relaxed","muscle","Wide","wide","Big","big",
                "carrot","plus","Plus"
        };

        static final String[] shit_words = {
                "fit","size","up",".", "#", "$", "[", "]" ,"with", "&" ,
                "design","in","t","and","asos","'farleigh'","waist","waisted","jean","jeans",
                "at","x","to","by","pu","the","tea","sunglasses","sandals","shoes","sandal","shirt","t-shirt",
                "bag","watch","swim","boots","jacket","dress","swimsuit",
        };

        public static String[] getList(String sub_category) {

            switch(sub_category){
                case "swimsuit":
                    return Arrays.SWIMSUIT;
                case "ripped":
                    return Arrays.RIPPED;
                case "slim-fit":
                    return Arrays.SLIM;
                case "jeggings":
                    return Arrays.JEGGINGS;
                case "oversized":
                    return Arrays.SUPER_SIZE;
                case "maternity":
                    return Arrays.MATERNITY;
                case "sandals":
                    return Arrays.SANDALS;
                case "leather":
                    return Arrays.LEATHER;
                case "running":
                    return Arrays.RUNNING;
                case "mini":
                    return Arrays.MINI;
                case "midi":
                    return Arrays.MIDI;
                case "skinny":
                    return Arrays.SKINNY;
                case "ring":
                    return Arrays.RINGS;
                case "loafers":
                    return Arrays.LOAFERS;
                case "necklace":
                    return Arrays.NECKLACE;
                case "sundress":
                    return Arrays.SUNDRESS;
                case "slim":
                    return Arrays.SLIM;
                case "straight":
                    return Arrays.STRAIGHT;
                case "puffer":
                    return Arrays.PUFFER;
                case "super-skinny":
                    return Arrays.SUPER_SKINNY;
                case "bracelet":
                    return Arrays.BRACELET;
                case "round":
                    return Arrays.ROUND;
                case "earrings":
                    return Arrays.EARRINGS;
                case "square":
                    return Arrays.SQUARE;
                case "classic":
                    return Arrays.CLASSIC;
                case "cat-eye":
                    return Arrays.CAT_EYE;
                case "boat":
                    return Arrays.BOAT;
                case "espadrilles":
                    return Arrays.ESPADRILLES;
                case "tank":
                    return Arrays.TANK;
                case "t-shirt":
                case "t-shirts":
                    return Arrays.T_SHIRT;
                case "oxford":
                    return Arrays.OXFORD;
                case "muscle":
                    return Arrays.MUSCLE;
                case "smart":
                    return Arrays.SMART;
                case "mesh":
                    return Arrays.MESH;
                case "digital":
                    return Arrays.DIGITAL;
                case "shorts":
                    return Arrays.SHORTS;
                case "co-ord":
                    return Arrays.CO_ORD;
                case "long":
                    return Arrays.LONG;
                case "bum":
                    return Arrays.BUM;
                case "clutch":
                    return Arrays.CLUTCH;
                case "backpack":
                    return Arrays.BACKPACK;
                case "cross body":
                    return Arrays.CROSS;
                case "tote":
                    return Arrays.TOTE;
                case "petite":
                    return Arrays.PETITE;
                case "denim":
                    return Arrays.DENIM;
                case "coat":
                    return Arrays.COAT;
                case "bomber":
                    return Arrays.BOMBER;
                case "boots":
                    return Arrays.BOOTS;
                case "heels":
                    return Arrays.HEELS;
                case "sliders":
                    return Arrays.SLIDERS;
                case "anklet":
                    return Arrays.ANKLET;
                case "bikini":
                    return Arrays.BIKINI;
                case "stretch":
                    return Arrays.STRETCH;
                case "trainers":
                    return Arrays.TRAINERS;
                case "trench":
                    return Arrays.TRENCH;
                case "evening":
                    return Arrays.EVENING;
                case "wedding":
                    return Arrays.WEDDING;
                case "jumper":
                    return Arrays.JUMPER;
                case "hoodies":
                    return Arrays.HOODIES;
                case "biker":
                    return Arrays.BIKER;
                case "high-waist":
                    return Arrays.HIGH_WAIST;
                case "party":
                    return Arrays.PARTY;
                case "sweatshirts":
                    return Arrays.SWEATSHIRTS;
                case "blouses":
                    return Arrays.BLOUSES;
                case "camis":
                    return Arrays.CAMIS;
                case "summer-top":
                    return Arrays.SUMMER_TOP;
                case "aviator":
                    return Arrays.AVIATOR;
                case "winter":
                    return Arrays.WINTER;
                case "teddy":
                    return Arrays.TEDDY;
                case "jacket":
                    return Arrays.JACKET;
                case "overcoat":
                    return Arrays.OVERCOAT;
            }
            return null;
        }

        public static String[] getAllItemTypes(){
            return new String[]{BAG,SHOES,JEANS,DRESS,SHIRT,SUNGLASSES,SWIMWEAR,JACKETS,JEWELLERY,WATCH};
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

        public static final String TOTE_RES = "https://cdn.shopify.com/s/files/1/0166/6656/products/Oh_honey_tote022.jpg?v=1563184080";
        public static final String CROSS_RES = "https://cdn-com.thelittlegreenbag.com/upload/artikelen/Matt-Nat/ss20/bee-dwell-crossbody-bag/Matt-Nat-Bee-Dwell-Crossbody-bag-SS20-crossbody-tas-mojito-crossbody-bag-model-600.jpg";
        public static final String LEATHER_RES = "https://www.danburymint.co.uk/wp-content/uploads/2017/10/ce211ff0-be9d-4dd3-b911-fd158deaa6a0.jpg";
        public static final String BACKPACK_RES = "https://i.pinimg.com/236x/67/4f/a0/674fa0e41bcc893f8a4112c288ca9442.jpg";
        public static final String BUM_RES = "https://www.mohito.com/media/catalog/product/cache/1200/a4e40ebdc3e371adff845072e1c73f37/X/Y/XY082-99X-020_1.jpg";
        public static final String CLUTCH_RES = "https://ae01.alicdn.com/kf/HTB14qFGLpXXXXX.aXXXq6xXFXXXb/women-genuine-leather-evening-clutch-bag-stone-pattern-flap-handbag-for-female-wristlet-purse-fashion-shoulder.jpg";

        public static final String WOMEN_DENIM_JACKETS_RES ="https://images-na.ssl-images-amazon.com/images/I/514zli1feNL.jpg";
        public static final String WOMEN_LEATHER_JACKETS_RES = "https://ae01.alicdn.com/kf/HTB1UnSaXjDuK1Rjy1zjq6zraFXa3/2018-Hot-Women-Winter-Warm-Faux-Leather-Jackets-with-Fur-Collar-Lady-White-Black-Pink-Wine.jpg";
        public static final String WOMEN_COAT_JACKETS_RES ="https://images-na.ssl-images-amazon.com/images/I/51lP12bg6PL._AC_UX385_.jpg";
        public static final String WOMEN_BOMBER_JACKETS_RES ="https://imcut.jollychic.com//uploads/jollyimg/imageService/img/goods/2019/11/11/18/40/523ac1d8-7d4e-49ff-b161-f8cfa668413f.jpg";

        public static final String MEN_LEATHER_JACKETS_RES = "https://images-na.ssl-images-amazon.com/images/I/81gPumH6e1L._AC_UL1500_.jpg";
        public static final String MEN_PUFFER_JACKETS_RES = "https://ae01.alicdn.com/kf/HTB1h.XdNkvoK1RjSZFDq6xY3pXaE/ZOGAA-Mens-Winter-Cotton-Hooded-Coat-Jacket-Brand-New-Casual-Men-Puffer-Jacket-Solid-Color-Men.jpg";
        public static final String MEN_DENIM_JACKETS_RES = "https://www.thefashionisto.com/wp-content/uploads/2019/09/HM-Men-Skinny-Jeans.jpg";
        public static final String MEN_TRENCH_JACKETS_RES = "https://i.pinimg.com/originals/41/b1/7a/41b17ad4c3d86eb07c038d8d0e4c6d3a.png";
        public static final String MEN_BOMBER_JACKETS_RES = "https://fasbest.com/wp-content/uploads/2017/04/Top-Best-Model-Men-Bomber-Jacket-Outfit-22.jpg";

        public static final String FLAT_SANDALS_RES = "https://shoedazed.com/image/cache/catalog/AIMEIGAO1//AIMEIGAO-2019-New-Summer-Sandals-Women-Casual-Flat-Sandals-Comfortable-Sandals-For-Women-Large-Size--32972219136-1711-550x550.jpeg";
        public static final String WOMEN_BOOTS_RES = "https://c4.wallpaperflare.com/wallpaper/53/168/878/kendall-jenner-women-model-brunette-boots-hd-wallpaper-preview.jpg";
        public static final String WOMEN_HEELS_RES = "https://cdn.shopify.com/s/files/1/0727/7899/products/Fashion-Summer-High-Heels-Shoes-Women-s-Sandals-Strap-Ladies-Ankle-High-Heels-Size-35-43.jpg_640x640_35a7641d-8003-4da0-a38d-a1bad21dd539_1024x1024.jpg?v=1571438898";
        public static final String WOMEN_SLIDERS_RES = "https://www.twinset.com/on/demandware.static/-/Sites-master-catalog/default/dwaab16f5b/images/TwinSet/Images/Catalog/201TCT022-00519-0S.JPG";

        public static final String MINI_DRESSES_RES = "https://i.pinimg.com/originals/c6/4a/2b/c64a2b4c4daa4a48b3a9f787353b1953.jpg";
        public static final String MIDI_DRESS_RES = "https://d356cpcjxoolwe.cloudfront.net/media/catalog/product/cache/1/image/533x704/72b4a3c89279b6295f5413414e9ad668/6/4/6410424.01.6400_baa7f97.jpg";
        public static final String TIE_WAIST_DRESS_RES = "https://bec2df9eb90bb6604cfc-660d71a7a33bc04488a7427f5fddcedf.ssl.cf3.rackcdn.com/uploads/product_image/photo/5dd2e44a791b5a6f483f7e06/large_2019_11_18_Jen_Mollie_AXparis234512.jpg";
        public static final String SUNDRESS_DRESS_RES = "https://img.ltwebstatic.com/images2_pi/2019/04/30/15566129523968879493_thumbnail_600x799.webp";
        public static final String PETITE_DRESS_RES = "https://www.chichiclothing.com/media/catalog/product/cache/3374943167b162b1b6d3bb58994d69bf/c/c/cchb021-_23.jpg";

        public static final String WOMEN_LEATHER_SHOES = "https://i.ebayimg.com/images/g/PbEAAOSwCFNbW9SB/s-l300.jpg";

        public static final String MEN_SLIM_JEANS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/sks-009-blu-2-1547859752085/skinny-slim-jeans-in-dark-worn-wash-product.jpg?1547859758";
        public static final String MEN_SKINNY_JEANS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/mens-skinny-jeans-in-faded-black-3/mens-faded-black-skinny-jeans-1-product.jpg?1527794179";
        public static final String MEN_SUPER_SKINNY_JEANS_RES = "https://i1.adis.ws/i/boohooamplience/mzz50622_black_xl?pdp.template";
        public static final String MEN_STRAIGHT_JEANS_RES = "https://d4zpg1jklewne.cloudfront.net/steak/spree-variant/straight-jeans-in-black-worn-05/straight-jeans-in-black-worn-product.jpg?1537903204";
        public static final String WOMEN_SLIM_JEANS_RES = "https://cdn11.bigcommerce.com/s-pkla4xn3/images/stencil/1280x1280/products/21185/187304/Skinny-Jeans-Woman-Autumn-New-2018-High-waist-Women-Fashion-Slim-Jeans-Female-washed-casual-skinny__35509.1546333221.jpg?c=2?imbypass=on";
        public static final String WOMEN_SKINNY_JEANS_RES = "https://www.argo-holidays.com/images/a/skinny%20jeans%20for%20women-942nkp.jpg";
        public static final String WOMEN_STRAIGHT_JEANS_RES = "https://www1.assets-gap.com/webcontent/0017/347/802/cn17347802.jpg";
        public static final String WOMEN_RIPPED_JEANS_RES = "https://www.tmtopshop.com/wp-content/uploads/2018/12/IMG_2957.jpg";
        public static final String WOMEN_JEGGINGS = "https://cottonon.com/dw/image/v2/BBDS_PRD/on/demandware.static/-/Sites-catalog-master-women/default/dwd3c519fd/241163/241163-207-2.jpg?sw=566&sh=849&sm=fit";
        public static final String WOMEN_HIGH_WAIST = "https://media.missguided.com/s/missguided/G1806691_set/1/blue-sinner-distressed-knee-high-waisted-jeans";

        public static final String MEN_NECKLACE_RES = "https://images-na.ssl-images-amazon.com/images/I/91sUTxBPQlL._SL1500_.jpg";
        public static final String MEN_RING_RES = "https://www.anitolia.com/plain-model-black-stone-silver-ring-mens-ring-ani-yuzuk-749-14-B.jpg";
        public static final String MEN_BRACELET_RES = "https://www.ties.com/blog/wp-content/uploads/2015/10/Your_Guide_to_Men%E2%80%99s_Jewelry_01.jpg";
        public static final String MEN_EARRING_RES = "https://miro.medium.com/max/870/0*gigjbEBGJQ_0PIUQ.jpeg";
        public static final String WOMEN_NECKLACE_RES = "https://ae01.alicdn.com/kf/HLB1TVEBaizxK1RkSnaVq6xn9VXaT/Choker-Necklace-Heart-New-Design-Punk-Gold-Necklace-Boho-Gothic-Fashion-Jewelry-For-Women-Wicca-Chain.jpg";
        public static final String WOMEN_RING_RES = "https://cdn.shopify.com/s/files/1/0259/0713/products/DOC-R8121-M_Designer_Platinum_Ring_with_Diamonds_with_Partly_Rose_Gold_Polish_JL_PT_566_Model_View_showing_how_the_ring_looks_when_worn_in_hand_of_a_woman_grande.jpg?v=1520407031";
        public static final String WOMEN_BRACELET_RES = "https://images.neimanmarcus.com/ca/1/product_assets/Y/5/0/X/U/NMY50XU_la.jpg";
        public static final String WOMEN_EARRING_RES = "https://www.besttohave.com/images/925-sterling-silver-white-freshwater-pearl-drop-earrings-p183-2477_image.jpg";
        public static final String WOMEN_ANKLET_RES = "https://cdn.lisaangel.co.uk/image/cache/data/product-images/ss19/su-ss19/layered-beaded-anklet-in-grey-and-rose-gold-4x3a1249-515x515.jpg";

        public static final String MEN_ROUND_GLASSES_RES = "https://global2019-static-cdn.kikuu.com/upload-productImg-1533462659898_320_234.jpeg?";
        public static final String WOMEN_ROUND_GLASSES_RES = "https://ae01.alicdn.com/kf/HTB1q60jdLjM8KJjSZFyq6xdzVXaC/DOKLY-New-Polarized-Mirror-Round-Sunglasses-women-Vintage-RetroWomen-Designer-Eyewear-Green-Color-UV400-Glasses-oculos.jpg";
        public static final String MEN_OVERSIZED_GLASSES_RES = "https://i.pinimg.com/736x/f7/07/c3/f707c36f92f10bb6afe727523e0e39ca.jpg";
        public static final String WOMEN_CATEYE_GLASSES_RES = "https://ae01.alicdn.com/kf/HLB1YKs2LHvpK1RjSZFqq6AXUVXah.jpg";
        public static final String MEN_SQUARE_GLASSES_RES = "https://cdn.shopify.com/s/files/1/2403/6313/products/product-image-491666868_1200x1200.jpg?v=1571709186";
        public static final String WOMEN_SQUARE_GLASSES_RES = "https://ae01.alicdn.com/kf/H661c51c6971345b58d2c0fb61b7607c82/GIFANSEE-women-square-sunglasses-oversized-uxury-brand-glasses-vintage-design-eyewear-uv400.jpg";
        public static final String WOMEN_AVIATOR_GLASSES_RES = "https://cdn.shopify.com/s/files/1/0166/6656/products/Skinnydip_London_Rainbow_Aviator_Sunglasses_Model_Image_1_600x.jpg?v=1579790986";

        public static final String MEN_LOAFERS_RES = "https://cf.shopee.ph/file/906adc125c1045c1dd079ed0cdd8afb9";
        public static final String MEN_BOOTS_RES = "https://clarks.scene7.com/is/image/Pangaea2Build/db-winter-boots-2-854-wk31-aw19?wid=854&fmt=pjpeg";
        public static final String MEN_SLIDERS_RES = "https://2.bp.blogspot.com/-6PQM-tOmFfY/W2YXjmi95yI/AAAAAAAAcpE/uy0q7iOHaI8XsHlBO0CU05hFJ5ir8TUPwCLcBGAs/s640/top-5-men-sliders-under%252430.jpg";
        public static final String MEN_SANDALS_RES = "https://ae01.alicdn.com/kf/HTB1Bmw.cWQoBKNjSZJnq6yw9VXaC/2018-New-Men-s-Sandals-Explosive-models-Half-Slippers-Couple-Mesh-Sandals-Lazy-Bird-Nest-Slippers.jpg";
        public static final String MEN_TRAINERS_RES = "https://5.imimg.com/data5/AP/NV/BK/SELLER-9615630/big-fox-men-s-running-training-walking-555-mesh-sports-shoes-500x500.jpg";

        public static final String WOMEN_TSHIRT_RES = "https://ae01.alicdn.com/kf/HTB1ZMMqXPDuK1Rjy1zjq6zraFXac/2019-New-Women-T-shirts-Casual-Harajuku-Love-Printed-Tops-Tee-Summer-Female-T-shirt-Short.jpg";
        public static final String WOMEN_LONG_RES = "https://image-tb.vova.com/image/500_500/filler/29/59/e4881c7ab0f98261bc11d55670bd2959.jpg?format=webp";
        public static final String WOMEN_TANK_RES = "https://cdn11.bigcommerce.com/s-1js1zluvaj/images/stencil/1280x1280/products/34219/106790/ZAFUL-Butterfly-Embroidered-Scooped-Tank-Top-Casual-Women-Tank-Tops-Sexy-Short-Tops-Streetwear-Summer-Tanks__85470.1563288786.jpg?c=2&imbypass=on";
        public static final String MEN_TSHIRT_RES = "https://sc01.alicdn.com/kf/HT1IltqFOxbXXagOFbXg/220128135/HT1IltqFOxbXXagOFbXg.jpg";
        public static final String MEN_LONG_RES = "https://www.rankandstyle.com/media/products/j/james-perse-long-sleeve-t-shirt-mens-long-slee.jpg";
        public static final String MEN_TANK_RES = "https://www.bellymonk.com/wp-content/uploads/2019/04/om_men_tank_model.png";
        public static final String MEN_OXFORD_RES = "https://sc01.alicdn.com/kf/HTB1Jd5iffjM8KJjSZFNq6zQjFXa7.jpg_350x350.jpg";
        public static final String MEN_MUSCLE_RES = "https://i1.adis.ws/i/boohooamplience/mzz70546_black_xl?pdp.template";

        public static final String MEN_SMART_WATCH_RES = "https://ae01.alicdn.com/kf/HTB1UJqQLxTpK1RjSZR0q6zEwXXaI/Q9-Sport-Smart-Watch-15-Days-Work-Message-Display-Multi-Sport-Model-Heart-Rate-Waterproof-Women.jpg";
        public static final String MEN_LEATHER_WATCH_RES = "https://gloimg.gbtcdn.com/soa/gb/pdm-provider-img/straight-product-img/20180725/T025773/T0257730004/goods_img_big-v1/205829-5848.jpg";
        public static final String MEN_BRACELET_WATCH_RES = "https://cdn.shopify.com/s/files/1/0627/5517/products/single-tan-black_1000x.jpg?v=1563463745";
        public static final String MEN_DIGITAL_RES = "https://www.micahtec.com/wp-content/uploads/2020/02/1815-b88b6f.jpg";
        public static final String MEN_CHRONOGRAPH_WATCH_RES = "https://cdn.shopify.com/s/files/1/0122/6748/7328/products/CURREN-Fashion-Watches-Men-Coffee-Clock-Men-Quartz-Wristwatch-Stainless-Steel-Band-Chronograph-Watch-Male-Relogio_849c3883-4623-41fc-91ac-d3a0910f3700_1024x1024.jpg?v=1568644053";
        public static final String MEN_MESH_WATCH_RES = "https://ae01.alicdn.com/kf/H1696f766eed6495b82173d648664483c0.jpg";
        public static final String WOMEN_DIGITAL_RES = "https://images-na.ssl-images-amazon.com/images/I/71LVIC%2B2kGL._AC_UX522_.jpg";

        public static final String MEN_SHORTS_SWIM_RES = "https://www.iciw.com/bilder/artiklar/zoom/10397-092_1.jpg";
        public static final String MEN_CO_ORD_SWIM_RES = "https://images.asos-media.com/products/asos-design-co-ord-swim-shorts-in-multi-coloured-stripe-short-length/14028780-1-multi?$n_480w$&wid=476&fit=constrain";
        public static final String MEN_PLUS_SWIM_RES = "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTqBJ_k6Dg4LBHbXufOQ-6hsXS9Gjwo-DBCH-PP18fcA3wHM0Tc&usqp=CAU";
        public static final String WOMEN_BIKINI_RES = "https://ae01.alicdn.com/kf/HTB1GFvBRgHqK1RjSZFPq6AwapXaW/Trend-Plus-Size-2XL-Swimsuit-Women-Sexy-Bikini-Swimwear-Women-Bikini-Push-Up-Brazilian-Beach-Bathing.jpg";
        public static final String WOMEN_SWIMSUIT_RES = "https://www.womenfashionnista.com/wp-content/uploads/2019/06/costumi-curvy-9-1000-76.jpg";

        public static final String LIKED = "Liked";
        public static final String UNLIKED = "Unliked";
        public static final String LIKES_NUM = "likes";
        public static final String UNLIKES_NUM = "unlikes";
    }

    public static class CustomerMacros {

        public static final String MEN = "Men";
        public static final String WOMEN = "Women";

        public static final int SUGGESTION_PERCENTAGE = 75;

        public static final String LIKED = "Liked";
        public static final String UNLIKED = "Unliked";
        public static final String FAVOURITE = "favorite";
        public static final String PREFERRED_ITEMS = "Preferred items";
    }

    public static class Arrays {

        public static final int[] WOMEN_CLOTHES_TYPES = {
                6992,2623,13497
        };

        public static final int[] MEN_CLOTHES_TYPES = {
                17184,27441,13500
        };

        public static final String[] FAV_LINES = {
                "Great choice!", "Nice!", "Very good choice", "You gonna love it!" ,
                "Hey, We like it too!", "Very nice taste", "Lots of buyers for this one",
                "It's worth it","חדלות פירעון!","Hello Peachy Breezyyy",
        };

        public static final String[] RIPPED = {"ripped"};
        public static final String[] PRICES = {"between 50 to 100", "above 100" ,"less than 50"};
        public static final String[] SANDALS = {"sandals","sandal"};
        public static final String[] SUPER_SIZE = {"super","oversize","oversized","plus"};
        public static final String[] MATERNITY = {"maternity","mom","mother"};
        public static final String[] JEGGINGS = {"jeggings","jegging"};
        public static final String[] PUFFER = {"puffer","puff"};
        public static final String[] RUNNING = {"run","running","training","train","sport","track","track-suit","tracksuit"};
        public static final String[] SLIM = {"slim"};
        public static final String[] MINI = {"mini"};
        public static final String[] MIDI = {"midi","maxi"};
        public static final String[] STRAIGHT = {"straight"};
        public static final String[] SKINNY = {"skinny","skiny"};
        public static final String[] RINGS = {"ring","rings"};
        public static final String[] STRETCH = {"stretch"};
        public static final String[] SUPER_SKINNY = {"super","skinny","super-skinny"};
        public static final String[] BRACELET = {"bracelet","bracelets"};
        public static final String[] NECKLACE = {"necklace","neclaces"};
        public static final String[] EARRINGS = {"earrings","earring"};
        public static final String[] ROUND = {"round"};
        public static final String[] SQUARE = {"square","rectangle","rect"};
        public static final String[] CAT_EYE = {"cat","cat-aye"};
        public static final String[] CLASSIC = {"class","classic"};
        public static final String[] LOAFERS = {"loafers","loafer"};
        public static final String[] BOAT = {"boat"};
        public static final String[] ESPADRILLES = {"espadrilles"};
        public static final String[] LONG = {"long"};
        public static final String[] TANK = {"tank","tanks"};
        public static final String[] T_SHIRT = {"t","t-shirt"};
        public static final String[] OXFORD = {"oxford"};
        public static final String[] MUSCLE = {"muscle","muscle-fit"};
        public static final String[] SMART = {"smart"};
        public static final String[] DIGITAL = {"digital"};
        public static final String[] MESH = {"mesh"};
        public static final String[] SHORTS = {"short","shorts"};
        public static final String[] CO_ORD = {"co-ord","co-ords","ord","ords","coord","coords"};
        public static final String[] LEATHER = {"leather"};
        public static final String[] BUM = {"bum"};
        public static final String[] CLUTCH = {"clutch"};
        public static final String[] BACKPACK = {"backpack"};
        public static final String[] CROSS = {"cross","shoulder","crossbody","croc"};
        public static final String[] TOTE = {"tote"};
        public static final String[] PETITE = {"petite","small"};
        public static final String[] SUNDRESS = {"sundress"};
        public static final String[] DENIM = {"denim"};
        public static final String[] COAT = {"coat","coats"};
        public static final String[] BOMBER = {"bomber","bombers","puff"};
        public static final String[] BOOTS = {"boots","boot"};
        public static final String[] HEELS = {"heel","heels"};
        public static final String[] SLIDERS = {"sliders"};
        public static final String[] EVENING = {"evening"};
        public static final String[] WEDDING = {"wedding"};
        public static final String[] JUMPER = {"jumper"};
        public static final String[] ANKLET = {"anklet"};
        public static final String[] BIKINI = {"bikini"};
        public static final String[] SWIMSUIT = {"swimsuit","swim","suit"};
        public static final String[] TRAINERS = {"train","trainers","sport","workout"};
        public static final String[] TRENCH = {"trench"};
        public static final String[] BIKER = {"biker","bikers"};
        public static final String[] HOODIES = {"hoodies","hoodie"};
        public static final String[] HIGH_WAIST = {"high-waist"};
        public static final String[] PARTY = {"party"};
        public static final String[] SWEATSHIRTS = {"sweatshirt","shirt"};
        public static final String[] BLOUSES = {"blouse"};
        public static final String[] CAMIS = {"camis","cami"};
        public static final String[] SUMMER_TOP = {"summer-top","top","summer"};
        public static final String[] AVIATOR = {"aviator","aviation"};
        public static final String[] WINTER = {"winter"};
        public static final String[] TEDDY = {"teddy"};
        public static final String[] JACKET = {"jacket","jackets"};
        public static final String[] OVERCOAT = {"overcoat"};
    }

    public static class Functions {

        public static String translateCategoryToCastro(String category){
            switch (category){
                case BAG:
                    return "Bags-Wallets";
                case DRESS:
                    return "Dresses";
                case SHIRT:
                    return "Tops";
                case JEANS:
                    return "Jeans";
                case SWIMWEAR:
                    return "Swimwear";
                case SHOES:
                    return "Shoes";
                case JACKETS:
                    return "Blazers-Coats";
                case JEWELLERY:
                    return "Jewelry";
                default:
                    return "";
            }
        }

        public static String translateCategoryToTerminalX(String category){
            switch (category){
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
                default:
                    return "";
            }
        }

        public static void showSnackbar(View view, String text, Context context, int drawableRes) {
            Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
            View snackbarLayout = snackbar.getView();
            TextView textView = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0);
            textView.setAllCaps(true);
            textView.setBackgroundColor(Color.TRANSPARENT);
            snackbarLayout.setBackgroundColor(context.getColor(R.color.SnackbarBackground));
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(16);
            snackbar.show();
        }

        public static void buy(Context context, String site_link) {
            if (site_link == null) return;
            if (!site_link.startsWith("http://") && !site_link.startsWith("https://"))
                site_link = ("http://" + site_link);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site_link));
            context.startActivity(browserIntent);
        }

        public static void fullscreen(Context context, Object item) {
            Intent intent = new Intent(context, FullscreenImageActivity.class);
            Bundle bundle = new Bundle();
            if(item instanceof RecyclerItem){
                RecyclerItem recyclerItem = (RecyclerItem) item;
                bundle.putSerializable("item",recyclerItem);
            }
            else if(item instanceof ShoppingItem){
                ShoppingItem shoppingItem = (ShoppingItem) item;
                bundle.putSerializable("item",shoppingItem);
            }
            intent.putExtra("bundle", bundle);
            context.startActivity(intent);
        }

        public static void goToCustomerMain(Context context, RecyclerItem item){
            Intent intent = new Intent(context, CustomerMainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("gender", item.getGender());
            bundle.putString("type", item.getType());
            bundle.putString("imageUrl", item.getUserImageUrl());
            bundle.putString("sub_category", item.getItem_sub_category());
            intent.putExtra("bundle", bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }

        public static void sellerProfile(Context context, String sellerId){
            Intent intent = new Intent(context, CompanyProfileActivity.class);
            intent.putExtra("id",sellerId);
            intent.putExtra("customer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            context.startActivity(intent);
        }

        public static Pair<Integer,Integer> getCategoryNum(String gender, String sub_cat, String item_type) {
            switch (item_type) {
                case BAG:
                    return getBag(sub_cat);
                case JEANS:
                    return getJeans(gender,sub_cat);
                case SHOES:
                    return getShoes(gender,sub_cat);
                case DRESS:
                    return getDress(sub_cat);
                case SHIRT:
                    return getShirt(gender,sub_cat);
                case WATCH:
                    return getWatch(gender,sub_cat);
                case SUNGLASSES:
                    return getSunglasses(gender,sub_cat);
                case JACKETS:
                    return getJackets(gender,sub_cat);
                case JEWELLERY:
                    return getJewellery(gender,sub_cat);
                default:
                    return getSwimwear(sub_cat);
            }
        }

        private static Pair<Integer,Integer> getJeans(String gender, String sub_cat) {
            switch (sub_cat) {
                case "jeggings":
                    return new Pair<>(19057,1);
                case "slim":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(15157,2) : new Pair<>(5054,1);
                case "straight":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(3630,1) : new Pair<>(5052,1);
                case "ripped":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(19823,3) : new Pair<>(16463,1);
                case "high-waist":
                    return new Pair<>(15159,6);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(10769,1) : new Pair<>(5403,1);
            }
        }

        private static Pair<Integer,Integer> getShoes(String gender, String sub_cat) {
            switch (sub_cat) {
                case "boots":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(6455,1) : new Pair<>(5774,1);
                case "heels":
                    return new Pair<>(6461,1);
                case "sliders":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17170,1) : new Pair<>(17514,1);
                case "loafers":
                    return new Pair<>(11247,1);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17170,1) : new Pair<>(6593,1);
                //sandals
            }
        }

        private static Pair<Integer,Integer> getDress(String sub_cat) {
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
                default:
                    return new Pair<>(13934, 22);
            }
        }

        private static Pair<Integer,Integer> getShirt(String gender,String sub_cat) {
            switch (sub_cat) {
                case "hoodies":
                    return  gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17426,3) : new Pair<>(15427,10);
                case "sweatshirts":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17427,3) : new Pair<>(15426,10);
                case "blouses":
                    return new Pair<>(11318,13);
                case "t-shirts":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(4718,22) : new Pair<>(20778,20);
                case "check":
                    return new Pair<>(12299,1);
                case "denim":
                    return new Pair<>(13024,2);
                case "oxford":
                    return new Pair<>(14478,2);
                case "camis":
                    return new Pair<>(15202,3);
                case "summer-top":
                    return new Pair<>(28018,6);
                case "slim-fit":
                    return new Pair<>(21884,4);
                default: //evening
                    return new Pair<>(11320,3);
            }
        }

        private static Pair<Integer,Integer> getWatch(String gender, String sub_cat) {
            switch (sub_cat) {
                case "smart":
                    return new Pair<>(26217,1);
                case "digital":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11328,1) : new Pair<>(29087,1);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(5088,2) : new Pair<>(19855,3);
                //All
            }
        }

        private static Pair<Integer,Integer> getSunglasses(String gender, String sub_cat) {
            switch (sub_cat) {
                case "round":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(17433,2) : new Pair<>(19271,2);
                case "cat-eye":
                    return new Pair<>(17432,1);
                case "square":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(15192,2) : new Pair<>(21043,2);
                default:
                    return new Pair<>(15191,1); //"aviator"
            }
        }

        private static Pair<Integer,Integer> getJackets(String gender, String sub_cat) {
            switch (sub_cat) {
                case "jacket":
                    return new Pair<>(11894,7);
                case "coat":
                    return new Pair<>(11893,3);
                case "teddy":
                    return new Pair<>(29374,1);
                case "leather":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11895,1) : new Pair<>(11760,1);
                case "biker":
                    return new Pair<>(12340,1);
                case "trench":
                    return new Pair<>(11902,1);
                case "denim":
                    return new Pair<>(11908,3);
                case "winter":
                    return new Pair<>(29375,1);
                case "overcoat":
                    return new Pair<>(20990,1);
                default :
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(28643,1) : new Pair<>(28642,1);
                //puffer
            }
        }

        private static Pair<Integer,Integer> getJewellery(String gender, String sub_cat) {
            switch (sub_cat) {
                case "ring":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11407,2) : new Pair<>(13834,3);
                case "anklet":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(29193,1) : new Pair<>(29194,1);
                case "earrings":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11409,6) : new Pair<>(13837,1);
                case "necklace":
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11408,4) : new Pair<>(13836,3);
                default:
                    return gender.equals(CustomerMacros.WOMEN) ? new Pair<>(11410,1) : new Pair<>(13835,1);
            }
        }

        private static Pair<Integer,Integer> getSwimwear(String sub_cat) {
            switch (sub_cat) {
                case "bikini":
                    return new Pair<>(10117,20);
                case "swimsuit":
                    return new Pair<>(10118,4);
                default:
                    return new Pair<>(13210,10);
                //mens
            }
        }

        private static Pair<Integer,Integer> getBag(String sub_cat) {
            switch (sub_cat) {

                case "purses":
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
                default:
                    return new Pair<>(15142, 1);
            }
        }
    }
}
