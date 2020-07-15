package com.eitan.shopik.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Adapters.MainPagerAdapter;
import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.eitan.shopik.ViewModels.SuggestedModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INTERNAL_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_INVALID_REQUEST;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR;
import static com.google.android.gms.ads.AdRequest.ERROR_CODE_NO_FILL;
import static com.google.android.gms.ads.RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;
import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_LEFT;
import static com.google.android.gms.ads.formats.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE;
import static com.google.android.gms.ads.formats.NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_SQUARE;

public class CustomerMainActivity extends AppCompatActivity {

    private DatabaseReference customerDB;
    private String item_type;
    private String item_gender;
    private String item_sub_category;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private MainModel mainModel;
    private FirebaseAuth mAuth;
    private TextView mUserName;
    private CircleImageView mUserProfile;
    private String customerFirstName, imageUrl;
    private int color;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ViewPager mainPager;
    private androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener;
    private SuggestedModel suggestedModel;
    private ValueEventListener valueEventListener;
    private UnifiedNativeAd tempAd;
    private static final int NUM_OF_ADS = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        for(int i=0;i<NUM_OF_ADS;++i){
            loadAds();
        }

        valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    suggestedModel.setPreferred(new PreferredItem((Map) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(Macros.TAG, "Suggested::getPreferred() " + Objects.requireNonNull(databaseError.getMessage()));
            }
        };
        customerDB.addValueEventListener(valueEventListener);

        getItems get = new getItems();
        get.execute();

        loadCustomerInfo();

        setToolbarColor();

        setViewPager();

