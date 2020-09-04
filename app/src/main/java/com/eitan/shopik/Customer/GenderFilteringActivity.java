package com.eitan.shopik.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.eitan.shopik.Database;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.OutletsModel;
import com.eitan.shopik.explanation.ExplanationPagerViewAdapter;
import com.facebook.ads.CacheFlag;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

public class GenderFilteringActivity extends AppCompatActivity {

    private static final int WOMEN_OUTLET_NUM = 27391;
    private static final int MEN_OUTLET_NUM = 27396;
    private int color;

    private Spinner gender_spinner;
    private TabLayout tabLayout;
    private String name,imageUrl;
    private TextView marquee;
    private GenderModel model;
    private String gender;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ViewPager mMainPager;
    private androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener;
    private OutletsModel outletsModel;
    private EntranceViewModel entranceModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_gender_filtering);

        if(!isConnectedToInternet()){
            ViewPager MainPager = findViewById(R.id.gender_pager);
            Macros.Functions.showSnackbar (
                    MainPager,
                    "No Internet connection",
                    this,
                    R.drawable.ic_baseline_signal_cellular
            );
        }

        init();

        setToolbarColor();

        topNavListener = item -> {
            Intent selectedIntent = null;
            switch (item.getTitle().toString()) {
                case "Log Out":
                    FirebaseAuth.getInstance().signOut();
                    if(FirebaseAuth.getInstance().getCurrentUser() == null ) {
                        selectedIntent = new Intent(GenderFilteringActivity.this, LandingPageActivity.class);
                        startActivity(selectedIntent);
                    }
                    break;
                case "My Profile":
                    selectedIntent = new Intent(GenderFilteringActivity.this, CustomerSettingsActivity.class);
                    selectedIntent.putExtra("from_activity","GenderFilteringActivity");
                    break;
                default:
                    break;
            }
            if(selectedIntent!=null) {
                startActivity(selectedIntent);
                finish();
            }
            return true;
        };

        setToolbar();

        setViewPager();

        setSpinner();

        setTabLayout();

        updateModel();

        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model.setGender(parent.getItemAtPosition(position).toString());
                gender = parent.getItemAtPosition(position).toString();
                model.setImageUrl(imageUrl);
                model.setName(name);
                setColors();

                Objects.requireNonNull(entranceModel.getItems().getValue()).clear();

                //LIKED ITEMM
                new fetchLikedItems().execute();

                //Outlet Items
                int num = gender.equals(Macros.CustomerMacros.WOMEN) ? WOMEN_OUTLET_NUM : MEN_OUTLET_NUM;
                new fetchOutlet().execute(num,1);

                if (gender.equals(Macros.CustomerMacros.WOMEN))
                    new fetchNewInWomen().execute(1);
                else
                    new fetchNewInMen().execute(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setMarquee();
    }

    private void setViewPager() {
        ExplanationPagerViewAdapter mExplanationPagerViewAdapter = new ExplanationPagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mExplanationPagerViewAdapter);
        mMainPager.setPageTransformer(false, new ZoomOutPageTransformer());
    }

    private void updateModel() {
        model.setGender(gender);
        model.setImageUrl(imageUrl);
        model.setName(name);
    }

    private void setToolbar() {
        toolbar.setOnMenuItemClickListener(topNavListener);
        Objects.requireNonNull(toolbar.getOverflowIcon()).
                setTint(ContextCompat.getColor(this, R.color.GenderScreenTheme));
        toolbar.setSoundEffectsEnabled(true);
    }

    private void setTabLayout() {

        if(gender.equals(Macros.CustomerMacros.WOMEN))
            color = getColor(R.color.womenColor);
        else
            color = getColor(R.color.menColor);

        tabLayout.setupWithViewPager(mMainPager,true);
        tabLayout.setSelectedTabIndicatorColor(color);
        tabLayout.setTabIndicatorFullWidth(true);
        tabLayout.setTabTextColors(getColor(R.color.GenderScreenTheme),color);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("New in");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Categories");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("Outlet");

        tabLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private void setSpinner() {
        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.
                createFromResource(GenderFilteringActivity.this, R.array.gender, R.layout.gender_item_drop_down);
        gender_adapter.setDropDownViewResource(R.layout.gender_item_drop_down);
        gender_spinner.setAdapter(gender_adapter);
    }

    private void setColors() {
        if(gender.equals(Macros.CustomerMacros.WOMEN))
            color = getColor(R.color.womenColor);
        else
            color = getColor(R.color.menColor);

        tabLayout.setTabTextColors(getColor(R.color.GenderScreenTheme),color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    private void setToolbarColor() {
        if(gender.equals(Macros.CustomerMacros.WOMEN))
            color = getColor(R.color.womenColor);
        else
            color = getColor(R.color.menColor);
    }

    private void init() {

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        model = new ViewModelProvider(this).get(GenderModel.class);
        outletsModel = new ViewModelProvider(this).get(OutletsModel.class);
        entranceModel = new ViewModelProvider(this).get(EntranceViewModel.class);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        assert bundle != null;
        imageUrl = bundle.getString("imageUrl");
        name = bundle.getString("name");

        toolbar = findViewById(R.id.gender_toolbar);
        tabLayout = findViewById(R.id.gender_top_nav);
        mMainPager = findViewById(R.id.gender_pager);
        gender_spinner = findViewById(R.id.gender_spinner);
        marquee = findViewById(R.id.marquee);

        if (bundle.getString("gender") != null) {
            gender_spinner.setSelection(Objects.equals(bundle.getString("gender"), Macros.CustomerMacros.WOMEN) ? 0 : 1);
            gender = bundle.getString("gender");
        }
        else {
            gender_spinner.setSelection(0);
            gender = Macros.CustomerMacros.WOMEN;
        }

        setColors();
    }

    private void setNavigationBarButtonsColor(int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void setMarquee() {
        String brandy = "";
        for(String brand : Macros.Items.brands) {
            brandy = brandy.concat(brand) + "     ";
        }
        marquee.setText(brandy);
        marquee.setSelected(true);
    }

    @Override
    public void onBackPressed() {

        int current_tab = mMainPager.getCurrentItem();

        if(current_tab == 0)
            finish();
        else
            mMainPager.setCurrentItem(0);
    }

    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        model.getGender().removeObservers(this);
        model.getSub_category().removeObservers(this);
    }

    private boolean isConnectedToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        //we are connected to a network
        if(Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED){
            return internetIsConnected();
        }
        else
            return false;
    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //new setInterstitial().execute();
        if (model.getInterstitialAd() != null) {
            model.destroyInterstitialAd();
        }
        // AdSettings.addTestDevice("34464d11-359b-4022-86a5-22489c17269d");
        model.setInterstitialAd(new com.facebook.ads.InterstitialAd(getApplicationContext(), Macros.FB_PLACEMENT_ID));
        // Load a new interstitial.
        model.getInterstitialAd().loadAd(EnumSet.of(CacheFlag.VIDEO));
    }

    private class fetchLikedItems extends AsyncTask<Void,Void,Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            entranceModel.clearLiked();
            // Liked Items
            for (String type : Macros.Items.getAllItemTypes()) {
                FirebaseDatabase.getInstance().getReference().
                        child(Macros.ITEMS).
                        child(gender).
                        child(type).
                        orderByChild(Macros.CustomerMacros.LIKED).
                        addListenerForSingleValueEvent(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @SuppressWarnings("unchecked")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                entranceModel.removeAllType(type,gender);
                                if (snapshot.exists()) {
                                    long count = 0;
                                    long max = 0;
                                    String item_id = "";
                                    String imageUrl = "";
                                    String link = "";
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Map<String, Object> map = (Map<String, Object>) data.getValue();
                                        assert map != null;
                                        Map<String, Object> likes = (Map<String, Object>) map.get(Macros.CustomerMacros.LIKED);
                                        Map<String, Object> info = (Map<String, Object>) map.get("Info");

                                        if (likes != null) {
                                            count = likes.size();
                                        }
                                        if (count > max) {
                                            max = count;
                                            item_id = data.getKey();
                                            assert info != null;
                                            imageUrl = Objects.requireNonNull(info.get("image")).toString();
                                            link = Objects.requireNonNull(info.get("link")).toString();
                                        }
                                    }

                                    RecyclerItem recyclerItem = new RecyclerItem(max + " Likes", link);
                                    recyclerItem.setGender(gender);
                                    recyclerItem.setType(type);
                                    recyclerItem.setId(item_id);
                                    recyclerItem.setLikes(max);
                                    recyclerItem.setLink(link);

                                    ArrayList<String> list = new ArrayList<>();
                                    list.add(imageUrl);
                                    list.add(imageUrl);
                                    list.add(imageUrl);
                                    list.add(imageUrl);

                                    recyclerItem.setImages(list);

                                    if(gender.equals(Macros.CustomerMacros.WOMEN)) {
                                        entranceModel.addWomenLikedItem(recyclerItem);
                                    }
                                    else{
                                        entranceModel.addMenLikedItem(recyclerItem);
                                    }
                                }
                                entranceModel.setLiked_items(gender);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d(Macros.TAG,"Gender fetchData: " + error.getMessage());
                            }
                        });
            }
            return null;
        }
    }

    private class fetchOutlet extends AsyncTask<Integer,Integer,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outletsModel.clearAllOutlets();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... integers) {
            int i=0;
            try {
                Document document = Jsoup.connect("https://www.asos.com/cat/?cid=" + integers[0] + "&page=" + integers[1]).get();
                Elements products = document.getElementsByAttributeValue("data-auto-id", "productTile");

                for (Element prod : products) {
                    ++i;
                    RecyclerItem recyclerItem = new RecyclerItem(null, null);

                    // PRICE
                    String price = "", red = "";
                    Elements pook = prod.getElementsByAttributeValue("data-auto-id", "productTilePrice");
                    if (pook.size() == 1) {
                        try {
                            price = pook.get(0).childNode(2).childNode(0).toString().replace("£", "");
                        }
                        catch (IndexOutOfBoundsException ex){
                            price = pook.get(0).childNode(0).childNode(0).toString().replace("£", "");
                        }
                        recyclerItem.setPrice(price);
                    }
                    Elements pook2 = prod.getElementsByAttributeValue("data-auto-id", "productTileSaleAmount");
                    if (pook2.size() > 0) {
                        red = pook2.get(0).childNode(0).toString().replace("£", "");
                        recyclerItem.setSale(true);
                        recyclerItem.setReduced_price(red);
                    }

                    //ID
                    String id = prod.attr("id").split("-")[1];

                    //LINK + DESCRIPTION
                    Attributes attributes = prod.childNode(0).attributes();
                    String link = attributes.get("href");
                    String description = attributes.get("aria-label").split(",")[0];

                    //IMAGES
                    Document document2 = Jsoup.connect(link).get();
                    Elements images_ele = document2.getElementsByClass("image-thumbnail");
                    ArrayList<String> images = new ArrayList<>();
                    for (Element img : images_ele) {
                        String _img = img.childNode(1).childNode(1).attr("src").
                                split("\\?")[0] + "?$XXL$&wid=513&fit=constrain";
                        images.add(_img);
                    }

                    //BRAND
                    String brand = "";
                    Elements brand_ele;
                    try {
                        brand_ele = document2.getElementsByClass("brand-description");
                        Elements pop = brand_ele.get(0).getAllElements();
                        brand = pop.get(4).childNode(0).toString().replace("&amp;", "&");
                        recyclerItem.setBrand(brand);
                    } catch (IndexOutOfBoundsException ex) {
                        try {
                            brand_ele = document2.getElementsByClass("product-description");
                            brand = brand_ele.get(1).childNode(4).
                                    childNode(0).childNode(1).toString().
                                    replace(" by ", "");
                            recyclerItem.setBrand(brand);
                        } catch (IndexOutOfBoundsException e) {
                            recyclerItem.setBrand("ASOS");
                        }
                    }

                    Currency shekel = Currency.getInstance("ILS");
                    String currency_symbol = shekel.getSymbol();
                    Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                    String _price = new DecimalFormat("##.##").format(current) + currency_symbol;

                    recyclerItem.setSeller("ASOS");
                    recyclerItem.setImages(images);
                    recyclerItem.setPrice(_price);
                    recyclerItem.setReduced_price(red);
                    recyclerItem.setLink(link);
                    recyclerItem.setId(id);
                    recyclerItem.setDescription(new ArrayList<>(Arrays.asList(description.split(" "))));
                    recyclerItem.setType("OUTLET");

                    outletsModel.addToOutlets(recyclerItem);
                    publishProgress(i, products.size(), integers[1]);
                }
            }
            catch (Exception e ) {
                Log.d(Macros.TAG,"outlets failed : iteration :" + i + ", " + Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            outletsModel.postOutlets();
        }
    }

    private class fetchNewInMen extends AsyncTask<Integer,Integer,Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            try {
                for (int cat_num : Macros.Arrays.MEN_CLOTHES_TYPES) {
                    String cat = "";
                    switch (cat_num) {
                        case 17184:
                            cat = Macros.NEW_SHOES;
                            break;
                        case 27441:
                            cat = Macros.NEW_CLOTHING;
                            break;
                        case 13500:
                            cat = Macros.NEW_TRENDING;
                    }

                    String new_items_data;
                    entranceModel.addMen_new_num(cat, 0);

                    Document document = Jsoup.connect("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + page_num[0]).get();
                    DataNode node = (DataNode) document.childNode(3).childNode(3).childNode(28).childNode(0);

                    new_items_data = node.getWholeData();
                    String[] data_split = new_items_data.split("\"products\":", 2);
                    String koko = data_split[1];
                    koko = koko.replaceAll("u002F", "").
                            replaceAll("urban", ".urban").
                            replaceAll("gg-", "").
                            replaceAll("under", ".under").
                            replaceAll("ufluff", ".ufluff").
                            replaceAll("upper", ".upper").
                            replaceAll("uncommon", ".uncommon").
                            replaceAll("uoh", ".uoh");

                    new_items_data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                    JSONArray JA = new JSONArray(new_items_data);
                    int total_items = JA.length();
                    for (int i = 0; i < total_items; ++i) {
                        JSONObject JO = (JSONObject) JA.get(i);
                        String imageUrl = "https://" + JO.get("image").toString().
                                replace(".com", ".com/").
                                replace("products", "products/");
                        int opop = imageUrl.lastIndexOf("-");

                        String color = imageUrl.substring(opop + 1);
                        String id = JO.get("id").toString();
                        String link = "https://www.asos.com/" + JO.get("url").toString().
                                replace("prd", "/prd/").
                                replace("asos-designasos", "asos-design/asos");

                        String price = JO.get("price").toString();
                        String branda = "ASOS";

                        Currency shekel = Currency.getInstance("ILS");
                        String currency_symbol = shekel.getSymbol();
                        Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                        price = new DecimalFormat("##.##").format(current) + currency_symbol;

                        RecyclerItem recyclerItem = new RecyclerItem(branda, link);
                        recyclerItem.setPrice(price);
                        recyclerItem.setLink(link);
                        recyclerItem.setType(cat);

                        if ((boolean) JO.get("isOutlet")) {
                            recyclerItem.setOutlet(true);
                            recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                        } else
                            recyclerItem.setOutlet(false);

                        if ((boolean) JO.get("isSale")) {
                            recyclerItem.setSale(true);
                            recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                        } else
                            recyclerItem.setSale(false);

                        Database connection = new Database();

                        if (JO.get("url").toString().contains("prd")) {
                            recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                        } else {
                            recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));
                        }

                        entranceModel.addMenItem(recyclerItem);
                    }
                }
                entranceModel.setList(gender);
            }
            catch (Exception e ) {
                Log.d(Macros.TAG,"GenderFiltering::fetchNewInMen() " +  Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }
    }

    private class fetchNewInWomen extends AsyncTask<Integer,Integer,Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... integers) {
            try {
                for (int cat_num : Macros.Arrays.WOMEN_CLOTHES_TYPES) {
                    String new_items_data;
                    String cat = "";
                    switch (cat_num) {
                        case 6992:
                            cat = Macros.NEW_SHOES;
                            break;
                        case 2623:
                            cat = Macros.NEW_CLOTHING;
                            break;
                        case 13497:
                            cat = Macros.NEW_TRENDING;
                    }

                    entranceModel.addWomen_new_num(cat, 0);

                    Document document = Jsoup.connect("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + integers[0]).get();
                    DataNode node = (DataNode) document.childNode(3).childNode(3).childNode(28).childNode(0);

                    new_items_data = node.getWholeData();

                    String[] data_split = new_items_data.split("\"products\":", 2);
                    String koko = data_split[1];
                    koko = koko.replaceAll("u002F", "").
                            replaceAll("urban", ".urban").
                            replaceAll("gg-", "").
                            replaceAll("under", ".under").
                            replaceAll("ufluff", ".ufluff").
                            replaceAll("upper", ".upper").
                            replaceAll("uncommon", ".uncommon").
                            replaceAll("uoh", ".uoh");

                    new_items_data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                    JSONArray JA = new JSONArray(new_items_data);

                    for (int i = 0; i < JA.length(); ++i) {
                        JSONObject JO = (JSONObject) JA.get(i);
                        String imageUrl = "https://" + JO.get("image").toString().
                                replace(".com", ".com/").
                                replace("products", "products/");

                        int opop = imageUrl.lastIndexOf("-");

                        String color = imageUrl.substring(opop + 1);
                        String id = JO.get("id").toString();
                        String link = "https://www.asos.com/" + JO.get("url").toString().
                                replace("prd", "/prd/").
                                replace("asos-designasos", "asos-design/asos");

                        String price = JO.get("price").toString();

                        ArrayList<String> list = new ArrayList<>();
                        String[] name = JO.get("description").toString().split(" ");
                        for (String word : name) {
                            if (!word.equals("")) {
                                list.add(word.toLowerCase());
                            }
                        }

                        Currency shekel = Currency.getInstance("ILS");
                        String currency_symbol = shekel.getSymbol();
                        Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;

                        price = new DecimalFormat("##.##").format(current) + currency_symbol;
                        RecyclerItem recyclerItem = new RecyclerItem(cat, link);
                        recyclerItem.setPrice(price);
                        recyclerItem.setLink(link);
                        recyclerItem.setType(cat);
                        recyclerItem.setDescription(list);

                        if ((boolean) JO.get("isOutlet")) {
                            recyclerItem.setOutlet(true);
                            recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                        } else
                            recyclerItem.setOutlet(false);

                        if ((boolean) JO.get("isSale")) {
                            recyclerItem.setSale(true);
                            recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                        } else
                            recyclerItem.setSale(false);

                        Database connection = new Database();

                        if (JO.get("url").toString().contains("prd"))
                            recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                        else
                            recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));

                        entranceModel.addWomenItem(recyclerItem);
                    }
                }
                entranceModel.setList(gender);
            }
            catch (Exception e ) {
                Log.d(Macros.TAG,"GenderFiltering::fetchNewInWomen() " +  Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }
    }

}
