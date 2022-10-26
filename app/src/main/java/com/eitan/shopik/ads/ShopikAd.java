package com.eitan.shopik.ads;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;

public abstract class ShopikAd {

    protected boolean isAdLoaded;
    protected AdRequest mAdRequest;

    public ShopikAd(){
        isAdLoaded = false;
        mAdRequest = new AdRequest.Builder().build();
    }

    public boolean isAdLoaded() {
        return isAdLoaded;
    }
    public abstract void initialize(Context context);
    public abstract void dismissAd();
}