        setTabLayout();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setTint(color);
                if (tab.getPosition() == 1) {
                    tab.getOrCreateBadge().setNumber(0);
                    tab.getOrCreateBadge().setVisible(false, true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setTint(Color.BLACK);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        topNavListener = item -> {
            Intent selectedIntent = null;
            switch (item.getTitle().toString()) {
                case "Log Out":
                    mAuth.signOut();
                    if (mAuth.getCurrentUser() == null) {
                        selectedIntent = new Intent(CustomerMainActivity.this, LandingPageActivity.class);
                        startActivity(selectedIntent);
                    }
                    break;
                case "My Profile":
                    selectedIntent = new Intent(CustomerMainActivity.this, CustomerSettingsActivity.class);
                    selectedIntent.putExtra("from_activity", "CustomerMainActivity");
                    selectedIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    break;
                case "Home":
                    Intent intent = new Intent(CustomerMainActivity.this, GenderFilteringActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("gender", item_gender);
                    bundle.putString("name", customerFirstName);
                    bundle.putString("imageUrl", imageUrl);
                    intent.putExtra("bundle", bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    return true;
            }
            if (selectedIntent != null) {
                startActivity(selectedIntent);
                return true;
            }
            return false;
        };

        setToolbar();
    }

    private void loadAds() {

        VideoOptions videoOptions = new VideoOptions.Builder().
                setStartMuted(false).
                setClickToExpandRequested(true).
                build();

        NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder().
                setAdChoicesPlacement(ADCHOICES_TOP_LEFT).
                setMediaAspectRatio(NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE).
                setVideoOptions(videoOptions).
                build();

        AdLoader adLoader = new AdLoader.Builder(Objects.requireNonNull(this),"ca-app-pub-3940256099942544/2247696110")
                .forUnifiedNativeAd(unifiedNativeAd -> {
                    // Show the ad.
                    tempAd = unifiedNativeAd;
                })
                .withAdListener(new AdListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        ShoppingItem dummy = new ShoppingItem();
                        dummy.setAd(true);
                        dummy.setNativeAd(tempAd);
                        mainModel.addAd(dummy);
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        Toast.makeText(CustomerMainActivity.this, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show();
                    }

                })
                .withNativeAdOptions(nativeAdOptions)
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());
    }

    private void setToolbarColor() {
        if (item_gender.equals(Macros.CustomerMacros.WOMEN))
            color = getColor(R.color.womenColor);
        else
            color = getColor(R.color.menColor);
    }

    private void setTabLayout() {

        tabLayout.setupWithViewPager(mainPager, true);
        tabLayout.setSelectedTabIndicatorColor(color);
        tabLayout.setTabIndicatorFullWidth(true);
        tabLayout.setTabTextColors(Color.BLACK, color);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Swipe");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Likes");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("All Items");
        Objects.requireNonNull(tabLayout.getTabAt(3)).setText("Suggested");

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_touch_app_black);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_favorite_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_search);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_check_circle_green);

        tabLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(tabLayout.getSelectedTabPosition())).getIcon()).setTint(color);
    }

    private void setViewPager() {

        MainPagerAdapter mainPgerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPager = findViewById(R.id.customer_container);
        mainPager.setAdapter(mainPgerAdapter);
        mainPager.setPageTransformer(false, new ZoomOutPageTransformer());
    }

    private void setToolbar() {
        toolbar.setOnMenuItemClickListener(topNavListener);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar.setSoundEffectsEnabled(true);
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

    private void init() {

        mainModel = new ViewModelProvider(this).get(MainModel.class);
        suggestedModel = new ViewModelProvider(this).get(SuggestedModel.class);
        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        GenderModel genderModel = new ViewModelProvider(this).get(GenderModel.class);

        mUserName = findViewById(R.id.user_name);
        mUserProfile = findViewById(R.id.user_profile);
        progressBar = findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getBundleExtra("bundle");

        assert bundle != null;
        String extra_type = bundle.getString("type");

        if (extra_type != null)
            item_type = extra_type;
        else
            item_type = Macros.Items.BAG;

        String extra_gender = bundle.getString("gender");
        if (extra_gender != null)
            item_gender = extra_gender;
        else
            item_gender = Macros.CustomerMacros.WOMEN;

        String extra_sub_category = bundle.getString("sub_category");
        if (extra_sub_category != null) {
            item_sub_category = extra_sub_category;
        } else item_sub_category = "";

        genderModel.setGender(item_gender);
        genderModel.setType(item_type);
        genderModel.setSub_category(item_sub_category);

        customerDB = FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).
                child(item_gender).child(Macros.CustomerMacros.PREFERRED_ITEMS).
                child(item_type).child(item_sub_category);

        customerFirstName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        imageUrl = bundle.getString("imageUrl");
        mAuth = FirebaseAuth.getInstance();
        tabLayout = findViewById(R.id.top_nav);

        toolbar = findViewById(R.id.customer_toolbar);
    }

    private void loadCustomerInfo() {
        if (imageUrl != null)
            Glide.with(this).load(imageUrl).into(mUserProfile);

        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour > 12 && hour < 17) {
            greeting = "Good afternoon, ";
        } else if (hour > 4 && hour <= 12) {
            greeting = "Good morning, ";
        } else if (hour >= 17 && hour < 22) {
            greeting = "Good evening, ";
        } else {
            greeting = "Good night, ";
        }

        String first_name = customerFirstName.split(" ")[0];
        if (first_name.length() > 5) {
            String msg = greeting + System.lineSeparator() + first_name;
            mUserName.setText(msg);
        } else {
            String msg = greeting + first_name;
            mUserName.setText(msg);
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

    private class getItems extends AsyncTask<Void, Integer, Void> {

        String data = "";
        Pair<Integer,Integer> cat_num;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                cat_num = Macros.Functions.getCategoryNum(item_gender, item_sub_category, item_type);
                for (int i = 1; i < cat_num.second + 1; ++i) {
                    getAllItems(i);
                }
            } catch (Exception e) {
                Log.d(Macros.TAG, "MainCustomerActivity::getItems() " + e.getMessage());
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getAllItems(int page_num) throws IOException {

            URL url = new URL("https://www.asos.com/cat/?cid=" + cat_num.first + "&page=" + page_num);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            try {
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                String[] data_split = data.split("\"products\":", 2);
                String koko = data_split[1];
                koko = koko.replaceAll("u002F", "").
                        replaceAll("urban", ".urban").
                        replaceAll("gg-", "").
                        replaceAll("under", ".under").
                        replaceAll("ufluff", ".ufluff").
                        replaceAll("upper", ".upper").
                        replaceAll("uncommon", ".uncommon").
                        replaceAll("uoh", ".uoh");

                data = koko.substring(koko.indexOf("["), koko.indexOf("]")) + "]";

                JSONArray JA = new JSONArray(data);
                int total_items = JA.length();
                for (int i = 0; i < total_items; ++i) {

                    publishProgress(i, total_items);

                    JSONObject JO = (JSONObject) JA.get(i);
                    String imageUrl = "https://" + JO.get("image").toString().
                            replace(".com", ".com/").
                            replace("products", "products/");

                    int opop = imageUrl.lastIndexOf("-");
                    String id = JO.get("id").toString();

                    String color = imageUrl.substring(opop + 1);

                    String link = "https://www.asos.com/" + JO.get("url").toString().
                            replace("prd", "/prd/").
                            replace("asos-designasos", "asos-design/asos");

                    String seller_name = "ASOS";
                    String price = JO.get("price").toString();
                    String branda = "";
                    String seller_id = "gxGB5zUoNed0rizltWVC9y8FceA3";

                    ShoppingItem shoppingItem = new ShoppingItem();

                    boolean isExclusive = JO.get("description").toString().toLowerCase().contains("exclusive");
                    shoppingItem.setExclusive(isExclusive);

                    ArrayList<String> list = new ArrayList<>();
                    String[] name = JO.get("description").toString().split(" ");
                    for (String word : name) {
                        if (!word.equals("")) {
                            list.add(word.toLowerCase());
                        }
                    }

                    for (String brand : Macros.Items.brands) {
                        if (JO.get("description").toString().toLowerCase().contains(brand.toLowerCase()))
                            branda = brand;
                    }

                    if ((boolean) JO.get("isOutlet")) {
                        shoppingItem.setOutlet(true);
                        shoppingItem.setReduced_price(JO.get("reducedPrice").toString());
                    } else
                        shoppingItem.setOutlet(false);

                    if ((boolean) JO.get("isSale")) {
                        shoppingItem.setOn_sale(true);
                        shoppingItem.setReduced_price(JO.get("reducedPrice").toString());
                    } else
                        shoppingItem.setOn_sale(false);

                    shoppingItem.setId_in_seller(id);
                    shoppingItem.setColor(color);
                    shoppingItem.setType(item_type);
                    shoppingItem.setBrand(branda);
                    shoppingItem.setName(list);
                    shoppingItem.setPrice(price);
                    shoppingItem.setSeller(seller_name);
                    shoppingItem.setSellerId(seller_id);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setPage_num(page_num);
                    shoppingItem.setCatagory_num(cat_num.first);
                    shoppingItem.setGender(item_gender);
                    shoppingItem.setId(id);
                    shoppingItem.setAd(false);
                    shoppingItem.setLikes(0);
                    shoppingItem.setUnlikes(0);
                    shoppingItem.setLikedUsers(null);
                    shoppingItem.setUnlikedUsers(null);
                    shoppingItem.setSub_category(item_sub_category);

                    getLikes(shoppingItem);
                  //  getVideo(id,shoppingItem);
                }

            }
            catch (Exception e) {
                Log.d(Macros.TAG, "MainCustomerActivity::getAllItems() " + e.getMessage());
            }
            finally {
                httpURLConnection.disconnect();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress((values[0] / values[1]) * 100);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getLikes(final ShoppingItem shoppingItem) {

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(item_gender).
                child(item_type).
                child(shoppingItem.getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map<String, String> unliked_users = new HashMap<>();
                            Map<String, String> liked_users = new HashMap<>();

                            //set likes num
                            if (dataSnapshot.hasChild(Macros.Items.LIKED)) {
                                shoppingItem.setLikes(dataSnapshot.child(Macros.Items.LIKED).getChildrenCount());
                                liked_users = (Map) dataSnapshot.child(Macros.Items.LIKED).getValue();
                            } else {
                                shoppingItem.setLikes(0);
                                shoppingItem.setLikedUsers(null);
                            }
                            //set unlikes num
                            if (dataSnapshot.hasChild(Macros.Items.UNLIKED)) {
                                shoppingItem.setUnlikes(dataSnapshot.child(Macros.Items.UNLIKED).getChildrenCount());
                                unliked_users = (Map) dataSnapshot.child(Macros.Items.UNLIKED).getValue();
                            } else {
                                shoppingItem.setUnlikes(0);
                                shoppingItem.setUnlikedUsers(null);
                            }

                            if (liked_users == null || liked_users.isEmpty()) {
                                if (unliked_users == null || unliked_users.isEmpty()) {
                                    getCompanyInfo(shoppingItem);
                                } else {
                                    getUnlikedUserInfo(shoppingItem, unliked_users);
                                }
                            } else
                                getInteractedUsersInfo(shoppingItem, liked_users, unliked_users);

                        } else {
                            shoppingItem.setLikes(0);
                            shoppingItem.setUnlikes(0);
                            shoppingItem.setLikedUsers(null);
                            shoppingItem.setUnlikedUsers(null);
                            getCompanyInfo(shoppingItem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d(Macros.TAG, "CustomerHomeFragment::onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getInteractedUsersInfo(ShoppingItem shoppingItem, Map<String, String> liked_users, Map<String, String> unliked_users) {

        ArrayList<String> list = new ArrayList<>(liked_users.keySet());
        for (String customer_id : list) {
            FirebaseFirestore.getInstance().
                    collection(Macros.CUSTOMERS)
                    .whereEqualTo("id", customer_id).
                    get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        LikedUser likedUser = new LikedUser(
                                Objects.requireNonNull(doc.get("imageUrl")).toString(),
                                Objects.requireNonNull(doc.get("first_name")).toString(),
                                Objects.requireNonNull(doc.get("last_name")).toString()
                        );

                        if (doc.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                            likedUser.setLast_name(likedUser.getLast_name() + " (You)");

                        mainModel.setCustomers_info(customer_id, likedUser);
                        likedUser.setFavorite(Objects.equals(liked_users.get(doc.getId()), Macros.CustomerMacros.FAVOURITE));
                        shoppingItem.addLikedUser(likedUser);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d(Macros.TAG, "CustomerHomeFragment::users : " + customer_id + " do not exist");
                }
            });
        }
        if (unliked_users == null || unliked_users.isEmpty())
            getCompanyInfo(shoppingItem);
        else
            getUnlikedUserInfo(shoppingItem, unliked_users);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUnlikedUserInfo(final ShoppingItem shoppingItem, Map<String, String> unlikes_list) {

        assert unlikes_list != null;
        ArrayList<String> list = new ArrayList<>(unlikes_list.keySet());

        for (String customer_id : list) {
            FirebaseFirestore.getInstance().
                    collection(Macros.CUSTOMERS)
                    .whereEqualTo("id", customer_id).
                    get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        LikedUser likedUser = new LikedUser(
                                Objects.requireNonNull(doc.get("imageUrl")).toString(),
                                Objects.requireNonNull(doc.get("first_name")).toString(),
                                Objects.requireNonNull(doc.get("last_name")).toString()
                        );

                        if (doc.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))
                            likedUser.setLast_name(likedUser.getLast_name() + " (You)");

                        mainModel.setCustomers_info(customer_id, likedUser);
                        shoppingItem.addUnlikedUser(likedUser);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d(Macros.TAG, "CustomerHomeFragment::users : " + customer_id + " do not exist");
                }
            });
        }
        getCompanyInfo(shoppingItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getCompanyInfo(final ShoppingItem shoppingItem) {

        final String company_id = shoppingItem.getSellerId();

        if (Objects.requireNonNull(mainModel.getCompanies_info().getValue()).containsKey(company_id)) {
            shoppingItem.setSeller(Objects.requireNonNull((Objects.requireNonNull(mainModel.getCompanies_info().getValue().get(company_id))).get("seller")).toString());
            shoppingItem.setSellerLogoUrl(Objects.requireNonNull((Objects.requireNonNull(mainModel.getCompanies_info().getValue().get(company_id))).get("logo_url")).toString());
            mainModel.add_item(shoppingItem);
            androidx.core.util.Pair<String, ShoppingItem> pair = new androidx.core.util.Pair<>(shoppingItem.getId(), shoppingItem);
            mainModel.addItemId(pair);
        }
        else {
            FirebaseFirestore.getInstance().collection(Macros.COMPANIES).document(shoppingItem.getSellerId()).get().addOnSuccessListener(documentSnapshot -> {
                Map map = new HashMap<>();
                map.put("seller", Objects.requireNonNull(documentSnapshot.get("name")).toString());
                map.put("logo_url", Objects.requireNonNull(documentSnapshot.get("logo_url")).toString());
                mainModel.setCompanies_info(shoppingItem.getSellerId(), map);
                shoppingItem.setSeller(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                shoppingItem.setSellerLogoUrl(Objects.requireNonNull(documentSnapshot.get("logo_url")).toString());
                mainModel.add_item(shoppingItem);
                androidx.core.util.Pair<String, ShoppingItem> pair = new androidx.core.util.Pair<>(shoppingItem.getId(), shoppingItem);
                mainModel.addItemId(pair);
            });
        }
    }

    @Override
    public void onBackPressed() {
        InterstitialAd mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Macros.INTERSTITIAL_AD_DEBUG_CODE);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                switch (i){
                    case ERROR_CODE_INTERNAL_ERROR:
                        Log.d(Macros.TAG,"Something happened internally; for instance, an invalid response was received from the ad server.");
                        break;
                    case ERROR_CODE_INVALID_REQUEST:
                        Log.d(Macros.TAG,"The ad request was invalid; for instance, the ad unit ID was incorrect.");
                        break;
                    case ERROR_CODE_NETWORK_ERROR:
                        Log.d(Macros.TAG,"The ad request was unsuccessful due to network connectivity.");
                        break;
                    case ERROR_CODE_NO_FILL:
                        Log.d(Macros.TAG,"The ad request was successful, but no ad was returned due to lack of ad inventory.");
                        break;
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mInterstitialAd.show();
            }
        });

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customerDB.removeEventListener(valueEventListener);
        mainModel.getAll_items().removeObservers(this);
        mainModel.getAll_items().removeObservers(this);
        mainModel.getCustomers_info().removeObservers(this);
        mainModel.getCompanies_info().removeObservers(this);
        mainModel.clearAds();
        suggestedModel.getPreferred().removeObservers(this);
        progressBar = null;
    }
}