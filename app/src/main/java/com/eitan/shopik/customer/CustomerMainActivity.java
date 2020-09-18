package com.eitan.shopik.customer;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplicationActivity;
import com.eitan.shopik.adapters.MainPagerAdapter;
import com.eitan.shopik.items.PreferredItem;
import com.eitan.shopik.items.ShoppingItem;
import com.eitan.shopik.viewModels.GenderModel;
import com.eitan.shopik.viewModels.MainModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.ProgressIndicator;
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
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CHANNEL_ID = "channel_id";
    private DatabaseReference customerDB, pageListener;
    private ProgressIndicator progressIndicator;
    private FirebaseAuth mAuth;
    private String customerFirstName, imageUrl;
    private String userId;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private View hView;
    private NavigationView navigationView;
    private ImageView nav_bg;
    private ViewPager mainPager;
    private ValueEventListener pageEventListener;
    private ValueEventListener fav_single_listener;
    private String cover;
    private ValueEventListener valueEventListener;
    private ValueEventListener favoriteValueEventListener;
    private static int item_count = 0;
    private static int total_items = 0;
    private static Map<String,String> favorites;
    private static MainModel mainModel;
    private static String item_type;
    private static String item_gender;
    private static String item_sub_category;
    private ArrayList<AsyncTask <Integer, Integer, Void>> asyntask;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void getCompanyInfo(final ShoppingItem shoppingItem) {

        final String company_id = shoppingItem.getSellerId();
        if (Objects.requireNonNull(mainModel.getCompanies_info().getValue()).containsKey(company_id)) {

            shoppingItem.setSeller((Objects.requireNonNull(Objects.requireNonNull(mainModel.getCompanies_info().
                    getValue().get(company_id)).get("seller"))).toString());

            shoppingItem.setSellerLogoUrl((Objects.requireNonNull(Objects.requireNonNull(mainModel.getCompanies_info().
                    getValue().get(company_id)).get("logo_url"))).toString());

            mainModel.addItem(shoppingItem);
            ++item_count;
            mainModel.getCurrentItem().postValue(Pair.create(item_count, total_items));

            if (favorites.containsKey(shoppingItem.getId())){
                mainModel.addFavorite(shoppingItem);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void getInteractedUsersInfo(ShoppingItem shoppingItem, Map<String, String> liked_users,
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
                        LikedUser likedUser = new LikedUser (
                                doc.get("imageUrl") != null ? Objects.requireNonNull(doc.get("imageUrl")).toString() : null,
                                doc.get("first_name") != null ? Objects.requireNonNull(doc.get("first_name")).toString() : null,
                                doc.get("last_name") != null ? Objects.requireNonNull(doc.get("last_name")).toString() : null
                        );

                        //if current user liked this item
                        if (doc.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            likedUser.setLast_name(likedUser.getLast_name() + " (You)");
                            shoppingItem.setSeen(true);
                        }

                        mainModel.setCustomers_info(customer_id, likedUser);
                        likedUser.setFavorite(Objects.equals(liked_users.get(doc.getId()), Macros.CustomerMacros.FAVOURITE));
                        shoppingItem.addLikedUser(likedUser);
                    }
                }
            });
        }
        if (unliked_users == null || unliked_users.isEmpty())
            getCompanyInfo(shoppingItem);
        else
            getUnlikedUserInfo(shoppingItem, unliked_users);
    }
    private void getCoverPic() {

        String userid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        FirebaseFirestore.getInstance().
                collection(Macros.CUSTOMERS).
                document(userid).get().
                addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        cover = documentSnapshot.get("cover_photo") != null ?
                                Objects.requireNonNull(documentSnapshot.get("cover_photo")).toString() :
                                Macros.DEFAULT_COVER_PHOTO;
                    }
                }).addOnCompleteListener(task ->
                    Macros.Functions.GlidePicture(getApplicationContext(), cover, nav_bg)
                );
    }

    private void getCustomerFavorites() {

        for(String company : Macros.CompanyNames){
           FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    addListenerForSingleValueEvent(fav_single_listener);
        }
    }

    private void getAllCompaniesInfo() {
        FirebaseFirestore.getInstance().
                collection(Macros.COMPANIES).get().
                addOnSuccessListener(documentSnapshot -> {
                    for(DocumentSnapshot doc : documentSnapshot){
                        Map<String,Object> map = new HashMap<>();
                        map.put("seller", doc.get("seller"));
                        map.put("logo_url", doc.get("logo_url"));
                        mainModel.setCompanies_info(Objects.requireNonNull(doc.get("id")).toString(), map);
                    }
                });
    }

    private void setViewPager() {

        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPager = findViewById(R.id.customer_container);
        mainPager.setAdapter(mainPagerAdapter);
        mainPager.setPageTransformer(false, new ZoomOutPageTransformer());
        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageSelected(int position) {
                animateLogo();
                switch(position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.bottom_swipe).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setNumber(0);
                        bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setVisible(false);
                        bottomNavigationView.getMenu().findItem(R.id.bottom_favorites).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.bottom_search).setChecked(true);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected Value "+ position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void animateLogo(){
        if(findViewById(R.id.logo_image) != null)
            YoYo.with(Techniques.Bounce).duration(1000).playOn(findViewById(R.id.logo_image));
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

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        GenderModel genderModel = new ViewModelProvider(this).get(GenderModel.class);

        progressIndicator = findViewById(R.id.top_progress_bar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);

        favorites = new HashMap<>();

        Bundle bundle = getIntent().getBundleExtra("bundle");

        assert bundle != null;
        String extra_type = bundle.getString("type");

        cover = bundle.getString("cover",null);

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

        pageListener = FirebaseDatabase.getInstance().getReference().
                child(Macros.CUSTOMERS).
                child(userId).
                child(item_gender).
                child(Macros.PAGE_NUM).
                child(item_type).
                child(item_sub_category);

        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.customer_toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer,R.string.close_drawer);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private static void getLikes(final ShoppingItem shoppingItem) {

        FirebaseDatabase.getInstance().getReference().
                child(Macros.ITEMS).
                child(item_gender).
                child(item_type).
                child(shoppingItem.getSeller() + "-" + shoppingItem.getId()).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            HashMap<String,String> unliked_users = new HashMap<>();
                            HashMap<String,String> liked_users = new HashMap<>();

                            //set likes num
                            if (dataSnapshot.hasChild(Macros.Items.LIKED)) {
                                shoppingItem.setLikes(dataSnapshot.child(Macros.Items.LIKED).getChildrenCount());
                                liked_users = (HashMap<String,String>) dataSnapshot.child(Macros.Items.LIKED).getValue();
                            }
                            else {
                                shoppingItem.setLikes(0);
                                shoppingItem.setLikedUsers(null);
                            }
                            //set unlikes num
                            if (dataSnapshot.hasChild(Macros.Items.UNLIKED)) {
                                shoppingItem.setUnlikes(dataSnapshot.child(Macros.Items.UNLIKED).getChildrenCount());
                                unliked_users = (HashMap<String,String>) dataSnapshot.child(Macros.Items.UNLIKED).getValue();
                            }
                            else {
                                shoppingItem.setUnlikes(0);
                                shoppingItem.setUnlikedUsers(null);
                            }

                            if (liked_users == null || liked_users.isEmpty()) {
                                if (unliked_users == null || unliked_users.isEmpty())
                                    getCompanyInfo(shoppingItem);
                                else
                                    getUnlikedUserInfo(shoppingItem, unliked_users);
                            }
                            else
                                getInteractedUsersInfo(shoppingItem, liked_users, unliked_users);
                        }
                        else {
                            shoppingItem.setLikes(0);
                            shoppingItem.setUnlikes(0);
                            shoppingItem.setLikedUsers(null);
                            shoppingItem.setUnlikedUsers(null);
                            getCompanyInfo(shoppingItem);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void getUnlikedUserInfo(final ShoppingItem shoppingItem, Map<String, String> unlikes_list) {

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
                                doc.get("imageUrl") != null ? Objects.requireNonNull(doc.get("imageUrl")).toString() : null,
                                doc.get("first_name") != null ? Objects.requireNonNull(doc.get("first_name")).toString() : null,
                                doc.get("last_name") != null ? Objects.requireNonNull(doc.get("last_name")).toString() : null
                        );

                        if (doc.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            likedUser.setLast_name(likedUser.getLast_name() + " (You)");
                            shoppingItem.setSeen(true);
                        }

                        mainModel.setCustomers_info(customer_id, likedUser);
                        shoppingItem.addUnlikedUser(likedUser);
                    }
                }
            });
        }
        getCompanyInfo(shoppingItem);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent selectedIntent = null;
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mAuth.signOut();
                if (mAuth.getCurrentUser() == null) {
                    selectedIntent = new Intent(CustomerMainActivity.this, LandingPageActivity.class);
                    selectedIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(selectedIntent);
                    finish();
                }
                break;
            case R.id.nav_profile:
                selectedIntent = new Intent(CustomerMainActivity.this, CustomerSettingsActivity.class);
                selectedIntent.putExtra("from_activity", "CustomerMainActivity");
                selectedIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityOptions options = ActivityOptions.
                        makeSceneTransitionAnimation(this, Pair.create(findViewById(R.id.nav_profile),"profile_pic"));
                startActivity(selectedIntent, options.toBundle());
                return true;
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
             case R.id.nav_website:
                Macros.Functions.buy(CustomerMainActivity.this,"https://eitangoren.github.io");
        }
        if (selectedIntent != null) {
            startActivity(selectedIntent);
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        mainModel = new ViewModelProvider(this).get(MainModel.class);
        setInterstitialAd();

        createNotificationChannel();
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);

        init();

        navigationView.setOnClickListener(v -> getCoverPic());

        TextView copyright = findViewById(R.id.nav_copyright_text);
        String text = "Shopik Version 1.0 " + System.lineSeparator() + getResources().getString(R.string.copy_right) ;
        copyright.setText(text);

        getAllCompaniesInfo();
        favoriteValueEventListener = new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if( dataSnapshot.getValue() instanceof Map ) {
                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                        for ( Object item_id : map.keySet()) {
                            mainModel.addSwipedItemId(item_id.toString());
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        };

        for(String company : Macros.CompanyNames) {

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.UNLIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    addValueEventListener(favoriteValueEventListener);

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    addValueEventListener(favoriteValueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mainModel.setPreferred(new PreferredItem((Map<String,Long>) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        customerDB.addValueEventListener(valueEventListener);

        fav_single_listener = new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Map<String,String> likes = (Map<String, String>) snapshot.getValue();
                    assert likes != null;
                    favorites.putAll(likes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        getCustomerFavorites();

        pageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int page =  Integer.parseInt(String.valueOf(dataSnapshot.getValue()));
                    mainModel.setCurrent_page(page);
                }
                else
                    mainModel.setCurrent_page(1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        pageListener.addValueEventListener(pageEventListener);

        mainModel.getCurrent_page().observe(this, page -> {

            //Array of task
            asyntask = new ArrayList<>();

            //Add task to your array
            asyntask.add(new getAsos().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getShein().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getCastro().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getTFS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getTX().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getAldo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getRenuar().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));
            asyntask.add(new getHoodies().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, page, page));

        });

        getCoverPic();

        loadCustomerInfo();

        setViewPager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            animateLogo();
            switch(item.getItemId()) {
                case R.id.bottom_swipe:
                    mainPager.setCurrentItem(0);
                    break;
                case R.id.bottom_favorites:
                    bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setNumber(0);
                    bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setVisible(false);
                    mainPager.setCurrentItem(1);
                    break;
                case R.id.bottom_search:
                    mainPager.setCurrentItem(2);
                    break;
                default:
                    throw new IllegalStateException( "Unexpected Value " + item.getItemId());
            }
            return true;
        });

        startPostponedEnterTransition();

        mainModel.getCurrentItem().observe(this, pair -> {
            progressIndicator.setVisibility(View.VISIBLE);
            int progress = (int) (((float) pair.first / (float) pair.second) * 100);
            progressIndicator.setProgress(progress);

            if(progress == 100 || pair.first >= total_items){
                progressIndicator.setProgress(0);
                progressIndicator.setVisibility(View.GONE);
            }
        });
    }

    private void setInterstitialAd() {

        if(ShopikApplicationActivity.getInterstitialAd() != null){
            if( ShopikApplicationActivity.getInterstitialAd().isAdLoaded()) {
                ShopikApplicationActivity.getInterstitialAd().show();
            }
        }
        ShopikApplicationActivity.increaseCategoryClicks();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadCustomerInfo() {

        getCoverPic();

        CircleImageView nav_image = hView.findViewById(R.id.profile_pic);
        TextView nav_greetings = hView.findViewById(R.id.greeting);
        nav_bg = hView.findViewById(R.id.nav_bgImage);

        if (imageUrl != null)
            Glide.with(this).load(imageUrl).into(nav_image);
        else
            Glide.with(this).load(Macros.DEFAULT_PROFILE_IMAGE).into(nav_image);

        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour > 12 && hour < 17)
            greeting = "Good afternoon, ";
        else if (hour > 4 && hour <= 12)
            greeting = "Good morning, ";
        else if (hour >= 17 && hour < 22)
            greeting = "Good evening, ";
        else
            greeting = "Good night, ";

        String first_name = customerFirstName.split(" ")[0];
        String msg;

        if (first_name.length() > 5)
             msg = greeting + System.lineSeparator() + first_name;
        else
             msg = greeting + first_name;

        nav_greetings.setText(msg);
        TextView name = findViewById(R.id.name);
        String text = greeting + System.lineSeparator() + first_name;
        name.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        item_count = 0;
        total_items = 0;
        customerDB.removeEventListener(valueEventListener);
        pageListener.removeEventListener(pageEventListener);

        for(String company : Macros.CompanyNames){
            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    removeEventListener(fav_single_listener);

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.UNLIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    removeEventListener(favoriteValueEventListener);

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    removeEventListener(favoriteValueEventListener);
        }

        mainModel.getCustomers_info().removeObservers(this);
        mainModel.getCompanies_info().removeObservers(this);
        mainModel.getCurrentItem().removeObservers(this);
        mainModel.getCurrent_page().removeObservers(this);
        mainModel.getPreferred().removeObservers(this);

        for(AsyncTask <Integer, Integer, Void> asyncTask : asyntask){
            asyncTask.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        item_count = 0;
        total_items = 0;
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        super.onBackPressed();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        ShopikApplicationActivity.RefreshAds(5);
        item_count = 0;
        total_items = 0;
        super.finish();
    }

    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.45f;
        private static final float MIN_ALPHA = 0.4f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);
            }
            else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0)
                    view.setTranslationX(horzMargin - vertMargin / 2);
                else
                    view.setTranslationX(-horzMargin + vertMargin / 2);

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
        }
    }
    private static class getTX extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String tx_cat = Macros.Functions.translateCategoryToTerminalX(item_gender,item_type);
            String tx_sub_cat = Macros.Functions.translateSubCategoryToTerminalX(item_sub_category);

            int iter = 0;
            int total = 0;
            if(tx_cat != null && tx_sub_cat != null) {
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {
                        Document temp = Jsoup.connect("https://www.terminalx.com/" +
                                item_gender.toLowerCase() +
                                "/" + tx_cat +
                                "/" + tx_sub_cat +
                                "?p=" + page).get();

                        Elements list_items = temp.getElementsByClass("products list items product-items");
                        Elements items_photo = list_items.get(0).getElementsByClass("product-item-photo-shop");
                        Elements items_details = list_items.get(0).getElementsByClass("product details product-item-details");

                        total_items += items_photo.size();
                        total=items_photo.size();
                        mainModel.getTotalItems().postValue(total_items);
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
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
                                continue;
                            }

                            Elements info = doc.getElementsByClass( "product-info-main");
                            Elements media = doc.getElementsByClass( "product media");

                            String img_url = media.get(0).
                                    childNode(3).childNode(1).
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

                            String brand;
                            String price;  //by ILS
                            String id;
                            if (product_item_brand.size() == 0) {

                                brand = items_details.get(i).
                                        childNode(1).childNode(1).
                                        childNode(1).childNode(0).
                                        toString().replace("\n ", "");

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
                                brand = product_item_brand.get(0).childNode(1).childNode(0).toString();
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
                                    price = final_price.get(0).childNode(1).childNode(1).attr("data-price-amount");
                            }

                            shoppingItem.setId(id);
                            shoppingItem.setPrice(price);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setBrand(brand);
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            ArrayList<String> name = new ArrayList<>(Arrays.asList(title.split(" ")));
                            shoppingItem.setName(name);
                            shoppingItem.setSeller("Terminal X");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("KhrLuCxBoKc0DLAL8dA6WAbOFXT2");
                            shoppingItem.setImages(images);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getAsos extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            Pair<Integer, Integer> cat_num = Macros.Functions.getCategoryNum(item_gender, item_sub_category, item_type);

            int iter = 0;
            int total = 0;
            if(cat_num != null){
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {

                        Document document = Jsoup.connect("https://www.asos.com/cat/?cid=" + cat_num.first + "&page=" + page).get();
                        Elements products = document.getElementsByAttributeValue("data-auto-id", "productTile");

                        total_items += products.size();
                        total = products.size();
                        mainModel.getTotalItems().postValue(total_items);
                        for(Element prod : products) {

                            ShoppingItem shoppingItem = new ShoppingItem();

                            Elements pook = prod.getElementsByAttributeValue("data-auto-id", "productTilePrice");
                            if(pook.size() == 1) {
                                int las_ele_idx = pook.get(0).childNodes().size() - 1;
                                String price = pook.get(0).childNode(las_ele_idx).childNode(0).toString().replace("£","");
                                String final_price = String.valueOf(Double.parseDouble(price) * Macros.POUND_TO_ILS);
                                shoppingItem.setPrice(final_price);
                            }

                            Elements pook2 = prod.getElementsByAttributeValue( "data-auto-id", "productTileSaleAmount");
                            if(pook2.size() > 0){
                                String red = pook2.get(0).childNode(0).toString().replace("£","");
                                String final_price = String.valueOf(Double.parseDouble(red) * Macros.POUND_TO_ILS);
                                shoppingItem.setOn_sale(true);
                                shoppingItem.setReduced_price(final_price);
                            }

                            String id = prod.attr("id").split("-")[1];
                            Attributes attributes = prod.childNode(0).attributes();
                            String link = attributes.get("href");
                            String description = attributes.get("aria-label").split(",")[0];

                            Document document2;
                            try {
                                document2 = Jsoup.connect(link).get();
                            }
                            catch (org.jsoup.HttpStatusException ex){
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
                                continue;
                            }
                            Elements images_ele = document2.getElementsByClass("image-thumbnail");
                            ArrayList<String> images = new ArrayList<>();
                            for(Element img : images_ele){
                                String _img = img.childNode(1).childNode(1).attr("src").
                                        split("\\?")[0] + "?$XXL$&wid=513&fit=constrain";
                                images.add(_img);
                            }
                            if(images.size() < 4){
                                for(int j = images.size(); j<4; j++){
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
                            }
                            catch (IndexOutOfBoundsException ex) {
                                shoppingItem.setBrand("ASOS");
                            }

                            String[] name = description.split(" ");

                            String seller_name = "ASOS";
                            String seller_id = "odsIz0HNINevS2EP3mdIrryTIF72";
                            boolean isExclusive = description.toLowerCase().contains("exclusive");

                            shoppingItem.setType(item_type);
                            shoppingItem.setName(new ArrayList<>(Arrays.asList(name)));
                            shoppingItem.setSeller(seller_name);
                            shoppingItem.setSellerId(seller_id);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setPage_num(page);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setId(id);
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setImages(images);
                            shoppingItem.setExclusive(isExclusive);
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            iter++;
                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getAldo extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String aldo_cat = Macros.Functions.translateCategoryToAldo(item_gender,item_type);
            String aldo_sub_cat = Macros.Functions.translateSubCategoryToAldo(item_gender,item_sub_category);

            int iter = 0;
            int total = 0;
            if(aldo_cat != null && !aldo_sub_cat.equals("")) {
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {
                        Document temp = Jsoup.connect("https://www.aldoshoes.co.il/" +
                                item_gender.toLowerCase() +
                                "/" + aldo_cat +
                                "/" + aldo_sub_cat +
                                "?p=" + page).get();

                        Elements list_items = temp.getElementsByAttributeValue("id", "prod-list-cat");
                        Elements products = list_items.get(0).getElementsByClass("cat-product");

                        total_items += products.size();
                        total = products.size();
                        mainModel.getTotalItems().postValue(total_items);

                        for(Element prod : products){

                            String link = prod.childNode(7).attributes().get("href");
                            String price = prod.childNode(7).attributes().get("data-price");
                            String description = prod.childNode(7).attributes().get("data-name");
                            ArrayList<String> name = new ArrayList<>(Arrays.asList(description.split(" ")));
                            String id = prod.childNode(7).attributes().get("data-id");

                            Document prod_page;
                            try {
                                prod_page = Jsoup.connect(link).get();
                            }
                            catch (org.jsoup.HttpStatusException ex){
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
                                continue;
                            }

                            Elements prod_info = prod_page.getElementsByClass("product-image-gallery");
                            Elements images_elements = prod_info.get(0).getElementsByClass("gallery-image-link");

                            ShoppingItem shoppingItem = new ShoppingItem();
                            ArrayList<String> images = new ArrayList<>();
                            for(Node img : images_elements){
                                images.add(img.attr("data-source"));
                            }
                            if(images.size() < 4){
                                for(int j = images.size(); j<4; j++){
                                    images.add(images.get(0));
                                }
                            }

                            shoppingItem.setPrice(price);
                            shoppingItem.setImages(images);
                            shoppingItem.setBrand("Aldo");
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setSeller("Aldo");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("wnh2WvYzTsajbN7WDPWNA5ij3Vr1");
                            shoppingItem.setId(id);
                            shoppingItem.setName(name);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            iter++;

                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getTFS extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String cat = Macros.Functions.translateCategoryTo247(item_type,item_gender);
            String sub_cat = Macros.Functions.translateSubCategoryTo247(item_sub_category,item_gender);

            int iter = 0;
            int total = 0;
            if(cat != null && sub_cat != null) {
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {
                        Document document = Jsoup.connect("https://www.twentyfourseven.co.il/he/" +
                                item_gender.toLowerCase() + "/" + cat + "/" + sub_cat + ".html?p=" + page).get();

                        Element element = document.getElementsByClass("products-grid").get(0);
                        Elements items = element.getElementsByClass("item");

                        total_items += items.size();
                        mainModel.getTotalItems().postValue(total_items);
                        total = items.size();
                        for (Node item_node : items) {

                            ShoppingItem shoppingItem = new ShoppingItem();

                            String link = item_node.childNode(3).childNode(0).attr("href");

                            Document document1;
                            try {
                                document1 = Jsoup.connect(link).get();
                            }
                            catch (org.jsoup.HttpStatusException ex){
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
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

                            Elements sku = document1.getElementsByClass("sku");

                            String id = sku.get(0).childNode(3).childNode(0).toString();

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
                            shoppingItem.setBrand("TwentyFourSeven");
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setSeller("TwentyFourSeven");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("SjSqx6h2ZFd2CSW8HjIDhLejNza2");
                            shoppingItem.setId(id);
                            shoppingItem.setName(name);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);
                            iter++;
                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getCastro extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String cat = Macros.Functions.translateCategoryToCastro(item_type,item_sub_category,item_gender);
            Integer sub_cat = Macros.Functions.translateSubCategoryToCastro(item_sub_category);

            int iter = 0;
            int total = 0;
            if(cat != null){
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {
                        Document document;
                        if(sub_cat == null){
                            if(item_sub_category.equals("face masks") && item_gender.equals(Macros.CustomerMacros.MEN)){
                                document = Jsoup.connect("https://www.castro.com/" + item_gender.toLowerCase()
                                        + "/" + cat + "?p=" + page).get();
                            }
                            else {
                                document = Jsoup.connect("https://www.castro.com/" + item_gender.toLowerCase()
                                        + "/shop_by_product/" + cat + "?p=" + page).get();
                            }
                        }
                        else {
                            document = Jsoup.connect("https://www.castro.com/" + item_gender.toLowerCase()
                                    + "/shop_by_product/" + cat + "?p=" + page + "&vrp_product_type=" + sub_cat).get();
                        }

                        Elements elements = document.
                                getElementsByClass("products list items product-items ");
                        Elements filtered_elements = elements.get(0).
                                getElementsByAttributeValueStarting("id","product_category_");

                        total_items += filtered_elements.size();
                        total = filtered_elements.size();
                        mainModel.getTotalItems().postValue(total_items);

                        for (Element element : filtered_elements) {

                            ShoppingItem shoppingItem = new ShoppingItem();

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
                                    total_items--;
                                    total--;
                                    mainModel.getTotalItems().postValue(total_items);
                                    continue;
                                }
                            }

                            Elements images_doc = item_doc.getElementsByClass("product_gallery");

                            String price;
                            Elements price_doc = item_doc.getElementsByClass("price");

                            if (price_doc.hasClass("old-price")) {

                                price = price_doc.get(0).childNode(0).toString().
                                        replace("₪","").
                                        replace("&nbsp;","");
                                String old_price = price_doc.get(1).childNode(0).toString().
                                        replace("₪", "").
                                        replace("&nbsp;","");

                                shoppingItem.setReduced_price(price);
                                shoppingItem.setOn_sale(true);
                                shoppingItem.setPrice(old_price);
                            }
                            else {
                                price = price_doc.get(0).childNode(0).toString().
                                        replace("₪", "").
                                        replace("&nbsp;","");
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
                            shoppingItem.setBrand("Castro");
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setSeller("Castro");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("P6qfLsJbruQZbciTMF4oWnIjuZ63");
                            shoppingItem.setId(id);
                            shoppingItem.setName(description);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            iter++;

                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()),
                                        Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getRenuar extends AsyncTask <Integer, Integer, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String cat = Macros.Functions.translateCategoryToRenuar(item_type);
            String sub_cat = Macros.Functions.translateSubCategoryToRenuar(item_gender,item_sub_category);

            int iter = 0;
            int total = 0;
            if(cat != null && !sub_cat.equals("")){
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {

                        Document document = Jsoup.connect("https://www.renuar.co.il/he/" +
                                item_gender.toLowerCase()
                                + "/" + cat
                                + "/" + sub_cat + ".html"
                                + "?p=" + page).get();

                        Elements elements = document.getElementsByClass("products-grid");
                        Elements products = elements.get(0).getElementsByClass("item");

                        total_items += products.size();
                        mainModel.getTotalItems().postValue(total_items);
                        total = products.size();

                        for (Element element : products) {
                            ShoppingItem shoppingItem = new ShoppingItem();
                            String id = element.attr("data-sku");

                            String name = element.childNode(3).childNode(3).childNode(0).attr("title");
                            String link = element.childNode(3).childNode(3).childNode(0).attr("href");

                            Document item_doc;
                            try {
                                item_doc = Jsoup.connect(link).get();
                            }
                            catch (org.jsoup.HttpStatusException ex){
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
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
                            shoppingItem.setBrand("Renuar");
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setSeller("Renuar");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("ohmrSuMPLScbofAVksQsohMaBeJ2");
                            shoppingItem.setId(id);
                            shoppingItem.setName(description);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            iter++;

                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getHoodies extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String cat = Macros.Functions.translateCategoryToHoodies(item_gender,item_type,item_sub_category);
            String sub_cat = Macros.Functions.translateSubCategoryToHoodies(item_gender,item_sub_category);

            int iter = 0;
            int total = 0;
            if(cat != null) {
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {

                        Document document = Jsoup.connect("https://www.hoodies.co.il/" +
                                item_gender.toLowerCase() + "/" + cat + "/"+ sub_cat + "?p=" + page).get();
                        Elements elements = document.getElementsByClass("product-item-photo-shop");

                        total_items += elements.size();
                        total = elements.size();
                        mainModel.getTotalItems().postValue(total_items);

                        for(Element element : elements){

                            ShoppingItem shoppingItem = new ShoppingItem();
                            String link = element.childNode(1).attr("href");

                            Document prod_elements;
                            try {
                                prod_elements = Jsoup.connect(link).get();
                            }
                            catch (org.jsoup.HttpStatusException ex){
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
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

                            String id = jpook.get("sku").toString();
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
                            shoppingItem.setBrand("Hoodies");
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setSeller("Hoodies");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("Tac7y6IhTEYbDAvfsm5FMFeuW1t2");
                            shoppingItem.setId(id);
                            shoppingItem.setName(description_array);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);
                            iter++;
                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
    private static class getShein extends AsyncTask <Integer, Integer, Void> {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Integer... page_num) {

            String cat = Macros.Functions.translateCategoryToShein(item_gender,item_type,item_sub_category);
            Pair<String,Boolean> sub_cat = Macros.Functions.translateSubCategoryToShein(item_gender,item_sub_category);

            int iter = 0;
            int total = 0;
            if(cat != null) {
                try {
                    for (int page = page_num[0]; page < page_num[1] + 1; ++page) {
                        Document document;
                        if(!sub_cat.second) {
                            if (!sub_cat.first.equals("")) {
                                document = Jsoup.connect("https://il.shein.com/" +
                                          sub_cat.first + ".html?&page=" + page).get();
                            }
                            else {
                                document = Jsoup.connect("https://il.shein.com/" + cat +
                                         ".html?&page=" + page).get();
                            }
                        }
                        else {
                            try {
                                document = Jsoup.connect("https://il.shein.com/" +
                                        cat + ".html" + sub_cat.first + "&page=" + page).get();
                            }
                            catch (HttpStatusException se){
                                document = Jsoup.connect("https://il.shein.com/" +
                                        sub_cat.first + "&page=" + page ).get();
                            }
                        }

                        assert document != null;
                        Elements elements = document.getElementsByClass("j-goodsli");

                        if(elements.isEmpty()){
                            elements = document.getElementsByClass("j-expose__content-goodsls");
                        }

                        total_items += elements.size();
                        total = elements.size();
                        mainModel.getTotalItems().postValue(total_items);

                        for(Element element : elements){
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
                                total_items--;
                                total--;
                                mainModel.getTotalItems().postValue(total_items);
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

                            String name = details.getString("goods_url_name");

                            String id = details.getString("goods_id");
                            String brand = details.getString("brand").
                                    equals("") ? "SHEIN" : details.getString("brand");

                            ArrayList<String> description = new ArrayList<>(Arrays.asList(name.split(" ")));

                            shoppingItem.setPrice(price);
                            shoppingItem.setImages(images);
                            shoppingItem.setBrand(brand);
                            shoppingItem.setType(item_type);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setSeller("Shein");
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("7yIoEtn3uMXoIb5WGxHT84h7mQs1");
                            shoppingItem.setId(id);
                            shoppingItem.setName(description);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            iter++;
                            if (favorites.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favorites.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    total_items = Math.max(total_items - (total - iter), 0);
                    mainModel.getTotalItems().postValue(total_items);
                }
            }
            return null;
        }
    }
}