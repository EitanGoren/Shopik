package com.eitan.shopik.customer;

import static com.eitan.shopik.database.Database.LIKED;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.NoInternetActivity;
import com.eitan.shopik.R;
import com.eitan.shopik.ShopikApplication;
import com.eitan.shopik.adapters.MainPagerAdapter;
import com.eitan.shopik.ads.AdMobInterstitialAd;
import com.eitan.shopik.database.AppSqlDatabase;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.ShopikRepository;
import com.eitan.shopik.database.daos.ItemDao;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.items.PreferredItem;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.system.SystemUpdates;
import com.eitan.shopik.viewModels.GenderModel;
import com.eitan.shopik.viewModels.MainModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int objects = 0;
    private static final String CHANNEL_ID = "channel_id";
    private static final String TAG = "CustomerMain";
    private FloatingActionButton scrollUpFAB,scrollDownFAB;
    private static int total_items = 0;
    private static MainModel mainModel;
    private static String item_type;
    private static String item_gender;
    private LinearProgressIndicator progressIndicator;
    private BottomNavigationView bottomNavigationView;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton mFloatingActionButton;
    private DrawerLayout drawerLayout;
    private ViewPager mainPager;
    private NavigationView mNavigationView;
    private AdMobInterstitialAd adMobInterstitialAd;
    private FullScreenContentCallback mFullScreenContentCallback;
    private int prog = 0;
    private ShopikUser mShopikUser;
    private String lastItemId;
    private SystemUpdates mSystemUpdates;
    private boolean bottomNavIsFaded = false;
    private boolean scrollBtnHidden = false;

    public static final class ScrollUpEvent {};
    public static final class ScrollDownEvent {};
    public static final class ScrollingEvent{};
    public static final class NotScrollingEvent{};

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parseBundle();

        init();

        mFloatingActionButton = findViewById(R.id.center_floating_btn);
        mFloatingActionButton.setOnClickListener(view -> {
            if(!drawerLayout.isOpen()) {
                drawerLayout.openDrawer(GravityCompat.START, true);
                HideScrollButtons();
            }
            else {
                drawerLayout.closeDrawer(GravityCompat.START, true);
                if(mainPager.getCurrentItem() != 0)
                    ShowScrollButtons();
            }
        });

        bottomAppBar = findViewById(R.id.bottomAppBar);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mFloatingActionButton.hide();
                bottomNavigationView.setVisibility(View.INVISIBLE);
                bottomAppBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mFloatingActionButton.show();
                bottomNavigationView.setVisibility(View.VISIBLE);
                bottomAppBar.setVisibility(View.VISIBLE);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            animateLogo();
            switch (item.getItemId()) {
                case R.id.bottom_swipe:
                    mainPager.setCurrentItem(0);
                    break;
                case R.id.bottom_new:
                    mainPager.setCurrentItem(1);
                    break;
                case R.id.bottom_favorites:
                    bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setNumber(0);
                    bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setVisible(false);
                    mainPager.setCurrentItem(2);
                    break;
                case R.id.bottom_search:
                    mainPager.setCurrentItem(3);
                    break;
                default:
                    throw new IllegalStateException("Unexpected Value " + item.getItemId());
            }
            return true;
        });

        mFullScreenContentCallback = new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                adMobInterstitialAd.dismissAd();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                onInterstitialAdFailed();
            }
        };

        adMobInterstitialAd.isAdReady().observe(this, aBoolean -> {
            if(aBoolean && objects % 5 == 0) showInterstitialAd();
            objects++;
        });

        setViewPager();

        mainModel.getCurrentItem().observe(this, pair -> {
            progressIndicator.setVisibility(View.VISIBLE);
            int progress = (int) (((float) pair.first / (float) pair.second) * 100);
            progressIndicator.setProgress(progress);

            if (progress == 100 || pair.first >= total_items) {
                progressIndicator.setProgress(0);
                progressIndicator.setVisibility(View.GONE);
            }
        });

        scrollUpFAB.setOnClickListener(view -> {
            // add your code here
            EventBus.getDefault().post(new ScrollUpEvent());
        });
        scrollDownFAB.setOnClickListener(view -> {
            // add your code here
            EventBus.getDefault().post(new ScrollDownEvent());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        total_items = 0;

        mainModel.getCurrentItem().removeObservers(this);
        mainModel.getPreferred().removeObservers(this);

        Objects.requireNonNull(mainModel.getAllItems().getValue()).clear();
        Objects.requireNonNull(mainModel.getUnseenItems().getValue()).clear();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        total_items = 0;

        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);

        adMobInterstitialAd.dismissAd();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        total_items = 0;
        super.finish();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent selectedIntent = null;
        switch (item.getItemId()) {
            case R.id.nav_logout:
                mShopikUser.signOut();
                if (!mShopikUser.isAuthenticated()) {
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
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            case R.id.nav_website:
                Macros.Functions.buy(CustomerMainActivity.this, Macros.WEBSITE_URL);
                break;
            case R.id.nav_rate:
                Macros.Functions.buy(CustomerMainActivity.this, Macros.GOOGLE_PLAY_APP);
                break;
            case R.id.nav_comment:
                showContactDialog();
                break;
        }
        if (selectedIntent != null) {
            startActivity(selectedIntent);
            return true;
        }
        return false;
    }

    private void init() {

        initializeWindow();

        mShopikUser = ShopikUser.getInstance();
        mSystemUpdates = SystemUpdates.getInstance();

        EventBus.getDefault().register(this);
        mainModel = new ViewModelProvider(this).get(MainModel.class);

        Database database = Database.getInstance();
        database.listenToItems(item_type, item_gender);
        database.fetchPreferredAttributes(item_type, item_gender);

        progressIndicator = findViewById(R.id.top_progress_bar);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setBackground(null);
        drawerLayout = findViewById(R.id.drawerLayout);
        scrollUpFAB = findViewById(R.id.scroll_up);
        scrollDownFAB = findViewById(R.id.scroll_down);
        HideScrollButtons();

        // Navigation Drawer
        mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        setGreetings(mSystemUpdates.getCurrentGreeting());
        setImages();

        // Gender Model
        GenderModel genderModel = new ViewModelProvider(this).get(GenderModel.class);
        genderModel.setGender(item_gender);
        genderModel.setType(item_type);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.customer_toolbar);
        setSupportActionBar(toolbar);

        // Google AdMob Interstitial Ad
        adMobInterstitialAd = AdMobInterstitialAd.getInstance();
        adMobInterstitialAd.setFullScreenContentCallback(mFullScreenContentCallback);
        adMobInterstitialAd.loadNewAd(this);
    }
    private void initializeWindow() {
        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        postponeEnterTransition();
        createNotificationChannel();
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.new_activity_main);
        startPostponedEnterTransition();
    }
    private void parseBundle() {
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
    }
    private void setGreetings(String greeting) {
        String greetStr = greeting + mShopikUser.getFirstName();
        TextView name = findViewById(R.id.name);
        TextView navGreeting = mNavigationView.getHeaderView(0).findViewById(R.id.greeting);
        name.setText(greetStr);
        navGreeting.setText(greetStr);
    }
    private void setImages() {
        CircleImageView mNav_image = mNavigationView.getHeaderView(0).findViewById(R.id.profile_pic);
        CircleImageView mNav_provider_image = mNavigationView.getHeaderView(0).findViewById(R.id.profile_pic_provider);

        Macros.Functions.GlidePicture(this, mShopikUser.getImageUrl(), mNav_image, 350);

        String userProvider = mShopikUser.getProvider() != null ? mShopikUser.getProvider() :
                Macros.DEFAULT_PROFILE_IMAGE;

        switch (userProvider){
            case Macros.Providers.FACEBOOK:
                Macros.Functions.GlidePicture(this, R.drawable.fb, mNav_provider_image, 350);
                break;
            case Macros.Providers.GOOGLE:
                Macros.Functions.GlidePicture(this, R.drawable.google, mNav_provider_image, 350);
                break;
            default:
            case Macros.Providers.FIREBASE:
                Macros.Functions.GlidePicture(this, R.drawable.website, mNav_provider_image, 350);
                break;
        }
    }
    private void showInterstitialAd() {
        adMobInterstitialAd.showAd(this);
    }
    private void onInterstitialAdFailed() {
        adMobInterstitialAd.dismissAd();
        adMobInterstitialAd.loadNewAd(this);
    }
    private void setViewPager() {
        MainPagerAdapter mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainPager = findViewById(R.id.customer_container);
        mainPager.setAdapter(mainPagerAdapter);
        mainPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                animateLogo();
                switch(position) {
                    case 0:
                        HandleHomeFragmentConfiguration();
                        break;
                    case 1:
                        HandleNewFragmentConfiguration();
                        break;
                    case 2:
                        HandleFavoritesFragmentConfiguration();
                        break;
                    case 3:
                        HandleAllItemsFragmentConfiguration();
                        break;
                    default:
                        Log.e("main", position + "pressed");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void HandleAllItemsFragmentConfiguration() {
        bottomNavigationView.getMenu().findItem(R.id.bottom_search).setChecked(true);
        mFloatingActionButton.setExpanded(true);
        FadeOutBottomNav();
        ShowScrollButtons();
    }
    private void HandleFavoritesFragmentConfiguration() {
        bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setNumber(0);
        bottomNavigationView.getOrCreateBadge(R.id.bottom_favorites).setVisible(false);
        bottomNavigationView.getMenu().findItem(R.id.bottom_favorites).setChecked(true);
        HandleBottomNavFaded();
        ShowScrollButtons();
    }
    private void HandleNewFragmentConfiguration() {
        bottomNavigationView.getMenu().findItem(R.id.bottom_new).setChecked(true);
        HandleBottomNavFaded();
        ShowScrollButtons();
    }
    private void HandleHomeFragmentConfiguration() {
        bottomNavigationView.getMenu().findItem(R.id.bottom_swipe).setChecked(true);
        HandleBottomNavFaded();
        HideScrollButtons();
    }
    private void FadeOutBottomNav() {
        YoYo.with(Techniques.FadeOutDown).playOn(bottomNavigationView);
        YoYo.with(Techniques.FadeOutDown).playOn(bottomAppBar);
        bottomNavIsFaded = true;
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mainPager.getLayoutParams();
        lp.bottomMargin -= 20;
    }
    private void HandleBottomNavFaded() {
        if(bottomNavIsFaded) {
            YoYo.with(Techniques.FadeInUp).playOn(bottomNavigationView);
            YoYo.with(Techniques.FadeInUp).playOn(bottomAppBar);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mainPager.getLayoutParams();
            lp.bottomMargin += 20;
            bottomNavIsFaded = false;
        }
    }
    private void ShowScrollButtons(){
        if(scrollBtnHidden) {
            scrollUpFAB.show();
            scrollDownFAB.show();
            scrollBtnHidden = false;
        }
    }
    private void HideScrollButtons(){
        scrollBtnHidden = true;
        scrollUpFAB.hide();
        scrollDownFAB.hide();
    }

    private void animateLogo() {
        if(findViewById(R.id.logo_image) != null)
            YoYo.with(Techniques.Bounce).duration(1000).playOn(findViewById(R.id.logo_image));
    }
    private void setNavigationBarButtonsColor(int navigationBarColor) {
        View decorView = getWindow().getDecorView();
        int flags = decorView.getSystemUiVisibility();
        if (isColorLight(navigationBarColor)) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        decorView.setSystemUiVisibility(flags);
    }
    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
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
    private void showContactDialog() {
        prog = 0;
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.contact_form_dialog);
        CardView cardSheet = dialog.findViewById(R.id.card_sheet);
        BottomSheetBehavior<CardView> bottomSheetBehavior = BottomSheetBehavior.from(cardSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setDraggable(true);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dialog.dismiss();
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        TextView txt = dialog.findViewById(R.id.contact_form_header);
        Button submit = dialog.findViewById(R.id.submit_message);
        EditText message = dialog.findViewById(R.id.message);

        DatabaseReference messagesFS = FirebaseDatabase.getInstance().getReference().
                child(Macros.MESSAGES).child(mShopikUser.getId());

        ImageView checkIcon = dialog.findViewById(R.id.check_sign);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        TextView textView = dialog.findViewById(R.id.success);

        submit.setOnClickListener(v -> {
            String messageText = message.getText().toString();
            if (!messageText.isEmpty()) {
                Date currentTime = Calendar.getInstance().getTime();
                String datetime = DateFormat.getDateTimeInstance().format(currentTime);
                Map<String, Object> map = new HashMap<>();
                map.put("message", messageText);
                map.put("date", datetime);
                messagesFS.setValue(map);

                progressBar.setVisibility(View.VISIBLE);
                recursiveCirculate(progressBar, checkIcon, textView);

                message.setVisibility(View.INVISIBLE);
                submit.setVisibility(View.INVISIBLE);

                YoYo.with(Techniques.RollIn).delay(550).duration(3000).
                        onEnd(animator -> dialog.dismiss()).playOn(checkIcon);
            } else {
                Toast.makeText(this, "Please write something...", Toast.LENGTH_SHORT).show();
            }
        });

        txt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        txt.setText("Help Improve The App");

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }
    private void recursiveCirculate(ProgressBar progressBar, ImageView checkIcon, TextView textView) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prog += 1;
                progressBar.setProgress(prog);
                if (prog < 100)
                    handler.postDelayed(this, 5);
                else if (prog == 100) {
                    checkIcon.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
            }
        }, 5);
    }

    @Subscribe (threadMode = ThreadMode.BACKGROUND)
    public void onNewItemEvent(Database.NewItemEvent newItemEvent){
        total_items = newItemEvent.shoppingItems.size();
        ArrayList<ShoppingItem> list = new ArrayList<>(newItemEvent.shoppingItems);
        list.sort(Comparator.comparing(ShoppingItem::getId));

        ShoppingItem first = ((ShoppingItem)(list.get(0)));
        ShoppingItem last = ((ShoppingItem)(list.get(list.size()-1)));

        Database.getInstance().getLikesOfShoppingItem(item_type, item_gender,
                first.getSeller().toLowerCase() + "-" + first.getId(),
                last.getSeller().toLowerCase() + "-" + last.getId(),
                newItemEvent.shoppingItems
        );
    }

    @Subscribe
    public void onItemLikesInfoEvent(Database.ItemsLikesUnlikesInfo event){

        for(ShoppingItem item : event.allItems) {
            // AT LEAST 1 PERSON LIKED OR UNLIKED THIS ITEM
            if (!item.getConvertedLikedUsersIds().isEmpty() || !item.getConvertedUnlikedUsersIds().isEmpty()) {
                // GET INFO ABOUT USERS LIKED/UNLIKED
                Database.getInstance().getInteractedUserInfo(item);
            }
            // NOTHING
            else
                EventBus.getDefault().post(new Database.OnCompanyInfoReceivedEvent(item));
        }
    }

    @Subscribe
    public void onItemModifiedEvent(Database.ItemModifiedEvent itemModifiedEvent){
        ArrayList<ShoppingItem> list = new ArrayList<>(itemModifiedEvent.shoppingItems);
        list.sort(Comparator.comparing(ShoppingItem::getId));
        Database.getInstance().getLikesOfShoppingItem(item_type, item_gender,
                ((ShoppingItem)list.get(0)).getId(),
                ((ShoppingItem)(list.get(list.size()-1))).getId(),
                itemModifiedEvent.shoppingItems
        );
    }

    @Subscribe
    public void onPastInteractedItemsEvent(Database.UsersPastInteractedItemsEvent event){
        for(String itemId : event.interactedItems.keySet()) {
            if(Objects.equals(event.interactedItems.get(itemId), LIKED))
                mainModel.addLikedItemId(itemId, event.liked);
            else
                mainModel.addUnlikedItemId(itemId, event.unliked);
        }
        Log.i(TAG, "interactedItemsFinishedFetch");
        mainModel.updateUnseenItems();
    }

    @Subscribe
    public void onPreferredAttributesEvent(Database.PreferredAttributesEvent event){
        mainModel.setPreferred(new PreferredItem(event.preferredMap));
    }

    @Subscribe
    public void onLastItemEvent(Database.LastItemEvent lastItemEvent){
        lastItemId = lastItemEvent.itemId;
    }

    @Subscribe
    public void onUserDataChangedEvent(Database.UserDataUpdated event){
        setGreetings(mSystemUpdates.getCurrentGreeting());
        setImages();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onCompanyInfoReceived(Database.OnCompanyInfoReceivedEvent event){
        Company company = null;
        for(Company comp : ShopikApplication.companiesInfo){
            if(comp.getId().equals(event.item.getSellerId()))
                company = comp;
        }
        if (company != null ) {
            event.item.setSeller(company.getName());
            event.item.setSellerLogoUrl(company.getLogoUrl());
        }

        mainModel.addItem(event.item, total_items);

        mainModel.getCurrentItem().postValue(Pair.create(Objects.requireNonNull(mainModel.getAllItems().getValue()).size(), total_items));
        if(total_items == mainModel.getAllItems().getValue().size()){
            // FINISHED FETCHING ALL ITEMS
            Database.getInstance().fetchLikedUnlikedItems(item_type, item_gender);
        }
    }

    //INTERNET CONNECTION STATUS
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionChanged(SystemUpdates.InternetConnectionChanged internetConnectionChanged){
        /// DISCONNECTED FROM INTERNET
        if(!internetConnectionChanged.isConnected)
            HandleLostConnectionEvent();
    }
    private void HandleLostConnectionEvent() {
        Intent intent = new Intent(this, NoInternetActivity.class);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onDaytimeChangedEvent(SystemUpdates.GreetingsChangedEvent greetingsChanged){
        setGreetings(greetingsChanged.greetings);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onScrollingEvent(ScrollingEvent event){
        YoYo.with(Techniques.FadeOut).playOn(scrollDownFAB);
        YoYo.with(Techniques.FadeOut).playOn(scrollUpFAB);
        scrollDownFAB.setVisibility(View.GONE);
        scrollUpFAB.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void onNotScrollingEvent(NotScrollingEvent event){
        scrollDownFAB.setVisibility(View.VISIBLE);
        scrollUpFAB.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn).playOn(scrollDownFAB);
        YoYo.with(Techniques.FadeIn).playOn(scrollUpFAB);
    }
}
