package com.eitan.shopik;

import static com.eitan.shopik.database.Database.LIKED;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.eitan.shopik.ads.AdMobBannerAd;
import com.eitan.shopik.ads.AdMobInterstitialAd;
import com.eitan.shopik.ads.AdMobNativeAd;
import com.eitan.shopik.database.Database;
import com.eitan.shopik.database.ShopikRepository;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.system.SystemUpdates;
import com.eitan.shopik.system.UploadEngagementDataToDB;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.firebase.FirebaseApp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Keep
public class ShopikApplication extends Application implements LifecycleObserver {

    private static final String TAG = "ShopikApplication";
    private static ShopikApplication instance;
    private final AdMobInterstitialAd adMobInterstitialAd;
    private final AdMobNativeAd adMobNativeAd;
    private final AdMobBannerAd adMobBannerAd;
    public static List<Company> companiesInfo;
    public static Set<String> sellers;

    public ShopikApplication() {
        instance = this;
        adMobNativeAd = AdMobNativeAd.getInstance();
        adMobInterstitialAd = AdMobInterstitialAd.getInstance();
        adMobBannerAd = AdMobBannerAd.getInstance();
        companiesInfo = new ArrayList<>();
        sellers = new HashSet<>();
    }
    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        EventBus.getDefault().register(this);

        FacebookSdk.setClientToken("2578292a9bb4ac35fe1a3427f11f842f");
        FacebookSdk.sdkInitialize(getApplicationContext());

        SystemUpdates systemUpdates = SystemUpdates.getInstance();
        systemUpdates.Initialize(getContext());

        adMobInterstitialAd.initialize(getContext());
        adMobNativeAd.setNativeAdOptions(
                new NativeAdOptions.Builder().
                setRequestMultipleImages(true).
                build()
        );
        adMobBannerAd.initialize(getContext());
    }

    @Subscribe
    public void onCompanyInfoEvent(Database.NewCompanyInfo newCompanyInfo){
        companiesInfo = newCompanyInfo.info;
        for(Company comp: companiesInfo){
            sellers.add(comp.getName());
        }

        ShopikRepository repository = new ShopikRepository(this);
        for(Company company : companiesInfo) {
           repository.insertCompany(company);
        }
    }

    @Subscribe
    public void onPastInteractedItemsEvent(Database.UsersPastInteractedItemsEvent event){
        for(String itemId : event.interactedItems.keySet()) {
            if(Objects.equals(event.interactedItems.get(itemId), LIKED)){

            }
            else{

            }
        }
        Log.i(TAG, "interactedItemsFinishedFetch");
    }
}