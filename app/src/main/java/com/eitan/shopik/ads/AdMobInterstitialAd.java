package com.eitan.shopik.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.eitan.shopik.database.Database;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Collections;
import java.util.List;

public class AdMobInterstitialAd extends ShopikInterstitialAd {

    private static volatile AdMobInterstitialAd instance = null;

    private InterstitialAd mInterstitialAd;
    private final MutableLiveData<Boolean> adIsReady;

    private final String TAG = "AdMobInterstitialAd";
    private final String mAdUnitId = "ca-app-pub-5843605953860340/7855076428";
    private final String mAdUnitIdVideo = "ca-app-pub-3940256099942544/8691691433";
    private final String mAdUnitIdRewarded = "ca-app-pub-3940256099942544/5354046379";

    private InterstitialAdLoadCallback mLoadCallback;
    private FullScreenContentCallback mFullScreenContentCallback;

    private AdMobInterstitialAd (){
        adIsReady = new MutableLiveData<>();
        //adIsReady.setValue(false);;
    }

    public static AdMobInterstitialAd getInstance() {
        if (instance == null) {
            // To make thread safe
            synchronized (AdMobInterstitialAd.class) {
                // check again as multiple threads
                // can reach above step
                if (instance == null)
                    instance = new AdMobInterstitialAd();
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
        mLoadCallback = new InterstitialAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                isAdLoaded = false;
                Log.w(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
            }

            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                Log.i(TAG,"onAdLoaded");
                mInterstitialAd = interstitialAd;
                setListener();
                isAdLoaded = true;
                adIsReady.postValue(true);
            }
        };
    }

    @Override
    public void loadNewAd(Activity activity) {
        this.mAdRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, mAdUnitId, mAdRequest, mLoadCallback);
    }

    @Override
    public void dismissAd() {
        isAdLoaded = false;
        mInterstitialAd = null;
    }

    @Override
    public void showAd(Context context) {
        if(mInterstitialAd != null) mInterstitialAd.show((Activity) context);
    }

    public void setFullScreenContentCallback(FullScreenContentCallback fullScreenContentCallback){
        this.mFullScreenContentCallback = fullScreenContentCallback;
    }

    private void setListener() {
        if(mFullScreenContentCallback != null) {
            mInterstitialAd.setFullScreenContentCallback(mFullScreenContentCallback);
        }
    }

    public MutableLiveData<Boolean> isAdReady(){
        return adIsReady;
    }
}
