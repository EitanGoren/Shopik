package com.eitan.shopik.ads;

import android.app.Activity;
import android.content.Context;

import com.facebook.ads.InterstitialAd;

public class AudienceNetworkInterstitialAd extends ShopikInterstitialAd {

    public InterstitialAd mInterstitialAd;

    public AudienceNetworkInterstitialAd(){};

    public InterstitialAd getInterstitialAd() {
        return mInterstitialAd;
    }

    @Override
    public void initialize(Context context) {

    }

    @Override
    public void loadNewAd(Activity activity) {

    }

    @Override
    public void dismissAd() {

    }

    @Override
    public void showAd(Context context) {

    }
}
