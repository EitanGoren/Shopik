package com.eitan.shopik;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.eitan.shopik.Items.ShoppingItem;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;
import static com.google.android.gms.ads.formats.NativeAdOptions.ADCHOICES_TOP_LEFT;

public class ShopikApplicationActivity extends Application {

    private static final int NUM_OF_ADS = 12;
    private static ShopikApplicationActivity instance;
    private static com.facebook.ads.InterstitialAd interstitialAd;
    private static ArrayList<ShoppingItem> shoppingAdsArray;
    private static int categoryClicks = 0;

    public ShopikApplicationActivity() {
        instance = this;
        shoppingAdsArray = new ArrayList<>();
    }

    public static Context getContext() {
        return instance;
    }

    //FACEBOOK INTERSTITIAL AD
    public static InterstitialAd getInterstitialAd() {
        return interstitialAd;
    }

    public static void setInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        interstitialAd = new com.facebook.ads.InterstitialAd(getContext(), Macros.FB_PLACEMENT_ID);
        // Load a new interstitial.
        interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
    }

    //GOOGLE NATIVE ADS
    private static void clearAds(){
        for(ShoppingItem item : shoppingAdsArray) {
            item.destroyAd();
        }
        shoppingAdsArray.clear();
    }

    public static Object getNextAd() {
        Random random = new Random();
        if( shoppingAdsArray.size() > 0) {
            int idx = (random.nextInt(shoppingAdsArray.size()));
            return shoppingAdsArray.get(idx);
        }
        else
            return null;
    }

    //TIMES CLICKED ON MAIN CUSTOMER
    public static int getCategoryClicks() {
        return categoryClicks;
    }

    public static void increaseCategoryClicks() {
         categoryClicks++;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Google Ads
        List<String> testDeviceIds = Collections.singletonList(Macros.TEST_DEVICE_ID);
        RequestConfiguration configuration = new RequestConfiguration.Builder().
                setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        //Facebook Ads
        AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        AudienceNetworkAds.initialize(this);

        // AdSettings.addTestDevice("34464d11-359b-4022-86a5-22489c17269d");
        interstitialAd = new com.facebook.ads.InterstitialAd(getApplicationContext(), Macros.FB_PLACEMENT_ID);
        // Load a new interstitial.
        interstitialAd.loadAd(EnumSet.of(CacheFlag.VIDEO));
    }

    public static void LoadAds(){
        new getAds().execute();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        clearAds();
    }

    private static class getAds extends AsyncTask<Void, Void, Void> {

        private UnifiedNativeAd tempAd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            clearAds();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Void... voids) {
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
                        .Builder(getContext(), Macros.NATIVE_ADVANCED_AD)
                        .forUnifiedNativeAd(unifiedNativeAd -> tempAd = unifiedNativeAd)
                        .withAdListener(new AdListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                ShoppingItem dummy = new ShoppingItem();
                                dummy.setAd(true);
                                dummy.setNativeAd(tempAd);
                                shoppingAdsArray.add(dummy);
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            tempAd = null;
        }
    }
}