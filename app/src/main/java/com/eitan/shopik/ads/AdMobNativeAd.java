package com.eitan.shopik.ads;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

import java.util.Collections;
import java.util.List;

public class AdMobNativeAd extends ShopikNativeAd{

    private static volatile AdMobNativeAd instance = null;

    private NativeAd mNativeAd;

    private final String mAdUnitId = "ca-app-pub-3940256099942544/2247696110";
    private final String mAdUnitIdVideo = "ca-app-pub-3940256099942544/1044960115";

    private AdLoader mAdLoader;
    private NativeAdOptions mNativeAdOptions;

    private AdMobNativeAd(){}

    public void setNativeAdOptions(NativeAdOptions nativeAdOptions) {
        this.mNativeAdOptions = nativeAdOptions;
    }

    public static AdMobNativeAd getInstance() {
        if (instance == null) {
            // To make thread safe
            synchronized (AdMobNativeAd.class) {
                // check again as multiple threads
                // can reach above step
                if (instance == null)
                    instance = new AdMobNativeAd();
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

        mAdLoader = new AdLoader.Builder(context, mAdUnitIdVideo)
                .forNativeAd(nativeAd -> {
                    mNativeAd = nativeAd;
                    isAdLoaded = true;
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        dismissAd();
                       //loadNewAd();
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        isAdLoaded = true;
                    }
                })
                .withNativeAdOptions(mNativeAdOptions)
                .build();

        loadNewAd();
    }

    @Override
    public void loadNewAd() {
        this.mAdRequest = new AdRequest.Builder().build();
        mAdLoader.loadAd(mAdRequest);
    }

    @Override
    public void dismissAd() {
        isAdLoaded = false;
        mNativeAd.destroy();
    }

    public NativeAd getNativeAd() {
        return mNativeAd;
    }

}
