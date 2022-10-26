package com.eitan.shopik.ads;

import android.app.Activity;
import android.content.Context;

public abstract class ShopikInterstitialAd extends ShopikAd{
    public abstract void showAd(Context context);
    public abstract void loadNewAd(Activity activity);
}
