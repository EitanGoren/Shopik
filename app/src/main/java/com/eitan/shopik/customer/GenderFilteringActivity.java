package com.eitan.shopik.customer;

import static org.greenrobot.eventbus.EventBus.getDefault;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.NoInternetActivity;
import com.eitan.shopik.R;
import com.eitan.shopik.adapters.ExplanationPagerViewAdapter;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.ShopikRepository;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.database.models.User;
import com.eitan.shopik.system.SystemUpdates;
import com.eitan.shopik.viewModels.GenderModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

public class GenderFilteringActivity extends AppCompatActivity {

    private int color;
    private TabLayout tabLayout;
    private static String name;
    private TextView marquee;
    private GenderModel model;
    private androidx.appcompat.widget.Toolbar toolbar;
    private ViewPager mMainPager;
    private androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener;
    private static MutableLiveData<Long> entries;
    private ShopikUser mShopikUser;
    private ExtendedFloatingActionButton mGenderBtn;
    private Snackbar snackbar;
    private MaterialCardView card;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

        mGenderBtn.setOnClickListener(view -> {
            if (mShopikUser.getGender().equals(Database.MEN)) {
                mGenderBtn.setIcon(getDrawable(R.drawable.ic_outline_female_24));
                mGenderBtn.setText(Database.MEN);
                model.setGender(Database.WOMEN);
            }
            else{
                mGenderBtn.setIcon(getDrawable(R.drawable.ic_baseline_male_24));
                mGenderBtn.setText(Database.WOMEN);
                model.setGender(Database.MEN);
            }
            updateModel();
            setColors();
            snackbar.dismiss();
        });
        mGenderBtn.performClick();

        setViewPager();

        setTabLayout();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setMarquee();

        topNavListener = item -> {
            Intent selectedIntent = null;
            switch (item.getTitle().toString()) {
                case "Log Out":
                    mShopikUser.signOut();
                    if (!mShopikUser.isAuthenticated()) {
                        selectedIntent = new Intent(GenderFilteringActivity.this, LandingPageActivity.class);
                        selectedIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

        entries.observe(this, aLong -> SystemUpdates.getInstance().launchReview(this));

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                //see Snackbar.Callback docs for event details
                card.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onShown(Snackbar snackbar) {
                card.setVisibility(View.VISIBLE);
            }
        });
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

    @Override
    protected void onDestroy() {
        getDefault().unregister(this);
        super.onDestroy();
    }

    private void init() {

        getDefault().register(this);

        postponeEnterTransition();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_gender_filtering);
        startPostponedEnterTransition();
        mMainPager = findViewById(R.id.gender_pager);

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());
        mShopikUser = ShopikUser.getInstance();
        model = new ViewModelProvider(this).get(GenderModel.class);
        name = mShopikUser.getFirstName() + " " + mShopikUser.getLastName();
        toolbar = findViewById(R.id.gender_toolbar);
        tabLayout = findViewById(R.id.gender_top_nav);
        marquee = findViewById(R.id.marquee);
        mGenderBtn = findViewById(R.id.gender_btn);
        card = findViewById(R.id.snackbar_card);
        entries = new MutableLiveData<>();

        CoordinatorLayout cl = findViewById(R.id.myCoordinatorLayout);
        cl.bringToFront();
        snackbar = Snackbar.make(cl, "Welcome Back " + mShopikUser.getFirstName() + "!", Snackbar.LENGTH_SHORT);
    }

    private void setViewPager() {
        ExplanationPagerViewAdapter mExplanationPagerViewAdapter = new ExplanationPagerViewAdapter(getSupportFragmentManager());
        mMainPager.setAdapter(mExplanationPagerViewAdapter);
    }

    private void updateModel() {
        model.setGender(mShopikUser.getGender());
        model.setName(name);
    }

    private void setToolbar() {
        toolbar.setOnMenuItemClickListener(topNavListener);

        Objects.requireNonNull(toolbar.getOverflowIcon()).
                setTint(ContextCompat.getColor(this, R.color.GenderScreenTheme));

        toolbar.setSoundEffectsEnabled(true);
    }

    private void setTabLayout() {
        tabLayout.setupWithViewPager(mMainPager,true);
        tabLayout.setSelectedTabIndicatorColor(color);
        tabLayout.setTabIndicatorFullWidth(true);
        tabLayout.setTabTextColors(getColor(R.color.GenderScreenTheme),color);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("New in");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Categories");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("Outlet");

        tabLayout.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private void setColors() {
        setToolbarColor();
        tabLayout.setTabTextColors(getColor(R.color.GenderScreenTheme),color);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    private void setToolbarColor() {
        if(mShopikUser.getGender().equals(Database.WOMEN))
            color = getColor(R.color.womenColor);
        else
            color = getColor(R.color.menColor);
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

    private void setMarquee() {
        String brandy = "";
        for(String brand : Macros.Items.brands) {
            brandy = brandy.concat(brand) + "     ";
        }
        marquee.setText(brandy);
        marquee.setSelected(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserDataChanged(Database.UserDataUpdated event){
        name = mShopikUser.getFirstName() + " " + mShopikUser.getLastName();
    }

    //INTERNET CONNECTION STATUS
    @Subscribe
    public void onInternetConnectionChanged(SystemUpdates.InternetConnectionChanged internetConnectionChanged){
        /// DISCONNECTED FROM INTERNET
        if(!internetConnectionChanged.isConnected)
            HandleLostConnectionEvent();
    }
    private void HandleLostConnectionEvent() {
        snackbar.dismiss();
        Intent intent = new Intent(this, NoInternetActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Subscribe
    public void onEntranceAppEvent(LandingPageActivity.EnterAppEvent event) {
        card.setVisibility(View.VISIBLE);
        snackbar.setActionTextColor(Color.BLACK);
        View snackbarView = snackbar.getView();

        snackbar.setAction("OK", view -> {
            snackbar.dismiss();
        });

        snackbarView.setBackgroundColor(Color.parseColor("#00000000"));
        TextView textView = snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(20);
        snackbar.show();
    }
}
