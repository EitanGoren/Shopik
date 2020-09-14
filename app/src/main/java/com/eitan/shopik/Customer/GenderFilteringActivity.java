package com.eitan.shopik.Customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Adapters.ExplanationPagerViewAdapter;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplicationActivity;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.OutletsModel;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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
                animateLogo();

                model.setGender(parent.getItemAtPosition(position).toString());
                gender = parent.getItemAtPosition(position).toString();
                model.setImageUrl(imageUrl);
                model.setName(name);
                setColors();

                Objects.requireNonNull(entranceModel.getItems().getValue()).clear();

                //LIKED ITEMM
                new fetchLikedItems().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                //Outlet Items
                int num = gender.equals(Macros.CustomerMacros.WOMEN) ? WOMEN_OUTLET_NUM : MEN_OUTLET_NUM;
                new fetchOutlet().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,num,1);

                if (gender.equals(Macros.CustomerMacros.WOMEN))
                    new getNewInWomenShein().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1);
                else
                    new getNewInMenShein().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1);
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
            gender_spinner.setSelection(Objects.equals(bundle.getString("gender"),
                    Macros.CustomerMacros.WOMEN) ? 0 : 1);
            gender = bundle.getString("gender");
        }
        else {
            gender_spinner.setSelection(0);
            gender = Macros.CustomerMacros.WOMEN;
        }

        setColors();

        Handler handler = new Handler();
        handler.postDelayed(() -> ShopikApplicationActivity.LoadAds(5),4000);
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
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
            }
            return null;
        }
    }

    private static class fetchOutlet extends AsyncTask<Integer,Integer,Void> {

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

                outletsModel.setTotalItems(products.size());
                for (Element prod : products) {
                    ++i;
                    RecyclerItem recyclerItem = new RecyclerItem(null,null);

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
                    String brand;
                    Elements brand_ele;
                    try {
                        brand_ele = document2.getElementsByClass("brand-description");
                        Elements pop = brand_ele.get(0).getAllElements();
                        brand = pop.get(4).childNode(0).toString().replace("&amp;", "&");
                        recyclerItem.setBrand(brand);
                    }
                    catch (IndexOutOfBoundsException ex) {
                        try {
                            brand_ele = document2.getElementsByClass("product-description");
                            brand = brand_ele.get(1).childNode(4).
                                    childNode(0).childNode(1).toString().
                                    replace(" by ", "");
                            recyclerItem.setBrand(brand);
                        }
                        catch (IndexOutOfBoundsException e) {
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
                    outletsModel.setCurrentItem(i);
                }
            }
            catch (Exception e ) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class getNewInWomenShein extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {
            try {
                for (String cat_name : Macros.Arrays.WOMEN_CLOTHES_TYPES) {
                    String cat = "";
                    switch (cat_name) {
                        case "Shoes-sc-00200195":
                            cat = Macros.NEW_SHOES;
                            break;
                        case "Clothing-sc-00200200":
                            cat = Macros.NEW_CLOTHING;
                            break;
                        case "Home-sc-00214997":
                            cat = Macros.NEW_TRENDING;
                    }

                    Document document = Jsoup.connect("https://il.shein.com/" +
                            gender.toLowerCase() + "-" + cat_name + ".html").get();

                    assert document != null;
                    Elements elements = document.getElementsByClass("j-goodsli");

                    for (int j = 0; j < 20; ++j) {

                        String link2 = elements.get(j).getElementsByClass("c-goodsitem__goods-name").
                                get(1).attr("href");
                        String link = "https://il.shein.com" + link2;

                        RecyclerItem recyclerItem = new RecyclerItem(cat, link);

                        Document prod_elements;
                        try {
                            prod_elements = Jsoup.connect(link).get();
                        } catch (org.jsoup.HttpStatusException ex) {
                            continue;
                        }

                        String json_prepare = prod_elements.
                                childNode(2).childNode(3).
                                childNode(15).childNode(17).
                                childNode(0).toString();

                        String productIntroData = json_prepare.
                                split("productIntroData:")[1].
                                split("abt: ")[0];

                        String json = "[" + productIntroData + "]";

                        JSONArray jsonArray = new JSONArray(json);
                        JSONArray imagesArray = jsonArray.getJSONObject(0).
                                getJSONObject("goods_imgs").
                                getJSONArray("detail_image");

                        ArrayList<String> images = new ArrayList<>();
                        for (int i = 0; i < imagesArray.length(); ++i) {
                            String img = imagesArray.getJSONObject(i).getString("origin_image");
                            images.add("http:" + img);
                        }
                        if (images.size() < 4) {
                            for (int k = images.size(); k < 4; k++) {
                                images.add(images.get(0));
                            }
                        }
                        JSONObject details = jsonArray.getJSONObject(0).
                                getJSONObject("detail");

                        String price = details.
                                getJSONObject("retailPrice").
                                getString("amountWithSymbol");

                        String salePrice = details.
                                getJSONObject("salePrice").
                                getString("amount");

                        if (!salePrice.equals(price)) {
                            recyclerItem.setReduced_price(salePrice);
                            recyclerItem.setSale(true);
                        }

                        String brand = details.getString("brand").
                                equals("") ? "SHEIN" : details.getString("brand");

                        recyclerItem.setPrice(price);
                        recyclerItem.setImages(images);
                        recyclerItem.setLink(link);
                        recyclerItem.setType(cat);
                        recyclerItem.setBrand(brand);
                        entranceModel.addWomenItem(recyclerItem);
                    }
                    entranceModel.setList(gender);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static class getNewInMenShein extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {
            try {
                for (String cat_name : Macros.Arrays.MEN_CLOTHES_TYPES) {
                    String cat = "";
                    switch (cat_name) {
                        case "Shoes-Accessories-sc-00201624":
                            cat = Macros.NEW_SHOES;
                            break;
                        case "new-in-sc-00200410":
                            cat = Macros.NEW_CLOTHING;
                            break;
                        case "Activewear-sc-00227302":
                            cat = Macros.NEW_TRENDING;
                    }

                    Document document = Jsoup.connect("https://il.shein.com/" +
                            gender.toLowerCase() + "-" + cat_name + ".html").get();

                    assert document != null;
                    Elements elements = document.getElementsByClass("j-goodsli");

                    for (int j = 0; j < elements.size(); ++j) {

                        String link2 = elements.get(j).getElementsByClass("c-goodsitem__goods-name").
                                get(1).attr("href");
                        String link = "https://il.shein.com" + link2;

                        RecyclerItem recyclerItem = new RecyclerItem(cat, link);

                        Document prod_elements;
                        try {
                            prod_elements = Jsoup.connect(link).get();
                        } catch (org.jsoup.HttpStatusException ex) {
                            continue;
                        }

                        String json_prepare = prod_elements.
                                childNode(2).childNode(3).
                                childNode(15).childNode(17).
                                childNode(0).toString();

                        String productIntroData = json_prepare.
                                split("productIntroData:")[1].
                                split("abt: ")[0];

                        String json = "[" + productIntroData + "]";

                        JSONArray jsonArray = new JSONArray(json);
                        JSONArray imagesArray = jsonArray.getJSONObject(0).
                                getJSONObject("goods_imgs").
                                getJSONArray("detail_image");

                        ArrayList<String> images = new ArrayList<>();
                        for (int i = 0; i < imagesArray.length(); ++i) {
                            String img = imagesArray.getJSONObject(i).getString("origin_image");
                            images.add("http:" + img);
                        }
                        if (images.size() < 4) {
                            for (int k = images.size(); k < 4; k++) {
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

                        if (!salePrice.equals(price)) {
                            recyclerItem.setReduced_price(salePrice);
                            recyclerItem.setSale(true);
                        }

                        String brand = details.getString("brand").
                                equals("") ? "SHEIN" : details.getString("brand");

                        recyclerItem.setPrice(price);
                        recyclerItem.setImages(images);
                        recyclerItem.setLink(link);
                        recyclerItem.setType(cat);
                        recyclerItem.setBrand(brand);
                        entranceModel.addMenItem(recyclerItem);
                    }
                    entranceModel.setList(gender);
                }
                entranceModel.setList(gender);
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
}
