package com.eitan.shopik.ads;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.Collections;
import java.util.List;

public class AdMobBannerAd extends ShopikBannerAd{

    private static volatile AdMobBannerAd instance = null;

    private final String mAdUnitId = "ca-app-pub-3940256099942544/6300978111";

    private AdMobBannerAd (){}

    public static AdMobBannerAd getInstance() {
        if (instance == null) {
            // To make thread safe
            synchronized (AdMobInterstitialAd.class) {
                // check again as multiple threads
                // can reach above step
                if (instance == null)
                    instance = new AdMobBannerAd();
            }
        }
        return instance;
    }

    @Override
    public void initialize(Context context) {
        //Google Ads
        List<String> testDeviceIds = Collections.singletonList("99223A94D4457F59E4DD34AD19AF149C");
        RequestConfiguration configuration = new RequestConfiguration.
                Builder().
                setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);

        isAdLoaded = false;

        MobileAds.initialize(context, initializationStatus -> {
            isAdLoaded = true;
            // BannerAd Showing
        });

        mAdRequest = new AdRequest.Builder().build();
    }

    @Override
    public void dismissAd() {
        isAdLoaded = false;
    }

    @Override
    public void loadNewAd(AdView view) {
        if(view == null) return;
        view.loadAd(mAdRequest);
        view.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
        this.mAdRequest = new AdRequest.Builder().build();
    }
}
