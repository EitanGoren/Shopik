package com.eitan.shopik.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Adapters.MainPagerAdapter;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.RecyclerItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.AllItemsModel;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.eitan.shopik.ViewModels.SuggestedModel;
import com.eitan.shopik.ViewModels.SwipesModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;
import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_LEFT;

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
    private String userId;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ViewPager mainPager;
    private androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener;
    private SuggestedModel suggestedModel;
    private SwipesModel swipesModel;
    private AllItemsModel allItemsModel;
    private ValueEventListener valueEventListener;
    private UnifiedNativeAd tempAd;
    private Pair<Integer,Integer> cat_num;
    private ArrayList<ShoppingItem> allItems;
    private com.facebook.ads.InterstitialAd interstitialAd;
    private BottomNavigationView main_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //facebook ads
        // If you call AudienceNetworkAds.buildInitSettings(Context).initialize()
        // in Application.onCreate() this call is not really necessary.
        // Otherwise call initialize() onCreate() of all Activities that contain ads or
        // from onCreate() of your Splash Activity.
        AdSettings.addTestDevice("a0e31a81-5e54-4e90-80c9-e76b9820ba80");
        AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        AudienceNetworkAds.initialize(this);

        setContentView(R.layout.activity_main);

        if(!isConnectedToInternet()) {
            RelativeLayout LandingLayout = findViewById(R.id.mainLayout);
            Macros.Functions.showSnackbar (
                    LandingLayout,
                    "No Internet connection",
                    this,
                    R.drawable.ic_baseline_signal_cellular
            );
        }

        init();

        getAllCompaniesInfo();

        getLastSwipedItem();

        for( int i=0; i < Macros.NUM_OF_ADS; ++i ){
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

        //initFBInterstitial();

        loadCustomerInfo();

        setToolbarColor();

        setViewPager();

       /*setTabLayout();

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
        */

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

    private void setToolbarColor() {
        if (item_gender.equals(Macros.CustomerMacros.WOMEN))
            color = getColor(R.color.womenColor);
        else
            color = getColor(R.color.menColor);
    }

    private void getAllCompaniesInfo(){
        FirebaseFirestore.getInstance().collection(Macros.COMPANIES).get().
                addOnSuccessListener(documentSnapshot -> {
                    for(DocumentSnapshot doc : documentSnapshot){
                        Map<String,Object> map = new HashMap<>();
                        map.put("seller", doc.get("name"));
                        map.put("logo_url", doc.get("logo_url"));
                        mainModel.setCompanies_info(Objects.requireNonNull(doc.get("id")).toString(), map);
                    }
                });
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

        main_bottom = findViewById(R.id.main_bottom_navigation_view);
        BottomNavigationView.OnNavigationItemSelectedListener navigation_listener = item -> {
            switch(item.getItemId())
            {
                case R.id.bottom_swipe:
                    mainPager.setCurrentItem(0);
                    break;
                case R.id.bottom_favorites:
                    main_bottom.getOrCreateBadge(item.getItemId()).setNumber(0);
                    main_bottom.getOrCreateBadge(item.getItemId()).setVisible(false, true);
                    mainPager.setCurrentItem(1);
                    break;
                case R.id.bottom_search:
                    mainPager.setCurrentItem(2);
                    break;
                case R.id.bottom_suggested:
                    mainPager.setCurrentItem(3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected Value "+ item.getItemId());
            }
            return true;
        };

        main_bottom.setOnNavigationItemSelectedListener(navigation_listener);
        mainPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position)
                {
                    case 0:
                        main_bottom.getMenu().findItem(R.id.bottom_swipe).setChecked(true);
                        break;
                    case 1:
                        main_bottom.getOrCreateBadge(R.id.bottom_favorites).setNumber(0);
                        main_bottom.getOrCreateBadge(R.id.bottom_favorites).setVisible(false, true);
                        main_bottom.getMenu().findItem(R.id.bottom_favorites).setChecked(true);
                        break;
                    case 2:
                        main_bottom.getMenu().findItem(R.id.bottom_search).setChecked(true);
                        break;
                    case 3:
                        main_bottom.getMenu().findItem(R.id.bottom_suggested).setChecked(true);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected Value "+ position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
        swipesModel = new ViewModelProvider(this).get(SwipesModel.class);
        allItemsModel = new ViewModelProvider(this).get(AllItemsModel.class);

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        GenderModel genderModel = new ViewModelProvider(this).get(GenderModel.class);

        mUserName = findViewById(R.id.user_name);
        mUserProfile = findViewById(R.id.user_profile);
        progressBar = findViewById(R.id.progressBar);
        allItems = new ArrayList<>();

        Bundle bundle = getIntent().getBundleExtra("bundle");

        assert bundle != null;
        String extra_type = bundle.getString("type");

        if (extra_type != null)
            item_type = extra_type;
        else
            item_type = Macros.BAG;

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
                child(item_gender).
                child(Macros.CustomerMacros.PREFERRED_ITEMS).
                child(item_type).
                child(item_sub_category);

        customerFirstName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        imageUrl = bundle.getString("imageUrl");
        mAuth = FirebaseAuth.getInstance();
        //tabLayout = findViewById(R.id.top_nav);

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getCompanyInfo(final ShoppingItem shoppingItem, boolean search) {

        final String company_id = shoppingItem.getSellerId();
        if (Objects.requireNonNull(mainModel.getCompanies_info().getValue()).containsKey(company_id)) {
            shoppingItem.setSeller(Objects.requireNonNull((Objects.requireNonNull(mainModel.getCompanies_info().getValue().get(company_id))).get("seller")).toString());
            shoppingItem.setSellerLogoUrl(Objects.requireNonNull((Objects.requireNonNull(mainModel.getCompanies_info().getValue().get(company_id))).get("logo_url")).toString());

            if( search ){
                allItemsModel.addItem(ShoppingItemToRecyclerItem(shoppingItem));
                if ((Objects.requireNonNull(allItemsModel.getItems().getValue()).size() % Macros.SEARCH_TO_AD == 0)) {
                    ShoppingItem shoppingItemAd = (ShoppingItem) mainModel.getNextAd();
                    if (shoppingItemAd != null) {
                        RecyclerItem adItem = new RecyclerItem(null,null);
                        adItem.setNativeAd(shoppingItemAd.getNativeAd());
                        adItem.setAd(true);
                        allItemsModel.addItem(adItem);
                    }
                }
            }
            else{
                androidx.core.util.Pair<String, ShoppingItem> pair = new androidx.core.util.Pair<>(shoppingItem.getSeller(), shoppingItem);
                mainModel.addItemId(pair);
                mainModel.postAllItemsIds();
            }
        }
    }

    private void getLikes(final ShoppingItem shoppingItem) {

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(item_gender).
                child(shoppingItem.getSeller()).
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
                                    getCompanyInfo(shoppingItem,false);
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
                            getCompanyInfo(shoppingItem,false);
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
    private void getInteractedUsersInfo(ShoppingItem shoppingItem, Map<String, String> liked_users,
                                        Map<String, String> unliked_users) {

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
            getCompanyInfo(shoppingItem,false);
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
        getCompanyInfo(shoppingItem,false);
    }

    @Override
    protected void onDestroy() {

        customerDB.removeEventListener(valueEventListener);
        mainModel.getCustomers_info().removeObservers(this);
        mainModel.getCompanies_info().removeObservers(this);
        mainModel.clearAds();
        suggestedModel.getPreferred().removeObservers(this);
        progressBar = null;

        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }

        super.onDestroy();
    }

    private void initFBInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }

        // Create the interstitial unit with a placement ID (generate your own on the Facebook app settings).
        // Use different ID for each ad placement in your app.
        interstitialAd = new com.facebook.ads.InterstitialAd(this, Macros.FB_PLACEMENT_ID);

        // Set a listener to get notified on changes or when the user interact with the ad.
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {}

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Cleanup.
                interstitialAd.destroy();
                interstitialAd = null;
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if(ad == interstitialAd)
                    Log.d(Macros.TAG, adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (interstitialAd != null && interstitialAd.isAdLoaded()) {
                    // Ad was loaded, show it!
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                Log.d(Macros.TAG,"onAdClicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                Log.d(Macros.TAG,"onLoggingImpression");
            }

        });

        // Load a new interstitial.
        interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
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

    private void loadAds() {

        NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder().
                setAdChoicesPlacement(ADCHOICES_TOP_LEFT).
                build();

        AdLoader adLoader = new AdLoader
                .Builder(this, Macros.NATIVE_ADVANCED_AD)
                .forUnifiedNativeAd(unifiedNativeAd -> tempAd = unifiedNativeAd)
                .withAdListener(new AdListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        ShoppingItem dummy = new ShoppingItem();
                        dummy.setAd(true);
                        dummy.setNativeAd(tempAd);
                        mainModel.addAd(dummy);
                        if(mainModel.getAdsContainerSize() == Macros.NUM_OF_ADS) {
                            getItems get = new getItems(1);
                            get.execute();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad ( int errorCode ) {
                        Log.d(Macros.TAG,"Failed to load native ad: " + errorCode);
                    }
                })
                .withNativeAdOptions(nativeAdOptions)
                .build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());
    }

    private RecyclerItem ShoppingItemToRecyclerItem(ShoppingItem shoppingItem){

        RecyclerItem recyclerItem = new RecyclerItem(shoppingItem.getBrand(), shoppingItem.getSite_link());
        recyclerItem.setPrice(shoppingItem.getPrice());
        recyclerItem.setLink(shoppingItem.getSite_link());
        recyclerItem.setDescription(shoppingItem.getName());
        recyclerItem.setType(shoppingItem.getType());
        recyclerItem.setId(shoppingItem.getId());
        recyclerItem.setSale(shoppingItem.isOn_sale());
        recyclerItem.setVideo_link(shoppingItem.getVideo_link());
        recyclerItem.setOutlet(shoppingItem.isOutlet());
        recyclerItem.setReduced_price(shoppingItem.getReduced_price());
        recyclerItem.setSeller(shoppingItem.getSeller());
        recyclerItem.setBrand(shoppingItem.getBrand());
        recyclerItem.setSeller_id(shoppingItem.getSellerId());
        recyclerItem.setImages(shoppingItem.getImages());

        recyclerItem.setSellerImageUrl(shoppingItem.getSellerLogoUrl());
        return recyclerItem;
    }

    private void getLastSwipedItem() {
        for(String company : Macros.CompanyNames) {
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    orderByKey().
                    limitToLast(1).
                    addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (Object item_id : ((Map) Objects.requireNonNull(dataSnapshot.getValue())).keySet()) {
                                    swipesModel.setLast_item_id(item_id.toString(), company);
                                }
                            }
                            FirebaseDatabase.getInstance().getReference().
                                    child(Macros.CUSTOMERS).
                                    child(userId).
                                    child(item_gender).
                                    child(Macros.Items.UNLIKED).
                                    child(company).
                                    child(item_type).
                                    child(item_sub_category).
                                    orderByKey().
                                    limitToLast(1).
                                    addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (Object item_id : ((Map) Objects.requireNonNull(dataSnapshot.getValue())).keySet()) {
                                                    if (swipesModel.getLast_item_id().getValue() != null) {
                                                        String last_item = Objects.requireNonNull(((Map) swipesModel.getLast_item_id().getValue()).get(company)).toString();
                                                        String res;
                                                        if (item_id.toString().compareTo(last_item) > 0)
                                                            res = item_id.toString();
                                                        else res = last_item;
                                                        swipesModel.setLast_item_id(res,company);
                                                    }
                                                    else
                                                        swipesModel.setLast_item_id(item_id.toString(),company);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Log.d(Macros.TAG, "CustomerHomeFragment::onCancelled()" + databaseError.getMessage());
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(Macros.TAG, "CustomerHomeFragment::onCancelled()" + databaseError.getMessage());
                        }

                    });
        }
    }

    public class getItems extends AsyncTask<Void, Integer, Void> {

        String data = "";
        int page_num;

        getItems(int page_num){
            this.page_num = page_num;
        }
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

                String tx_cat = Macros.Functions.translateCategoryToTerminalX(item_type);
                getTerminalX(tx_cat,page_num,item_sub_category);

                String c_cat = Macros.Functions.translateCategoryToCastro(item_type);
                getCastro(c_cat);

                getAsos(page_num);

                for(ShoppingItem item : allItems) {
                    getLikes(item);
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG, "MainCustomerActivity::getItems() " + e.getMessage());
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getTerminalX(String cat, int page_num,String sub_cat) {
            try {

                Document temp = Jsoup.connect("https://www.terminalx.com/" +
                        item_gender.toLowerCase() +
                        "/" + cat +
                        "/" + sub_cat +
                        "?p=" + page_num ).get();

                Elements list_items = temp.getElementsByAttributeValue("class", "products list items product-items");
                Elements items_photo = list_items.get(0).getElementsByAttributeValue("class", "product-item-photo-shop");
                Elements items_details = list_items.get(0).getElementsByAttributeValue("class", "product details product-item-details");

                for (int i=0; i<items_photo.size(); ++i) {

                    String link = items_photo.get(i).childNode(1).attr("href");

                    Attributes attributes = items_photo.get(i).childNode(1).
                            childNode(1).childNode(1).
                            childNode(1).attributes();

                    String title = attributes.get("alt");

                    Document doc = Jsoup.connect(link).get();
                    Elements info = doc.getElementsByAttributeValue("class", "product-info-main");
                    Elements media = doc.getElementsByAttributeValue("class", "product media");

                    String img_url = media.get(0).
                            childNode(3).childNode(1).
                            childNode(1).childNode(3).
                            childNode(1).childNode(5).
                            childNode(1).childNode(1).
                            attr("src");

                    ArrayList<String> images = new ArrayList<>();

                    images.add(img_url);
                    images.add(img_url.replace("-1","-2"));
                    images.add(img_url.replace("-1","-3"));
                    images.add(img_url.replace("-1","-4"));

                    Elements product_item_brand = info.get(0).getElementsByAttributeValue("class", "product-item-brand");
                    ShoppingItem shoppingItem = new ShoppingItem();

                    String brand;
                    String price;
                    String id;
                    if(product_item_brand.size() == 0) {

                        brand = items_details.get(i).
                                childNode(1).childNode(1).
                                childNode(1).childNode(0).
                                toString().replace("\n ","");

                        id = items_details.get(i).
                                childNode(1).childNode(5).
                                attr("data-id");

                        price = items_details.get(i).
                                childNode(1).childNode(3).
                                childNode(0).childNode(1).
                                childNode(1).childNode(1).
                                childNode(0).
                                toString().replace("&nbsp;₪","");
                    }
                    else {
                        brand = product_item_brand.get(0).childNode(1).childNode(0).toString();
                        Elements final_price = info.get(0).getElementsByAttributeValue("class", "price-box price-final_price");
                        id = final_price.get(0).attr("data-product-id");

                        if(final_price.get(0).childNodeSize() == 5){
                            String reduced_price = final_price.get(0).
                                    childNode(1).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            price = final_price.get(0).
                                    childNode(3).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            shoppingItem.setOn_sale(true);
                            shoppingItem.setReduced_price(reduced_price);
                        }
                        else
                            price = final_price.get(0).childNode(1).childNode(1).attr("data-price-amount");
                    }

                    shoppingItem.setPrice(price);
                    shoppingItem.setId_in_seller(id);
                    shoppingItem.setId(id);
                    shoppingItem.setPrice(price);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setBrand(brand);
                    shoppingItem.setType(item_type);
                    shoppingItem.setGender(item_gender);
                    ArrayList<String> name = new ArrayList<>(Arrays.asList(title.split(" ")));
                    shoppingItem.setName(name);
                    shoppingItem.setSeller("Terminal X");
                    shoppingItem.setSub_category(cat);
                    shoppingItem.setSellerId("KhrLuCxBoKc0DLAL8dA6WAbOFXT2");
                    shoppingItem.setImages(images);
                    shoppingItem.setVideo_link(null);

                    allItems.add(shoppingItem);
                    getCompanyInfo(shoppingItem,true);
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG,"getTerminalX() " + e.getMessage());
            }
        }

     //   @RequiresApi(api = Build.VERSION_CODES.N)
     /*   private void getShein(String cat, int page_num,String sub_cat) {
            try {

                Document temp = Jsoup.connect("https://www.terminalx.com/" +
                        item_gender.toLowerCase() +
                        "/" + cat +
                        "/" + sub_cat +
                        "?p=" + page_num ).get();

                Elements list_items = temp.getElementsByAttributeValue("class", "products list items product-items");
                Elements items_photo = list_items.get(0).getElementsByAttributeValue("class", "product-item-photo-shop");
                Elements items_details = list_items.get(0).getElementsByAttributeValue("class", "product details product-item-details");

                for (int i=0; i<items_photo.size(); ++i) {

                    String link = items_photo.get(i).childNode(1).attr("href");

                    Attributes attributes = items_photo.get(i).childNode(1).
                            childNode(1).childNode(1).
                            childNode(1).attributes();

                    String title = attributes.get("alt");

                    Document doc = Jsoup.connect(link).get();
                    Elements info = doc.getElementsByAttributeValue("class", "product-info-main");
                    Elements media = doc.getElementsByAttributeValue("class", "product media");

                    String img_url = media.get(0).
                            childNode(3).childNode(1).
                            childNode(1).childNode(3).
                            childNode(1).childNode(5).
                            childNode(1).childNode(1).
                            attr("src");

                    ArrayList<String> images = new ArrayList<>();

                    images.add(img_url);
                    images.add(img_url.replace("-1","-2"));
                    images.add(img_url.replace("-1","-3"));
                    images.add(img_url.replace("-1","-4"));

                    Elements product_item_brand = info.get(0).getElementsByAttributeValue("class", "product-item-brand");
                    ShoppingItem shoppingItem = new ShoppingItem();

                    String brand;
                    String price;
                    String id;
                    if(product_item_brand.size() == 0) {

                        brand = items_details.get(i).
                                childNode(1).childNode(1).
                                childNode(1).childNode(0).
                                toString().replace("\n ","");

                        id = items_details.get(i).
                                childNode(1).childNode(5).
                                attr("data-id");

                        price = items_details.get(i).
                                childNode(1).childNode(3).
                                childNode(0).childNode(1).
                                childNode(1).childNode(1).
                                childNode(0).
                                toString().replace("&nbsp;₪","");
                    }
                    else {
                        brand = product_item_brand.get(0).childNode(1).childNode(0).toString();
                        Elements final_price = info.get(0).getElementsByAttributeValue("class", "price-box price-final_price");
                        id = final_price.get(0).attr("data-product-id");

                        if(final_price.get(0).childNodeSize() == 5){
                            String reduced_price = final_price.get(0).
                                    childNode(1).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            price = final_price.get(0).
                                    childNode(3).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            shoppingItem.setOn_sale(true);
                            shoppingItem.setReduced_price(reduced_price);
                        }
                        else
                            price = final_price.get(0).childNode(1).childNode(1).attr("data-price-amount");
                    }

                    shoppingItem.setPrice(price);
                    shoppingItem.setId_in_seller(id);
                    shoppingItem.setId(id);
                    shoppingItem.setPrice(price);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setBrand(brand);
                    shoppingItem.setType(item_type);
                    shoppingItem.setGender(item_gender);
                    ArrayList<String> name = new ArrayList<>(Arrays.asList(title.split(" ")));
                    shoppingItem.setName(name);
                    shoppingItem.setSeller("Terminal X");
                    shoppingItem.setSub_category(cat);
                    shoppingItem.setSellerId("KhrLuCxBoKc0DLAL8dA6WAbOFXT2");
                    shoppingItem.setImages(images);
                    shoppingItem.setVideo_link(null);

                    allItems.add(shoppingItem);
                    getCompanyInfo(shoppingItem,true);
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG,"getTerminalX() " + e.getMessage());
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getZara(String cat, int page_num,String sub_cat) {
            try {

                Document temp = Jsoup.connect("https://www.terminalx.com/" +
                        item_gender.toLowerCase() +
                        "/" + cat +
                        "/" + sub_cat +
                        "?p=" + page_num ).get();

                Elements list_items = temp.getElementsByAttributeValue("class", "products list items product-items");
                Elements items_photo = list_items.get(0).getElementsByAttributeValue("class", "product-item-photo-shop");
                Elements items_details = list_items.get(0).getElementsByAttributeValue("class", "product details product-item-details");

                for (int i=0; i<items_photo.size(); ++i) {

                    String link = items_photo.get(i).childNode(1).attr("href");

                    Attributes attributes = items_photo.get(i).childNode(1).
                            childNode(1).childNode(1).
                            childNode(1).attributes();

                    String title = attributes.get("alt");

                    Document doc = Jsoup.connect(link).get();
                    Elements info = doc.getElementsByAttributeValue("class", "product-info-main");
                    Elements media = doc.getElementsByAttributeValue("class", "product media");

                    String img_url = media.get(0).
                            childNode(3).childNode(1).
                            childNode(1).childNode(3).
                            childNode(1).childNode(5).
                            childNode(1).childNode(1).
                            attr("src");

                    ArrayList<String> images = new ArrayList<>();

                    images.add(img_url);
                    images.add(img_url.replace("-1","-2"));
                    images.add(img_url.replace("-1","-3"));
                    images.add(img_url.replace("-1","-4"));

                    Elements product_item_brand = info.get(0).getElementsByAttributeValue("class", "product-item-brand");
                    ShoppingItem shoppingItem = new ShoppingItem();

                    String brand;
                    String price;
                    String id;
                    if(product_item_brand.size() == 0) {

                        brand = items_details.get(i).
                                childNode(1).childNode(1).
                                childNode(1).childNode(0).
                                toString().replace("\n ","");

                        id = items_details.get(i).
                                childNode(1).childNode(5).
                                attr("data-id");

                        price = items_details.get(i).
                                childNode(1).childNode(3).
                                childNode(0).childNode(1).
                                childNode(1).childNode(1).
                                childNode(0).
                                toString().replace("&nbsp;₪","");
                    }
                    else {
                        brand = product_item_brand.get(0).childNode(1).childNode(0).toString();
                        Elements final_price = info.get(0).getElementsByAttributeValue("class", "price-box price-final_price");
                        id = final_price.get(0).attr("data-product-id");

                        if(final_price.get(0).childNodeSize() == 5){
                            String reduced_price = final_price.get(0).
                                    childNode(1).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            price = final_price.get(0).
                                    childNode(3).childNode(1).
                                    childNode(3).attr("data-price-amount");

                            shoppingItem.setOn_sale(true);
                            shoppingItem.setReduced_price(reduced_price);
                        }
                        else
                            price = final_price.get(0).childNode(1).childNode(1).attr("data-price-amount");
                    }

                    shoppingItem.setPrice(price);
                    shoppingItem.setId_in_seller(id);
                    shoppingItem.setId(id);
                    shoppingItem.setPrice(price);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setBrand(brand);
                    shoppingItem.setType(item_type);
                    shoppingItem.setGender(item_gender);
                    ArrayList<String> name = new ArrayList<>(Arrays.asList(title.split(" ")));
                    shoppingItem.setName(name);
                    shoppingItem.setSeller("Terminal X");
                    shoppingItem.setSub_category(cat);
                    shoppingItem.setSellerId("KhrLuCxBoKc0DLAL8dA6WAbOFXT2");
                    shoppingItem.setImages(images);
                    shoppingItem.setVideo_link(null);

                    allItems.add(shoppingItem);
                    getCompanyInfo(shoppingItem,true);
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG,"getTerminalX() " + e.getMessage());
            }
        }*/

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getAsos(int page_num) throws IOException {

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
                    }
                    else
                        shoppingItem.setOutlet(false);

                    if ((boolean) JO.get("isSale")) {
                        shoppingItem.setOn_sale(true);
                        shoppingItem.setReduced_price(JO.get("reducedPrice").toString());
                    }
                    else
                        shoppingItem.setOn_sale(false);

                    shoppingItem.setId_in_seller(id);
                    shoppingItem.setColor(color);

                    ArrayList<String> images = new ArrayList<>();
                    Database connection = new Database();
                    images.add(connection.getASOSimageUrl(1,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
                    images.add(connection.getASOSimageUrl(2,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
                    images.add(connection.getASOSimageUrl(3,shoppingItem.getColor(),shoppingItem.getId_in_seller()));
                    images.add(connection.getASOSimageUrl(4,shoppingItem.getColor(),shoppingItem.getId_in_seller()));

                    Document doc = Jsoup.connect("https://video.asos-media.com/products/0/" + id + "-catwalk-AVS.m3u8").get();
                    System.out.println(doc);

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
                    shoppingItem.setSub_category(item_sub_category);
                    shoppingItem.setImages(images);

                    allItems.add(shoppingItem);
                    getCompanyInfo(shoppingItem,true);
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG, "MainCustomerActivity::getAllItems() " + e.getMessage());
            }
            finally {
                httpURLConnection.disconnect();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getCastro(String cat) {
            try {
                Document document = Jsoup.connect("https://www.castro.com/he/" + item_gender.toUpperCase() + "/" + cat + ".html").get();
                Elements elements = document.getElementsByAttributeValueContaining("class","products-grid cols-3");

                for (Element elem : elements) {

                    ArrayList<Node> list = new ArrayList<>();
                    int size = elem.childNodeSize();
                    for(int i=0; i<size; ++i){
                        if( i%2 == 1){
                            list.add(elem.childNode(i));
                        }
                    }

                    for( Node node : list){

                        ShoppingItem shoppingItem = new ShoppingItem();

                        String link = node.childNode(1).attr("href");

                        String description = node.childNode(1).attr("title");

                        ArrayList<String> name = new ArrayList<>(Arrays.asList(description.split(" ")));

                        Document item_doc = Jsoup.connect(link).get();
                        Elements elems = item_doc.getElementsByAttributeValueContaining("class","MagicToolboxContainer");
                        Elements price_elem = item_doc.getElementsByAttributeValue("class", "price-box");

                        String price;

                        if (price_elem.get(0).childNode(1).childNodeSize() > 3)
                            price = price_elem.get(0).childNode(1).childNode(3).childNode(2).toString();
                        else
                            price = price_elem.get(0).childNode(1).childNode(1).childNode(1).toString();

                        List<Node> nodes = elems.get(0).childNode(3).childNode(3).childNodes();

                        String id = node.childNode(node.childNodeSize() - 6).attr("data-id");

                        if (node.childNode(node.childNodeSize() - 2).childNode(5).childNodeSize() > 3) {

                            shoppingItem.setOn_sale(true);
                            shoppingItem.setReduced_price(price.replace("&nbsp;",""));

                            String old_price = node.
                                    childNode(node.childNodeSize() - 2).
                                    childNode(5).childNode(3).
                                    childNode(3).childNode(2).
                                    toString();

                            shoppingItem.setPrice(old_price.replace("&nbsp;",""));
                        }
                        else
                            shoppingItem.setPrice(price.replace("&nbsp;",""));

                        ArrayList<String> images = new ArrayList<>();
                        for (int i=0; i<nodes.size(); ++i) {
                            if( i % 2 == 1 && i < 8 ){
                                images.add(nodes.get(i).childNode(0).attr("href"));
                            }
                        }

                        shoppingItem.setImages(images);
                        shoppingItem.setBrand("Castro");
                        shoppingItem.setType(item_type);
                        shoppingItem.setGender(item_gender);
                        shoppingItem.setSeller("Castro");
                        shoppingItem.setSub_category(cat);
                        shoppingItem.setSellerId("P6qfLsJbruQZbciTMF4oWnIjuZ63");
                        shoppingItem.setId(id);
                        shoppingItem.setColor(node.attributes().get("data-color"));
                        shoppingItem.setName(name);
                        shoppingItem.setSite_link(link);
                        shoppingItem.setVideo_link(null);

                        allItems.add(shoppingItem);
                        getCompanyInfo(shoppingItem,true);
                    }
                }
            }
            catch (Exception e){
                Log.d(Macros.TAG,"getCastro() " + e.getMessage());
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

}