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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.eitan.shopik.Adapters.MainPagerAdapter;
import com.eitan.shopik.Database;
import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.eitan.shopik.ViewModels.SuggestedModel;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.navigation.NavigationView;
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
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

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

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference customerDB;
    private String item_type;
    private String item_gender;
    private String item_sub_category;
    private ProgressBar progressBar;
    private MainModel mainModel;
    private FirebaseAuth mAuth;
    private TextView mUserName;
    private CircleImageView mUserProfile;
    private String customerFirstName, imageUrl;
    private String userId;
    private androidx.appcompat.widget.Toolbar toolbar;
    private androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener;
    private SuggestedModel suggestedModel;
    private ValueEventListener valueEventListener;
    private UnifiedNativeAd tempAd;
    private Pair<Integer,Integer> cat_num;
    private com.facebook.ads.InterstitialAd interstitialAd;
    public static final int NUM_OF_ADS = 15;
    private TabLayout top_nav;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Google Ads
        List<String> testDeviceIds = Arrays.asList("7255B1C36174A2C33091060576730302");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        //Facebook Ads
        AdSettings.addTestDevice("a0e31a81-5e54-4e90-80c9-e76b9820ba80");
        AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        AudienceNetworkAds.initialize(this);
       // initFBInterstitial();

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

        for( int i=0; i < NUM_OF_ADS; ++i ){
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

        new getItems().execute(1);

        loadCustomerInfo();

        setViewPager();

        setTabLayout();

        Objects.requireNonNull(top_nav.getTabAt(0)).select();
        top_nav.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        tab.setIcon(R.drawable.ic_touch_app_black);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_favorite_black_24dp);
                        tab.getOrCreateBadge().setNumber(0);
                        tab.getOrCreateBadge().setVisible(false, true);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_baseline_thumbs_up_down);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        tab.setIcon(R.drawable.ic_outline_touch_app_24);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_outline_favorite_border_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_outline_thumbs_up_down_24);
                        break;
                }
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

    private void getAllCompaniesInfo() {
        FirebaseFirestore.getInstance().
                collection(Macros.COMPANIES).get().
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

        top_nav.setSelectedTabIndicatorColor(Color.BLACK);
        top_nav.setTabIndicatorFullWidth(true);
        top_nav.setTabTextColors(Color.BLACK, Color.BLACK);

    //    Objects.requireNonNull(top_nav.getTabAt(0)).setText("Swipe");
    //    Objects.requireNonNull(top_nav.getTabAt(1)).setText("Likes");
    //    Objects.requireNonNull(top_nav.getTabAt(2)).setText("Suggested");
    //    Objects.requireNonNull(top_nav.getTabAt(3)).setText("All items");

        Objects.requireNonNull(top_nav.getTabAt(0)).setIcon(R.drawable.ic_outline_touch_app_24);
        Objects.requireNonNull(top_nav.getTabAt(1)).setIcon(R.drawable.ic_outline_favorite_border_24);
        Objects.requireNonNull(top_nav.getTabAt(2)).setIcon(R.drawable.ic_outline_thumbs_up_down_24);
        Objects.requireNonNull(top_nav.getTabAt(3)).setIcon(R.drawable.ic_search);

        top_nav.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private void setViewPager() {

        MainPagerAdapter mainPgerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        ViewPager mainPager = findViewById(R.id.customer_container);
        mainPager.setAdapter(mainPgerAdapter);
        mainPager.setPageTransformer(false, new ZoomOutPageTransformer());
        top_nav.setupWithViewPager(mainPager);
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
        top_nav = findViewById(R.id.top_nav);
        navigationView = findViewById(R.id.nav_view);

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

        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.customer_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                R.string.open_drawer,R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void loadCustomerInfo() {

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user_name = hView.findViewById(R.id.name);
        CircleImageView nav_image = hView.findViewById(R.id.profile_pic);
        TextView nav_greetings = hView.findViewById(R.id.greeting);

        if (imageUrl != null) {
            Macros.Functions.GlidePicture(this,imageUrl,mUserProfile);
            Macros.Functions.GlidePicture(this,imageUrl,nav_image);
        }

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
        String msg = "";

        if (first_name.length() > 5)
             msg = greeting + System.lineSeparator() + first_name;
        else
             msg = greeting + first_name;

        mUserName.setText(msg);
        nav_user_name.setText(first_name);
        nav_greetings.setText(msg);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent selectedIntent = null;
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                if (mAuth.getCurrentUser() == null) {
                    selectedIntent = new Intent(CustomerMainActivity.this, LandingPageActivity.class);
                    startActivity(selectedIntent);
                }
                break;
            case R.id.nav_my_profile:
                selectedIntent = new Intent(CustomerMainActivity.this, CustomerSettingsActivity.class);
                selectedIntent.putExtra("from_activity", "CustomerMainActivity");
                selectedIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case R.id.nav_home:
                Intent intent = new Intent(CustomerMainActivity.this, GenderFilteringActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("gender", item_gender);
                bundle.putString("name", customerFirstName);
                bundle.putString("imageUrl", imageUrl);
                intent.putExtra("bundle", bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            case R.id.nav_orders:
                Toast.makeText(this, "my orders", Toast.LENGTH_SHORT).show();
                break;
        }
        if (selectedIntent != null) {
            startActivity(selectedIntent);
            return true;
        }
        return false;
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
    private void getCompanyInfo(final ShoppingItem shoppingItem,int size) {

        final String company_id = shoppingItem.getSellerId();
        if (Objects.requireNonNull(mainModel.getCompanies_info().getValue()).containsKey(company_id)) {

            shoppingItem.setSeller((mainModel.getCompanies_info().
                    getValue().get(company_id).get("seller")).toString());

            shoppingItem.setSellerLogoUrl((mainModel.getCompanies_info().
                    getValue().get(company_id).get("logo_url")).toString());

            mainModel.addItem(shoppingItem,size);
        }
    }

    private void getLikes(final ShoppingItem shoppingItem, int size) {

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(item_gender).
                child(item_type).
                child(shoppingItem.getSeller() + "-" + shoppingItem.getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Map unliked_users = new HashMap<>();
                            Map liked_users = new HashMap<>();

                            //set likes num
                            if (dataSnapshot.hasChild(Macros.Items.LIKED)) {
                                shoppingItem.setLikes(dataSnapshot.child(Macros.Items.LIKED).getChildrenCount());
                                liked_users = (Map) dataSnapshot.child(Macros.Items.LIKED).getValue();
                            }
                            else {
                                shoppingItem.setLikes(0);
                                shoppingItem.setLikedUsers(null);
                            }
                            //set unlikes num
                            if (dataSnapshot.hasChild(Macros.Items.UNLIKED)) {
                                shoppingItem.setUnlikes(dataSnapshot.child(Macros.Items.UNLIKED).getChildrenCount());
                                unliked_users = (Map) dataSnapshot.child(Macros.Items.UNLIKED).getValue();
                            }
                            else {
                                shoppingItem.setUnlikes(0);
                                shoppingItem.setUnlikedUsers(null);
                            }

                            if (liked_users == null || liked_users.isEmpty()) {
                                if (unliked_users == null || unliked_users.isEmpty())
                                    getCompanyInfo(shoppingItem,size);
                                else
                                    getUnlikedUserInfo(shoppingItem, unliked_users,size);
                            }
                            else
                                getInteractedUsersInfo(shoppingItem, liked_users, unliked_users,size);
                        }
                        else {
                            shoppingItem.setLikes(0);
                            shoppingItem.setUnlikes(0);
                            shoppingItem.setLikedUsers(null);
                            shoppingItem.setUnlikedUsers(null);
                            getCompanyInfo(shoppingItem,size);
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
                                        Map<String, String> unliked_users,int size) {

        ArrayList<String> list = new ArrayList<>(liked_users.keySet());
        for (String customer_id : list) {
            FirebaseFirestore.getInstance().
                    collection(Macros.CUSTOMERS)
                    .whereEqualTo("id", customer_id).
                    get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc : documentSnapshots) {
                        LikedUser likedUser = new LikedUser (
                                Objects.requireNonNull(doc.get("imageUrl")).toString(),
                                Objects.requireNonNull(doc.get("first_name")).toString(),
                                Objects.requireNonNull(doc.get("last_name")).toString()
                        );

                        //if current user liked this item
                        if (doc.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            likedUser.setLast_name(likedUser.getLast_name() + " (You)");
                        }

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
            getCompanyInfo(shoppingItem,size);
        else
            getUnlikedUserInfo(shoppingItem, unliked_users,size);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getUnlikedUserInfo(final ShoppingItem shoppingItem, Map<String, String> unlikes_list,int size) {

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
        getCompanyInfo(shoppingItem,size);
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

    private void getLastSwipedItem() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (Object item_id : ((Map) Objects.requireNonNull(dataSnapshot.getValue())).keySet()) {
                        mainModel.addSwipedItemId(item_id.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Macros.TAG, "CustomerHomeFragment::onCancelled()" + databaseError.getMessage());
            }

        };
        for(String company : Macros.CompanyNames) {

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    addValueEventListener(valueEventListener);

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.UNLIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    addValueEventListener(valueEventListener);
        }
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private class getItems extends AsyncTask <Integer, Integer, Void> {

        String data = "";

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {
            try {
                cat_num = Macros.Functions.getCategoryNum(item_gender, item_sub_category, item_type);

                String tx_cat = Macros.Functions.translateCategoryToTerminalX(item_type);
                String tx_sub_cat = Macros.Functions.translateSubCategoryToTerminalX(item_sub_category);
                getTerminalX(tx_cat,page_num[0],tx_sub_cat);

                String c_cat = Macros.Functions.translateCategoryToCastro(item_type);
                getCastro(c_cat);

                getAsos(page_num[0]);
            }
            catch (Exception e) {
                Log.d(Macros.TAG, "MainCustomerActivity::getItems() " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
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
                    String price;  //by ILS
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
                    shoppingItem.setExclusive(name.contains("exclusive"));
                    shoppingItem.setSeen(mainModel.isSwiped(id));

                    getLikes(shoppingItem,items_photo.size());
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG,"getTerminalX() " + e.getMessage());
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getAsos(int page_num) {

            try {

                Document document = Jsoup.connect("https://www.asos.com/cat/?cid=" + cat_num.first + "&page=" + page_num).get();
                DataNode node = (DataNode) document.childNode(2).childNode(2).childNode(28).childNode(0);

                data = node.getWholeData();
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

                    shoppingItem.setType(item_type);
                    shoppingItem.setBrand(branda);
                    shoppingItem.setName(list);
                    shoppingItem.setPrice(price); //by POUND
                    shoppingItem.setSeller(seller_name);
                    shoppingItem.setSellerId(seller_id);
                    shoppingItem.setSite_link(link);
                    shoppingItem.setPage_num(page_num);
                    shoppingItem.setCatagory_num(cat_num.first);
                    shoppingItem.setGender(item_gender);
                    shoppingItem.setId(id);
                    shoppingItem.setSub_category(item_sub_category);
                    shoppingItem.setImages(images);
                    shoppingItem.setExclusive(list.contains("exclusive"));
                    shoppingItem.setSeen(mainModel.isSwiped(id));

                    getLikes(shoppingItem,total_items);
                }
            }
            catch (Exception e) {
                Log.d(Macros.TAG, "MainCustomerActivity::getAllItems() " + e.getMessage());
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void getCastro(String cat) {
            try {
                Document document = Jsoup.connect("https://www.castro.com/en/" + item_gender.toUpperCase() + "/" + cat + ".html").get();
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
                            price = price_elem.get(0).childNode(1).childNode(3).childNode(0).toString();
                        else
                            price = price_elem.get(0).childNode(1).childNode(1).childNode(0).toString();

                        List<Node> nodes = elems.get(0).childNode(3).childNode(3).childNodes();
                        String id = node.childNode(node.childNodeSize() - 6).attr("data-id");

                        // by ILS
                        if (node.childNode(node.childNodeSize() - 2).childNode(5).childNodeSize() > 3) {

                            shoppingItem.setOn_sale(true);
                            shoppingItem.setReduced_price(price.replace("&nbsp;",""));

                            String old_price = node.
                                    childNode(node.childNodeSize() - 2).
                                    childNode(5).childNode(3).
                                    childNode(3).childNode(0).
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
                        shoppingItem.setExclusive(name.contains("exclusive"));
                        shoppingItem.setSeen(mainModel.isSwiped(id));

                        getLikes(shoppingItem,list.size());
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