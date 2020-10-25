package com.eitan.shopik.customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplicationActivity;
import com.eitan.shopik.adapters.ExplanationPagerViewAdapter;
import com.eitan.shopik.items.RecyclerItem;
import com.eitan.shopik.items.ShoppingItem;
import com.eitan.shopik.viewModels.EntranceViewModel;
import com.eitan.shopik.viewModels.GenderModel;
import com.eitan.shopik.viewModels.OutletsModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GenderFilteringActivity extends AppCompatActivity {

    private static final int WOMEN_OUTLET_NUM = 27391;
    private static final int MEN_OUTLET_NUM = 27396;
    private int color;
    private Spinner gender_spinner;
    private TabLayout tabLayout;
    private static String name,imageUrl;
    private TextView marquee;
    private GenderModel model;
    private static String gender;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ViewPager mMainPager;
    private androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener;
    private static OutletsModel outletsModel;
    private static EntranceViewModel entranceModel;
    private ArrayList<AsyncTask <Void, Void, Void>> asyntask;
    private static long entriesNum = 0;
    private static DocumentReference customerFS;
    private static MutableLiveData<Long> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_gender_filtering);

        startPostponedEnterTransition();
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

        new appEntriesNum().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        setToolbarColor();

        topNavListener = item -> {
            Intent selectedIntent = null;
            switch (item.getTitle().toString()) {
                case "Log Out":
                    FirebaseAuth.getInstance().signOut();
                    if(FirebaseAuth.getInstance().getCurrentUser() == null ) {
                        selectedIntent = new Intent(GenderFilteringActivity.this, LandingPageActivity.class);
                        startActivity(selectedIntent);
                        finish();
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
                animateLogo();

                model.setGender(parent.getItemAtPosition(position).toString());
                gender = parent.getItemAtPosition(position).toString();
                model.setImageUrl(imageUrl);
                model.setName(name);

                setColors();

                for(AsyncTask <Void, Void, Void> asyncTask : asyntask){
                    asyncTask.cancel(true);
                }

                //Add task to your array
                // OUTLET ITEMS
                asyntask.add(new fetchOutlet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // LIKED ITEMS
                asyntask.add(new fetchLikedItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // ALDO ITEMS
                asyntask.add(new getAldo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // CASTRO ITEMS
                asyntask.add(new getCastro().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // ASOS ITEMS
                asyntask.add(new getAsos().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // SHEIN ITEMS
                asyntask.add(new getShein().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // RENUAR ITEMS
                asyntask.add(new getRenuar().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // TERMINAL X ITEMS
                asyntask.add(new getTerminalX().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // TFS ITEMS
                asyntask.add(new getTFS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                // HOODIES ITEMS
                asyntask.add(new getHoodies().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setMarquee();

        entries.observe(this, aLong -> ShopikApplicationActivity.launchReview(this));
    }

    private void setViewPager() {
        ExplanationPagerViewAdapter mExplanationPagerViewAdapter = new ExplanationPagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mExplanationPagerViewAdapter);
        mMainPager.setPageTransformer(false, new ZoomOutPageTransformer());
        mMainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                animateLogo();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
                createFromResource(GenderFilteringActivity.this,
                        R.array.gender, R.layout.gender_item_drop_down);
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

        //Array of task
        asyntask = new ArrayList<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        customerFS = FirebaseFirestore.getInstance().collection(Macros.CUSTOMERS).document(userId);

        toolbar = findViewById(R.id.gender_toolbar);
        tabLayout = findViewById(R.id.gender_top_nav);
        mMainPager = findViewById(R.id.gender_pager);
        gender_spinner = findViewById(R.id.gender_spinner);
        marquee = findViewById(R.id.marquee);

        entries = new MutableLiveData<>();

        if (bundle.getString("gender") != null) {
            gender_spinner.setSelection(Objects.equals(bundle.getString("gender"),
                    Macros.CustomerMacros.WOMEN) ? 0 : 1);
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

    @Override
    protected void onStop() {
        super.onStop();
        model.getGender().removeObservers(this);
        model.getSub_category().removeObservers(this);
    }

    //ADDED LATER - NEED CHECKING
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (AsyncTask<Void, Void, Void> asyncTask : asyntask) {
            asyncTask.cancel(true);
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_MOBILE:
                    // connected to mobile data
                    // connected to wifi
                    return internetIsConnected();
                default:
                    return true;
            }
        }
        else {
            // not connected to the internet
            return false;
        }

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

    private static class fetchLikedItems extends AsyncTask<Void,Void,Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            entranceModel.clearLiked();
            // Liked items
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
                                entranceModel.removeAllType( type, gender );
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

                                    RecyclerItem recyclerItem = new RecyclerItem(max + " Likes",null, link);
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
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
            }
            return null;
        }
    }
    private static class fetchOutlet extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            outletsModel.clearAllOutlets();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... integers) {
            int i=0;
            int num = gender.equals(Macros.CustomerMacros.WOMEN) ? WOMEN_OUTLET_NUM : MEN_OUTLET_NUM;
            try {
                Document document = Jsoup.connect("https://www.asos.com/cat/?cid="
                        + num
                        + "&page="
                        + 1).get();
                Elements products = document.getElementsByAttributeValue("data-auto-id", "productTile");

                outletsModel.setTotalItems(products.size());
                for (Element prod : products) {
                    ++i;
                    ShoppingItem shoppingItem = new ShoppingItem();

                    // PRICE
                    String price = "", red = "";
                    Elements productTilePrice = prod.
                            getElementsByAttributeValue("data-auto-id", "productTilePrice");
                    if (productTilePrice.size() == 1) {
                        try {
                            price = productTilePrice.get(0).childNode(2).childNode(0).
                                    toString().replace("£", "");
                        } catch (IndexOutOfBoundsException ex) {
                            price = productTilePrice.get(0).childNode(0).childNode(0).
                                    toString().replace("£", "");
                        }
                    }
                    Elements productTileSaleAmount = prod.
                            getElementsByAttributeValue("data-auto-id", "productTileSaleAmount");
                    if (productTileSaleAmount.size() > 0) {
                        red = productTileSaleAmount.get(0).childNode(0).
                                toString().replace("£", "");
                        shoppingItem.setOn_sale(true);
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
                    if(images.size() < 4){
                        for( int k=images.size(); k<4; ++k){
                            images.add(images.get(0));
                        }
                    }

                    //BRAND
                    String brand;
                    Elements brand_ele;
                    try {
                        brand_ele = document2.getElementsByClass("brand-description");
                        Elements poki = brand_ele.get(0).getElementsByTag("strong");
                        brand = poki.get(0).text();
                        shoppingItem.setBrand(brand);
                    } catch (IndexOutOfBoundsException ex) {
                        shoppingItem.setBrand("ASOS");
                    }

                    Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                    String _price = new DecimalFormat("##.##").format(current);

                    Double reducedPrice = Double.parseDouble(red) * Macros.POUND_TO_ILS;
                    String reducedPriceString = new DecimalFormat("##.##").format(reducedPrice);

                    shoppingItem.setSeller("ASOS");
                    shoppingItem.setImages(images);
                    shoppingItem.setPrice(_price);
                    shoppingItem.setReduced_price(reducedPriceString);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setId(id);
                    shoppingItem.setSellerId("odsIz0HNINevS2EP3mdIrryTIF72");
                    shoppingItem.setName(new ArrayList<>(Arrays.asList(description.split(" "))));
                    shoppingItem.setType("OUTLET");

                    outletsModel.addToOutlets(shoppingItem);
                    outletsModel.setCurrentItem(i);
                }
            }
            catch (Exception e ) {
                e.printStackTrace();
            }
            return null;
        }
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

    private static class getAldo extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllShoesItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {
            int iter = 0;
            try {
                Document temp;
                temp = Jsoup.connect("https://www.aldoshoes.co.il/"+ gender.toLowerCase()
                        +"/new-arrivals.html").get();

                Elements list_items = temp.getElementsByAttributeValue("id", "prod-list-cat");
                Elements products = list_items.get(0).getElementsByClass("cat-product");

                for(Element prod : products){

                    iter++;
                    String link = prod.childNode(7).attributes().get("href");
                    String price = prod.childNode(7).attributes().get("data-price");
                    String id = prod.childNode(7).attributes().get("data-id");
                    String description = prod.childNode(7).attributes().get("data-name");
                    ArrayList<String> name = new ArrayList<>(Arrays.asList(description.split(" ")));


                    Document prod_page;
                    try {
                        prod_page = Jsoup.connect(link).get();
                    }
                    catch (org.jsoup.HttpStatusException ex){
                        continue;
                    }

                    Elements prod_info = prod_page.getElementsByClass("gallery-image");
                    if(prod_info.size() == 0) continue;

                    ShoppingItem shoppingItem = new ShoppingItem();
                    ArrayList<String> images = new ArrayList<>();
                    for(Node img : prod_info){
                        images.add(img.attr("src"));
                    }
                    if(images.size() < 4){
                        for(int j = images.size(); j<4; j++){
                            images.add(images.get(0));
                        }
                    }

                    shoppingItem.setPrice(price);
                    shoppingItem.setImages(images);
                    shoppingItem.setGender(gender);
                    shoppingItem.setSeller("Aldo");
                    shoppingItem.setSite_link(link);
                    shoppingItem.setName(name);

                    entranceModel.addItem(shoppingItem);
                    entranceModel.setCurrentShoesItem(iter);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class getCastro extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllClothingItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {
            try {
                int iter = 0;

                Document document;
                document = Jsoup.connect("https://www.castro.com/"
                        + gender.toLowerCase()
                        + "/shop_by_collection/hot_trends").get();

                Elements elements = document.
                        getElementsByClass("products list items product-items ");
                Elements filtered_elements = elements.get(0).
                        getElementsByAttributeValueStarting("id","product_category_");

                for (Element element : filtered_elements) {

                    String id = element.getElementsByClass("start-product-item").
                            get(0).attr("data-product-sku");

                    String link = "https://www.castro.com/" + id;

                    Document item_doc;
                    try {
                        item_doc = Jsoup.connect(link).get();
                    }
                    catch (org.jsoup.HttpStatusException ex){
                        String prod_id = element.attr("data-productid");
                        link = "https://www.castro.com/catalog/product/view/id/" + prod_id;
                        try {
                            item_doc = Jsoup.connect(link).get();
                        }
                        catch (org.jsoup.HttpStatusException e){
                            continue;
                        }
                    }

                    Elements images_doc = item_doc.getElementsByClass("product_gallery");

                    String price;
                    Elements price_doc = item_doc.getElementsByClass("price");

                    ShoppingItem shoppingItem = new ShoppingItem();

                    if (price_doc.hasClass("old-price")) {
                        price = price_doc.get(0).childNode(0).toString().replace("₪","");
                        String old_price = price_doc.get(1).childNode(0).toString().
                                replace("₪", "");
                        shoppingItem.setReduced_price(price);
                        shoppingItem.setOn_sale(true);
                        shoppingItem.setPrice(old_price);
                    }
                    else {
                        price = price_doc.get(0).childNode(0).toString().replace("₪", "");
                        shoppingItem.setPrice(price);
                    }

                    String name = "";
                    ArrayList<String> images = new ArrayList<>();
                    Elements images_elements = images_doc.get(0).getElementsByClass("idus-slider-slide ");
                    for (Node img : images_elements) {
                        String image = img.childNode(1).attr("src");
                        if (image.equals(""))
                            image = img.childNode(1).attr("data-lazy");

                        name = img.childNode(1).attr("alt");
                        images.add(image);
                    }
                    if(images.size() < 4){
                        for( int i=images.size(); i<4; ++i){
                            images.add(images.get(0));
                        }
                    }

                    ArrayList<String> description = new ArrayList<>(Arrays.asList(name.split(" ")));

                    shoppingItem.setImages(images);
                    shoppingItem.setGender(gender);
                    shoppingItem.setSeller("Castro");
                    shoppingItem.setName(description);
                    shoppingItem.setSite_link(link);

                    iter++;
                    entranceModel.addItem(shoppingItem);
                    entranceModel.setCurrentClothingItem(iter);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class getTerminalX extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllTxItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {

            int iter = 0;

            try {
                Document temp = Jsoup.connect("https://www.terminalx.com/justlanded/" +
                        gender.toLowerCase()).get();

                Elements list_items = temp.getElementsByClass("products list items product-items");
                Elements items_photo = list_items.get(0).getElementsByClass("product-item-photo-shop");
                Elements items_details = list_items.get(0).getElementsByClass("product details product-item-details");

                for (int i = 0; i < items_photo.size(); ++i) {

                    String link = items_photo.get(i).childNode(1).attr("href");
                    String title = items_photo.get(i).childNode(1).
                            childNode(1).childNode(1).
                            childNode(1).attributes().get("alt");

                    Document doc;
                    try {
                        doc = Jsoup.connect(link).get();
                    }
                    catch (org.jsoup.HttpStatusException ex){
                        continue;
                    }

                    Elements info = doc.getElementsByClass( "product-info-main");
                    Elements media = doc.getElementsByClass( "product media");

                    String img_url = media.get(0).
                            childNode(6).childNode(1).
                            childNode(1).childNode(3).
                            childNode(1).childNode(5).
                            childNode(1).childNode(1).
                            attr("src");

                    ArrayList<String> images = new ArrayList<>();

                    images.add(img_url);
                    images.add(img_url.replace("-1", "-2"));
                    images.add(img_url.replace("-1", "-3"));
                    images.add(img_url.replace("-1", "-4"));

                    Elements product_item_brand = info.get(0).getElementsByClass( "product-item-brand");
                    ShoppingItem shoppingItem = new ShoppingItem();

                    String price;  //by ILS
                    String id;
                    if (product_item_brand.size() == 0) {

                        id = items_details.get(i).
                                childNode(1).childNode(5).
                                attr("data-id");

                        price = items_details.get(i).
                                childNode(1).childNode(3).
                                childNode(0).childNode(1).
                                childNode(1).childNode(1).
                                childNode(0).
                                toString().replace("&nbsp;₪", "");
                    } else {
                        Elements final_price = info.get(0).getElementsByAttributeValue("class", "price-box price-final_price");
                        id = final_price.get(0).attr("data-product-id");

                        if (final_price.get(0).childNodeSize() == 5) {
                            String reduced_price = final_price.get(0).
                                    childNode(1).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            price = final_price.get(0).
                                    childNode(3).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            shoppingItem.setOn_sale(true);
                            shoppingItem.setReduced_price(reduced_price);
                        } else
                            price = final_price.get(1).childNode(0).childNode(1).childNode(3).attr("data-price-amount");
                    }

                    shoppingItem.setId(id);
                    shoppingItem.setPrice(price);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setType("New In");
                    shoppingItem.setGender(gender);
                    ArrayList<String> name = new ArrayList<>(Arrays.asList(title.split(" ")));
                    shoppingItem.setName(name);
                    shoppingItem.setSeller("Terminal X");
                    shoppingItem.setSellerId("KhrLuCxBoKc0DLAL8dA6WAbOFXT2");
                    shoppingItem.setImages(images);

                    iter++;
                    entranceModel.addItem(shoppingItem);
                    entranceModel.setCurrentTerminalXItem(iter);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class getAsos extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllAsosItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {
            int iter = 0;
            try {
                String url = gender.equals(Macros.CustomerMacros.MEN) ?
                        "https://www.asos.com/men/new-in/cat/?cid=27110" :
                        "https://www.asos.com/women/new-in/cat/?cid=27108";

                Document document = Jsoup.connect(url).get();
                Elements products = document.getElementsByAttributeValue("data-auto-id", "productTile");

                for(Element prod : products.subList(0,40)) {

                    ShoppingItem shoppingItem = new ShoppingItem();

                    Elements pook = prod.getElementsByAttributeValue("data-auto-id", "productTilePrice");
                    if(pook.size() == 1) {
                        int las_ele_idx = pook.get(0).childNodes().size() - 1;
                        String price = pook.get(0).childNode(las_ele_idx).childNode(0).
                                toString().replace("£", "");
                        String final_price = String.valueOf(Double.parseDouble(price));
                        shoppingItem.setPrice(final_price);
                    }

                    Elements pook2 = prod.getElementsByAttributeValue( "data-auto-id", "productTileSaleAmount");
                    if(pook2.size() > 0){
                        String red = pook2.get(0).childNode(0).toString().replace("£", "");
                        String final_price = String.valueOf(Double.parseDouble(red));
                        shoppingItem.setOn_sale(true);
                        shoppingItem.setReduced_price(final_price);
                    }

                    Attributes attributes = prod.childNode(0).attributes();
                    String link = attributes.get("href");
                    String description = attributes.get("aria-label").split(",")[0];
                    String[] _name = description.split(" ");

                    Document document2;
                    try {
                        document2 = Jsoup.connect(link).get();
                    } catch (org.jsoup.HttpStatusException ex) {
                        continue;
                    }
                    Elements images_ele = document2.getElementsByClass("image-thumbnail");
                    ArrayList<String> images = new ArrayList<>();
                    for(Element img : images_ele){
                        String _img = img.childNode(1).childNode(1).attr("src").
                                split("\\?")[0] + "?$XXL$&wid=513&fit=constrain";
                        images.add(_img);
                    }
                    if (images.size() < 4) {
                        for (int j = images.size(); j < 4; j++) {
                            images.add(images.get(0));
                        }
                    }

                    String seller_name = "Asos";

                    //BRAND
                    String brand;
                    Elements brand_ele;
                    try {
                        brand_ele = document2.getElementsByClass("brand-description");
                        Elements poki = brand_ele.get(0).getElementsByTag("strong");
                        brand = poki.get(0).text();
                        shoppingItem.setBrand(brand != null ? brand : seller_name);
                    } catch (IndexOutOfBoundsException ex) {
                        shoppingItem.setBrand(seller_name);
                    }

                    shoppingItem.setSeller(seller_name);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setGender(gender);
                    shoppingItem.setImages(images);
                    shoppingItem.setName(new ArrayList<>(Arrays.asList(_name)));

                    iter++;
                    entranceModel.addItem(shoppingItem);
                    entranceModel.setCurrentAsosItem(iter);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class getRenuar extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllRenuarItems();
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {

            int iter = 0;
            try {
                Document document = Jsoup.connect(
                        "https://www.renuar.co.il/he/" + gender.toLowerCase() + "/hdw.html").get();

                Elements elements = document.getElementsByClass("products-grid");
                Elements products = elements.get(0).getElementsByClass("item");

                for (Element element : products) {
                    ShoppingItem shoppingItem = new ShoppingItem();

                    String name = element.childNode(3).childNode(3).childNode(0).attr("title");
                    String link = element.childNode(3).childNode(3).childNode(0).attr("href");

                    Document item_doc;
                    try {
                        item_doc = Jsoup.connect(link).get();
                    }
                    catch (org.jsoup.HttpStatusException ex){
                        continue;
                    }

                    Elements item_elements = item_doc.getElementsByClass("images-slideshow");
                    Elements images_elements = item_elements.get(0).getElementsByClass("item");

                    ArrayList<String> images = new ArrayList<>();
                    for (Element img : images_elements) {
                        Elements pook = img.getElementsByClass("zoom");
                        images.add(pook.get(0).attr("src"));
                    }
                    if(images.size() < 4){
                        for(int j = images.size(); j<4; j++){
                            images.add(images.get(0));
                        }
                    }

                    ArrayList<String> description = new ArrayList<>(Arrays.asList(name.split(" ")));

                    Element price_box = item_doc.getElementsByClass("price-box").get(0);
                    Elements old_price = price_box.getElementsByClass("old-price");

                    if( old_price.size() > 0 ) {
                        Elements _price = old_price.get(0).getElementsByClass("price");
                        String price = _price.get(0).childNode(0).toString().replace("&nbsp;₪ ","");

                        Elements _reduced_price = price_box.getElementsByClass("special-price").get(0).
                                getElementsByClass("price");
                        String reduced_price = _reduced_price.get(0).childNode(0).toString().replace("&nbsp;₪ ","");

                        shoppingItem.setPrice(price);
                        shoppingItem.setReduced_price(reduced_price);
                        shoppingItem.setOn_sale(true);
                    }
                    else {
                        String _price = price_box.childNode(1).childNode(1).childNode(0).toString().replace("&nbsp;₪", "");
                        shoppingItem.setPrice(_price);
                    }

                    shoppingItem.setImages(images);
                    shoppingItem.setGender(gender);
                    shoppingItem.setSeller("Renuar");
                    shoppingItem.setName(description);
                    shoppingItem.setSite_link(link);

                    iter++;
                    entranceModel.addItem(shoppingItem);
                    entranceModel.setCurrentRenuarItem(iter);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class getShein extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllSheinItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {

            int iter = 0;
            try {
                Document document;
                String women_url = "https://il.shein.com/daily-new.html";
                String men_url = "https://il.shein.com/new/Men-new-in-sc-00200410.html";
                String url = gender.equals(Macros.CustomerMacros.WOMEN) ? women_url : men_url;
                document = Jsoup.connect(url).get();

                assert document != null;
                Elements elements = document.getElementsByClass("j-goodsli");

                if(elements.isEmpty()){
                    elements = document.getElementsByClass("j-expose__content-goodsls");
                }

                for(Element element : elements.subList(0,40)){
                    ShoppingItem shoppingItem = new ShoppingItem();

                    String link = "";
                    try {
                        String link2 = element.getElementsByClass("c-goodsitem__goods-name").
                                get(1).attr("href");
                        link = "https://il.shein.com" + link2;
                    }
                    catch (IndexOutOfBoundsException ex){
                        System.out.println(ex.getMessage());
                    }
                    Document prod_elements;
                    try {
                        prod_elements = Jsoup.connect(link).get();
                    }
                    catch (org.jsoup.HttpStatusException ex){
                        continue;
                    }
                    String json_prepare = "";
                    try {
                        json_prepare = prod_elements.
                                childNode(2).childNode(3).
                                childNode(15).childNode(17).
                                childNode(0).toString();
                    }
                    catch (IndexOutOfBoundsException iobe){
                        System.out.println(iobe.getMessage());
                    }
                    String productIntroData = json_prepare.
                            split("productIntroData:")[1].
                            split("abt: ")[0];

                    String json = "[" + productIntroData + "]";

                    JSONArray jsonArray = new JSONArray(json);
                    JSONArray imagesArray = jsonArray.getJSONObject(0).
                            getJSONObject("goods_imgs").
                            getJSONArray("detail_image");

                    ArrayList<String> images = new ArrayList<>();
                    for(int i = 0; i < imagesArray.length(); ++i){
                        String img = imagesArray.getJSONObject(i).getString("origin_image");
                        images.add("http:" + img);
                    }
                    if(images.size() < 4){
                        for(int j = images.size(); j<4; j++){
                            images.add(images.get(0));
                        }
                    }
                    JSONObject details = jsonArray.getJSONObject(0).
                            getJSONObject("detail");

                    String price = details.
                            getJSONObject("retailPrice").
                            getString("amount");

                    String salePrice = details.
                            getJSONObject("salePrice").
                            getString("amount");

                    if(!salePrice.equals(price)){
                        shoppingItem.setReduced_price(salePrice);
                        shoppingItem.setOn_sale(true);
                    }

                    String _name = details.getString("goods_url_name");

                    shoppingItem.setPrice(price);
                    shoppingItem.setImages(images);
                    shoppingItem.setGender(gender);
                    shoppingItem.setSeller("Shein");
                    shoppingItem.setSite_link(link);
                    ArrayList<String> description = new ArrayList<>(Arrays.asList(_name.split(" ")));
                    shoppingItem.setName(description);

                    iter++;
                    entranceModel.addItem(shoppingItem);
                    entranceModel.setCurrentSheinItem(iter);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private static class getTFS extends AsyncTask <Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllTfsItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {

            int iter = 0;
                try {
                    Document document = Jsoup.connect("https://www.twentyfourseven.co.il/he/" +
                            gender.toLowerCase() +"/new.html").get();

                    Element element = document.getElementsByClass("products-grid").get(0);
                    Elements items = element.getElementsByClass("item");

                    for (Node item_node : items) {

                        ShoppingItem shoppingItem = new ShoppingItem();

                        String link = item_node.childNode(3).childNode(0).attr("href");

                        Document document1;
                        try {
                            document1 = Jsoup.connect(link).get();
                        }
                        catch (org.jsoup.HttpStatusException ex){
                            continue;
                        }
                        Elements elements1 = document1.
                                getElementsByClass("product-image product-image-zoom");

                        Elements old_prices = document1.getElementsByClass("old-price");
                        Elements special_prices = document1.getElementsByClass("special-price");

                        String price;
                        if (old_prices.size() > 0 && special_prices.size() > 0) {

                            String old_p = old_prices.get(0).
                                    childNode(3).childNode(0).
                                    toString().replace("&nbsp;₪", "");
                            shoppingItem.setPrice(old_p);

                            shoppingItem.setOn_sale(true);
                            String reduced_p = special_prices.get(0).
                                    childNode(3).childNode(0).
                                    toString().replace("&nbsp;₪", "");

                            shoppingItem.setReduced_price(reduced_p);
                        } else {
                            price = item_node.
                                    childNode(7).childNode(1).
                                    childNode(1).childNode(0).
                                    toString().replace("&nbsp;₪", "");

                            shoppingItem.setPrice(price);
                        }

                        Elements elements2 = document1.getElementsByClass("std shortDescription");
                        String description = elements2.get(0).childNode(0).toString().replace("\n", "");
                        ArrayList<String> name = new ArrayList<>(Arrays.asList(description.split(" ")));

                        String imageUrl;

                        try {
                            imageUrl = elements1.get(0).
                                    childNode(7).childNode(1).
                                    childNode(1).childNode(1).attr("src");
                        } catch (IndexOutOfBoundsException ex) {
                            imageUrl = elements1.get(0).
                                    childNode(7).childNode(1).
                                    childNode(3).childNode(1).attr("src");
                        }

                        ArrayList<String> images = new ArrayList<>();
                        int num = elements1.get(0).childNode(7).childNodeSize();
                        int images_num = (num - 3) / 2;

                        for (int i = 1; i < images_num + 1; ++i) {
                            if (i > 4) break;
                            String image = imageUrl.split("-")[0] + "-" + i + ".jpg";
                            images.add(image);
                        }
                        if (images.size() < 4) {
                            for (int i = images.size(); i < 4; ++i) {
                                images.add(imageUrl);
                            }
                        }

                        shoppingItem.setImages(images);
                        shoppingItem.setGender(gender);
                        shoppingItem.setSeller("TwentyFourSeven");
                        shoppingItem.setName(name);
                        shoppingItem.setSite_link(link);

                        iter++;
                        entranceModel.addItem(shoppingItem);
                        entranceModel.setCurrentTfsItem(iter);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            return null;
        }
    }
    private static class getHoodies extends AsyncTask <Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            entranceModel.clearAllHoodiesItems();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... page_num) {

            int iter = 0;

                try {
                    String url = gender.equals(Macros.CustomerMacros.WOMEN) ?
                            "https://www.hoodies.co.il/new?p=1" :
                            "https://www.hoodies.co.il/men/newm";
                    Document document = Jsoup.connect(url).get();
                    Elements elements = document.getElementsByClass("product-item-photo-shop");

                    for(Element element : elements){

                        ShoppingItem shoppingItem = new ShoppingItem();
                        String link = element.childNode(1).attr("href");

                        Document prod_elements;
                        try {
                            prod_elements = Jsoup.connect(link).get();
                        }
                        catch (org.jsoup.HttpStatusException ex){
                            continue;
                        }

                        ArrayList<String> images = new ArrayList<>();
                        Elements pook6 = prod_elements.getElementsByAttributeValue("data-gallery-role", "gallery-placeholder");
                        Elements pook7 = pook6.get(0).getElementsByClass("idus-slider-slide-img");

                        for (int i=0; i < pook7.size()/2; ++i){
                            images.add(pook7.get(i).attr("src"));
                        }
                        if(images.size() < 4){
                            for(int j = images.size(); j<4; j++){
                                images.add(images.get(0));
                            }
                        }

                        Elements elements1 = prod_elements.getElementsByAttributeValue("type", "application/ld+json");
                        Node json_node = elements1.get(0).childNode(0);
                        String pook = json_node.toString();
                        JSONObject jpook = new JSONObject(pook);
                        String description = jpook.get("description").toString();
                        ArrayList<String> description_array = new ArrayList<>(Arrays.asList(description.split(" ")));

                        JSONObject offers = new JSONObject(jpook.get("offers").toString());
                        String price = offers.getString("price");

                        String o_price;
                        Elements old_price = prod_elements.getElementsByAttributeValue("data-price-type", "oldPrice");
                        if(old_price.size() > 0) {
                            o_price = old_price.get(0).childNode(0).childNode(0).toString().replace("&nbsp;₪", "");
                            shoppingItem.setPrice(o_price);
                            shoppingItem.setReduced_price(price);
                            shoppingItem.setOn_sale(true);
                        }
                        else{
                            shoppingItem.setPrice(price);
                        }

                        shoppingItem.setImages(images);
                        shoppingItem.setGender(gender);
                        shoppingItem.setSeller("Hoodies");
                        shoppingItem.setName(description_array);
                        shoppingItem.setSite_link(link);

                        iter++;
                        entranceModel.addItem(shoppingItem);
                        entranceModel.setCurrentHoodiesItem(iter);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            return null;
        }
    }

    private void animateLogo(){
        if(findViewById(R.id.company_img_logo) != null)
            YoYo.with(Techniques.Bounce).duration(1000).playOn(findViewById(R.id.company_img_logo));
    }

    private static class appEntriesNum extends AsyncTask<Integer, Long, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... integers) {

            Map<String, Object> map = new HashMap<>();
            customerFS.get().addOnSuccessListener(documentSnapshot -> {

                if(documentSnapshot.exists()) {
                    entriesNum = documentSnapshot.get("app_entries") != null ?
                            (long) documentSnapshot.get("app_entries") : 0;
                }

                entriesNum++;
                map.put("app_entries",entriesNum);
                customerFS.update(map);

                if(entriesNum == 5 || entriesNum == 20){
                    publishProgress(entriesNum);
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
            entries.postValue(values[0]);
        }
    }
}
