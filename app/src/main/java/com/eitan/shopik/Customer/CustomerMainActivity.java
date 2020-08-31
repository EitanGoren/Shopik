package com.eitan.shopik.Customer;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActivityNavigator;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Adapters.MainPagerAdapter;
import com.eitan.shopik.Items.PreferredItem;
import com.eitan.shopik.Items.ShoppingItem;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.LikedUser;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.ViewModels.GenderModel;
import com.eitan.shopik.ViewModels.MainModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
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
import org.jsoup.nodes.DataNode;
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

import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_LEFT;

public class CustomerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference customerDB,pageListener;
    private String item_type;
    private String item_gender;
    private String item_sub_category;
    private ProgressIndicator progressIndicator;
    private MainModel mainModel;
    private FirebaseAuth mAuth;
    private String customerFirstName, imageUrl;
    private String userId;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private View hView;
    private NavigationView navigationView;
    private ImageView nav_bg;
    private ViewPager mainPager;
    private MutableLiveData<Boolean> isFinished;
    private int item_count = 0;
    private int total_items_found = 0;
    private Map<String,String> favs;
    private ValueEventListener pageEventListener;
    private ValueEventListener fav_single_listener;
    private String cover;
    private ValueEventListener valueEventListener;
    private ValueEventListener FavvalueEventListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );

       // new setInterstitial().execute();

        mainModel = new ViewModelProvider(this).get(MainModel.class);

        // LOAD NATIVE ADS
        new getAds().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        setContentView(R.layout.activity_main);

        init();

        navigationView.setOnClickListener(v -> getCoverPic());

        TextView copyright = findViewById(R.id.nav_copyright_text);
        String text = "Shopik Version 1.0 " + System.lineSeparator() + getResources().getString(R.string.copy_right);
        copyright.setText(text);

        getAllCompaniesInfo();
        FavvalueEventListener = new ValueEventListener() {
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Macros.TAG, "CustomerHomeFragment::onCancelled()" + databaseError.getMessage());
            }

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
                    addValueEventListener(FavvalueEventListener);

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    addValueEventListener(FavvalueEventListener);
        }

        valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressWarnings("unchecked")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mainModel.setPreferred(new PreferredItem((Map<String,Object>) dataSnapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(Macros.TAG, "getPreferred() " + Objects.requireNonNull(databaseError.getMessage()));
            }
        };
        customerDB.addValueEventListener(valueEventListener);

        fav_single_listener = new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Map<String,String> likes = (Map<String, String>) snapshot.getValue();
                    assert likes != null;
                    favs.putAll(likes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(Macros.TAG," fav_single_listener failed: " + error.getMessage());
            }
        };
        getCustomerFavorites();

        pageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long page = (long) (dataSnapshot.getValue());
                    mainModel.setCurrent_page(page);
                }
                else
                    mainModel.setCurrent_page((long)1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(Macros.TAG, "CustomerHomeFragment::pook " + databaseError.getMessage());
            }

        };
        pageListener.addValueEventListener(pageEventListener);

        mainModel.getCurrent_page().observe(this, page -> {

            progressIndicator.setProgress(0);
            progressIndicator.setVisibility(View.VISIBLE);

            mainModel.clearFavorite();
            mainModel.clearAllItems();

            new getAsos().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,page);
            new getCastro().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,page);
            new getTFS().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,page);
            new getTX().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,page);
            new getAldo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,page);
            new getRenuar().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,page);

        });

        mainModel.getCurrentItem().observe(this, integerBooleanPair -> {
            int progress = (int) (((float) integerBooleanPair.first / (float) total_items_found)*100);
            progressIndicator.setProgress(progress);
            mainModel.getTotalItems().postValue(progress);

            if(progress == 100){
                progressIndicator.setVisibility(View.GONE);
                item_count = 0;
                total_items_found = 0;
            }
        });

        getCoverPic();

        loadCustomerInfo();

        setViewPager();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {

            YoYo.with(Techniques.Bounce).duration(1000).playOn(findViewById(R.id.logo_image));

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
                Macros.Functions.GlidePicture(getApplicationContext(), cover, nav_bg));
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
                        map.put("seller", doc.get("name"));
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
        mainPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPageSelected(int position) {

                YoYo.with(Techniques.Bounce).duration(1000).playOn(findViewById(R.id.logo_image));

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
        progressIndicator.setVisibility(View.VISIBLE);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView = navigationView.getHeaderView(0);

        isFinished = new MutableLiveData<>();
        favs = new HashMap<>();

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

    private void loadCustomerInfo() {

        getCoverPic();

        CircleImageView nav_image = hView.findViewById(R.id.profile_pic);
        TextView nav_greetings = hView.findViewById(R.id.greeting);
        nav_bg = hView.findViewById(R.id.nav_bgImage);

        if (imageUrl != null)
            Macros.Functions.GlidePicture(this, imageUrl, nav_image);

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
            case R.id.nav_profile:
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
    private void getCompanyInfo(final ShoppingItem shoppingItem, int size) {

        final String company_id = shoppingItem.getSellerId();
        if (Objects.requireNonNull(mainModel.getCompanies_info().getValue()).containsKey(company_id)) {

            shoppingItem.setSeller((Objects.requireNonNull(Objects.requireNonNull(mainModel.getCompanies_info().
                    getValue().get(company_id)).get("seller"))).toString());

            shoppingItem.setSellerLogoUrl((Objects.requireNonNull(Objects.requireNonNull(mainModel.getCompanies_info().
                    getValue().get(company_id)).get("logo_url"))).toString());

            mainModel.addItem(shoppingItem,size);
            Pair<Integer,Boolean> pair = Pair.create(++item_count,shoppingItem.isSeen());
            mainModel.getCurrentItem().postValue(pair);

            if (favs.containsKey(shoppingItem.getId())){
                mainModel.addFavorite(shoppingItem);
            }
        }
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
                            shoppingItem.setSeen(true);
                        }

                        mainModel.setCustomers_info(customer_id, likedUser);
                        likedUser.setFavorite(Objects.equals(liked_users.get(doc.getId()), Macros.CustomerMacros.FAVOURITE));
                        shoppingItem.addLikedUser(likedUser);
                    }
                }
                else {
                    Log.d(Macros.TAG, "CustomerHomeFragment::users : " + customer_id + " do not exist");
                }
            });
        }
        if (unliked_users == null || unliked_users.isEmpty())
            getCompanyInfo(shoppingItem,size);
        else
            getUnlikedUserInfo(shoppingItem, unliked_users,size);
    }

    private void getLikes(final ShoppingItem shoppingItem, int size) {

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
                        Log.d(Macros.TAG, "CustomerHomeFragment::onCancelled: " + databaseError.getMessage());
                    }
                });
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

                        if (doc.getId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) {
                            likedUser.setLast_name(likedUser.getLast_name() + " (You)");
                            shoppingItem.setSeen(true);
                        }

                        mainModel.setCustomers_info(customer_id, likedUser);
                        shoppingItem.addUnlikedUser(likedUser);
                    }
                } else {
                    Log.d(Macros.TAG, "CustomerHomeFragment::users : " + customer_id + " do not exist");
                }
            });
        }
        getCompanyInfo(shoppingItem,size);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                    removeEventListener(FavvalueEventListener);

            FirebaseDatabase.getInstance().getReference().
                    child(Macros.CUSTOMERS).
                    child(userId).
                    child(item_gender).
                    child(Macros.Items.LIKED).
                    child(company).
                    child(item_type).
                    child(item_sub_category).
                    removeEventListener(FavvalueEventListener);
        }

        mainModel.getCustomers_info().removeObservers(this);
        mainModel.getCompanies_info().removeObservers(this);
        mainModel.getPreferred().removeObservers(this);
    }

    @Override
    protected void onDestroy() {
        mainModel.clearAds();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        ActivityNavigator.applyPopAnimationsToPendingTransition(this);
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

    private class getTX extends AsyncTask <Long, Integer, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Long... page_num) {

            String tx_cat = Macros.Functions.translateCategoryToTerminalX(item_gender,item_type);
            String tx_sub_cat = Macros.Functions.translateSubCategoryToTerminalX(item_gender,item_sub_category);

            if(tx_cat != null && tx_sub_cat != null) {
                try {
                    for (int page = 1; page < page_num[0] + 1; ++page) {
                        Document temp = Jsoup.connect("https://www.terminalx.com/" +
                                item_gender.toLowerCase() +
                                "/" + tx_cat +
                                "/" + tx_sub_cat +
                                "?p=" + page).get();

                        Elements list_items = temp.getElementsByAttributeValue("class", "products list items product-items");
                        Elements items_photo = list_items.get(0).getElementsByAttributeValue("class", "product-item-photo-shop");
                        Elements items_details = list_items.get(0).getElementsByAttributeValue("class", "product details product-item-details");

                        publishProgress(items_photo.size());
                        for (int i = 0; i < items_photo.size(); ++i) {

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
                            images.add(img_url.replace("-1", "-2"));
                            images.add(img_url.replace("-1", "-3"));
                            images.add(img_url.replace("-1", "-4"));

                            Elements product_item_brand = info.get(0).getElementsByAttributeValue("class", "product-item-brand");
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
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setSellerId("KhrLuCxBoKc0DLAL8dA6WAbOFXT2");
                            shoppingItem.setImages(images);
                            shoppingItem.setVideo_link(null);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            if (favs.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem, items_photo.size());
                        }
                    }
                } catch (Exception e) {
                    Log.d(Macros.TAG, "getTerminalX() " + e.getMessage());
                }
            }
            else{
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //totalItems.postValue(values[0]);
            total_items_found += values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished.postValue(true);
            Log.d(Macros.TAG,"getTX FINISHED");
        }
    }

    private class getAsos extends AsyncTask <Long, Integer, Void> {

        String data = "";

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Long... page_num) {

            Pair<Integer, Integer> cat_num = Macros.Functions.getCategoryNum(item_gender, item_sub_category, item_type);

            if(cat_num != null){
                try {
                    for ( int page = 1; page < page_num[0] + 1; ++page ) {

                        Document document = Jsoup.connect("https://www.asos.com/cat/?cid=" + cat_num.first + "&page=" + page).get();
                        DataNode node = (DataNode) document.childNode(3).childNode(3).childNode(28).childNode(0);

                        data = node.getWholeData();
                        String[] data_split = data.split("\"products\":",2);
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
                        publishProgress(JA.length());
                        for (int i = 0; i < JA.length(); ++i) {

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
                            ArrayList<String> list = new ArrayList<>();

                            boolean isExclusive = JO.get("description").toString().toLowerCase().contains("exclusive");
                            shoppingItem.setExclusive(isExclusive);

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
                            ArrayList<String> images = new ArrayList<>();

                            images.add("https://images.asos-media.com/products/0/" + id + "-1-" + color + "?$S$&wid=595&hei=760");
                            images.add("https://images.asos-media.com/products/0/" + id + "-" + 2 + "?$S$&wid=595&hei=760");
                            images.add("https://images.asos-media.com/products/0/" + id + "-" + 3 + "?$S$&wid=595&hei=760");
                            images.add("https://images.asos-media.com/products/0/" + id + "-" + 4 + "?$S$&wid=595&hei=760");

                            shoppingItem.setBrand(branda);
                            shoppingItem.setType(item_type);
                            shoppingItem.setName(list);
                            shoppingItem.setPrice(price); //by POUND
                            shoppingItem.setSeller(seller_name);
                            shoppingItem.setSellerId(seller_id);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setPage_num(page_num[0]);
                            shoppingItem.setCatagory_num(cat_num.first);
                            shoppingItem.setGender(item_gender);
                            shoppingItem.setId(id);
                            shoppingItem.setSub_category(item_sub_category);
                            shoppingItem.setImages(images);
                            shoppingItem.setExclusive(list.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            if (favs.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }

                            getLikes(shoppingItem, JA.length());
                        }
                    }
                }
                catch (Exception e) {
                    Log.d(Macros.TAG, "MainCustomerActivity::getASOS() " + e.getMessage());
                }
            }
            else{
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //totalItems.postValue(values[0]);
            total_items_found += values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished.postValue(true);
            Log.d(Macros.TAG,"getASOS FINISHED");
        }
    }

    private class getAldo extends AsyncTask <Long, Integer, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Long... page_num) {

            String aldo_cat = Macros.Functions.translateCategoryToAldo(item_gender,item_type);
            String aldo_sub_cat = Macros.Functions.translateSubCategoryToAldo(item_gender,item_sub_category);

            if(aldo_cat != null) {
                try {

                    for (int page = 1; page < page_num[0] + 1; ++page) {
                        Document temp = Jsoup.connect("https://www.aldoshoes.co.il/" +
                                item_gender.toLowerCase() +
                                "/" + aldo_cat +
                                "/" + aldo_sub_cat +
                                "?p=" + page).get();

                        Elements list_items = temp.getElementsByAttributeValue("id", "prod-list-cat");
                        Elements products = list_items.get(0).getElementsByClass("cat-product");

                        publishProgress(products.size());
                        for(Element prod : products){

                            Attributes attributes = prod.childNode(7).attributes();
                            String link = attributes.get("href");
                            String price = attributes.get("data-price");
                            String description = attributes.get("data-name");
                            ArrayList<String> name = new ArrayList<>(Arrays.asList(description.split(" ")));
                            String id = attributes.get("data-id");

                            Document prod_page = Jsoup.connect(link).get();
                            Elements prod_info = prod_page.getElementsByClass("product-image-gallery");
                            Elements images_elements = prod_info.get(0).getElementsByClass("gallery-image-link");

                            ShoppingItem shoppingItem = new ShoppingItem();

                            ArrayList<String> images = new ArrayList<>();
                            for(Node img : images_elements){
                                images.add(img.attr("data-source"));
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

                            if (favs.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem, products.size());
                        }
                    }
                } catch (Exception e) {
                    Log.d(Macros.TAG, "getAldo() " + e.getMessage());
                }
            }
            else{
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            total_items_found += values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished.postValue(true);
            Log.d(Macros.TAG,"getAldo FINISHED");
        }
    }

    private class getTFS extends AsyncTask <Long, Integer, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Long... page_num) {

            String cat = Macros.Functions.translateCategoryTo247(item_type,item_gender);
            String sub_cat = Macros.Functions.translateSubCategoryTo247(item_sub_category,item_gender);

            if(cat != null && sub_cat != null) {
                try {

                    for (int page = 1; page < page_num[0] + 1; ++page) {
                        Document document = Jsoup.connect("https://www.twentyfourseven.co.il/he/" +
                                item_gender.toLowerCase() + "/" + cat + "/" + sub_cat + ".html?p=" + page).get();

                        Element element = document.getElementsByClass("products-grid").get(0);
                        Elements items = element.getElementsByClass("item");

                        publishProgress(items.size());
                        for (Node item_node : items) {

                            ShoppingItem shoppingItem = new ShoppingItem();

                            String link = item_node.childNode(3).childNode(0).attr("href");

                            Document document1 = Jsoup.connect(link).get();
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
                            shoppingItem.setSellerId("7yIoEtn3uMXoIb5WGxHT84h7mQs1");
                            shoppingItem.setId(id);
                            shoppingItem.setName(name);
                            shoppingItem.setSite_link(link);
                            shoppingItem.setVideo_link(null);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            if (favs.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem, item_node.childNodeSize());
                        }
                    }
                } catch (Exception e) {
                    Log.d(Macros.TAG, "MainCustomerActivity::getTFS() " + e.getMessage());
                }
            }
            else{
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            total_items_found += values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished.postValue(true);
            Log.d(Macros.TAG,"getTFS FINISHED");
        }
    }

    private class getCastro extends AsyncTask <Long, Integer, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Long... page_num) {

            String cat = Macros.Functions.translateCategoryToCastro(item_type);
            long sub_cat = Macros.Functions.translateSubCategoryToCastro(item_sub_category);

            if(cat != null && sub_cat != 0){
                try {
                    for (int page = 1; page < page_num[0] + 1; ++page) {

                        Document document = Jsoup.connect("https://www.castro.com/" + item_gender.toLowerCase()
                                + "/shop_by_product/" + cat + "?p=" + page + "&vrp_product_type=" + sub_cat).get();

                        Elements elements = document.getElementsByClass("products list items product-items ");
                        Elements filtered_elements = elements.get(0).getElementsByAttributeValueStarting("id","product_category_");

                        publishProgress(filtered_elements.size());
                        for (Node node : filtered_elements) {

                            ShoppingItem shoppingItem = new ShoppingItem();

                            String id = node.childNode(0).childNode(4).childNode(0).childNode(0).childNode(0).childNode(0).attr("data-product-sku");
                            String link = node.childNode(0).childNode(4).childNode(0).childNode(0).childNode(0).childNode(0).attr("href");

                            Document item_doc = null;
                            try {
                                item_doc = Jsoup.connect(link).get();
                            }
                            catch(HttpStatusException se){
                                Log.d(Macros.TAG,"404: " + se.getUrl());
                            }
                            if(item_doc == null)
                                continue;

                            Elements images_doc = item_doc.getElementsByClass("product_gallery");

                            String price = "";
                            Elements price_doc = item_doc.getElementsByClass("price");
                            shoppingItem.setPrice(price);
                            if (price_doc.hasClass("old-price")) {
                                price = price_doc.get(0).childNode(0).toString().replace("₪", "");
                                String old_price = price_doc.get(1).childNode(0).toString().replace("₪", "");
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
                            for (Node img : images_doc.get(0).childNode(0).childNode(0).childNodes()) {
                                String image = img.childNode(1).attr("src");
                                if (image.equals(""))
                                    image = img.childNode(1).attr("data-lazy");

                                name = img.childNode(1).attr("alt");
                                images.add(image);
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
                            shoppingItem.setVideo_link(null);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            if (favs.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem, filtered_elements.size());
                        }
                    }
                } catch (Exception e) {
                    Log.d(Macros.TAG, "getCastro() Failed " + e.getMessage());
                }
            }
            else{
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            total_items_found += values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished.postValue(true);
            Log.d(Macros.TAG,"getCASTRO FINISHED");
        }
    }

    private class getRenuar extends AsyncTask <Long, Integer, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Long... page_num) {

            String cat = Macros.Functions.translateCategoryToRenuar(item_gender,item_type);
            String sub_cat = Macros.Functions.translateSubCategoryToRenuar(item_gender,item_sub_category);

            if(cat != null){
                try {
                    for (int page = 1; page < page_num[0] + 1; ++page) {

                        Document document = Jsoup.connect("https://www.renuar.co.il/he/" +
                                item_gender.toLowerCase()
                                + "/" + cat
                                + "/" + sub_cat + ".html"
                                + "?p=" + page).get();

                        Elements elements = document.getElementsByClass("products-grid");
                        Elements products = elements.get(0).getElementsByClass("item");

                        publishProgress(products.size());
                        for (Node node : products) {

                            ShoppingItem shoppingItem = new ShoppingItem();

                            String id = node.attr("data-sku");

                            String name = node.childNode(3).childNode(3).childNode(0).attr("title");
                            String link = node.childNode(3).childNode(3).childNode(0).attr("href");

                            Document item_doc = null;
                            try {
                                item_doc = Jsoup.connect(link).get();
                            }
                            catch(HttpStatusException se){
                                Log.d(Macros.TAG,"404: " + se.getUrl());
                            }
                            if(item_doc == null)
                                continue;

                            Elements item_elements = item_doc.getElementsByClass("images-slideshow");
                            Elements images_elements = item_elements.get(0).getElementsByClass("item");

                            ArrayList<String> images = new ArrayList<>();
                            for(Element img : images_elements){
                                Elements pook = img.getElementsByClass("zoom");
                                images.add(pook.get(0).attr("src"));
                            }

                            ArrayList<String> description = new ArrayList<>(Arrays.asList(name.split(" ")));

                            Element price_box = item_doc.getElementsByClass("price-box").get(0);

                            if(price_box.hasClass("old-price")){
                               shoppingItem.setPrice(price_box.getElementsByClass("old-price").get(0).
                                        childNode(1).childNode(1).childNode(0).nodeName().replace("&nbsp;₪",""));
                               shoppingItem.setReduced_price(price_box.getElementsByClass("special-price").get(0).
                                        childNode(1).childNode(1).childNode(0).nodeName().replace("&nbsp;₪",""));
                                shoppingItem.setOn_sale(true);
                            }
                            else {
                                shoppingItem.setPrice(price_box.childNode(1).childNode(1).childNode(0).toString().replace("&nbsp;₪", ""));
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
                            shoppingItem.setVideo_link(null);
                            shoppingItem.setExclusive(name.contains("exclusive"));
                            shoppingItem.setSeen(mainModel.isSwiped(id));
                            shoppingItem.setPage_num(page);

                            if (favs.containsKey(shoppingItem.getId())) {
                                shoppingItem.setFavorite(Objects.equals(favs.get(shoppingItem.getId()), Macros.CustomerMacros.FAVOURITE));
                            }
                            getLikes(shoppingItem, products.size());
                        }
                    }
                } catch (Exception e) {
                    Log.d(Macros.TAG, "getRenuar() Failed " + e.getMessage());
                }
            }
            else{
                publishProgress(0);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            total_items_found += values[0];
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isFinished.postValue(true);
            Log.d(Macros.TAG,"getRenuar FINISHED");
        }
    }

    private class getAds extends AsyncTask <Void, Void, Void> {

        private UnifiedNativeAd tempAd;

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {

            int NUM_OF_ADS = 20;
            for (int i = 0; i < NUM_OF_ADS; ++i ) {
                VideoOptions videoOptions = new VideoOptions.Builder().
                        setStartMuted(false).
                        setClickToExpandRequested(true).
                        build();

                NativeAdOptions nativeAdOptions = new NativeAdOptions.Builder().
                        setAdChoicesPlacement(ADCHOICES_TOP_LEFT).
                        setRequestMultipleImages(true).
                        setVideoOptions(videoOptions).
                        build();

                AdLoader adLoader = new AdLoader
                        .Builder(getApplicationContext(), Macros.NATIVE_ADVANCED_AD)
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
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                Log.d(Macros.TAG, "Failed to load native ad: " + loadAdError.getMessage());
                            }
                        })
                        .withNativeAdOptions(nativeAdOptions)
                        .build();

                adLoader.loadAd(new PublisherAdRequest.Builder().build());
            }
            return null;
        }
    }
}