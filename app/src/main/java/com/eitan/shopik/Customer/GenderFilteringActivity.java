package com.eitan.shopik.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.EntranceViewModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.OutletsModel;
import com.eitan.shopik.explanation.E3Fragment;
import com.eitan.shopik.explanation.ExplanationPagerViewAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenderFilteringActivity extends AppCompatActivity {

    private static final int WOMEN_OUTLET_NUM = 27391;
    private static final int MEN_OUTLET_NUM = 27396;
    private static final int BULK = 5;
    private int page;
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
        setContentView(R.layout.activity_gender_filtering);

        init();

        setToolbarColor();

        topNavListener = item -> {
            Intent selectedIntent = null;
            switch (item.getTitle().toString()) {
                case "Purchase History":
                    //  selectedFragment = new PurchaseHistoryFragment();
                    break;
                case "Log Out":
                    FirebaseAuth.getInstance().signOut();
                    //TODO SIGN OUT FROM SMART LOCK
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
                page = 1;
                model.setGender(gender);
                model.setImageUrl(imageUrl);
                model.setName(name);
                setColors();

                outletsModel.clearAllOutlets();
                fetchData fetchData = new fetchData();
                fetchData.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        outletsModel.clearAllOutlets();

        fetchData fetchData = new fetchData();
        fetchData.execute();

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
        Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(ContextCompat.getColor(this, R.color.GenderScreenTheme));
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

        page = 1;

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

    private class fetchData extends AsyncTask<Void,Integer,Void> {

        String outlet_data = "";
        String new_items_data = "";
        Map map;

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected Void doInBackground(Void... voids) {

            try {
                //liked items
                for (String type : Macros.Items.getAllItemTypes()) {
                    FirebaseDatabase.getInstance().getReference().
                            child(Macros.ITEMS).
                            child(gender).
                            child(type).
                            orderByChild(Macros.CustomerMacros.LIKED).
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        if (gender.equals(Macros.CustomerMacros.WOMEN))
                                            getWomenItem(type, dataSnapshot);
                                        else
                                            getMenItem(type, dataSnapshot);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d(Macros.TAG, "getLikedItems()::doInBackground() " + databaseError.getMessage());
                                }

                            });
                }

                //Outlet Items
                int num = gender.equals(Macros.CustomerMacros.WOMEN) ? WOMEN_OUTLET_NUM : MEN_OUTLET_NUM;
                int cur_page = page;
                for (int i = cur_page; i < cur_page + BULK; ++i) {
                    page++;
                    getOutletItems(i, num);
                }

                //New Items
                map = new HashMap<>();
                if(gender.equals(Macros.CustomerMacros.WOMEN)){
                    for(int cat_num : Macros.Arrays.WOMEN_CLOTHES_TYPES) {
                        getWomenItems(cat_num);
                    }
                }
                else {
                    for (int cat_num : Macros.Arrays.MEN_CLOTHES_TYPES) {
                        getMenItems(cat_num);
                    }
                }

            }
            catch (IOException e) {
                Log.d(Macros.TAG,"GenderFilteringActivity::fetchData(): " + e.getMessage());
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getWomenItem(final String type, DataSnapshot dataSnapshot){
            entranceModel.removeAllType(type,gender);
            long count = 0;
            long max = 0;
            String item_id = "";
            for(DataSnapshot data : dataSnapshot.getChildren()){
                Map<String,Object> map = (Map) data.getValue();
                if( map != null && map.get(Macros.CustomerMacros.LIKED) != null ) {
                    Map<String, Object> map2 = (Map<String, Object>) map.get(Macros.CustomerMacros.LIKED);
                    assert map2 != null;
                    count = map2.size();
                }
                if(count > max) {
                    max = count;
                    item_id = data.getKey();
                }
            }

            RecyclerItem recyclerItem = new RecyclerItem(max + " Likes",null);
            recyclerItem.setGender(gender);
            recyclerItem.setType(type);
            recyclerItem.setId(item_id);
            recyclerItem.setLikes(max);

            Database connection = new Database();
            ArrayList<String> list = new ArrayList<>();
            list.add(connection.getASOSimageUrl(2, null, item_id));
            list.add(connection.getASOSimageUrl(4, null, item_id));
            list.add(connection.getASOSimageUrl(3, null, item_id));
            list.add(connection.getASOSimageUrl(2, null, item_id));

            recyclerItem.setImages(list);

            entranceModel.addWomenLikedItem(recyclerItem);
            entranceModel.setLiked_items(gender);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getMenItem(final String type, DataSnapshot dataSnapshot){
            entranceModel.removeAllType(type,gender);
            long count = 0;
            long max = 0;
            String item_id = "";
            for(DataSnapshot data : dataSnapshot.getChildren()){
                Map<String,Object> map = (Map) data.getValue();
                if( map != null && map.get(Macros.CustomerMacros.LIKED) != null ) {
                    Map<String, Object> map2 = (Map<String, Object>) map.get(Macros.CustomerMacros.LIKED);
                    assert map2 != null;
                    count = map2.size();
                }
                if(count > max) {
                    max = count;
                    item_id = data.getKey();
                }
            }

            RecyclerItem recyclerItem = new RecyclerItem(max + " Likes",null);
            recyclerItem.setGender(gender);
            recyclerItem.setType(type);
            recyclerItem.setId(item_id);
            recyclerItem.setLikes(max);

            Database connection = new Database();
            ArrayList<String> list = new ArrayList<>();
            list.add(connection.getASOSimageUrl(2, null, item_id));
            list.add(connection.getASOSimageUrl(4, null, item_id));
            list.add(connection.getASOSimageUrl(3, null, item_id));
            list.add(connection.getASOSimageUrl(2, null, item_id));

            recyclerItem.setImages(list);

            entranceModel.addMenLikedItem(recyclerItem);
            entranceModel.setLiked_items(gender);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getOutletItems(int page_num, int cat_num) throws IOException {
            URL url = new URL("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + page_num);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    outlet_data = outlet_data + line;
                }

                String[] data_split = outlet_data.split("\"products\":", 2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon").
                        replaceAll("uoh",".uoh");

                outlet_data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(outlet_data);
                int total_items =  JA.length();
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

                    ArrayList<String> list = new ArrayList<>();
                    String[] name = JO.get("description").toString().split(" ");
                    for(String word : name){
                        if (!word.equals(""))
                            list.add(word.toLowerCase());
                    }

                    Currency shekel = Currency.getInstance("ILS");
                    String currency_symbol = shekel.getSymbol();
                    Double current = Double.parseDouble(price) * Macros.POUND_TO_ILS;
                    price = new DecimalFormat("##.##").format(current) + currency_symbol;
                    RecyclerItem recyclerItem = new RecyclerItem(branda, link);
                    recyclerItem.setPrice(price);
                    recyclerItem.setLink(link);
                    recyclerItem.setDescription(list);
                    recyclerItem.setId(id);
                    recyclerItem.setType("OUTLET");

                    if((boolean) JO.get("isOutlet") ){
                        recyclerItem.setOutlet(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setOutlet(false);

                    if((boolean) JO.get("isSale") ){
                        recyclerItem.setSale(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setSale(false);

                    Database connection = new Database();

                    if (JO.get("url").toString().contains("prd"))
                        recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                    else
                        recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));

                    outletsModel.addToOutlets(recyclerItem);
                    publishProgress(i+1,total_items,page_num);
                }
            }
            catch (Exception e ) {
                Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
            }
            finally {
                httpURLConnection.disconnect();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getMenItems(int cat_num) throws IOException {
            URL url = new URL("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + 1);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {
                String cat = "";
                switch (cat_num){
                    case 17184:
                        cat = Macros.Items.NEW_SHOES;
                        break;
                    case 27441:
                        cat = Macros.Items.NEW_CLOTHING;
                        break;
                    case 13500:
                        cat = Macros.Items.NEW_TRENDING;
                }
                entranceModel.addMen_new_num(cat,0);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    new_items_data += line;
                }

                String[] data_split = new_items_data.split("\"products\":", 2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon").
                        replaceAll("uoh",".uoh");

                new_items_data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(new_items_data);
                int total_items =  JA.length();
                for (int i = 0; i < total_items; ++i) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    String imageUrl = "https://" + JO.get("image").toString().
                            replace(".com", ".com/").
                            replace("products", "products/");
                    int opop = imageUrl.lastIndexOf("-");

                    String color = imageUrl.substring(opop + 1);
                    String id = JO.get("id").toString();
                    String link = "https://www.asos.com/" + JO.get("url").toString().
                            replace("prd","/prd/").
                            replace("asos-designasos","asos-design/asos");

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

                    if((boolean) JO.get("isOutlet")){
                        recyclerItem.setOutlet(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setOutlet(false);

                    if((boolean) JO.get("isSale") ){
                        recyclerItem.setSale(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setSale(false);

                    Database connection = new Database();

                    if (JO.get("url").toString().contains("prd")) {
                        recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                    } else {
                        recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));
                    }

                    entranceModel.addMenItem(recyclerItem);
                    entranceModel.setList(gender);
                }
            }
            catch (Exception e ) {
                Log.d(Macros.TAG, "E1::getMenItems() " + Objects.requireNonNull(e.getMessage()));
            }
            finally {
                httpURLConnection.disconnect();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getWomenItems(int cat_num) throws IOException {

            URL url = new URL("https://www.asos.com/cat/?cid=" + cat_num + "&page=" + 1);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {

                String cat = "";
                switch (cat_num){
                    case 6992:
                        cat = Macros.Items.NEW_SHOES;
                        break;
                    case 2623:
                        cat = Macros.Items.NEW_CLOTHING;
                        break;
                    case 13497:
                        cat = Macros.Items.NEW_TRENDING;
                }

                entranceModel.addWomen_new_num(cat,0);
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    new_items_data = new_items_data + line;
                }

                String[] data_split = new_items_data.split("\"products\":", 2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon").
                        replaceAll("uoh",".uoh");

                new_items_data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(new_items_data);
                int total_items =  JA.length();
                for (int i = 0; i < total_items; ++i) {
                    JSONObject JO = (JSONObject) JA.get(i);
                    String imageUrl = "https://" + JO.get("image").toString().
                            replace(".com", ".com/").
                            replace("products", "products/");

                    int opop = imageUrl.lastIndexOf("-");

                    String color = imageUrl.substring(opop + 1);
                    String id = JO.get("id").toString();
                    String link = "https://www.asos.com/" + JO.get("url").toString().
                            replace("prd","/prd/").
                            replace("asos-designasos", "asos-design/asos");

                    String price = JO.get("price").toString();

                    ArrayList<String> list = new ArrayList<>();
                    String[] name = JO.get("description").toString().split(" ");
                    for(String word : name){
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

                    if((boolean) JO.get("isOutlet")){
                        recyclerItem.setOutlet(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setOutlet(false);

                    if((boolean) JO.get("isSale") ){
                        recyclerItem.setSale(true);
                        recyclerItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        recyclerItem.setSale(false);

                    Database connection = new Database();

                    if (JO.get("url").toString().contains("prd"))
                        recyclerItem.setImages(connection.getASOSRecyclerImage("product", color, id));
                    else
                        recyclerItem.setImages(connection.getASOSRecyclerImage("group", color, id));

                    entranceModel.addWomenItem(recyclerItem);
                    entranceModel.setList(gender);
                }
            }
            catch (Exception e ) {
                Log.d(Macros.TAG,"E1::getWomenItems() " +  Objects.requireNonNull(e.getMessage()));
            }
            finally {
                httpURLConnection.disconnect();
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (mMainPager.getCurrentItem()){
            case 0:
                finish();
                break;
            case 1:
                mMainPager.setCurrentItem(0);
                break;
            case 2:
                mMainPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.getGender().removeObservers(this);
        model.getSub_category().removeObservers(this);
    }
}
